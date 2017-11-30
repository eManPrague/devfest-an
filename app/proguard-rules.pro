# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/vsouhrada/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Basic ProGuard rules for Firebase Android SDK 2.0.0+
#-keep class com.firebase.** { *; }
#-keep class org.apache.** { *; }
#-keepnames class com.fasterxml.jackson.** { *; }
#-keepnames class javax.servlet.** { *; }
#-keepnames class org.ietf.jgss.** { *; }
#-dontwarn org.apache.**
#-dontwarn org.w3c.dom.**

# Add this global rule
#-keepattributes Signature
#
## This rule will properly ProGuard all the model classes in
## the package com.yourcompany.models. Modify to fit the structure
## of your app.
#-keepclassmembers class cz.eman.android.devfest.models.** {
#*;
#}


#-keep class com.facebook.login.DefaultAudience
#-keep class com.facebook.login.LoginBehavior
#-keep class com.facebook.login.widget.ToolTipPopup$Style
#-keep class com.facebook.login.widget.LoginButton$ToolTipMode
#-keep class com.facebook.login.widget.LoginButton$LoginButtonProperties
#-keep class com.facebook.login.LoginManager
#-keep class com.facebook.login.widget.ProfilePictureView$OnErrorListener
#-keep class com.facebook.share.internal.LikeBoxCountView$LikeBoxCountViewCaretPosition
#-keep class com.facebook.share.model.ShareContent
#-keep class com.facebook.share.widget.LikeView$Style
#-keep class com.facebook.share.widget.LikeView$AuxiliaryViewPosition
#-keep class com.facebook.share.widget.LikeView$HorizontalAlignment
#-keep class com.facebook.share.widget.LikeView$OnErrorListener
#-keep class com.facebook.share.model.ShareContent

-keepattributes Signature
-keepattributes *Annotation*