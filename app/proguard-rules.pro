# Add project specific ProGuard rules here.
-keep class com.redmagic.gamespace.data.model.** { *; }
-keep class * extends androidx.room.RoomDatabase
-dontwarn kotlinx.coroutines.**
