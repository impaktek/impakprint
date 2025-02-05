plugins {
    //alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("com.android.library")
    id("maven-publish")
}

/*afterEvaluate { // ✅ Ensures components["release"] exists
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"]) // 👈 Use 'default' instead of 'release' if needed
                groupId = "com.impaktek"
                artifactId = "impak-pos-print"
                version = "1.0.0"
            }
        }
    }
}*/





android {
    namespace = "com.impaktek.impakprint"
    compileSdk = 35

    defaultConfig {
        minSdk = 27

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }// 👈 Enables publishing for release variant
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"]) // 👈 Use 'default' instead of 'release' if needed
                groupId = "com.impaktek"
                artifactId = "impakprint"
                version = "1.1.2"
            }
        }
    }
}



dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.zxing)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}