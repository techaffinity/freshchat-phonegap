//
//  HotlinePlugin.java
//
//  Copyright (c) 2014 Freshdesk. All rights reserved.


package com.freshdesk.freshchat.android;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.ConnectionResult;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.os.Bundle;
import android.widget.Toast;

import com.freshchat.consumer.sdk.*;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import java.util.ArrayList;
import java.util.List;





public class freshchatPlugin extends CordovaPlugin {

    private boolean isInitialized = false;
    private FreshchatConfig freshchatConfig;
    private FaqOptions faqOptions;
    private ConversationOptions conversationOptions;
    private Context cordovaContext;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 3458;
    private static final String LOG_TAG = "Freshchat";
    private FreshchatUser freshchatUser;
    private FreshchatMessage freshchatMessage;
    private Map<String, String> userMeta;
    private Bundle bundle;

    private CallbackContext resCallbackContext;
    private CallbackContext unreadResCallbackContext;
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        cordovaContext = cordova.getActivity().getApplicationContext();
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
            public void onReceive(Context context, Intent intent) {
            PluginResult result;
            String restoreId = Freshchat.getInstance(cordovaContext).getUser().getRestoreId();
            String externalId = Freshchat.getInstance(cordovaContext).getUser().getExternalId();
            try{
                JSONObject parameter = new JSONObject();
                parameter.put("restoreId", restoreId);
                parameter.put("externalId", externalId);
                result = new PluginResult(PluginResult.Status.OK,parameter);
                result.setKeepCallback(false);
                if (resCallbackContext != null) {
                    resCallbackContext.sendPluginResult(result);
                    resCallbackContext = null;
                }
            }catch(Exception e){
                Log.e(LOG_TAG, e.toString());
            }

        }
    };
    BroadcastReceiver unreadCountChangeReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        Freshchat.getInstance(cordovaContext).getUnreadCountAsync(new UnreadCountCallback() {
            @Override
            public void onResult(FreshchatCallbackStatus freshchatCallbackStatus, int unreadCount) {
                PluginResult result = new PluginResult(PluginResult.Status.OK,Integer.toString(unreadCount));
                result.setKeepCallback(false);
                if (unreadResCallbackContext != null) {
                    unreadResCallbackContext.sendPluginResult(result);
                    unreadResCallbackContext = null;
                }
            }
        });
    }
};

    public Bundle jsonToBundle(JSONObject jsonObject) throws JSONException {
        Bundle bundle = new Bundle();
        if(jsonObject == null) {
            return bundle;
        }
        Iterator iterator = jsonObject.keys();
        while(iterator.hasNext()){
            String key = (String)iterator.next();
            String value = jsonObject.getString(key);
            bundle.putString(key,value);
        }
        return bundle;
     }
    
    @Override
    public boolean execute(String action, JSONArray args,final CallbackContext callbackContext) throws JSONException {

        try {
                if(action.equals("init")) {
                    if(args.length() == 0) {
                        Log.e(LOG_TAG,"Please provide parameters for initializing Freshchat");
                        return false;
                    }
                    JSONObject initArgs = new JSONObject(args.getString(0));
                    Log.d(LOG_TAG,"inside init call");
                    String appId = initArgs.getString("appId");
                    String appKey = initArgs.getString("appKey");

                    freshchatConfig = new FreshchatConfig(appId,appKey);

                    if(initArgs.has("domain")) {
                        freshchatConfig.setDomain(initArgs.getString("domain"));
                    }
                    if(initArgs.has("cameraCaptureEnabled")) {
                        freshchatConfig.setCameraCaptureEnabled(initArgs.getBoolean("cameraCaptureEnabled"));
                    }
                    if(initArgs.has("gallerySelectionEnabled")){

                        freshchatConfig.setGallerySelectionEnabled(initArgs.getBoolean("gallerySelectionEnabled"));
                    }
                     if(initArgs.has("teamMemberInfoVisible")){

                        freshchatConfig.setTeamMemberInfoVisible(initArgs.getBoolean("teamMemberInfoVisible"));
                    }
                    Freshchat.getInstance(cordovaContext).init(freshchatConfig);
                    callbackContext.success();
                    this.isInitialized = true;
                    return true;
                }

                if(action.equals("showFAQs")) {
                    Log.d(LOG_TAG,"Show FAQs has been called");
                    if(args.length() == 0) {
                        Freshchat.showFAQs(cordovaContext);
                        return true;
                    }
                    JSONObject faqArgs = new JSONObject(args.getString(0));
                    faqOptions = new FaqOptions();
                    if(faqArgs.has("showFaqCategoriesAsGrid")) {
                        faqOptions.showFaqCategoriesAsGrid(faqArgs.getBoolean("showFaqCategoriesAsGrid"));
                    }
                    if(faqArgs.has("showContactUsOnAppBar")) {
                        faqOptions.showContactUsOnAppBar(faqArgs.getBoolean("showContactUsOnAppBar"));
                    }
                    if(faqArgs.has("showContactUsOnFaqScreens")) {
                        faqOptions.showContactUsOnFaqScreens(faqArgs.getBoolean("showContactUsOnFaqScreens"));
                    }
                    if(faqArgs.has("showContactUsOnFaqNotHelpful")) {
                        faqOptions.showContactUsOnFaqNotHelpful(faqArgs.getBoolean("showContactUsOnFaqNotHelpful"));
                    }

                    List<String> tagsList = new ArrayList<String>();
                    if(faqArgs.optJSONArray("tags") != null) {
                        JSONArray tags = faqArgs.getJSONArray("tags");
                        for (int i = 0; i < tags.length(); i++) {
                            tagsList.add(tags.getString(i));
                        }
                        String title = faqArgs.getString("filteredViewTitle");
                        if(faqArgs.getString("articleType").equals("category")){
                            faqOptions.filterByTags(tagsList, title, FaqOptions.FilterType.CATEGORY);
                        } else {
                                faqOptions.filterByTags(tagsList, title, FaqOptions.FilterType.ARTICLE);
                        }
                        List<String> contactusTagsList = new ArrayList<String>();
                        if(faqArgs.optJSONArray("contactusTags") != null) {
                            JSONArray contactusTags = faqArgs.getJSONArray("contactusTags");
                            for (int i = 0; i < contactusTags.length(); i++) {
                                contactusTagsList.add(contactusTags.getString(i));
                            }
                            title = faqArgs.getString("contactusFilterTitle");
                            faqOptions.filterContactUsByTags(contactusTagsList, title);
                        }
                        Freshchat.showFAQs(cordovaContext, faqOptions);
                    } else {
                        Freshchat.showFAQs(cordovaContext);
                    }
                    callbackContext.success();
                    return true;
                }

                if(action.equals("showConversations")) {
                    Log.d(LOG_TAG,"show Conversations has been called");
                    if(args.length() == 0) {
                        Freshchat.showConversations(cordovaContext);
                        return true;
                    }
                    JSONObject conversationArgs = new JSONObject(args.getString(0));
                    conversationOptions = new ConversationOptions();
                    List<String> tagsList = new ArrayList<String>();
                    if(conversationArgs.optJSONArray("tags") != null) {
                        JSONArray tags = conversationArgs.getJSONArray("tags");
                        for (int i = 0; i < tags.length(); i++) {
                            tagsList.add(tags.getString(i));
                        }
                        String title = conversationArgs.getString("filteredViewTitle");
                        conversationOptions.filterByTags(tagsList, title);
                    }
                    Freshchat.showConversations(cordovaContext, conversationOptions);
                    callbackContext.success();
                    return true;
                }

                if(action.equals("clearUserData")) {
                    Log.d(LOG_TAG,"inside clearUserData");
                     Freshchat.resetUser(cordovaContext);
                     callbackContext.success();
                    return true;
                }

                if(action.equals("updateUser")) {
                    if(args.length() == 0) {
                        Log.e(LOG_TAG,"Please provide parameters to update a user");
                        return false;
                    }
                    JSONObject jsonArgs = new JSONObject(args.getString(0));

                    freshchatUser=Freshchat.getInstance(cordovaContext).getUser();

                    if(jsonArgs.has("firstName")) {
                        freshchatUser.setFirstName(jsonArgs.getString("firstName"));
                    }
                    if(jsonArgs.has("lastName")) {
                        freshchatUser.setLastName(jsonArgs.getString("lastName"));
                    }
                    if(jsonArgs.has("email")) {
                        freshchatUser.setEmail(jsonArgs.getString("email"));
                    }
                    if(jsonArgs.has("countryCode") && jsonArgs.has("phoneNumber")) {
                        freshchatUser.setPhone(jsonArgs.getString("countryCode"),jsonArgs.getString("phoneNumber"));
                    }
                    Freshchat.getInstance(cordovaContext).setUser(freshchatUser);
                    callbackContext.success();
                    return true;
                }

                if(action.equals("updateUserProperties")) {

                        if(args.length() == 0) {
                            Log.e(LOG_TAG,"Please provide user properties to update the user");
                            return false;
                        }

                        JSONObject metadata = new JSONObject(args.getString(0));
                        userMeta = new HashMap<String, String>();
                        Iterator<String> keys  = metadata.keys();

                        while(keys.hasNext()) {
                                String key = keys.next();
                                userMeta.put(key, metadata.getString(key));
                        }
                        Freshchat.getInstance(cordovaContext).setUserProperties(userMeta);
                        callbackContext.success();
                        return true;
                }

                if(action.equals("unreadCount")) {
                    Log.d(LOG_TAG," unreadCount has been called");
                    if(args.length() == 0) {
                    Freshchat.getInstance(cordovaContext).getUnreadCountAsync(new UnreadCountCallback() {
                        @Override
                        public void onResult(FreshchatCallbackStatus freshchatCallbackStatus, int count)
                        {
                            if (freshchatCallbackStatus == FreshchatCallbackStatus.STATUS_SUCCESS) {
                                Log.i(LOG_TAG,"unreadcount is :"+count);
                                callbackContext.success(count);
                            }
                            else{
                                callbackContext.error(freshchatCallbackStatus.toString());
                            }
                                
                        }
                    });
                    return true;
                    }
                    JSONObject unreadCountArgs = new JSONObject(args.getString(0));
                    List<String> tagsList = new ArrayList<String>();
                    if(unreadCountArgs.has("tag")) {
                        tagsList.add(unreadCountArgs.getString("tag"));
                       Freshchat.getInstance(cordovaContext).getUnreadCountAsync(new UnreadCountCallback() {
                        @Override
                        public void onResult(FreshchatCallbackStatus freshchatCallbackStatus, int count)
                        {
                            if (freshchatCallbackStatus == FreshchatCallbackStatus.STATUS_SUCCESS) {
                                Log.i(LOG_TAG,"unreadcount is :"+count);
                                callbackContext.success(count);
                            }
                            else{
                                callbackContext.error(freshchatCallbackStatus.toString());
                            }
                                
                            }
                    },tagsList);
                    }
                    return true;
                  
                }
                if(action.equals("unreadCountlistenerRegister")){
                    IntentFilter intentFilter = new IntentFilter(Freshchat.FRESHCHAT_UNREAD_MESSAGE_COUNT_CHANGED);
                    LocalBroadcastManager.getInstance(cordovaContext).registerReceiver(unreadCountChangeReceiver, intentFilter);
                    PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
                    result.setKeepCallback(true);
                    unreadResCallbackContext = callbackContext;
                    callbackContext.sendPluginResult(result);
                    return true;
                }
                 if(action.equals("unreadCountlistenerUnregister")){
                    if(unreadResCallbackContext != null){
                        LocalBroadcastManager.getInstance(cordovaContext).unregisterReceiver(unreadCountChangeReceiver);
                        callbackContext.success("success");
                    }
                    
                    return true;
                }

                if(action.equals("getVersionName")) {
                    Log.d(LOG_TAG,"version number called");
                    int versionNumber = Freshchat.getInstance(cordovaContext).getSDKVersionCode();
                    callbackContext.success(versionNumber);
                    return true;
                }

                if(action.equals("_isFreshchatPushNotification")) {
                    Log.d(LOG_TAG,"check if a particular push notificaiton is a freshchat push notification or not");
                    if(args.length() == 0) {
                        Log.e(LOG_TAG,"Please provide the notification payload to be verified ");
                        return false;
                    }
                    JSONObject jsonArgs = new JSONObject(args.getString(0));
                    Bundle bundle = jsonToBundle(jsonArgs);
                    if(Freshchat.getInstance(cordovaContext).isFreshchatNotification(bundle)) {
                        callbackContext.success(1);
                    }
                    else {
                        callbackContext.success(0);
                    }
                    return true;
                }

                if(action.equals("handlePushNotification")) {
                    Log.d(LOG_TAG,"Handling Push Notification!");
                    if(args.length() == 0) {
                        Log.e(LOG_TAG,"Please provide parameters for initializing FreshChat");
                        return false;
                    }
                    JSONObject jsonArgs = new JSONObject(args.getString(0));
                    bundle = jsonToBundle(jsonArgs);
                    cordova.getThreadPool().execute( new Runnable() {
                       public void run() {
                            Freshchat.getInstance(cordovaContext).handleFcmMessage(cordovaContext,bundle);
                            callbackContext.success();
                       }
                    });

                    return true;
                }
                if(action.equals("getRestoreID")) {
                    Log.d(LOG_TAG,"getRestoreID called");
                    freshchatUser= Freshchat.getInstance(cordovaContext).getUser();
                    String restoreID = freshchatUser.getRestoreId();
                    Log.d(LOG_TAG,restoreID);
                    callbackContext.success(restoreID);
                    return true;
                }
                if(action.equals("registerRestoreIdNotification")){
                     Log.d(LOG_TAG,"listenRestoreId called");
                    IntentFilter intentFilter = new IntentFilter(Freshchat.FRESHCHAT_USER_RESTORE_ID_GENERATED);
                    LocalBroadcastManager.getInstance(cordovaContext).registerReceiver(broadcastReceiver, intentFilter);
                    PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
                    result.setKeepCallback(true);
                    resCallbackContext = callbackContext;
                    callbackContext.sendPluginResult(result);
                    return true;
                }
                if(action.equals("unregisterRestoreIdNotification")){
                    if(resCallbackContext != null){
                        LocalBroadcastManager.getInstance(cordovaContext).unregisterReceiver(broadcastReceiver);
                    }
                    
                    return true;
                }
                if (action.equals("identifyUser")) {
                    if (args.length() == 0) {
                        String errorMsg = "Please provide parameters to identifyUser";
                        Log.e(LOG_TAG, errorMsg);
                        callbackContext.error("identifyUser failed - " + errorMsg);
                        return false;
                    }
                    try {
                        JSONObject jsonArgs = new JSONObject(args.getString(0));
                        String externalId = null;
                        String restoreId = null;

                        if (jsonArgs.has("externalId")) {
                            externalId = jsonArgs.getString("externalId");
                        }

                        if (jsonArgs.has("restoreId")) {
                            restoreId = jsonArgs.getString("restoreId");
                        }

                        Freshchat.getInstance(cordovaContext).identifyUser(externalId, restoreId);
                        callbackContext.success("identifyUser successfully");
                        return true;
                    } catch (Exception e) {
                        callbackContext.error("identifyUser failed - " + e.toString());
                        return false;
                    }
                }

                if(action.equals("sendMessage")) {
                        if(args.length() == 0) {
                        Log.e(LOG_TAG,"Please provide parameters to send Message");
                        return false;
                    }
                    JSONObject jsonArgs = new JSONObject(args.getString(0));
                    freshchatMessage = new FreshchatMessage();
                    if(jsonArgs.has("tag")&&  jsonArgs.has("message"))  {
                        freshchatMessage.setTag(jsonArgs.getString("tag")).setMessage(jsonArgs.getString("message"));
                        Freshchat.getInstance(cordovaContext).sendMessage(cordovaContext, freshchatMessage);
                        callbackContext.success("message sent.");
                    }else{
                        callbackContext.error("Failed to send");
                    }

                    return true;
                }

                if(action.equals("updatePushNotificationToken")) {
                    if(args.length() == 0) {
                        Log.e(LOG_TAG,"Please provide the token to register");
                        return false;
                    }
                    String token = args.getString(0);
                    Log.i(LOG_TAG,"update GCM registration has been called");
                    Freshchat.getInstance(cordovaContext).setPushRegistrationToken(token);
                    return true;
                }

                if(action.equals("updateAndroidNotificationProperties")) {
                    Log.d(LOG_TAG,"setting notification properties!");
                    if(args.length() == 0) {
                        Log.e(LOG_TAG,"Please provide parameters to update a user");
                        return false;
                    }
                    JSONObject jsonArgs = new JSONObject(args.getString(0));
                    FreshchatNotificationConfig notificationConfig = new FreshchatNotificationConfig();
                    
                    if(jsonArgs.has("notificationSoundEnabled")) {
                        notificationConfig.setNotificationSoundEnabled(jsonArgs.getBoolean("notificationSoundEnabled"));
                    }
                    if(jsonArgs.has("smallIcon")) {
                        String iconName = jsonArgs.getString("smallIcon");
                        int iconId = cordovaContext.getResources().getIdentifier(iconName, "drawable", cordovaContext.getPackageName());
                        notificationConfig.setSmallIcon(iconId);
                    }
                    if(jsonArgs.has("largeIcon")) {
                        String iconName = jsonArgs.getString("largeIcon");
                        int iconId = cordovaContext.getResources().getIdentifier(iconName, "drawable", cordovaContext.getPackageName());
                        notificationConfig.setLargeIcon(iconId);
                    }
                    if(jsonArgs.has("launchActivityOnFinish")) {
                        notificationConfig.launchActivityOnFinish(jsonArgs.getString("launchActivityOnFinish"));
                    }
                    if(jsonArgs.has("notificationPriority")) {
                        notificationConfig.setPriority(jsonArgs.getInt("notificationPriority"));
                    }
                    Freshchat.getInstance(cordovaContext).setNotificationConfig(notificationConfig);
                    return true;
                }
                Log.d(LOG_TAG,"action does not have a function to match it:"+action);

            } catch (Exception e) {
                Log.e(LOG_TAG,"exception while perfroming action:"+action,e);
                callbackContext.error("exception while performing action"+action);
            }
        return true;
    }



}
