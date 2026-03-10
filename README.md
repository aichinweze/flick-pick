# 🎬 Flick Pick — Android Movie Recommender App

> A native Android application built in **Kotlin** with **Jetpack Compose**, demonstrating end-to-end mobile development across UI, architecture, authentication, cloud persistence, and third-party API integration.

---

## Overview

Flick Pick helps users pick a moview to watch by asking a short series of preference questions and using 
those answers to query the **TMDB (The Movie Database) API** in real time. Results are filtered by 
genre, region, release decade, runtime, and quality rating. Users can browse recommendations, 
save selections to a personal watch history, and rate movies they've seen — all backed by a cloud-synced 
user account.

This project was built as a portfolio piece to demonstrate practical Android development skills across 
the full stack of a modern mobile application.

---

## ✨ Features

- **Google Sign-In** — Secure, frictionless authentication via Firebase Auth and the Android Credential Manager API
- **Dynamic Movie Recommendations** — Questionnaire responses are composed into parameterised TMDB API queries at runtime
- **Personal Watch History** — Selected movies are persisted to Firestore and displayed across sessions
- **User Rating System** — Users can rate their watched movies via an in-app slider, with ratings synced to the cloud
- **Account Management** — Users can view and edit their profile details and saved baseline preferences
- **Fully Themed UI** — Consistent Material 3 design with a custom colour scheme applied app-wide

---

## 🛠️ Tech Stack

| Category | Technology |
|:--|:--|
| Language | Kotlin |
| UI Framework | Jetpack Compose + Material 3 |
| Architecture | MVVM (ViewModel + StateFlow) |
| Authentication | Firebase Authentication (Google Sign-In) |
| Cloud Database | Firebase Firestore |
| HTTP Client | Retrofit 2 + Gson |
| Navigation | Jetpack Navigation Compose |
| Build Tool | Gradle (Kotlin DSL) |
| Testing | JUnit 4, Mockito-Kotlin, Kotlinx Coroutines Test |

---

## 🏗️ Architecture

The app follows the **MVVM (Model-View-ViewModel)** pattern throughout:

- **ViewModels** own all business logic and state, exposed as `StateFlow` streams
- **Composable screens** observe state reactively and are fully decoupled from data concerns
- **Repository layer** (`CsvRepositoryImpl`) abstracts all local asset reads behind a coroutine-safe interface
- **Firestore** is accessed directly from ViewModels, with user data scoped to Firebase Auth UIDs (not emails) for security

Navigation is handled by **Jetpack Navigation Compose**, with the start destination determined at launch based on the current Firebase Auth session.

Credentials are managed securely via `gradle.properties` and injected at build time through `BuildConfig`, ensuring no secrets are committed to source control.

---

## 📱 App Flow

```
Login (Google Sign-In)
    │
    └─▶ Dashboard
            ├─▶ Baseline Questionnaire  →  Saves genre & region preferences to Firestore
            └─▶ Flick Pick              →  5-question flow → TMDB API query → Results list
                                                │
                                                └─▶ Movie Detail View  →  Save to Watch History
                                                
        Account Screen  →  View/edit profile & saved baseline preferences
        History Screen  →  Browse saved movies, add/edit personal ratings
```

---

## 🔐 Security Considerations

- Firestore documents are keyed by **Firebase Auth UID**, not by email, ensuring data cannot be accessed or guessed by other users
- Firestore Security Rules enforce that each authenticated user can only read and write their own documents
- The TMDB API bearer token and Google OAuth client ID are stored in `gradle.properties` (gitignored) and injected via `BuildConfig` — neither appears in source code
- Google Sign-In uses a cryptographically random **nonce** on every request to prevent replay attacks

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Ladybug or later
- A Firebase project with Authentication (Google Sign-In) and Firestore enabled
- A TMDB API account with a bearer token

### Setup

1. Clone the repository
2. Copy `gradle.properties.example` to `gradle.properties` and fill in your credentials:
   ```properties
   TMDB_AUTH_TOKEN=your_tmdb_bearer_token
   WEB_CLIENT_ID=your_google_oauth_web_client_id
   ```
3. Download your `google-services.json` from the Firebase console and place it at `app/google-services.json`
4. Add the following Firestore Security Rules in the Firebase console:
   ```
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /account_details/{userId} {
         allow read, write: if request.auth != null && request.auth.uid == userId;
       }
       match /baseline_questions/{userId}/{document=**} {
         allow read, write: if request.auth != null && request.auth.uid == userId;
       }
       match /watch_history/{userId}/{document=**} {
         allow read, write: if request.auth != null && request.auth.uid == userId;
       }
     }
   }
   ```
5. Build and run on a device or emulator running Android 8.0 (API 26) or above

---

## 🧪 Testing

The project includes both **unit tests** and **instrumented tests**:

- **`RepositoryUtilsTest`** — Unit tests for all CSV parsing functions (question data, genre data, region data, time bounds, movie quality)
- **`CsvRepositoryTest`** — Instrumented tests verifying correct asset reads from the Android asset manager
- **`ApiTest`** — Integration tests for the TMDB API service, validating live responses for movie discovery and genre endpoints

Run unit tests with:
```bash
./gradlew test
```

Run instrumented tests with:
```bash
./gradlew connectedAndroidTest
```

---

## 📂 Project Structure

```
app/src/main/java/com/ichinweze/flickpick/
├── data/                   # Data classes, API models, Firestore models, constants
├── interfaces/             # Repository and API service interfaces
├── repositiories/          # CSV asset repository implementation
├── screens/                # Composable UI screens
│   └── utils/              # Shared composables and screen route constants
├── ui/theme/               # Material 3 theme, colour scheme, typography
└── viewmodels/             # MVVM ViewModels for each screen
    └── utils/              # Shared ViewModel utility functions
```

---

## 🔭 Future Work

- **Baseline Questionnaire** — Users can provide general movie preferences for genre and region. The next step is to use that information to bias the movie database search for more relevant results.
- **Expand Regions and Genres** — A relatively short selection of movies and genres are currently available in questionnaires. Future versions would involve expansion in both areas.
- **Expand Sign-In Options** — This version of the application only supports Google Sign-In but in future, this should be expanded to include email and other methods.  
- **UI Refinement** — As always, the user experience can be improved. General improvements to the look and feel of the app can be made going forward.

---

## 👤 Author

**aichinweze**  
[GitHub](https://github.com/aichinweze)
[LinkedIn](https://www.linkedin.com/in/ifeanyi-chinweze-673b0916b/)

---

*Built as a portfolio project to demonstrate native Android development in Kotlin.*
