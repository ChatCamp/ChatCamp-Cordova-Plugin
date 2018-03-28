package com.chatcamp.plugin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import android.content.res.Resources;

import com.chatcamp.*;

/**
 * Created by shubhamdhabhai on 15/02/18.
 */

public class MediaPreviewActivity extends AppCompatActivity {

    public static final String  IMAGE_URI = "image_uri";
    public static final String  VIDEO_URI = "video_uri";

    private ImageView imageView;
    private VideoView videoView;
    private FrameLayout frameLayout;
    private Resources resources;
    private String package_name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        package_name = getApplication().getPackageName();
        resources = getApplication().getResources();
        setContentView(resources.getIdentifier("image_preview_layout", "layout", package_name));

        final Intent intent = getIntent();
        final String uri = intent.getStringExtra(IMAGE_URI);
        final String videoUri = intent.getStringExtra(VIDEO_URI);
        imageView = (ImageView)findViewById(resources.getIdentifier("preview_image_view", "id", package_name));
        videoView = findViewById(resources.getIdentifier("preview_video_view", "id", package_name));
        frameLayout = findViewById(resources.getIdentifier("fl_container_video", "id", package_name));
        TextView sendButton = findViewById(resources.getIdentifier("tv_send", "id", package_name));
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent();
                intent1.putExtra(IMAGE_URI, uri);
                setResult(RESULT_OK, intent1);
                finish();
            }
        });
        if(!TextUtils.isEmpty(videoUri)) {
            sendButton.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
            videoView.setVideoURI(Uri.parse(videoUri));
            MediaController mediaController = new MediaController(this);
            videoView.setMediaController(mediaController);
            videoView.start();
        } else if(getContentResolver().getType(Uri.parse(uri)).contains("image")) {
            frameLayout.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageURI(Uri.parse(uri));
        } else if(getContentResolver().getType(Uri.parse(uri)).contains("video")) {
            imageView.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
            videoView.setVideoPath((FilePath.getPath(this, Uri.parse(uri))));
            MediaController mediaController = new MediaController(this);
            videoView.setMediaController(mediaController);
            videoView.start();
        }
    }
}
