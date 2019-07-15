package com.example.parstagram.fragments;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
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
import com.example.parstagram.R;
import com.example.parstagram.model.Post;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PostDetailsFragment extends Fragment {

    Post post;
    private ImageView ivDAvatar;
    private TextView tvDUsername;
    private ImageView ivDPost;
    private TextView tvDCaption;
    private TextView tvTimeStamp;
    private ImageButton btnDHeart;
    private TextView tvDLikes;
    private int likedPosition;
    public static final String TAG = "DetailsFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the Parcelable object
        post = this.getArguments().getParcelable("post");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivDAvatar = view.findViewById(R.id.ivDAvatar);
        tvDUsername = view.findViewById(R.id.tvDUsername);
        ivDPost = view.findViewById(R.id.ivDPost);
        tvDCaption = view.findViewById(R.id.tvDCaption);
        tvTimeStamp = view.findViewById(R.id.tvTimeStamp);
        btnDHeart = view.findViewById(R.id.btnDHeart);
        tvDLikes = view.findViewById(R.id.tvDLikes);

        btnDHeart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                JSONArray likes = post.getLikes();
                // check if the current user has liked the image - check if the button is currently enabled
                if (btnDHeart.isSelected()) {
                    // the current user has already liked the post and is now disliking it
                    btnDHeart.setSelected(false);
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
                    btnDHeart.setSelected(true);
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
                // update the textview with the number of likes
                String sourceLikes = "<b>" + likes.length() + " likes </b>";
                tvDLikes.setText(Html.fromHtml(sourceLikes));
            }
        });

        // on click listeners to get to the profile fragment
        ivDAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileClick(post.getUser());
            }
        });

        tvDUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileClick(post.getUser());
            }
        });

        JSONArray likedUsers = post.getLikes();

        // populate the views according to this data
        String username = post.getUser().getUsername();
        tvDUsername.setText(username);

        // caption configured so that username is in bold typeface
        String sourceString = "<b>" + username + "</b>  " + post.getDescription();
        tvDCaption.setText(Html.fromHtml(sourceString));

        // setting the number of likes
        if (likedUsers != null) {
            sourceString = "<b>" + likedUsers.length() + " likes </b>";
        } else {
            sourceString =  "<b>No likes</b>";
        }
        tvDLikes.setText(Html.fromHtml(sourceString));

        for (int index = 0; index < likedUsers.length(); index++) {
            try {
                if (likedUsers.getString(index).equals(ParseUser.getCurrentUser().getObjectId())) {
                    // the like button shows up red if the user's id can be found in the array of likes
                    btnDHeart.setSelected(true);
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // check if the user has a profilePicture on their account
        ParseFile profile = post.getUser().getParseFile("profilePicture");

        if (profile != null) {
            Glide.with(this).load(profile.getUrl()).asBitmap().centerCrop().into(new BitmapImageViewTarget(ivDAvatar) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    ivDAvatar.setImageDrawable(circularBitmapDrawable);
                }
            });
        }


        ParseFile image = post.getImage();
        if (image != null) {

            Glide.with(this).load(image.getUrl()).into(ivDPost);
        }

        Date timeStamp = post.getCreatedAt();
        //create a date string.
        tvTimeStamp.setText(getRelativeTimeAgo(timeStamp.toString()));
    }

    public void profileClick (ParseUser user) {
        //Put the value
        ProfileFragment ldf = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        ldf.setArguments(args);
        //Inflate the fragment
        getFragmentManager().beginTransaction().replace(R.id.flContainer, ldf).commit();
    }

    // to accept the relevant post as an argument from either the posts or profile fragment
    public static PostDetailsFragment newInstance(Post post) {
        PostDetailsFragment detailsFragment = new PostDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("post", post);
        detailsFragment.setArguments(args);
        return detailsFragment;
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


