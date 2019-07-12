package com.example.parstagram.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.parstagram.R;
import com.example.parstagram.model.Post;
import com.parse.ParseFile;

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

        // unwrap the post object from the parcel


        // populate the views according to this data
        String username = post.getUser().getUsername();
        tvDUsername.setText(username);

        // caption configured so that username is in bold typeface
        String sourceString = "<b>" + username + "</b>  " + post.getDescription();
        tvDCaption.setText(Html.fromHtml(sourceString));

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


