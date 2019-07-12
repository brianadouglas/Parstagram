package com.example.parstagram;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.parstagram.fragments.PostDetailsFragment;
import com.example.parstagram.model.Post;
import com.parse.ParseFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    // the list of posts to be displayed
    private List<Post> mPosts;
    Context context;

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
        // populate the views according to this data
        String username = post.getUser().getUsername();
        holder.tvUsername.setText(username);

        // caption configured so that username is in bold typeface
        String sourceString = "<b>" + username + "</b>  " + post.getDescription();
        holder.tvCaption.setText(Html.fromHtml(sourceString));

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

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView ivAvatar;
        TextView tvUsername;
        ImageView ivPost;
        TextView tvCaption;
        TextView tvTimeStampPost;


        public ViewHolder(View itemView) {
            super(itemView);

            // view lookups
            ivAvatar = (ImageView) itemView.findViewById(R.id.ivAvatar);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            ivPost = (ImageView) itemView.findViewById(R.id.ivPost);
            tvCaption = (TextView) itemView.findViewById(R.id.tvCaption);
            tvTimeStampPost = (TextView) itemView.findViewById(R.id.tvTimeStampPost);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // go to the Post Details View with clicking on a single post
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Post post = mPosts.get(position);

                // cast the context of the parent activity
                FeedActivity activity = (FeedActivity)context;
                // start the fragment transaction
                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                // creating a new instance of the details fragment and passing the post as the attribute
                PostDetailsFragment detailsFragment = PostDetailsFragment.newInstance(post);
                // change the container to the details fragment and add commit
                fragmentTransaction.replace(R.id.flContainer, detailsFragment).commit();
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
}
