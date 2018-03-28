package com.chatcamp.plugin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.chatcamp.sdk.ChatCampException;
import io.chatcamp.sdk.GroupChannel;
import io.chatcamp.sdk.Participant;

import android.content.res.Resources;

public class GroupDetailActivity extends AppCompatActivity implements GroupDetailAdapter.OnParticipantClickedListener {
    public  static final String KEY_GROUP_ID = "key_group_id";


    private RecyclerView participantRv;
    private ImageView toolbarIv;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private GroupDetailAdapter adapter;
    private GroupChannel groupChannelGLobal;
    private Resources resources;
    private String package_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        package_name = getApplication().getPackageName();
        resources = getApplication().getResources();
        setContentView(resources.getIdentifier("activity_group_detail", "layout", package_name));
        toolbar = (Toolbar) findViewById(resources.getIdentifier("toolbar", "id", package_name));
        collapsingToolbarLayout = findViewById(resources.getIdentifier("collapsingToolbar", "id", package_name));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        participantRv  = findViewById(resources.getIdentifier("rv_participant_list", "id", package_name));
        LinearLayoutManager manager = new LinearLayoutManager(this);
        participantRv.setLayoutManager(manager);
        adapter = new GroupDetailAdapter(this);
        adapter.setParticipantClickedListener(this);
        participantRv.setAdapter(adapter);
        toolbarIv = findViewById(resources.getIdentifier("toolbarImage", "id", package_name));
        String id = getIntent().getStringExtra(KEY_GROUP_ID);
        GroupChannel.get(id, new GroupChannel.GetListener() {
            @Override
            public void onResult(GroupChannel groupChannel, ChatCampException e) {
                populateUi(groupChannel);
            }
        });

    }

    private void populateUi(GroupChannel groupChannel) {
        groupChannelGLobal = groupChannel;
        collapsingToolbarLayout.setTitle(groupChannel.getName());
        Picasso.with(this).load(groupChannel.getAvatarUrl())
          .placeholder(resources.getIdentifier("icon_default_contact", "drawable", package_name))
                .error(resources.getIdentifier("icon_default_contact", "drawable", package_name))
                .into(toolbarIv);
        List<ParticipantView> participantList = new ArrayList<>();
        for(Participant participant : groupChannel.getParticipants()) {
            participantList.add(new ParticipantView(participant));
        }
        adapter.clear();
        adapter.addAll(participantList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(resources.getIdentifier("menu_group_detail", "menu", package_name), menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        } else if(item.getItemId() == resources.getIdentifier("action_edit_group", "id", package_name)) {
            //Toast.makeText(this, "Edit group", Toast.LENGTH_LONG).show();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(GroupDetailActivity.this);
            alertDialog.setTitle("Update Group");
            alertDialog.setMessage("Enter Group Name");

            final EditText input = new EditText(GroupDetailActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input);

            alertDialog.setPositiveButton("UPDATE",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if(TextUtils.isEmpty(input.getText())) {
                                input.setError("Group name could not be empty");
                            } else {
                                groupChannelGLobal.update(input.getText().toString(), null, null, new GroupChannel.UpdateListener() {
                                    @Override
                                    public void onResult(GroupChannel groupChannel, ChatCampException e) {
                                        populateUi(groupChannel);
                                    }
                                });
                            }
                        }
                    });

            alertDialog.setNegativeButton("CANCEL",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAddParticipantClicked() {
        Toast.makeText(this, "Add Participant Clicked", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onParticipantClicked(Participant participant) {
        Toast.makeText(this, participant.getDisplayName() + " Participant Clicked", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.KEY_PARTICIPANT_ID, participant.getId());
        intent.putExtra(UserProfileActivity.KEY_GROUP_ID, groupChannelGLobal.getId());
        startActivity(intent);
    }

    @Override
    public void onExitGroupClicked() {
        groupChannelGLobal.leave(new GroupChannel.LeaveListener() {
            @Override
            public void onResult(ChatCampException e) {
                finish();
            }
        });
    }
}
