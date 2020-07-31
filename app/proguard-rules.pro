# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/Veeresh/Library/Android/sdk/tools/proguard/proguard-android.txt
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

-optimizationpasses 5
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose


-keep class com.aandssoftware.aandsinventory.models** { *; }
-keep public class com.fasterxml**{*;}
-keep class org.apache**{*;}
-keep class org.codehaus**{*;}
-keep class com.bea**{*;}
-keep class org.etsi**{*;}
-keep class com.fasterxml**{*;}
-keep class com.microsoft**{*;}
-keep class aavax.xml**{*;}
-keep class org.openxmlformats**{*;}
-keep class schemaorg_apache_xmlbeans**{*;}
-keep class com.aandssoftware.aandsinventory.pdfgenarator**{*;}