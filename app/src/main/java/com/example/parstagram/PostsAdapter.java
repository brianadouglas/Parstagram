package com.example.parstagram;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.parstagram.fragments.PostDetailsFragment;
import com.example.parstagram.fragments.ProfileFragment;
import com.example.parstagram.model.Post;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    // the list of posts to be displayed
    private List<Post> mPosts;
    Context context;
    int likedPosition; // denotes the position of the post to be disliked
    public static final String TAG = "PostsFragment";

    // pass in the posts array in the constructor
    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.mPosts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.post_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // get the data according to position
        Post post = mPosts.get(position);
        JSONArray likedUsers = post.getLikes();
        // populate the views according to this data
        String username = post.getUser().getUsername();
        holder.tvUsername.setText(username);

        // caption configured so that username is in bold typeface
        String sourceString = "<b>" + username + "</b>  " + post.getDescription();
        holder.tvCaption.setText(Html.fromHtml(sourceString));

        // setting the number of likes
        if (likedUsers != null) {
            sourceString = "<b>" + likedUsers.length() + " likes </b>";
        } else {
            sourceString =  "<b>No likes</b>";
        }
        holder.tvLikes.setText(Html.fromHtml(sourceString));


        // check if the user has a profilePicture on their account
        ParseFile profile = post.getUser().getParseFile("profilePicture");

        if (profile != null) {
            Glide.with(context).load(profile.getUrl()).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.ivAvatar) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    holder.ivAvatar.setImageDrawable(circularBitmapDrawable);
                }
            });
        }

        ParseFile image = post.getImage();
        if (image != null) {

            Glide.with(context).load(image.getUrl()).into(holder.ivPost);
        }
        holder.tvTimeStampPost.setText(getRelativeTimeAgo(post.getCreatedAt().toString()));

        // reflect whether or not the current user has liked the post
        assert likedUsers != null;
        for (int index = 0; index < likedUsers.length()+1; index++) {
            try {
                if (likedUsers.getString(index).equals(ParseUser.getCurrentUser().getObjectId())) {
                    // the like button shows up red if the user's id can be found in the array of likes
                    holder.btnHeart.setSelected(true);
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    // Methods used for pull to refresh feature
    // Clear all the elements from the recycler
    public void clear() {
        mPosts.clear();
        notifyDataSetChanged();
    }

    // Repopulates the list and the recyclerview
    public void addAll(List<Post> list) {
        mPosts.addAll(list);
        notifyDataSetChanged();
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivAvatar;
        TextView tvUsername;
        ImageView ivPost;
        TextView tvCaption;
        TextView tvTimeStampPost;
        ImageButton btnHeart;
        TextView tvLikes;


        public ViewHolder(View itemView) {
            super(itemView);

            // view lookups
            ivAvatar = (ImageView) itemView.findViewById(R.id.ivAvatar);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            ivPost = (ImageView) itemView.findViewById(R.id.ivPost);
            tvCaption = (TextView) itemView.findViewById(R.id.tvCaption);
            tvTimeStampPost = (TextView) itemView.findViewById(R.id.tvTimeStampPost);
            btnHeart = (ImageButton) itemView.findViewById(R.id.btnHeart);
            tvLikes = (TextView) itemView.findViewById(R.id.tvLikes);
            itemView.setOnClickListener(this);

            btnHeart.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Post post = mPosts.get(position);
                        JSONArray likes = post.getLikes();
                        // check if the current user has liked the image - check if the button is currently enabled
                        if (btnHeart.isSelected()) {
                            // the current user has already liked the post and is now disliking it
                            btnHeart.setSelected(false);
                            for (int index = 0; index < likes.length(); index ++) {
                                try {
                                    if (likes.getString(index).equals(ParseUser.getCurrentUser().getObjectId())) {
                                        // end search when the current user has been found
                                        likedPosition = index;
                                        break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            // remove them from the list of likes that the post has
                            likes.remove(likedPosition);
                        } else {
                            btnHeart.setSelected(true);
                            likes.put(ParseUser.getCurrentUser().getObjectId());
                        }
                        post.setLikes(likes);
                        post.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(com.parse.ParseException e) {
                                if (e == null) {
                                    Log.d(TAG, "Success with like button");
                                } else {
                                    e.printStackTrace();
                                }
                            }
                        });
                        String sourceLikes = "<b>" + likes.length() + " likes </b>";
                        tvLikes.setText(Html.fromHtml(sourceLikes));
                    }
                }
            });

            ivAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    profileOnClick();
                }
            });

            tvUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    profileOnClick();
                }
            });

        }

        @Override
        public void onClick(View v) {
            // go to the Post Details View with clicking on a single post
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Post post = mPosts.get(position);

                // cast the context of the parent activity
                FeedActivity activity = (FeedActivity) context;
                // start the fragment transaction
                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                // creating a new instance of the details fragment and passing the post as the attribute
                PostDetailsFragment detailsFragment = PostDetailsFragment.newInstance(post);
                // change the container to the details fragment and add commit
                fragmentTransaction.replace(R.id.flContainer, detailsFragment).commit();
            }
        }

        public void profileOnClick() {
            // go to the Profile Fragment when clicking on the profile image or the username with a post
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Post post = mPosts.get(position);

                // cast the context of the parent activity
                FeedActivity activity = (FeedActivity) context;
                // start the fragment transaction
                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                // creating a new instance of the details fragment and passing the post as the attribute
                ProfileFragment profileFragment = ProfileFragment.newInstance(post.getUser());
                // change the container to the details fragment and add commit
                fragmentTransaction.replace(R.id.flContainer, profileFragment).commit();
            }
        }

    }
}
