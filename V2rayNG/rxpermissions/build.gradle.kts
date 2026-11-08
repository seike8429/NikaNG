plugins {
    id("com.android.library")
}

android {
    namespace = "com.tbruyelle.rxpermissions3"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.rxjava)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.fragment)
}
