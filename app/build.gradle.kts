plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "ict.ihu.gr.arf"
    compileSdk = 34

    defaultConfig {
        applicationId = "ict.ihu.gr.arf"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.9"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/java", "src/main/aidl")
            aidl.srcDirs("src/main/aidl")
            aidl {
                srcDirs("src/main/aidl")
            }
        }
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
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }

    buildFeatures {
        viewBinding = true
    }
    packagingOptions {
        resources {
            excludes += setOf("META-INF/NOTICE.md", "META-INF/LICENSE.md", "META-INF/DEPENDENCIES")
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.sun.mail:android-mail:1.6.6")
    implementation("com.sun.mail:android-activation:1.6.7")
    implementation("com.sunmi:printerx:1.0.15")

    //qr code generation
    implementation("com.google.zxing:core:3.4.1")
    implementation("com.journeyapps:zxing-android-embedded:4.1.0")

    implementation("com.google.android.gms:play-services-tasks:18.0.2")
    implementation("com.google.code.gson:gson:2.8.9")
    //firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")

//    implementation("org.bouncycastle:bcpkix-jdk15on:1.70")
//    implementation("org.bouncycastle:bcpg-jdk15on:1.70")
//    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    implementation("org.pgpainless:pgpainless-sop:1.6.2")
//    implementation("javax.mail:mail:1.4.7")

    implementation("com.google.api-client:google-api-client-android:1.32.1")
    implementation("com.google.api-client:google-api-client-gson:1.32.1")
    implementation("com.google.android.gms:play-services-auth:19.2.0")
    implementation("com.google.api-client:google-api-client:2.0.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
    implementation("com.google.apis:google-api-services-sheets:v4-rev20220927-2.0.0")

    implementation("com.google.android.material:material:1.4.0")

    //ftp stuff
    implementation("commons-net:commons-net:3.6")

    implementation("androidx.work:work-runtime:2.7.1")

    //SOS HERE
//    implementation("com.amitshekhar.android:android-networking:1.0.2") TO EBGALA GT DEN DOULEUE TO APP OLOKLHRO DEN EKANE BUILD
    // na balw auto mesa sta quotes ean den douleuei kapoio function "com.github.amitshekhariitbhu.Fast-Android-Networking:android-networking:v1.0.2"



//    implementation("com.github.amitshekhariitbhu.Fast-Android-Networking:android-networking:v1.0.2")

    // Include any additional dependencies required by the SDK here
    // For example, if the SDK comes with any jar or aar files, include them
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar", "*.aidl"))))

}
