# 🎬 CineBook — Movie Booking Android App

A fully-featured Android movie ticket booking application written in **Java**, following modern Android development practices with a dark cinema-themed Material Design UI.

---

## 📱 Screenshots & Features

| Screen | Description |
|---|---|
| Splash | Animated logo with auto-login routing |
| Login | Email + password with SHA-256 hashing |
| Register | Full validation, auto-login on success |
| Home | Grid of movies, genre chips, live search |
| Movie Details | Collapsing banner, ratings, synopsis, cast |
| Seat Selection | Theater + date + time picker, interactive seat grid |
| Payment | Order summary, UPI/Card/Wallet mock payment |
| Confirmation | QR code ticket, share & download options |
| Profile | Edit details, dark mode toggle, logout |
| Booking History | Full history with cancel support |

---

## 🏗️ Project Structure

```
app/src/main/
├── java/com/moviebooking/app/
│   ├── activities/
│   │   ├── MainActivity.java              # Splash + routing
│   │   ├── LoginActivity.java             # Login screen
│   │   ├── RegisterActivity.java          # Registration
│   │   ├── HomeActivity.java              # Movie grid + search
│   │   ├── MovieDetailsActivity.java      # Full movie info
│   │   ├── SeatSelectionActivity.java     # Seat grid picker
│   │   ├── PaymentActivity.java           # Mock payment flow
│   │   ├── BookingConfirmationActivity.java  # QR ticket
│   │   ├── BookingHistoryActivity.java    # Past bookings
│   │   └── ProfileActivity.java           # User profile
│   ├── adapters/
│   │   ├── MovieAdapter.java              # Home grid adapter
│   │   ├── SeatAdapter.java               # Seat grid adapter
│   │   ├── ShowTimeAdapter.java           # Time slot adapter
│   │   └── BookingHistoryAdapter.java     # History list adapter
│   ├── models/
│   │   ├── Movie.java                     # Movie data model
│   │   ├── User.java                      # User data model
│   │   ├── Booking.java                   # Booking + QR data
│   │   └── Seat.java                      # Seat state model
│   ├── database/
│   │   └── DatabaseHelper.java            # SQLite ORM layer
│   └── utils/
│       ├── SessionManager.java            # SharedPreferences session
│       ├── ValidationUtils.java           # Input validation + SHA-256
│       ├── MovieDataProvider.java         # Sample movie seed data
│       └── NotificationHelper.java        # Local push notifications
├── res/
│   ├── layout/                            # All XML layouts
│   ├── drawable/                          # Shapes, gradients, icons
│   ├── values/                            # Colors, strings, themes, dimens
│   ├── color/                             # State-list color selectors
│   ├── menu/                              # Bottom navigation menu
│   └── mipmap-*/                          # Launcher icons
└── AndroidManifest.xml
```

---

## 🛠️ Tech Stack

| Component | Library / API |
|---|---|
| Language | Java 8 |
| Min SDK | 21 (Android 5.0 Lollipop) |
| Target SDK | 34 (Android 14) |
| UI | Material Components 1.12, ConstraintLayout 2.1 |
| Images | Glide 4.16 |
| QR Code | ZXing / journeyapps 4.3 |
| Database | SQLite via SQLiteOpenHelper |
| Session | SharedPreferences |
| Notifications | NotificationCompat (local) |
| Circular Images | CircleImageView 3.1 |

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17+
- Android SDK 34

### Steps

1. **Clone / extract** the project folder.

2. **Open** in Android Studio:
   ```
   File → Open → Select MovieBookingApp/
   ```

3. **Sync Gradle** — Android Studio will automatically download all dependencies.

4. **Run** on an emulator or physical device (API 21+):
   ```
   Run → Run 'app'   or   Shift+F10
   ```

5. **First launch** seeds the database with 12 sample movies automatically. No internet required (sample poster images use `picsum.photos`).

---

## 🔑 Key Implementation Details

### Authentication
- Passwords are hashed with **SHA-256** before storage — never stored in plain text.
- Session is persisted in `SharedPreferences` and cleared on logout.
- Email uniqueness is enforced at DB level (`UNIQUE` constraint).

### Database (SQLite)
- Singleton `DatabaseHelper` manages 4 tables: `users`, `movies`, `bookings`, `favorites`.
- All DB operations run on **background threads** to keep the UI responsive.
- Bookings store seats as a comma-separated string for simplicity.

### Seat Selection
- **11 rows × 10 columns** grid: rows A–F Standard, G–I Premium, J–K Recliner.
- Pre-booked seats are generated with a **seeded Random** (different seed per date/theater/time = realistic variation).
- Price varies by category: Standard (base), Premium (1.5×), Recliner (2×).
- Maximum **8 seats per booking** enforced in UI.

### QR Code Ticket
- Generated client-side using **ZXing** with all booking fields encoded.
- Can be shared as text or saved to the device gallery via `MediaStore`.

### Dark Mode
- Toggled via `AppCompatDelegate.setDefaultNightMode()`.
- Preference persisted in `SessionManager`.

---

## 📦 Dependencies (app/build.gradle)

```groovy
// Core
implementation 'androidx.appcompat:appcompat:1.7.0'
implementation 'com.google.android.material:material:1.12.0'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
implementation 'androidx.recyclerview:recyclerview:1.3.2'
implementation 'androidx.cardview:cardview:1.0.0'

// Image Loading
implementation 'com.github.bumptech.glide:glide:4.16.0'

// QR Code
implementation 'com.google.zxing:core:3.5.3'
implementation 'com.journeyapps:zxing-android-embedded:4.3.0'

// Circular Image
implementation 'de.hdodenhof:circleimageview:3.1.0'

// Lottie (animations ready to use)
implementation 'com.airbnb.android:lottie:6.4.0'
```

---

## 🔮 Bonus Features Implemented

- ✅ Dark mode support (toggle in Profile)
- ✅ Local push notification on booking confirmation
- ✅ Favorites list (heart toggle on every movie card)
- ✅ QR code ticket with share + download
- ✅ Dynamic seat pricing per category
- ✅ Booking cancellation from history

## 🔮 Future Enhancements

- 🔄 Firebase Auth + Firestore (replace SQLite)
- 🌐 TMDb API integration for live movie data
- 💳 Real payment gateway (Razorpay / Stripe)
- 🔔 FCM push notifications
- 🎯 Recommendation engine based on booking history

---

## 📄 License

This project is provided for educational purposes. Free to use and modify.
