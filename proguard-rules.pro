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

############################### my rule begin ###############################
# 未混淆的类和成员
-printseeds proguard-log/seeds.txt

# 列出从 apk 中删除的代码
-printusage proguard-log/unused.txt

# 混淆前后的映射
-printmapping proguard-log/mapping.txt

# R8 在构建项目时应用的所有规则的完整报告
-printconfiguration proguard-log/full-r8-config.txt

# -keep class com.yang.androidaar.MainActivity{*;}
-keep class com.yang.androidaar.MainActivity{
  public <methods>; #保持该类下所有的共有方法不被混淆
}



