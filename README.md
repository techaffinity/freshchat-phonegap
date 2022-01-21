# Freshchat Phonegap plugin
(https://twitter.com/freshchatapp)

This plugin integrates Freshchat's SDK into a Phonegap/Cordova project.

You can reach us anytime at support@freshchat.com if you run into trouble.

AppId and AppKey
You'll need these keys while integrating Freshchat SDK with your app. you can get the same from the [Settings -> API&SDK](https://web.Freshchat.com/settings/apisdk) page. Do not share them with anyone.
If you do not have an account, you can get started for free at [Freshchat.com](https://www.freshworks.com/live-chat-software/) 

<!-- [Where to find AppId and AppKey](https://Freshchat.freshdesk.com/solution/articles/9000041894-where-to-find-app-id-and-app-key-) -->

For platform-specific details please refer to the [Documentation](https://support.freshchat.com/support/solutions)

Supported platforms :
* Android
* iOS

**Note: This is an early version and so expect changes to the API**

### Cordova Integration :

1. Add required platforms to your PhoneGap project
```shell
ionic cordova platform add android
ionic cordova platform add ios
```

2. Add the Freshchat plugin to your project.

You can add the plugin from the command line like:

```shell
ionic cordova plugin add https://github.com/techaffinity/freshchat-phonegap.git
```

3. To prevent build failures caused by including different versions of the support libraries in Android gradle. Add the below plugin

  `` ionic cordova plugin add https://github.com/dpa99c/cordova-android-support-gradle-release ``

To resolve these version collisions, this plugin injects a Gradle configuration file into the native Android platform project, which overrides any versions specified by other plugins, and forces them to the version specified in its Gradle file.

4. Incase user face the  androidx.annotation.RequiresApi import issue than add this plugin (Android only)
  
 Add plugin to enable AndroidX in the project 

  `` ionic cordova plugin add cordova-plugin-androidx ``

 Add plugin to patch existing plugin source that uses the Android Support Library to use AndroidX
  
 `` ionic cordova plugin add cordova-plugin-androidx-adapter ``

## Capacitor Integration
 

1. Install the Freshchat plugin to your project

   `` npm install https://github.com/techaffinity/freshchat-phonegap.git ``
   
2. Add required platforms to your ionic project

```shell
npx cap add android
npx cap add ios

```
 To open the project in native platform IDE by  `` npx cap open android/ios ``


3. i) To prevent build failures caused by Manifest merger follow this steps in  (Android studio)

   Fix Missing File Provider Error please follow this [video](https://freshworks.wistia.com/medias/qrhrj1vzp1) steps
   
 -  Remove the FileProvider tag called `provider`  from "/android/capacitor-cordova-android-plugins/src/main/AndroidManifest.xml
   
 - In app Manifest.xml (android/app/src/main/AndroidManifest.xml) just replace the Provider code from video and "android:authorities" it should be your app id Ex:(xxx.xxxx.xxx.provider) 
   
 - add the String value in string.xml (android/app/src/main/res/values)
    
       `` <string name="freshchat_file_provider_authority">xxx.xxxx.xxx.provider</string> ``
       
   ii) To prevent build failures caused by '.h file not found ' follow these steps in (Xcode)

 - Add the `` pod 'FreshchatSDK' `` in the pod file then open the terminal in the ios/app folder and install the pod ``pod install``  reopen the Xcode 
 
 - once install the Freshchat SDK then we have to move the plugin files to the main app folder
 
   we can find the "Freshchat" folder in pods/Development_pods/Cordovaplugins/Freshchat and move the three files to the app/app folder in Xcode  
   
 - Add the App-Bridging-header.h file path to  build setting -> swift compiler General -> Objective-c Bridging Header in Xcode


### Initializing the plugin

_Freshchat.init_  needs to be called from _ondeviceready_  event listener to make sure the SDK is initialized before use.

```javascript
document.addEventListener("deviceready", function(){
  // Initialize Freshchat with your AppId & AppKey from your portal https://web.Freshchat.com/settings/apisdk 
  window.Freshchat.init({
    appId       : "<Your App Id>",
    appKey      : "<Your App Key>"
  });
});
```


For Ionic 2 & 3 :

Access Freshchat variable in the development as below 

((window as any).Freshchat) likewise 

  // Initialize Freshchat with your AppId & AppKey from your portal https://web.Freshchat.com/settings/apisdk 
  (window as any).Freshchat.init({
    appId       : "<Your App Id>",
    appKey      : "<Your App Key>"
  });
});




 The following optional boolean parameters can be passed to the init Object
 -  cameraCaptureEnabled
 -  gallerySelectionEnabled 
 -  teamMemberInfoVisible 
 -  notificationSoundEnabled _(ios only)_
 -  showNotificationBanner _(ios only)_

 Here is a sample init code with the optional parameters

 ```javascript
 window.Freshchat.init({
    appId       : "<Your App Id>",
    appKey      : "<Your App Key>",
    gallerySelectionEnabled   : true,
    cameraCaptureEnabled      : true,
    teamMemberInfoVisible     : true
});
```
 The init function is also a callback function and can be implemented like so:

 ```javascript
 window.Freshchat.init({
      appId       : "<Your App Id>",
      appKey      : "<Your App Key>",
      gallerySelectionEnabled      : true,
      cameraCaptureEnabled    : true,
      teamMemberInfoVisible   : true,
  }, function(success){
      console.log("This is called form the init callback");
  });
 ```

 Once initialized you can call Freshchat APIs using the window.Freshchat object.
 In ionic (window as any).Freshchat

```javascript
//After initializing Freshchat
showSupportChat = function() {
  window.Freshchat.showConversations();
};
document.getElementById("launch_conversations").onclick = showSupportChat;


//in index.html
//<button id="launch_conversations"> Inbox </button>
```

### Freshchat APIs
* Freshchat.showFAQs()
    - Launch FAQ / Self Help
    
    The following FAQOptions can be passed to the showFAQs() API
    
        -showFaqCategoriesAsGrid
        -showContactUsOnAppBar
        -showContactUsOnFaqScreens
        -showContactUsOnFaqNotHelpful
        
    Here is a sample call to showFAQs() with the additional parameters:
    ```javascript
    window.Freshchat.showFAQs( {
        showFaqCategoriesAsGrid     :true,
        showContactUsOnAppBar       :true,
        showContactUsOnFaqScreens   :true,
        showContactUsOnFaqNotHelpful:false
    });
    ```
    #### Filtering and displaying a subset of FAQ Categories and/or FAQs
    Eg. To filter and display a set of FAQs tagged with *sample* and *video*
    ```javascript
    window.Freshchat.showFAQs( {
        tags : ["sample","video"],
        filteredViewTitle   : "Tags",
        articleType   : Freshchat.FilterType.ARTICLE
    });
    ```
    Not specifying an article type, by default filters by Article.
    
    Filtering FAQ categories are available.
    Eg. To filter and display a set of categories tagged with *sample* and *video* ,
    ```javascript
    window.Freshchat.showFAQs( {
        tags : ["sample","video"],
        filteredViewTitle : "Tags"
        articleType : Freshchat.FilterType.CATEGORY
    });
    ```

* Freshchat.showConversations()
    - Launch Channels / Conversations.
  
  v1.1 also adds support to filter conversations with tags. This filters the list of channels shown to the user.
  Example showing how to filter converstions using tags.
```javascript
    window.Freshchat.showConversations( {
        tags :["new","test"],
        filteredViewTitle   : "Tags"
    });
```
NOTE:- Filtering conversations is also supported inside FAQs, i.e show conversation button from the category list
or the article list view can also be filtered. Here is a sample.
```javascript
    window.Freshchat.showFAQs( {
        tags :["sample","video"],
        filteredViewTitle   : "Tags",
        articleType   : Freshchat.FilterType.CATEGORY,
        contactusTags : ["test"], 
        contactusFilterTitle: "contactusTags"
    });
```

In the above example clicking on show conversations in the filtered category list view takes you to a conversation
view filtered by the tag "test".

* Freshchat.unreadCount(callback)
    - Fetch count of unread messages from agents.
    
* Freshchat.updateUser(userInfo)
    - Update user info. Accepts a JSON with the following format  
```javascript
{
   "firstName" : "John",
   "lastName" : "Doe"
   "email" : "johndoe@dead.man",
   "countryCode" : "+91",
   "phoneNumber" : "1234234123"
}
```
* Freshchat.updateUserProperties(userPropertiesJson)
    - Update custom user properties using a Json containing key, value pairs. A sample json follows
```javascript
{
   "user_type" : "Paid",
   "plan" : "Gold"
}
```
* Freshchat.clearUserData()
    - Clear user data when users logs off your app.

You can pass in an optional callback function to an API as the last parameter, which gets called when the native API is completed. 
Eg.
```javascript
window.Freshchat.unreadCount(function(success,val) {
    //success indicates whether the API call was successful
    //val contains the no of unread messages
});
```
* Send Message Programmatically

Eg.
```javascript
    window.Freshchat.sendMessage(
	    {tag:”premium”,message:”Helo..”},function(success){
        //success indicates whether the API call was successful
	});

```

#### UnRead Count  

If you would like to obtain the number of unread messages for the user at app launch or any other specific event, 
use this function.
Eg.
```javascript
window.Freshchat.unreadCount(function(success,val) {
    //success indicates whether the API call was successful
    //val contains the no of unread messages
});
```
The plugin can also choose to listen to changes to unread count when the app is open. The way to listen to the broadcast is described below.
To register : 
Eg.
```javascript
window.Freshchat.unreadCountlistenerRegister(function(success,val) {
    //success indicates whether the API call was successful
    //val contains the no of unread messages
});
```
To unregister : 
Eg.
```javascript
window.Freshchat.unreadCountlistenerUnregister(function(success,val) {
    //success indicates whether the API call was successful
    //val contains the message for the success.
});
```


#### Restore user 

* Restore user and chat messages across device/platforms/sessions

For retaining the chat messages across devices/sessions/platforms, the mobile app needs to pass the same external id and restore the id combination for the user. This will allow users to seamlessly pick up the conversation from any of the supported platforms - Android, iOS, and Web.



External Id - This should (ideally) be a unique identifier for the user from your systems like a user id or email id etc and is set using Freshchat.identifyUser() API. This cannot be changed once set for the user



Restore Id - This is generated by Freshchat for the current user, given an external id was set and can be retrieved anytime using the Freshchat.getUser().getRestoreId() API. The app is responsible for storing and later presenting the combination of external id and restore id to the Freshchat SDK to continue the chat conversations across sessions on the same device or across devices and platforms.



Note 1: Restore Id for a user is typically generated only when the user has sent a message.


Note 2: Notifications are supported in only one mobile device at any point in time and are currently the last restored device or device with the last updated push token



To set external id : 
Eg.
```javascript

    window.Freshchat.identifyUser({externalId:"123456",restoreId:null},function(success){
       //success indicates whether the API call was successful
    });

```
To retrieve the restore id:

Eg.
```javascript
Freshchat.getRestoreID(function(success,restoreId){
     //success indicates whether the API call was successful
     //restoreId contains the restoreId generated for user.
      console.log("value restore Id : "+ restoreId);
    });
``` 

To restore user :

Eg.
```javascript
    window.Freshchat.identifyUser({externalId:"123456",restoreId:"ce85c2ba-cc2a-4ad2-8074-aa3b46cdc3c3"},function(success){
       //success indicates whether the API call was successful
    });

```
To lookup and restore user by external id and restore id: 

To Register 
Eg.
```javascript
   window.Freshchat.registerRestoreIdNotification(function(success,jsonOBJ){
       //success indicates whether the API call was successful
       //jsonOBJ contains the restoreId and externalId
      console.log("value  : "+ JSON.stringify(jsonOBJ));
  });

```
  To Unregister 

Eg.
```javascript
   window.Freshchat.unregisterRestoreIdNotification(function(success){
       //success indicates whether the API call was successful
  });

```

#### Push Notifications
##### 1. Recommended Option
To setup push notifications we recommend using our forked version of the phonegap-plugin-push available [here] (https://github.com/techaffinity/phonegap-plugin-push).

It can be installed by the following command : 
```shell
cordova plugin add https://github.com/techaffinity/phonegap-plugin-push.git
```
Or you can add it to your config.xml like:
```javascript
<plugin name="phonegap-plugin-push" spec="https://github.com/techaffinity/phonegap-plugin-push.git">
    <param name="SENDER_ID" value="XXXXXXXXXX" />
</plugin>
```

To Support Android FCM Push Notification and Cordova-android versions above 7.1.0, use the below plugin 
https://github.com/techaffinity/phonegap-plugin-push-1

Changes:
1. Removed Depreciated GCM and moved to FCM for Android

It can be installed by the following command : 
```shell
cordova plugin add https://github.com/techaffinity/phonegap-plugin-push-1.git
```
Or you can add it to your config.xml like:
```javascript
<plugin name="phonegap-plugin-push" spec="https://github.com/techaffinity/phonegap-plugin-push-1">
    <param name="SENDER_ID" value="XXXXXXXXXX" />
</plugin>
```

Initialize the push plugin and it will handle registering the tokens and displaying the notifications.
here is a sample init function, call this in your onDeviceReady
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
```
##### 2. Alternate Option for Push
Follow the steps below if your app handles push notifications using any other plugin.

##### 2a. Pass device token to Freshchat

When you receive a deviceToken from GCM or APNS , you need to send the deviceToken to Freshchat as follows
API name has changed for passing the token since version 1.2.0.

```javascript
// Example illustrates usage for phonegap-push-plugin
push.on('registration',function(data) {
  window.Freshchat.updatePushNotificationToken(data.registrationId);
});
```
##### 2b. Pass the notification to Freshchat SDK

Whenever the app receives a push notification, check and pass the notification to Freshchat SDK 

```javascript
// Example illustrates usage for phonegap-push-plugin
push.on('notification', function(data) {
  window.Freshchat.isFreshchatPushNotification(data, function(success, isFreshchatNotif) {
    if( success && isFreshchatNotif ) {
      window.Freshchat.handlePushNotification(data.additionalData);
    }
  });
});
```
##### Push Notification Customizations
Android notifications can be customized with the *updateAndroidNotificationProperties* API. Following is a list of properties that can be customized.

-  "notificationSoundEnabled" : Notification sound enabled or not.
-  "smallIcon": Setting a small notification icon (move the image to the drawable folder and pass the name of the jpeg file as a parameter).
-  "large icon": setting a large notification icon.
-  "notification priority": set the priority of notification through Freshchat.
-  "launchActivityOnFinish": Activity to launch on up navigation from the messages screen launched from notification. The messages screen will have no activity to navigate up to in the back stack when it's launched from notification. Specify the activity class name to be launched.


The API can be invoked as below:
    
```javascript
window.Freshchat.updateAndroidNotificationProperties({
                "smallIcon" : "image",
                "largeIcon" : "image",
                "notificationPriority" : window.Freshchat.NotificationPriority.PRIORITY_MAX,
                "notificationSoundEnabled" : false,
                "launchActivityOnFinish" : "MainActivity.class.getName()"
            });
```
Options for *notificationPriority* are as below:

-  Freshchat.NotificationPriority.PRIORITY_DEFAULT
-  Freshchat.NotificationPriority.PRIORITY_HIGH
-  Freshchat.NotificationPriority.PRIORITY_LOW
-  Freshchat.NotificationPriority.PRIORITY_MAX
-  Freshchat.NotificationPriority.PRIORITY_MIN

This follows the same priority order as Android's NotificaitonCompat class.

#### Restore User
```javascript
    window.Freshchat.identifyUser( {
        externalId: "USER_EXTERNAL_ID",
        restoreId: "USER_RESTORE_ID"
    });
```
Note: If the user does not have restoreId, call identifyUser() with just externalId.

#### Caveats

##### Android :
* Needs appcompat-v7 : 21+
* Needs support-v4 : 21+
* MinSdkVersion must be atleast 10 (in config.xml)

##### iOS
* Needs iOS 8 and above
