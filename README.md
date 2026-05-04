# SubTotal — Easy Sub Tracker

## ⚠️NOTE: THIS PROJECT IS UNSTABLE AND MAY CAUSE CRASHES AND ISSUES



> Never miss a bill. Track all your subscriptions in one place.

A native Android app built with Java that helps users manage their subscriptions, track upcoming bills, and get insights on monthly spending.

---

## Screenshots (yet to be updated)

| Welcome | Sign In | Home | Add Subscription | Timeline | Insights |
|---|---|---|---|---|---|
| S1 | S3 | S6 | S7 | S11 | S12 |

---

## Features

-  **Authentication** — Sign up / Sign in with Email and Google (Firebase Auth)
-  **Notifications** — Bill reminders 3 days before due date
-  **Subscription Management** — Add, view, and delete subscriptions
-  **Timeline** — See all upcoming bills sorted by due date
-  **Insights** — Monthly spend totals and category breakdown
-  **Money Saving Tips** — Personalized suggestions based on your data

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| UI | XML Layouts |
| Database | Room (SQLite) |
| Architecture | MVVM (ViewModel + LiveData) |
| Auth | Firebase Authentication |
| Min SDK | API 24 (Android 7.0) |

---

## Project Structure

```
com.g05.subtotal/
├── model/
│   └── Subscription.java
├── database/
│   ├── AppDatabase.java
│   └── SubscriptionDao.java
├── viewmodel/
│   └── SubscriptionViewModel.java
└── activities/
    ├── WelcomeActivity.java
    ├── SignUpActivity.java
    ├── SignInActivity.java
    ├── NotificationActivity.java
    ├── HomeActivity.java
    ├── AddSubscriptionActivity.java
    ├── SuccessActivity.java
    ├── SubDetailActivity.java
    ├── TimelineActivity.java
    └── InsightsActivity.java
```

---

## Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17+
- Android Emulator or physical device (API 24+)

### Setup

1. Clone the repository
```bash
git clone https://github.com/almostidle/SubTotal.git
cd SubTotal
```

2. Open in Android Studio
```
File → Open → select the cloned folder
```

3. Sync Gradle
```
File → Sync Project with Gradle Files
```

4. Run the app
```
Run → Run 'app' (Shift + F10)
```

---

## Dependencies

Add these to your `build.gradle (app)`:

```gradle
dependencies {
    // Room
    def room_version = "2.6.1"
    implementation "androidx.room:room-runtime:$room_version"
    annotationProcessor "androidx.room:room-compiler:$room_version"

    // Lifecycle
    implementation "androidx.lifecycle:lifecycle-viewmodel:2.7.0"
    implementation "androidx.lifecycle:lifecycle-livedata:2.7.0"

    // Firebase
    implementation platform('com.google.firebase:firebase-bom:32.7.0')
    implementation 'com.google.firebase:firebase-auth'
    implementation 'com.google.android.gms:play-services-auth:21.0.0'
}
```

---

## Team

| Name | Role | Screens |
|---|---|---|
| Dushyant | Database Architecture + Timeline + Insights | S11, S12 |
| Yash | Subscription Management | S7, S8, S9, S10 |
| Devashish | Home + Notifications | S4, S5, S6 |
| Dev | Auth + Onboarding | S1, S2, S3 |

---

## Course

**Mobile Application Development (MAD)**
B.Tech CSE — Group 05
