# Playlist Maker App

**Playlist Maker** is an Android application designed for searching music tracks, creating personalized playlists, and managing a media library. This project was developed as a comprehensive case study during the Android Developer program at Yandex Practicum.

---

## Tech Stack & Architecture

The application is built using modern Android development practices and follows the principles of clean, maintainable code.

* **Language:** Kotlin
* **Architecture:** Clean Architecture + MVVM (Model-View-ViewModel)
* **UI Components:** Traditional View system (XML layout), Fragments, ViewBinding, Navigation Component
* **Asynchronous Programming:** Kotlin Coroutines & Flow
* **Networking:** Retrofit & OkHttp (integration with iTunes Search API via REST API)
* **Data Storage:** Room ORM (SQLite) & SharedPreferences
* **Dependency Injection:** Koin
* **Design & UI:** Dark/Light Theme support, RecyclerView for lists, Material Design components

---

## Key Features Implemented

- **Track Search:** Real-time music search via iTunes API with request history saving.
- **Audio Player:** Track playback simulation, adding tracks to favorites and custom playlists.
- **Media Library:** Fragment-based tabs for favorite tracks and user playlists.
- **Settings:** App-wide dark theme toggle and options to share the app or contact support.

---

## Tools Used

* Android Studio
* Git / GitHub (Version Control)
