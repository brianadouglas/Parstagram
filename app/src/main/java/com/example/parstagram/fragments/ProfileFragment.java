package com.example.parstagram.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.parstagram.ProfileAdapter;
import com.example.parstagram.R;
import com.example.parstagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class ProfileFragment extends Fragment {
    // inheriting all of the behaviour of PostsFragment
    private RecyclerView rvPostsGrid;
    protected ProfileAdapter adapter;
    private ImageView ivProfilePicture;
    private TextView tvProfileUsername;
    protected List<Post> mPosts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // load the profile image and username first
        ivProfilePicture = (ImageView) view.findViewById(R.id.ivProfilePicture);
        tvProfileUsername = (TextView) view.findViewById(R.id.tvProfileUsername);

        Glide.with(getContext()).load(R.drawable.instagram_user_filled_24).asBitmap().centerCrop().into(new BitmapImageViewTarget(ivProfilePicture) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                ivProfilePicture.setImageDrawable(circularBitmapDrawable);
            }
        });

        tvProfileUsername.setText(ParseUser.getCurrentUser().getUsername());


        rvPostsGrid = view.findViewById(R.id.rvPostsGrid); // reference to the recycler view
        // create the data source
        mPosts = new ArrayList<>();
        // create the adapter
        adapter = new ProfileAdapter(getContext(), mPosts);
        // set the adapter on the recycler view
        rvPostsGrid.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvPostsGrid.setLayoutManager(new GridLayoutManager(getContext(), 3));


        queryPosts();
    }


    private void queryPosts() {
        ParseQuery<Post> postQuery = new ParseQuery<Post>(Post.class);
        postQuery.include(Post.KEY_USER);
        postQuery.setLimit(20); // returns only the first 20 posts
        // to filter the posts to show only those made by the signed-in user
        postQuery.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        postQuery.addDescendingOrder(Post.KEY_CREATED); // order chronologically
        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e("ProfileFragment", "Error with query");
                    e.printStackTrace();
                    return;
                }
                mPosts.addAll(posts);
                adapter.notifyDataSetChanged();
                for (int i = 0; i < posts.size(); i++) {
                    Post post = posts.get(i);
                    Log.d("ProfileFragment", String.format("Post: %s from %s", post.getDescription(), post.getUser().getUsername()));
                }
            }
        });
    }
}
