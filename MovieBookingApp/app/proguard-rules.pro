// ProGuard rules for MovieBookingApp
// Add project specific ProGuard rules here.

# Keep model classes (needed for SQLite cursor mapping)
-keep class com.moviebooking.app.models.** { *; }

# Keep ZXing QR code classes
-keep class com.google.zxing.** { *; }
-keep class com.journeyapps.** { *; }

# Keep Glide generated API
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule { <init>(...); }
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

# Keep CircleImageView
-keep class de.hdodenhof.circleimageview.** { *; }

# Material Components
-keep class com.google.android.material.** { *; }
