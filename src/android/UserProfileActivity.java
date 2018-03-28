package com.chatcamp.plugin;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.chatcamp.sdk.ChatCampException;
import io.chatcamp.sdk.GroupChannel;
import io.chatcamp.sdk.User;

public class UserProfileActivity extends AppCompatActivity {

    public static final String KEY_PARTICIPANT_ID = "key_participant_id";
    public static final String KEY_GROUP_ID = "key_group_id";

    private TextView onlineStatusTv;
    private ImageView onlineIv;
    private TextView lastSeenTv;
    private ImageView toolbarIv;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Resources resources;
    private String package_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        package_name = getApplication().getPackageName();
        resources = getApplication().getResources();
        setContentView(resources.getIdentifier("activity_user_profile", "layout", package_name));
        Toolbar toolbar = (Toolbar) findViewById(resources.getIdentifier("toolbar", "id", package_name));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        onlineStatusTv = findViewById(resources.getIdentifier("tv_online_status", "id", package_name));
        onlineIv = findViewById(resources.getIdentifier("iv_online", "id", package_name));
        lastSeenTv = findViewById(resources.getIdentifier("tv_last_seen", "id", package_name));
        toolbarIv = findViewById(resources.getIdentifier("toolbarImage", "id", package_name));
        collapsingToolbarLayout = findViewById(resources.getIdentifier("collapsingToolbar", "id", package_name));
        final String participantId = getIntent().getStringExtra(KEY_PARTICIPANT_ID);
        String groupId = getIntent().getStringExtra(KEY_GROUP_ID);
        if (!TextUtils.isEmpty(groupId)) {
            GroupChannel.get(groupId, new GroupChannel.GetListener() {
                @Override
                public void onResult(GroupChannel groupChannel, ChatCampException e) {
                    User user = groupChannel.getParticipant(participantId);
                    if (user != null) {
                        collapsingToolbarLayout.setTitle(user.getDisplayName());
                        Picasso.with(UserProfileActivity.this).load(user.getAvatarUrl())
                                .placeholder(resources.getIdentifier("icon_default_contact", "drawable", package_name))
                                .error(resources.getIdentifier("icon_default_contact", "drawable", package_name))
                                .into(toolbarIv);
                        if (user.isOnline()) {
                            onlineIv.setVisibility(View.VISIBLE);
                            lastSeenTv.setVisibility(View.GONE);
                            onlineStatusTv.setText("Online");
                        } else {
                            onlineIv.setVisibility(View.GONE);
                            lastSeenTv.setVisibility(View.VISIBLE);
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = new Date(user.getLastSeen() * 1000);
                            lastSeenTv.setText(format.format(date));
                            onlineStatusTv.setText("Last Seen");
                        }
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
