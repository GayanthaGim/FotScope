plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.fotscope"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.fotscope"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Firebase BOM (Bill of Materials to manage Firebase versions)
    implementation(platform("com.google.firebase:firebase-bom:30.0.1"))

    // Firebase Core (for all Firebase services like Analytics)
    implementation("com.google.firebase:firebase-analytics")

    // Firebase Firestore
    implementation("com.google.firebase:firebase-firestore")

    // Firebase Authentication (if using authentication)
    implementation("com.google.firebase:firebase-auth")

    // Firebase Storage (if using Firebase Storage)
    implementation("com.google.firebase:firebase-storage")

    // Firebase Realtime Database (if using Realtime Database)
    implementation("com.google.firebase:firebase-database")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    // Material Design components
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)

    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

// Apply the Google services plugin to enable Firebase features
apply(plugin = "com.google.gms.google-services")
