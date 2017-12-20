#Freshchat PUSH NOTIFICATIONS SETUP GUIDE
This is a guide to setup push notifications with the Freshchat Phonegap plugin.

The plugin supports GCM for push notifications in Android and APNS for iOS.

###Server Side 

After you setup your Freshchat account, go to https://web.Freshchat.com/settings/apisdk in this page,
add the server key for Android and the push certificate for iOS.

###Plugin Side

We support push notifications through a forked version of the phonegap push plugin 
found [here](https://github.com/techaffinity/phonegap-plugin-push).

It can be installed by the following command : 
```shell
cordova plugin add https://github.com/techaffinity/phonegap-plugin-push.git
```
If you are setting it up for Android as well, add the Sender Id as well, so the command would look like:
```shell
cordova plugin add https://github.com/techaffinity/phonegap-plugin-push.git --variable SENDER_ID=20738924380
```
Plugin can also be added from config.xml as follows:
```javascript
<plugin name="phonegap-plugin-push" spec="https://github.com/techaffinity/phonegap-plugin-push.git">
        <param name="SENDER_ID" value="XXXXXXXXXX" />
    </plugin>
```

This plugin needs to be initialized for Freshchat push notifiations to work.
Sample method to initialize the plugin, call this method in your onDeviceComplete

```javascript
function initializePush() {
    var push = PushNotification.init({
        "android":{
            "senderID":"XXXXXXXXXX"
        },
        "ios": {
            "alert": "true",
            "badge": "true",
            "sound": "true"
        },
        "windows": {}
    });
}

For ionic 2 & 3 

function initializePush() {
    var push = (window as any).PushNotification.init({
        "android":{
            "senderID":"XXXXXXXXXX"
        },
        "ios": {
            "alert": "true",
            "badge": "true",
            "sound": "true"
        },
        "windows": {}
    });
}

```

Android notification properties can be changed with the updateAndroidNotificationProperties API. The properties that can be updated are.

-  "notificationSoundEnabled" : Notifiction sound enabled or not.
-  "smallIcon" : Setting a small notification icon (move the image to drawbles folder and pass the name of the jpeg file as parameter).
-  "largeIcon" : setting a large notification icon.
-  "notificationPriority" : set the priority of notification through Freshchat.
-  "launchActivityOnFinish" : Activity to launch on up navigation from the messages screen launched from notification. The messages screen will have no activity to navigate up to in the backstack when its launched from notification. Specify the activity class name to be launched.


The API is called like:
    
```javascript
window.Freshchat.updateAndroidNotificationProperties({
                "smallIcon" : "image",
                "largeIcon" : "image",
                "notificationPriority" : window.Freshchat.NotificationPriority.PRIORITY_MAX,
                "notificationSoundEnabled" : false,
                "launchActivityOnFinish" : "MainActivity.class.getName()"
            });
```
List of Freshchat Priorities:

-  Freshchat.NotificationPriority.PRIORITY_DEFAULT
-  Freshchat.NotificationPriority.PRIORITY_HIGH
-  Freshchat.NotificationPriority.PRIORITY_LOW
-  Freshchat.NotificationPriority.PRIORITY_MAX
-  Freshchat.NotificationPriority.PRIORITY_MIN


Additional APIS to handle push with a different plugin: 
When you receive a deviceToken from GCM or APNS , you need to update the deviceToken in hotline as follows

```javascript
    push.on('registration',function(data) {
        window.Freshchat.updatePushNotificationToken(data.registrationId);
     });
```

Whenever a push notification is received. You will need to check if the notification originated from Hotline 
and have Hotline SDK handle it.

```javascript
push.on('notification',function(data) {
  window.Freshchat.isFreshchatPushNotification(data, function(success, isFreshchatNotif) {
    if( success && isFreshchatNotif ) {
      window.Freshchat.handlePushNotification(data);
    }
 });
});
```