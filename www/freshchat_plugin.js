//
//  freshchat_plugin.js
//
//  Copytright (c) 2016 Freshdesk. All rights reserved.


// ------------------------------------------------------------------------------------
// --------------------------------UTILITY FUNCTIONS-----------------------------------
// ------------------------------------------------------------------------------------

//Function accepts a function Name as parameter and returns a closure which calls the native function of that name
//The frst argument is ALWAYS the class Name.

var createWrapperForNativeFunction = function(functionName) {
    return function() {
        var argumentsArray = Array.prototype.slice.call(arguments || []);
        var success, failure;

        //if user has provded callback
        //  remove callback from arguments and assign it to userCallback
        //else
        // have a dummy callback
        // Set the callback function to be called on success and failure
        var userCallback;
        var size = argumentsArray.length - 1;

        if (size != -1 && typeof argumentsArray[size] == 'function')
            userCallback = argumentsArray.splice(size, 1)[0]; //remove the last param and store it in userCallback
        else
            userCallback = function() {}
        success = function(e) { userCallback(true, e); }
        failure = function(e) { userCallback(false, e); }

        //Call corresponding native function
        return cordova.exec(success, failure, "freshchatPlugin", functionName, argumentsArray);

    }
}

var Freshchat = {}

Freshchat.isFreshchatPushNotification = function(args, cb) {

    Freshchat._isFreshchatPushNotification(args, function(success, isFreshchat) {
        cb(success, isFreshchat === 1);
    });
}

Freshchat.init = function(args, cb) {
    createWrapperForNativeFunction("init")(args, function(success) {
        if (success) {
            Freshchat.trackPhoneGapSDKVersion();
        }
        if (cb) {
            cb(success);
        }
    });
}

Freshchat.trackPhoneGapSDKVersion = function() {
    this.updateUserProperties({ Phonegap: "v1.1.0" });
}

Freshchat.clearUserData = function() {
    createWrapperForNativeFunction("clearUserData")(function(success) {
        if (success) {
            Freshchat.trackPhoneGapSDKVersion();
        }
    });
}

//Add Wrapper functions to Freshchat
var functionList = [
    "unreadCount",
    "unreadCountlistenerRegister",
    "unreadCountlistenerUnregister",
    "updateUser",
    "updateUserProperties",
    "showConversations",
    "showFAQs",
    "getVersionName",
    "_isFreshchatPushNotification",
    "handlePushNotification",
    "updatePushNotificationToken",
    "updateAndroidNotificationProperties",
    "sendMessage",
    "identifyUser",
    "getRestoreID",
    "registerRestoreIdNotification",
    "unregisterRestoreIdNotification"
];

functionList.forEach(function(funcName) {
    Freshchat[funcName] = createWrapperForNativeFunction(funcName);
});

Freshchat.NotificationPriority = {};
Freshchat.NotificationPriority.PRIORITY_DEFAULT = 0;
Freshchat.NotificationPriority.PRIORITY_HIGH = 1;
Freshchat.NotificationPriority.PRIORITY_LOW = -1;
Freshchat.NotificationPriority.PRIORITY_MAX = 2;
Freshchat.NotificationPriority.PRIORITY_MIN = -2;

Freshchat.FilterType = {};
Freshchat.FilterType.ARTICLE = "article";
Freshchat.FilterType.CATEGORY = "category";

module.exports = Freshchat;