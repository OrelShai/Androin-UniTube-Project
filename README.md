![unitube logo for README.png](app/src/main/assets/README_pictures/unitube%20logo%20for%20README.png)

# UniTube (Server & Local Database Integrated) 🎬

This version of UniTube extends the Android application to work both with a backend server and a local database (Room) for offline capabilities.
Users can upload, view, and manage videos while ensuring data sync between local and remote storage when network connectivity is available.

## Key Changes in This Version
- **Server Integration**: The app now interacts with a Node.js/Express backend server, using MongoDB for data storage and Multer for file uploads.
- **Offline Mode with Room**: Added Room for local data persistence, enabling offline support for videos list and comments.
- **Retrofit**: Used for API requests to the server, including video uploads, comments, user authentication, and other features.
- **Token-Based Authentication**: Utilizes JWT for secure login and user management between the app and the server.

## Running the Application 🏃‍♂️
1. Clone the repository to your local machine.
2. Open the project in Android Studio.
3. Connect your Android device or start an emulator.
4. Build and run the app.
5. Update the `BASE_URL` variable to match your IP address. This variable is located in the `RetrofitClient` class under `com.project.unitube.network.RetroFit`.

   For example, if your IP address is `111.111.1.111`, the `BASE_URL` should be updated as follows:
    ```java
    private static final String BASE_URL = "http://111.111.1.111:8200/";
    ```
   Make sure to replace 111.111.1.111 with your actual IP address.
   Note: If a physical Android device is connected, make sure it is connected to the same internet network as the computer running the server.
6. Ensure the backend server is running (you can find the server project with instructions in this link: https://github.com/hilaelpeleg/-UniTube--Server).
   

## Features ✨
### Server Integration 🌐
- **User Authentication**: Users can register and log in, using JWT for secure token-based authentication.
- **Video Upload and Management**: Videos are uploaded to the server with metadata stored in MongoDB. Video thumbnails and media are handled by Multer.
- **Comments**: Users can add, edit, and delete comments on videos. Comments are synced between the local database and the server.

### Local Database Integration (Offline Mode) 💾
- **Room Database**: The app now uses Room for local storage, allowing users to view previously loaded data (videos, comments, etc.) while offline.

## Project Structure 📂
The project is structured using the MVVM architecture pattern, separating the app into three main components: Model, View, and ViewModel.
The View is responsible for displaying the data and handling user interactions.
The Model represents the data and business logic of the application.
The ViewModel acts as a bridge between the Model and the View, handling data operations and business logic.


### Key Components:
- **MainActivity**: Displays videos fetched from either the local Room database or the server.
- **RetrofitClient**: Manages API calls to the server using Retrofit.
- **Repository**: Acts as a single source of truth for data, handling data operations.
- **API Interface**: Contains API endpoints for server communication.
- **API classes**: Contains classes for API responses and requests to the server. sync between local and remote storage.

all basic classes and their usage are explained in the main branch README for part 1.

### Room Database (Local):
- **VideoDao**: Handles local video data.
- **CommentDao**: Handles local comment data.

## Server Interaction 📡
The app interacts with a Node.js/Express server for all backend functionality. This includes user authentication, video and comment management, and profile picture uploads.
For server setup, please refer to the backend README.

## Additional Features
- **Offline Mode**: Full support for offline video browsing and commenting.
- **Profile Picture Uploads**: Users can upload profile pictures from the gallery or camera. the files are stored on the server.
- **Syncing Local and Server Data**: after a successful response from the server, the data is stored in the local database for offline use.

## Installation and Setup for Server
Follow the instructions from the Server README for backend setup, including environment variables and MongoDB setup in the following link: https://github.com/hilaelpeleg/-UniTube--Server

## Detailed Features & Screenshots 📱
For a detailed explanation of the app's features, functionality, and screenshots illustrating the user interface, refer to the `main` branch.
The `main` branch includes visual examples and guides on how each feature works.




