import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

// Firebase config is not committed; apply the plugin only when google-services.json is present.
if (file("google-services.json").exists()) {
    apply(plugin = libs.plugins.google.services.get().pluginId)
}

// Read secrets from local.properties (not committed), e.g. SPOONACULAR_API_KEY=xxxx
val localProperties = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
val spoonacularApiKey: String = localProperties.getProperty("SPOONACULAR_API_KEY")
    ?: System.getenv("SPOONACULAR_API_KEY") ?: ""

android {
    namespace = "com.example.mealmate"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mealmate"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SPOONACULAR_API_KEY", "\"$spoonacularApiKey\"")
    }

    buildFeatures {
        buildConfig = true
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.activity)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.coordinatorlayout)
    implementation(libs.cardview)
    implementation(libs.recyclerview)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.appcheck.playintegrity)
    implementation(libs.play.services.auth)
    implementation(libs.facebook.login)

    implementation(libs.lottie)
    implementation(libs.volley)
    implementation(libs.glide)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.clans.fab)
    implementation(libs.circleimageview)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
