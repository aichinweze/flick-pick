# Flick Pick: Movie Recommender App
Flick-Pick is an intelligent movie recommender app built with Android Studio. It helps users decide what to watch by asking a few quick questions, then suggesting movies that match their preferences.

## Features
* User Authentication - Secure login and personalised user sessions
* Smart Movie Recommendations - Dynamically recommends movies based on user inputs and preferences.
* User History Tracking – Keeps a record of previously selected or watched movies for better suggestions over time.
* API Integration – Fetches real-time movie data from an external movie database API (e.g., TMDB or OMDb).
* Modern UI – Built using Android Studio with intuitive layouts and a clean, engaging design.

## Tech Stack
|Category |Technology  |
|:-------:|:----------:|
|Language |Kotlin      |
|Framework|Android SDK |
|API      |TMDB Movie Database|
|Storage  |SQLite (Room Database)|
|Build Tool|Gradle|

## How it Works
1. User Login: The user logs in or creates an account.
2. Preference Prompts: The app asks the user a series of questions (e.g., genre, release year, rating).
3. API Query: These responses form parameters for an API query that fetches matching movies.
4. Recommendation Display: The app displays a list of recommended movies with details such as title, synopsis, and poster.
5. User History: Selected movies are saved to the user’s profile for future reference and smarter recommendations.
