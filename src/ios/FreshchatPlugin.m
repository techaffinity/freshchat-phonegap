//
//  FreshchatPlugin.m
//  FreshchatSDK
//
//  Copyright(c) 2016 Freshdesk. All rights reserved.

#import<Cordova/CDV.h>


#import "FreshchatPlugin.h"
#import "FreshchatSDK/FreshchatSDK.h"

#define SYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(v)  ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] != NSOrderedAscending)

@implementation FreshchatPlugin:CDVPlugin


-(void) callbackToJavascriptWithResult:(CDVPluginResult*)result ForCommand:(CDVInvokedUrlCommand*)command {
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

-(void) callbackToJavascriptWithoutResultForCommand:(CDVInvokedUrlCommand*)command {
    CDVPluginResult* emptyResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self callbackToJavascriptWithResult:emptyResult ForCommand:command];
}

-(void) callbackToJavascriptWithoutResultFailureForCommand:(CDVInvokedUrlCommand*)command {
    CDVPluginResult* emptyResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    [self callbackToJavascriptWithResult:emptyResult ForCommand:command];
}

-(void) callbackToJavascriptWithException:(NSException*)e ForCommand:(CDVInvokedUrlCommand*)command {
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[e name]];
    [self callbackToJavascriptWithResult:result ForCommand:command];
}

-(void) init:(CDVInvokedUrlCommand*)command {
    NSArray* arguments = [command arguments];
    NSDictionary* initParams;
    if(arguments != nil && arguments.count > 0) {
        initParams = [arguments firstObject];
    } else {
        [self callbackToJavascriptWithoutResultForCommand:command];
    }
    NSString* domain = [initParams objectForKey:@"domain"];
    NSString* appId = [initParams objectForKey:@"appId"];
    NSString* appKey = [initParams objectForKey:@"appKey"];
    
    FreshchatConfig *config = [[FreshchatConfig alloc]initWithAppID:appId  andAppKey:appKey];
    NSLog(@"Inside init appId:%@ appKey:%@ domain:%@", appId, appKey, domain);
    
    if(domain) {
        NSLog(@"domain value: %@",domain);
        config.domain = domain;
    }
    
    if(initParams [@"cameraCaptureEnabled"]) {
        config.cameraCaptureEnabled = [[initParams objectForKey:@"cameraCaptureEnabled"] boolValue];
    }
     if(initParams [@"gallerySelectionEnabled"]) {
        config.gallerySelectionEnabled = [[initParams objectForKey:@"gallerySelectionEnabled"] boolValue];
    }
    
    // if(initParams [@"displayFAQsAsGrid"]) {
    //     config.displayFAQsAsGrid = [[initParams objectForKey:@"displayFAQsAsGrid"] boolValue];
    // }
    
    //  if(initParams [@"agentAvatarEnabled"]) {
    //     config.agentAvatarEnabled = [[initParams objectForKey:@"agentAvatarEnabled"] boolValue];
    // }
    
    // if(initParams [@"pictureMessagingEnabled"]) {
    //     config.pictureMessagingEnabled = [[initParams objectForKey:@"pictureMessagingEnabled"] boolValue];
    // }

    if(initParams [@"notificationSoundEnabled"]) {
        config.notificationSoundEnabled = [[initParams objectForKey:@"notificationSoundEnabled"] boolValue];
    }
    
    if(initParams [@"teamMemberInfoVisible"]) {
        config.teamMemberInfoVisible = [[initParams objectForKey:@"teamMemberInfoVisible"] boolValue];
    }

    if(initParams [@"showNotificationBanner"]) {
        config.showNotificationBanner = [[initParams objectForKey:@"showNotificationBanner"] boolValue];
    }
    [[Freshchat sharedInstance] initWithConfig:config];
    [self callbackToJavascriptWithoutResultForCommand:command];
}

- (void) showConversations :(CDVInvokedUrlCommand*)command {
        ConversationOptions *options = [ConversationOptions new];
        NSArray* arguments = [command arguments];
        NSDictionary* conversationParams;
        if(arguments != nil && arguments.count > 0) {
            conversationParams = [arguments firstObject];
            NSMutableArray *tagsList = [NSMutableArray array];
            NSArray* tags = [conversationParams objectForKey:@"tags"];
            if(tags != nil && tags.count > 0) {
                for(int i=0; i<tags.count; i++) {
                    [tagsList addObject:[tags objectAtIndex:i]];
                }
                NSString* title = [conversationParams objectForKey:@"filteredViewTitle"];
                [options filterByTags:tagsList withTitle:title];
            }
            [[Freshchat sharedInstance] showConversations:[self viewController] withOptions: options];
        } else {
            [[Freshchat sharedInstance] showConversations:[self viewController]];
        }
        [self callbackToJavascriptWithoutResultForCommand:command];
    }

- (void) showFAQs :(CDVInvokedUrlCommand*)command {
    NSArray* arguments = [command arguments];
    NSDictionary* faqParams;
    if(arguments != nil && arguments.count > 0) {
        faqParams = [arguments firstObject];
        FAQOptions *options = [FAQOptions new];
        
        if(faqParams [@"showFaqCategoriesAsGrid"]) {
            options.showFaqCategoriesAsGrid = [[faqParams objectForKey:@"showFaqCategoriesAsGrid"] boolValue];
        }
        if(faqParams [@"showContactUsOnFaqScreens"]) {
            options.showContactUsOnFaqScreens = [[faqParams objectForKey:@"showContactUsOnFaqScreens"] boolValue];
        }
        if(faqParams [@"showContactUsOnAppBar"]) {
            options.showContactUsOnAppBar = [[faqParams objectForKey:@"showContactUsOnAppBar"] boolValue];
        }
        NSMutableArray *tagsList = [NSMutableArray array];
        NSArray* tags = [faqParams objectForKey:@"tags"];
        NSString* articleType = [faqParams objectForKey:@"articleType"];
        if(tags != nil && tags.count > 0) {
            for(int i=0; i<tags.count; i++) {
                [tagsList addObject:[tags objectAtIndex:i]];
            }
            NSString* title = [faqParams objectForKey:@"filteredViewTitle"];
            if( [articleType isEqualToString:@"category"]) {
                [options filterByTags:tagsList withTitle:title andType: CATEGORY];
            } else {
                [options filterByTags:tagsList withTitle:title andType: ARTICLE];
            }
        }
        NSMutableArray *contactusTagsList = [NSMutableArray array];
        NSArray* contactusTags = [faqParams objectForKey:@"contactusTags"];
        if(contactusTags != nil && contactusTags.count > 0) {
            for(int i=0; i<contactusTags.count; i++) {
                [contactusTagsList addObject:[contactusTags objectAtIndex:i]];
            }
            NSString* contactusTitle = [faqParams objectForKey:@"contactusFilterTitle"];
            [options filterContactUsByTags:contactusTagsList withTitle:contactusTitle];
        }
        [[Freshchat sharedInstance]showFAQs:[self viewController] withOptions:options];
    } else {
        [[Freshchat sharedInstance]showFAQs:[self viewController]];
    }
    [self callbackToJavascriptWithoutResultForCommand:command];
}

- (void) clearUserData : (CDVInvokedUrlCommand*)command {
    [self.commandDelegate runInBackground:^{
        [[Freshchat sharedInstance]resetUserWithCompletion:^{
            [self callbackToJavascriptWithoutResultForCommand:command];
        }];
    }];
}

- (void) unreadCount :(CDVInvokedUrlCommand*)command {
    [[Freshchat sharedInstance] unreadCountWithCompletion:^(NSInteger unreadCount) {
        NSLog(@" The unread count value is: %ld", unreadCount);
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:(int)unreadCount];
        [self callbackToJavascriptWithResult:result ForCommand:command];
    }];
}

- (void) updateUser :(CDVInvokedUrlCommand*)command {
    NSArray* arguments = [command arguments];
    NSDictionary* args;
    if(arguments != nil && arguments.count > 0) {
        args = [arguments firstObject];
    } else {
        [self callbackToJavascriptWithoutResultForCommand:command];
    }
    FreshchatUser *user = [FreshchatUser sharedInstance];

    if([args objectForKey:@"firstName"] != nil) {
        user.firstName = [args objectForKey:@"firstName"];
    }
    
    if([args objectForKey:@"lastName"] != nil) {
        user.lastName = [args objectForKey:@"lastName"];
    }
    if([args objectForKey:@"email"] != nil) {
        user.email = [args objectForKey:@"email"];
    }
    if([args objectForKey:@"countryCode"] != nil) {
        user.phoneCountryCode = [args objectForKey:@"countryCode"];
    }
    if([args objectForKey:@"phoneNumber"] != nil) {
        user.phoneNumber = [args objectForKey:@"phoneNumber"];
    }
    [self.commandDelegate runInBackground:^{
        [[Freshchat sharedInstance] setUser:user];
        [self callbackToJavascriptWithoutResultForCommand:command];
    }];  
}

- (void) updateUserProperties :(CDVInvokedUrlCommand*)command {

    NSArray* arguments = [command arguments];
    NSDictionary* args;
    if(arguments != nil && arguments.count > 0) {
        args = [arguments firstObject];
    } else {
        [self callbackToJavascriptWithoutResultFailureForCommand:command];
    }

    NSArray *arrayOfKeys = [args allKeys];
    NSArray *arrayOfValues = [args allValues];

    NSString *key;
    NSString *value;

        for(int i=0; i<arrayOfKeys.count; i++) {
            key = [arrayOfKeys objectAtIndex:i];
            value = [arrayOfValues objectAtIndex:i];
            NSLog(@" The userMeta key is: %@ value is: %@", key,value);
            [[Freshchat sharedInstance] setUserPropertyforKey:key withValue:value];
        }
    [self callbackToJavascriptWithoutResultForCommand:command];
}

- (void) getVersionName :(CDVInvokedUrlCommand*)command {
    NSString* versionNumber = [Freshchat SDKVersion];
    NSLog(@"Freshchat version: %@", versionNumber);
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:(int)versionNumber];
    [self callbackToJavascriptWithResult:result ForCommand:command];
}

- (void) updatePushNotificationToken :(CDVInvokedUrlCommand*)command {
    NSArray* arguments = [command arguments];
    NSData* devToken;
    if(arguments != nil && arguments.count > 0) {
        devToken = [arguments firstObject];
    } else {
        [self callbackToJavascriptWithoutResultForCommand:command];
    }
    NSLog(@"Registration token value: %@", devToken);
    [self.commandDelegate runInBackground:^{
        [[Freshchat sharedInstance] setPushRegistrationToken:devToken];
        [self callbackToJavascriptWithoutResultForCommand:command];
    }];
    NSLog(@"Registration token has been updated");
}

- (void) _isFreshchatPushNotification :(CDVInvokedUrlCommand*)command {
    NSLog(@"checking if freshchat push notification");
    NSArray* arguments = [command arguments];
    NSDictionary* info;
    if(arguments != nil && arguments.count > 0) {
        info = [arguments firstObject];
    } else {
       [self callbackToJavascriptWithoutResultForCommand:command];
    }
    if ([[Freshchat sharedInstance]isFreshchatNotification:info]) {
        NSLog(@"It is a freshchat notification");
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:(int)1];
        [self callbackToJavascriptWithResult:result ForCommand:command];
    }
}

- (void) handlePushNotification : (CDVInvokedUrlCommand*)command {
    NSLog(@"Received a freshchat push notification");
    NSArray* arguments = [command arguments];
    NSDictionary* info = [arguments firstObject];
    [self.commandDelegate runInBackground:^{
        [[Freshchat sharedInstance]handleRemoteNotification:info andAppstate:[UIApplication sharedApplication].applicationState];
        [self callbackToJavascriptWithoutResultForCommand:command];
    }];
}
- (void) sendMessage : (CDVInvokedUrlCommand*)command {
    NSLog(@"Send Message called");

    NSArray* arguments = [command arguments];
    NSDictionary* args;
     if(arguments != nil && arguments.count > 0) {
        args = [arguments firstObject];
    } else {
        NSLog(@"Please provide tag and message field in object to send message");
        [self callbackToJavascriptWithoutResultForCommand:command];
    }
    if([args objectForKey:@"tag"] != nil && [args objectForKey:@"message"] != nil) {
         NSString* tag = [args objectForKey:@"tag"];
         NSString* message = [args objectForKey:@"message"];
            FreshchatMessage *userMessage = [[FreshchatMessage alloc] initWithMessage:message andTag:tag];
            [self.commandDelegate runInBackground:^{
            [[Freshchat sharedInstance] sendMessage:userMessage];
            [self callbackToJavascriptWithoutResultForCommand:command];
    }];
    }
    else{
        NSLog(@"Please provide tag and message field in object to send message");
        [self callbackToJavascriptWithoutResultForCommand:command];
    }
    
 
}

- (void) getRestoreID : (CDVInvokedUrlCommand*)command {
    NSLog(@"getRestoreID called");
    NSString* restoreID = [FreshchatUser sharedInstance].restoreID;
    NSLog(@"Freshchat restoreID: %@", restoreID);
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:restoreID];
    [self callbackToJavascriptWithResult:result ForCommand:command];
}

- (void) identifyUser : (CDVInvokedUrlCommand*)command {
    NSLog(@"identifyUser called");
    
    NSArray* arguments = [command arguments];
    NSDictionary* args;
    if(arguments != nil && arguments.count > 0) {
        args = [arguments firstObject];
    } else {
        NSLog(@"Please provide externalId and restoreId in object to identifyUser");
        [self callbackToJavascriptWithoutResultForCommand:command];
    }
    
    NSString* externalId = [args objectForKey:@"externalId"];
    NSString* restoreId = [args objectForKey:@"restoreId"];
    
    if([restoreId isEqual:[NSNull null]]) {
        restoreId = nil;
    }

    [self.commandDelegate runInBackground:^{
        [[Freshchat sharedInstance] identifyUserWithExternalID:externalId restoreID:restoreId];
        [self callbackToJavascriptWithoutResultForCommand:command];
    }];
}
-(void) registerRestoreIdNotification:(CDVInvokedUrlCommand*)command {
   [[NSNotificationCenter defaultCenter] addObserverForName:FRESHCHAT_USER_RESTORE_ID_GENERATED object:nil queue:nil usingBlock:^(NSNotification *note) {
      [self didReceiveNotification:note :command];
    }];
}
-(void) unregisterRestoreIdNotification:(CDVInvokedUrlCommand*)command{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:FRESHCHAT_USER_RESTORE_ID_GENERATED object:nil];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"sucessFully Unsubscrbed unread count listener "];
    [self callbackToJavascriptWithResult:result ForCommand:command];
}

-(void) unreadCountlistenerRegister:(CDVInvokedUrlCommand*)command {
    [[NSNotificationCenter defaultCenter]addObserverForName:FRESHCHAT_UNREAD_MESSAGE_COUNT_CHANGED object:nil queue:nil usingBlock:^(NSNotification *note) {
       [[Freshchat sharedInstance]unreadCountWithCompletion:^(NSInteger count) {
            NSLog(@"your unread count : %d", (int)count);
             CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:(int)count];
            [self callbackToJavascriptWithResult:result ForCommand:command];
       }]; 
}];
}
-(void) unreadCountlistenerUnregister:(CDVInvokedUrlCommand*)command{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:FRESHCHAT_UNREAD_MESSAGE_COUNT_CHANGED object:nil];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"sucessFully Unsubscrbed  unread count listener"];
    [self callbackToJavascriptWithResult:result ForCommand:command];
}

- (void) didReceiveNotification :(NSNotification*)notification :(CDVInvokedUrlCommand*)command {
    NSString* restoreID = [FreshchatUser sharedInstance].restoreID;
    NSString* externalID = [FreshchatUser sharedInstance].externalID;
    NSLog(@"Freshchat restoreID: %@", restoreID);
    NSMutableDictionary* param =[[NSMutableDictionary alloc]init];
    [param setValue:restoreID forKey:@"restoreId"];
    [param setValue:externalID forKey:@"externalId"];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:param];
    [self callbackToJavascriptWithResult:result ForCommand:command];
}



@end
