plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.albumapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.albumapp"
        minSdk = 28
        targetSdk = 34
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("jp.wasabeef:glide-transformations:4.3.0")

    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("com.github.chrisbanes:PhotoView:2.3.0")

    implementation ("androidx.recyclerview:recyclerview:1.3.2")



    // DS Photo Editor SDK
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation(files("libs/ds-photo-editor-sdk-v10.aar"))
//    implementation ("com.github.denzcoskun:ImageSlideshow:0.1.2")

    implementation ("com.github.smarteist:autoimageslider:1.4.0")

    implementation ("at.favre.lib:bcrypt:0.10.2")


}