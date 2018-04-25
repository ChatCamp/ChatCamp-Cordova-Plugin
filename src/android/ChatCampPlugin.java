package com.chatcamp.plugin;

import android.content.Context;
import android.util.Log;
import android.content.Intent;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.chatcamp.sdk.ChatCamp;
import io.chatcamp.sdk.ChatCampException;
import io.chatcamp.sdk.BaseChannel;
import io.chatcamp.sdk.GroupChannel;
import io.chatcamp.sdk.User;

import com.stfalcon.chatkit.messages.MessagesList;
import com.chatcamp.plugin.ConversationActivity;
import io.chatcamp.sdk.GroupChannelListQuery;

import com.google.gson.Gson;


public class ChatCampPlugin extends CordovaPlugin {

    private static final String TAG = "ChatcampPlugin";

    private String userId;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("init")) {
            this.initChatcamp(callbackContext, args);
            return true;
        } else if (action.equals("createGroup")) {
            this.createGroup(callbackContext, args);
        }
        return false;
    }

    private void initChatcamp(final CallbackContext callbackContext, JSONArray args) {

        try {
            JSONObject input = args.getJSONObject(0);
            userId = input.get("userId").toString();
            String appId = input.get("appId").toString();

            Context context = cordova.getActivity().getApplicationContext();
            ChatCamp.init(context, appId);
            ChatCamp.connect(userId, new ChatCamp.ConnectListener() {
                @Override
                public void onConnected(User user, ChatCampException e) {
                    if (e == null) {
                      //TODO Use Gson to create String of the User object,
                      //Getting some error right now, may be related to proguard of chatcamp sdk
                      // callbackContext.success(new Gson().toJson(groupChannel));
                        // callbackContext.success(new Gson().toJson(user));
                          callbackContext.success("success");
                    } else {
                        // callbackContext.error(new Gson().toJson(e));

                        callbackContext.error(e.getMessage());

                    }
                }
            });
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    private void createGroup(final CallbackContext callbackContext, JSONArray args) {
        try {
            final Context context = cordova.getActivity().getApplicationContext();
            JSONObject input = args.getJSONObject(0);
            String userIds = input.get("userIds").toString();
            String channelName = input.get("channelName").toString();
            boolean isDistinct = input.get("isDistinct").toString().equals("true");
            String[] participants = userIds.split(",");
            GroupChannel.create(channelName, participants, isDistinct, new BaseChannel.CreateListener() {
                @Override
                public void onResult(BaseChannel groupChannel, ChatCampException e) {
                  if(e == null) {
                    //TODO Use Gson to create String of the groupchannel object,
                    //Getting some error right now, may be related to proguard of chatcamp sdk
                    // callbackContext.success(new Gson().toJson(groupChannel));
                    callbackContext.success("success");
                    Intent intent = new Intent(context, ConversationActivity.class);
                    intent.putExtra("channelType", "group");
                    intent.putExtra("participantState", GroupChannelListQuery.ParticipantState.ALL.name());
                    intent.putExtra("channelId", groupChannel.getId());
                    intent.putExtra("userId", userId);
                    ChatCampPlugin.this.cordova.getActivity().startActivity(intent);
                  } else {
                    // callbackContext.error(new Gson().toJson(e));
                    callbackContext.error(e.getMessage());
                  }
                }
            });
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }
}
