package com.chatcamp.plugin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.chatcamp.uikit.messages.HeaderView;
import com.chatcamp.uikit.messages.MessageInput;
import com.chatcamp.uikit.messages.MessagesList;
import com.chatcamp.uikit.messages.messagetypes.FileMessageFactory;
import com.chatcamp.uikit.messages.messagetypes.ImageMessageFactory;
import com.chatcamp.uikit.messages.messagetypes.MessageFactory;
import com.chatcamp.uikit.messages.messagetypes.TextMessageFactory;
import com.chatcamp.uikit.messages.messagetypes.VideoMessageFactory;
import com.chatcamp.uikit.messages.sender.AttachmentSender;
import com.chatcamp.uikit.messages.sender.CameraAttachmentSender;
import com.chatcamp.uikit.messages.sender.FileAttachmentSender;
import com.chatcamp.uikit.messages.sender.GalleryAttachmentSender;
import com.chatcamp.uikit.messages.typing.DefaultTypingFactory;

import java.util.ArrayList;
import java.util.List;

import io.chatcamp.sdk.BaseChannel;
import io.chatcamp.sdk.ChatCampException;
import io.chatcamp.sdk.GroupChannel;
import io.chatcamp.sdk.GroupChannelListQuery;
import io.chatcamp.sdk.OpenChannel;
import io.chatcamp.sdk.PreviousMessageListQuery;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import android.content.res.Resources;

import org.apache.cordova.*;

//import static com.chatcamp.plugin.ConversationMessage.TYPING_TEXT_ID;
//import static com.chatcamp.plugin.GroupDetailActivity.KEY_GROUP_ID;

public class ConversationActivity extends AppCompatActivity implements AttachmentSender.UploadListener {

    private MessagesList mMessagesList;
    private String channelType;
    private String channelId;
    private MessageInput input;
    private MaterialProgressBar progressBar;
    private PreviousMessageListQuery previousMessageListQuery;
    private BaseChannel channel;
    private HeaderView headerView;
    private Resources resources;
    private String package_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        package_name = getApplication().getPackageName();
        resources = getApplication().getResources();
        setContentView(resources.getIdentifier("activity_conversation", "layout", package_name));

        mMessagesList = findViewById(resources.getIdentifier("messagesList", "id", package_name));
        input = findViewById(resources.getIdentifier("edit_conversation_input", "id", package_name));
        progressBar = findViewById(resources.getIdentifier("progress_bar", "id", package_name));
        headerView = findViewById(resources.getIdentifier("header_view", "id", package_name));

        // use a linear layout manager

        setSupportActionBar(headerView.getToolbar());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        channelType = getIntent().getStringExtra("channelType");
        channelId = getIntent().getStringExtra("channelId");
        if (channelType.equals("open")) {
            OpenChannel.get(channelId, new OpenChannel.GetListener() {
                @Override
                public void onResult(OpenChannel openChannel, ChatCampException e) {
                    final OpenChannel o = openChannel;
                    getChannel(o);
                    openChannel.join(new OpenChannel.JoinListener() {
                        @Override
                        public void onResult(ChatCampException e) {
                            previousMessageListQuery = o.createPreviousMessageListQuery();
                            channel = o;
                        }
                    });

                }
            });

        } else {
            //TODO check the participant state - INVITED, ALL,  ACCEPTED
            final GroupChannelListQuery.ParticipantState groupFilter = GroupChannelListQuery.ParticipantState.ACCEPTED;//GroupChannelListQuery.ParticipantState.valueOf(getIntent().getStringExtra("participantState"));
            GroupChannel.get(channelId, new GroupChannel.GetListener() {
                @Override
                public void onResult(final GroupChannel groupChannel, ChatCampException e) {
                    getChannel(groupChannel);
//                    groupChannel.sync(new GroupChannel.SyncListener() {
//                        @Override
//                        public void onResult(ChatCampException e) {
//
//                        }
//                    });
                    if (groupFilter == GroupChannelListQuery.ParticipantState.INVITED) {
                        groupChannel.acceptInvitation(new GroupChannel.AcceptInvitationListener() {
                            @Override
                            public void onResult(GroupChannel groupChannel, ChatCampException e) {
                                channel = groupChannel;
                            }
                        });
                    } else {
                        channel = groupChannel;
                    }

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dataFile) {
        input.onActivityResult(requestCode, resultCode, dataFile);
        mMessagesList.onActivityResult(requestCode, resultCode, dataFile);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        input.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mMessagesList.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void getChannel(BaseChannel channel) {
        headerView.setChannel(channel);
        input.setChannel(channel);
        MessageFactory[] messageFactories = new MessageFactory[4];
        messageFactories[0] = new TextMessageFactory();
        messageFactories[1] = new ImageMessageFactory(this);
        messageFactories[2] = new VideoMessageFactory(this);
        messageFactories[3] = new FileMessageFactory(this);
        mMessagesList.addMessageFactories(messageFactories);
        mMessagesList.setChannel(channel);
        mMessagesList.setTypingFactory(new DefaultTypingFactory());
//        mMessagesList.setAvatarImageLoader(new ImageLoader() {
//            @Override
//            public void loadImage(ImageView imageView, String url) {
//                // add loading image logic here
//            }
//        });

        FileAttachmentSender fileAttachmentSender = new FileAttachmentSender(this, channel, "File", resources.getIdentifier("ic_document", "drawable", package_name));
        fileAttachmentSender.setUploadListener(this);
        GalleryAttachmentSender galleryAttachmentSender = new GalleryAttachmentSender(this, channel, "Gallery", resources.getIdentifier("ic_gallery", "drawable", package_name));
        galleryAttachmentSender.setUploadListener(this);
        CameraAttachmentSender cameraAttachmentSender = new CameraAttachmentSender(this, channel, "Camera", resources.getIdentifier("ic_camera", "drawable", package_name));
        cameraAttachmentSender.setUploadListener(this);
        List<AttachmentSender> attachmentSenders = new ArrayList<AttachmentSender>();
        attachmentSenders.add(fileAttachmentSender);
        attachmentSenders.add(cameraAttachmentSender);
        attachmentSenders.add(galleryAttachmentSender);

        input.setAttachmentSenderList(attachmentSenders);
    }

    @Override
    public void onUploadProgress(int progress) {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(progress);
    }

    @Override
    public void onUploadSuccess() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onUploadFailed() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
