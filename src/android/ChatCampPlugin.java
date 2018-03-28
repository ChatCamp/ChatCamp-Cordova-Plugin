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
import io.chatcamp.sdk.GroupChannel;
import io.chatcamp.sdk.User;

import com.stfalcon.chatkit.messages.MessagesList;
import com.chatcamp.plugin.ConversationActivity;
import io.chatcamp.sdk.GroupChannelListQuery;


public class ChatCampPlugin extends CordovaPlugin {

    private static final String TAG = "ChatcampPlugin";

    private String userId;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("initChatcamp")) {
            this.initChatcamp(callbackContext, args);
            return true;
        } else if (action.equals("openConversation")) {
            this.openConversationActivity(args);
        }
        return false;
    }

    private void initChatcamp(CallbackContext callbackContext, JSONArray args) {

        try {
            Log.d(TAG, args.toString());
            JSONObject input = args.getJSONObject(0);
            userId = input.get("userId").toString();
            String appId = input.get("appId").toString();

            Context context = cordova.getActivity().getApplicationContext();
            ChatCamp.init(context, appId);
            ChatCamp.connect(userId, new ChatCamp.ConnectListener() {
                @Override
                public void onConnected(User user, ChatCampException e) {
                    if (e == null) {
                        callbackContext.success("connected to the chatcamp successful");
                    } else {
                        callbackContext.error("could not connect to chatcamp");
                    }
                }
            });
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    private void openConversationActivity(JSONArray args) {
        try {
            Log.d(TAG, args.toString());
            Context context = cordova.getActivity().getApplicationContext();
            JSONObject input = args.getJSONObject(0);
            String userIds = input.get("userIds").toString();
            String channelName = input.get("channelName").toString();
            boolean isDistinct = input.get("isDistinct").toString().equals("true");
            String[] participants = userIds.split(",");
            GroupChannel.create(channelName, participants, isDistinct, new GroupChannel.CreateListener() {
                @Override
                public void onResult(GroupChannel groupChannel, ChatCampException e) {
                    Log.d(TAG, "channel created with name" + groupChannel.getName());
                    Intent intent = new Intent(context, ConversationActivity.class);
                    intent.putExtra("channelType", "group");
                    intent.putExtra("participantState", GroupChannelListQuery.ParticipantState.ALL.name());
                    intent.putExtra("channelId", groupChannel.getId());
                    intent.putExtra("userId", userId);
                    ChatCampPlugin.this.cordova.getActivity().startActivity(intent);
                }
            });
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }
}
