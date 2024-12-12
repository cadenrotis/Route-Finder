Project2
==================
Repository for Iowa State University's CprE 388 project 2, in collaboration with Caden Otis and Noah Thompson.


# Project Overview:
Route Finder is an Android application designed for climbers to create, share, and rate custom climbing routes made by other climbers. It addresses the common issue faced by climbers at smaller gyms who might get bored with existing routes. Users can upload images of climbing walls, annotate them to define new routes using existing holds from various predefined routes, and share these routes with others either publicly or privately. The app uses Firebase Firestore and Firebase cloud storage to store user-generated routes, complete with details such as location, difficulty level, slope, descriptions, and community ratings. It also allows users to rate and comment on public routes and filter them based on specific criteria.

# My Role:
I contributed to the backend integration, camera functionality, and image processing for this app:

Activity Integration: Coordinated and linked different activities to ensure seamless navigation and functionality.
Camera Setup: Implemented the camera functionality, including image capture and processing workflows.
Database Integration: Debugged and optimized the process of uploading routes and associated data to Firebase Firestore.
Image Compression: Converted annotated images into compressed formats (Bitmaps and Strings) for efficient storage and retrieval in Firestore.

# Learnings:
During this project I learned a lot about various android APIs such as the camera, image processing libraries, and error handling and logging. Additionally I gained experience in integrating android applications with cloud technologies such as Firebase Cloud Storage and debugging complex errors caused by conflicting data types and encoding schemes.

# Resources Used: 
 - Firebase Firestore
 - Firebase Cloud Storage
 - Java
 - Android Studio & API
 - Git
 - XML

 # Directory Navigation: 
 To view the Java classes, navigate to `app/src/main/java/com/example/project2` (https://github.com/Leaflex/Route-Finder/tree/master/app/src/main/java/com/example/project2).

 To view the xml files and other files used for the views, navigate to `app/src/main/res/layout` (https://github.com/Leaflex/Route-Finder/tree/master/app/src/main/res/layout).