# Messenger
This a full stack three way architecture involving Firebase, Nodejs and Kotlin based Android, to create a messaging application. 

<img src=https://nodejs.org/static/images/logos/nodejs-new-pantone-black.svg width="150" style="margin:30px 30px" />

<img src=https://www.pngix.com/pngfile/big/213-2134143_kotlin-logo-png-transparent-kotlin-logo-png-png.png width="150" style="margin:30px 30px" />

<br>

<img src=https://developer.android.com/images/brand/Android_Robot.svg width="150" style="margin:30px 30px" />

<img src=https://firebase.google.com/images/brand-guidelines/logo-built_black.png width="150" style="margin:30px 30px" />

<br><br>

This application provides the following features:
* Users can signup after their number is verified via Firebase Authentication.
* The User management is handled in a Nodejs based server running in [heroku](https://messenger-node-server.herokuapp.com/).
* User data is stored remotely in MongoDB Atlas.
* The Node server exposes a required REST APIs for handling services like signup, login, sending requests, accepting requests, etc.
* Firebase Realtime Database is used for achieving P2P messaging solution as well as storing message data.

<br><br>

How to use the code:
1. Checkout this git repo.
2. Initialize Node library using the following commands:<br><code>cd Node_Server<br>npm install</code>
3. Open Messenger_Android dir using Android studio to resolve dependencies from gradle.
4. Customize either server logic or Android behaviour and enjoy messaging.

<br><br>

This solution uses the following architecture:
<br>
<img src=https://raw.githubusercontent.com/broto76/Messenger/main/Messenger_Arch.png style="margin:30px 30px" />

<br><br>

This server logic uses the following npm libraries
* [bcryptjs](https://www.npmjs.com/package/bcryptjs) : Used for generating the hash of the user password and verifying the it during user authentication. 
* [express](https://www.npmjs.com/package/express) : Self explainatory. Most important library. 
* [jsonwebtoken](https://www.npmjs.com/package/jsonwebtoken) : Creating Auth token with user credential to verify request authenticity during REST API call.
* [mongoose](https://www.npmjs.com/package/mongoose) : Used for maintaining a schema for data storing and retrieving data over MongoDB server.

<br>

The Android client has the following dependencies
* [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines) : Used for coroutine management for optimizing the application and use less Thread overheads.
* [Material Components for Android](https://github.com/material-components/material-components-android) : Used for enriching Android UI/UX.
* [Retrofit](https://github.com/square/retrofit) : Used for making REST API calls from Android application to heroku server.
* [Firebase RealTime Database](com.google.firebase:firebase-database-ktx) : Used for accessing Firebase Realtime Database.
* [Firebae Authentication](com.google.firebase:firebase-auth-ktx) : Used for verifying user phonenumber.