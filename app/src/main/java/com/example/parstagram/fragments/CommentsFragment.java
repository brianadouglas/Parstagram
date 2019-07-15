package com.example.parstagram.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.parstagram.R;
import com.example.parstagram.model.Post;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class CommentsFragment extends Fragment {

    private EditText etComment;
    private Button btnSubmitComment;
    Post post;
    String comment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the Parcelable object
        post = this.getArguments().getParcelable("post");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etComment = (EditText) view.findViewById(R.id.etComment);
        btnSubmitComment = (Button) view.findViewById(R.id.btnSubmitComment);

        btnSubmitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the JSON array from the post Parse object
                JSONObject comments = post.getComments();
                comment = etComment.getText().toString();
                if (comment.length() != 0) {
                    try {
                        comments.put(ParseUser.getCurrentUser().getUsername(), comment);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // set the comments for the post
                    post.setComments(comments);
                    // save the post to the Parse database
                    post.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("CommentsFragment", "Successfully saved comment");
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "No comment to post", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // to accept the relevant post as an argument from either the posts or profile fragment
    public static CommentsFragment newInstance(Post post) {
        CommentsFragment commentsFragment = new CommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable("post", post);
        commentsFragment.setArguments(args);
        return commentsFragment;
    }
}
