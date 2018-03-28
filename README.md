# ChatCamp-Cordova-Plugin
ChatCamp Cordova Plugin

Realtime Chat SDK and Messaging API for Apps & Websites
Integrate in-app messaging in your mobile apps & websites using ChatCamp platform

Signup at https://dashboard.chatcamp.io/register to get the application key.

## Note

This plugin is a wrapper around native code.

## Install the plugin

```$ cordova plugin add https://github.com/ChatCamp/ChatCamp-Cordova-Plugin.git```
    
## Android
Open /platforms/android/ folder in Android Studio.
This plugin uses Chatkit for UI components so you need to add **android.library.reference.3=chatkit** in project.properties in platform/android folder. This is how your project.properties file should look like.
```
    target=android-26
    android.library.reference.1=CordovaLib
    android.library.reference.2=app
    android.library.reference.3=chatkit // This the line that needs to be added
    cordova.gradle.include.1=com.chatcamp.plugin/chatcampapp-chatcamp.gradle
```
### Steps to integrate:
#### Login User
This function registes the user with the ChatCamp SDK,    
**user id** should the id of the current user like "1", "2" etc    
**application id** is the id acquired from Chatcamp 
```
   var input = {userId : "user id", appId: "application id" }
   cordova.plugins.ChatCampPlugin.init(input, initSuccess, initFailure);
   
   var initSuccess = function(message) {
       alert(message);
   }

   var initFailure = function(chatcampException) {
       alert(chatcampException);
   }
```
#### Launch Chat Screen
This function will create a new group if the group does not exists or open the already created group if *isDistinct* is set true. If *isDistinct* is set false then it will always create a new Group.    
**user id** is the list of user ids like "1,2,3"    
**channel name** is the name of the channel
```
    var input = {userIds : "user ids", channelName : "channel name", isDistinct: "true" }
    cordova.plugins.ChatCampPlugin.createGroup(input, createGroupSuccess, createGroupFailure);
    
    var createGroupSuccess = function(message) {
        alert(message);
    }

    var createGroupFailure = function(chatcampException) {
        alert(chatcampException);
    }
```

