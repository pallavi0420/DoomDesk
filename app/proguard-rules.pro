# DormDesk ProGuard / R8 Rules
# -----------------------------------------
# Keep Gson serialization models
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep data model classes (used by Firebase & Gson)
-keep class com.dormdesk.app.data.model.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Jetpack Compose
-dontwarn androidx.compose.**

# Keep Compose @Composable functions for navigation
-keep class com.dormdesk.app.ui.** { *; }

# Keep ViewModel classes
-keep class com.dormdesk.app.viewmodel.** { *; }

# R8 full mode compatibility
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod
