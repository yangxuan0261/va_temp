# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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

######### jni 入口 #########
-keep class com.yang.androidaar.MainActivity{
  public <methods>; # 保持该类下所有的共有方法不被混淆
}
-keep class com.yang.androidaar.model.** { <fields>; } # json 序列化的数据结构

######### bugly #########
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

######### 阿里云 OSS #########
-keep class com.alibaba.sdk.android.oss.** { *; }
-dontwarn okio.**
-dontwarn org.apache.commons.codec.binary.**

######### gson #########
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
