package com.example.parstagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.parstagram.fragments.ComposeFragment;
import com.example.parstagram.fragments.PostsFragment;
import com.example.parstagram.fragments.ProfileFragment;
import com.example.parstagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    //TODO: define the fields for the elements in the views
    private BottomNavigationView bottomNavigationView;
    //protected EndlessRecyclerViewScroller scrollListener;

    // member variables concerned with the camera activity
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    File photoFile;

    // the adapter and recycler view
    PostsAdapter postsAdapter;
    ArrayList<Post> posts;
    RecyclerView rvPosts;
    LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        // fragment manager
        final FragmentManager fragmentManager = getSupportFragmentManager();

        //TODO: associate fields with views

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);


        // loadTopPosts(); // load the top 20 posts

        // TODO: onClick listener definition to get the attributes for the new post being created

        //TODO: Navigation items listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = null;
                switch(menuItem.getItemId()) {
                    case R.id.home:
                        fragment = new PostsFragment();
                        Toast.makeText(FeedActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.newPost:
                        fragment = new ComposeFragment();
                        Toast.makeText(FeedActivity.this, "New Post", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.icon:
                    default:
                        fragment = new ProfileFragment();
                        Toast.makeText(FeedActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                        break;
                }
                // begin a transaction (The way fragments are switched onto the screen) to replace the contents of the frame layout with the fragment
                // then commit - do the operation immediately
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Set default selection - app opens at the home selection where the feed is displayed
        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feed, menu);
        return true;
    }

    // first thing done when the feed loads
    private void loadTopPosts() {
        final Post.Query postQuery = new Post.Query();
        postQuery.getTop().withUser();

        ParseQuery.getQuery(Post.class).findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Log.d("FeedActivity", String.format("Post[ %s ] = %s", i, objects.get(i).getDescription()));
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void logOut() {
        ParseUser.logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        this.startActivity(intent); // returns to the Log in Activity
        return; // prevents being able to come back to this activity through builtin Android back button
    }

}
