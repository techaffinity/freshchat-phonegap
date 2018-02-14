//
//  HotlinePlugin.h
//  HotlineSDK
//
//  copyright (c) 2016 Freshdesk. All rights reserved.
//

#import <Cordova/CDV.h>
#import "FreshchatSDK/FreshchatSDK.h"

@interface FreshchatPlugin:CDVPlugin
    
-(void)init:(CDVInvokedUrlCommand*)command;
-(void)clearUserData:(CDVInvokedUrlCommand*)command;
-(void)unreadCount:(CDVInvokedUrlCommand*)command;
-(void)updatePushNotificationToken:(CDVInvokedUrlCommand*)command;
-(void)updateUser:(CDVInvokedUrlCommand*)command;
-(void)updateUserProperties:(CDVInvokedUrlCommand*)command;
-(void)getVersionName:(CDVInvokedUrlCommand*)command;
-(void)showConversations:(CDVInvokedUrlCommand*)command;
-(void)showFAQs:(CDVInvokedUrlCommand*)command;
-(void)sendMessage:(CDVInvokedUrlCommand*)command;
-(void)setExternalID:(CDVInvokedUrlCommand*)command;
-(void)getRestoreID:(CDVInvokedUrlCommand*)command;

@end