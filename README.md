# Freshchat Phonegap plugin
(https://twitter.com/freshchatapp)

This plugin integrates Freshchat's SDK into a Phonegap/Cordova project.

You can reach us anytime at support@freshchat.com if you run into trouble.

AppId and AppKey
You'll need these keys while integrating Freshchat SDK with your app. you can get the same from the [Settings -> API&SDK](https://web.Freshchat.com/settings/apisdk) page. Do not share them with anyone.
If you do not have an account, you can get started for free at [Freshchat.com](https://www.freshworks.com/live-chat-software/) 

<!-- [Where to find AppId and AppKey](https://Freshchat.freshdesk.com/solution/articles/9000041894-where-to-find-app-id-and-app-key-) -->

For platform specific details please refer to the [Documentation](https://support.freshchat.com/support/solutions)

Supported platforms :
* Android
* iOS

**Note : This is an early version and so expect changes to the API**

### Integrating the Plugin :

1. Add required platforms to your PhoneGap project
```shell
cordova platform add android
cordova platform add ios
```

2. Add the Freshchat plugin to your project.

You can add the plugin from command line like:
```shell
cordova plugin add https://github.com/techaffinity/freshchat-phonegap.git
```

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
    Not specifying an articleType, by default filters by Article.
    
    Filtering FAQ categores is available.
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

You can pass in an optional callback function to an API as the last parameter, which gets called when native API is completed. 
Eg.
```javascript
window.Freshchat.unreadCount(function(success,val) {
    //success indicates whether the API call was successful
    //val contains the no of unread messages
});
```

#### Push Notifications
##### 1. Recommended Option
To setup push notifications we recommend using our forked version of the phonegap-plugin-push available [here] (https://github.com/techaffinity/phonegap-plugin-push) .

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

-  "notificationSoundEnabled" : Notifiction sound enabled or not.
-  "smallIcon" : Setting a small notification icon (move the image to drawbles folder and pass the name of the jpeg file as parameter).
-  "largeIcon" : setting a large notification icon.
-  "notificationPriority" : set the priority of notification through Freshchat.
-  "launchActivityOnFinish" : Activity to launch on up navigation from the messages screen launched from notification. The messages screen will have no activity to navigate up to in the backstack when its launched from notification. Specify the activity class name to be launched.


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

This follow the same priority order as Android's NotificaitonCompat class.

#### Caveats

##### Android :
* Needs appcompat-v7 : 21+
* Needs support-v4 : 21+
* MinSdkVersion must be atleast 10 (in config.xml)

##### iOS
* Needs iOS 8 and above
