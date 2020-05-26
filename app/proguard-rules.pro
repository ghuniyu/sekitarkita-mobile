-keep class com.linkensky.ornet.data.model.** { *; }

-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
-repackageclasses

-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# And if you use AsyncExecutor:
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}