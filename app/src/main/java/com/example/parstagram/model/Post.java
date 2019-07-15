package com.example.parstagram.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

@ParseClassName("Post")
public class Post extends ParseObject {
    // extension of Parse object to make the fields easily accessible

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED = "createdAt";
    public static final String KEY_LIKES = "likes";


    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    // the likes field contains an array of the object ids of the users that have liked the post
    public JSONArray getLikes() {
        // prevents returning null in the case that the post doesn't have likes as yet
        JSONArray likes =  getJSONArray(KEY_LIKES);
        if (likes == null) {
            return new JSONArray();
        } else {
            return likes;
        }
    }

    public void setLikes(JSONArray newLikes) {
        put(KEY_LIKES, newLikes);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public static class Query extends ParseQuery<Post> {
        public Query() {
            super(Post.class);
        }
        // will be querying a post

        public Query getTop() {
            // limit to only grab the first 20
            setLimit(20);
            return this; // this is a builder pattern that allows users to chain these methods
        }

        public Query withUser() {
            // ensures that the query returns the associated user
            include("user");
            return this;
        }
    }
}
