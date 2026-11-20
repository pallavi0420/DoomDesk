# Project ProGuard rules for Google Play Store Production Release

-keepattributes *Annotation*,Signature,Exceptions,InnerClasses,EnclosingMethod

# Keep @Keep annotations
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}
-keep @androidx.annotation.Keep class * { *; }

# Kotlinx Serialization
-dontnote kotlinx.serialization.SerializationKt
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,allowobfuscation,allowshrinking class * {
    <init>(...);
}

# Data Models & Enums serialization integrity
-keep class com.dormdesk.app.data.model.** { *; }
-keepclassmembers class com.dormdesk.app.data.model.** {
    *** Companion;
    *;
}
-keepclassmembers enum com.dormdesk.app.data.model.** { *; }
-keep class com.dormdesk.app.data.remote.** { *; }
-keepclassmembers class com.dormdesk.app.data.remote.** { *; }

# Retrofit & OkHttp Networking
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers interface * {
    @retrofit2.http.* <methods>;
}

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# Coil Image Loading
-keep class coil.** { *; }
-dontwarn coil.**

# Jetpack Compose
-keep class androidx.compose.material.icons.** { *; }
-dontwarn androidx.compose.**
