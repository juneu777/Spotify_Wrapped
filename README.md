# Spotify Wrapped Android App

An Android app that lets users sign in, connect to Spotify, generate a simple wrapped summary of their listening taste, and view recommended artists based on their Spotify history.

The app uses Firebase for account management and saved wrapped data, then calls the Spotify Web API to retrieve top artists, top tracks, and related artist recommendations.

## Features

- Email/password signup and login with Firebase Authentication
- Spotify OAuth login using the Spotify Android Auth SDK
- Wrapped generation from a user's top Spotify artists and tracks
- Long-term, medium-term, and short-term Spotify listening time ranges
- Saved wrapped history in Cloud Firestore
- Related artist recommendations based on Spotify taste data
- Profile actions for logout, account deletion, and account updates

## Tech Stack

- Java
- Android SDK
- Gradle
- Firebase Authentication
- Cloud Firestore
- Firebase Analytics
- Spotify Android Auth SDK
- Spotify App Remote AAR
- OkHttp
- Gson
- Glide
- AndroidX Navigation
- ViewBinding

## Prerequisites

- Android Studio
- JDK 17, required by Android Gradle Plugin 8.1.1
- Android SDK 34 installed
- A Firebase project
- A Spotify Developer application
- A physical Android device or emulator running Android 13/API 33 or newer

## Setup

1. Clone or open this repository in Android Studio.

2. Configure Firebase:
   - Create a Firebase project.
   - Add an Android app with package name:

     ```text
     com.cs2340.project2
     ```

   - Download `google-services.json`.
   - Place it at:

     ```text
     app/google-services.json
     ```

   - Enable Email/Password sign-in in Firebase Authentication.
   - Enable Cloud Firestore.

3. Configure Spotify:
   - Create an app in the Spotify Developer Dashboard.
   - Add this redirect URI:

     ```text
     project2://auth
     ```

   - Make sure the Spotify client ID in `MainActivity.java` matches your Spotify app:

     ```java
     public static final String CLIENT_ID = "...";
     ```

4. Sync Gradle in Android Studio.

5. Build and run the app on an emulator or Android device.

## Running From The Terminal

Build a debug APK:

```sh
./gradlew assembleDebug
```

Run unit tests, if tests are added:

```sh
./gradlew test
```

Run Android instrumentation tests, if tests are added:

```sh
./gradlew connectedAndroidTest
```

## App Flow

1. The app launches to the login screen.
2. Users can create an account or sign in with Firebase Authentication.
3. After login, the main screen opens and starts Spotify authorization.
4. On the Home tab, users choose a listening timeframe and tap **Generate Wrapped**.
5. The app fetches top artists and tracks from Spotify and stores the wrapped entry in Firestore.
6. The Recommended tab shows related artist recommendations based on saved Spotify taste data.
7. The Profile tab provides account actions such as logout, delete account, and update account details.

