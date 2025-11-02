# CineReserve

CineReserve is a **Kotlin Android movie booking app** that allows users to browse popular movies, view movie details, select show dates and time slots, choose the number of seats, and manage their bookings efficiently. The app demonstrates modern Android development practices including **MVVM architecture**, **Hilt dependency injection**, **Room database** for local storage, and **Coil** for image loading.

---

## Features

- **Browse Popular Movies**  
  Displays a list of popular movies fetched from a Movie Database API. Users can scroll to see more movies.

- **Movie Details**  
  View detailed information about a movie, including title, genre, rating, duration, and poster. Add movies to the cart from this screen.

- **Cart Management**  
  - Add movies to the cart for booking.  
  - Select **date** and **time slot** for each movie.  
  - Choose the **number of seats** (up to 10).  
  - Update or delete movies from the cart.  
  - Cart selections are stored locally using **Room**, so data persists when navigating away.

- **Checkout**  
  Confirm bookings for selected movies. Once checked out, movies are moved to the **Booked Movies** screen.

- **Booked Movies**  
  View all successfully booked movies in one place.

---

## App Flow

1. **Home Screen**  
   - Shows a list of popular movies.  
   - Top-right icons:  
     - **Cart**: Opens the cart screen.  
     - **Booked Movies**: Opens the booked movies screen.  

2. **Movie Details Screen**  
   - Displays detailed movie info.  
   - Button to add the movie to the cart.

3. **Cart Screen**  
   - Shows all movies selected for booking.  
   - Select **date**, **time slot**, and **number of seats**.  
   - Options to **checkout** or **delete** a movie.  
   - All selections are saved locally and persist if navigating away.

4. **Booked Movies Screen**  
   - Displays all movies that have been successfully booked.

---

## Tech Stack

- **Language:** Kotlin  
- **Architecture:** MVVM (Model-View-ViewModel)  
- **Dependency Injection:** Hilt  
- **Local Database:** Room  
- **Image Loading:** Coil  
- **Networking:** Retrofit (Movie Database API)

---
