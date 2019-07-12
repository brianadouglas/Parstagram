package com.example.parstagram;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.parstagram.fragments.PostDetailsFragment;
import com.example.parstagram.model.Post;
import com.parse.ParseFile;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    // the list of posts to be displayed
    private List<Post> mPosts;
    Context context;
    // pass in the posts array in the constructor
    public ProfileAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.mPosts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.profile_post_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // get the data according to position
        Post post = mPosts.get(position);
        // populate the views according to this data
        // only the image file is shown in this grid view
        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(context).load(image.getUrl()).into(holder.ivSingleProfilePost);
        }

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView ivSingleProfilePost;


        public ViewHolder(View itemView) {
            super(itemView);

            // view lookups
            ivSingleProfilePost = (ImageView) itemView.findViewById(R.id.ivSingleProfilePost);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            // go to the Post Details Fragment with clicking on a single post

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
}
