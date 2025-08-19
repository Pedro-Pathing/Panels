plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
    id("com.pedropathing.panels.svelte-assets")
}

val pluginNamespace = "com.pedropathing.panels.core"
val pluginVersion = "0.0.23"

svelteAssets {
    webAppPath = "web"
    buildDirPath = "build"
    assetsPath = "web"
}

android {
    namespace = "com.pedropathing.panels"

    defaultConfig {
        compileSdk = 35
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    kotlinOptions {
        jvmTarget = "11"
    }

    publishing {
        singleVariant("release") {}
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.12.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    compileOnly("org.firstinspires.ftc:RobotCore:10.3.0")
    compileOnly("org.firstinspires.ftc:Hardware:10.3.0")
    compileOnly("org.firstinspires.ftc:FtcCommon:10.3.0")

    implementation("org.nanohttpd:nanohttpd-websocket:2.3.1") {
        exclude(module = "nanohttpd")
    }

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.23")

    implementation("org.tukaani:xz:1.9")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = pluginNamespace.substringBeforeLast('.')
                artifactId = pluginNamespace.substringAfterLast('.')
                version = pluginVersion

                pom {
                    description.set("All in one toolbox dashboard for FTC.")
                    name.set("Panels")
                    url.set("https://maven.pedropathing.com")

                    developers {
                        developer {
                            id.set("lazar")
                            name.set("Lazar Dragos George")
                            email.set("hi@bylazar.com")
                        }
                    }
                }
            }
        }

        repositories {
            maven {
                name = "publishing"
                url = uri("../../../maven.pedropathing.com")
            }

            //            maven {
//                name = "localDevRepo"
//                url = uri("file:///C:/Users/lazar/Documents/GitHub/ftcontrol-maven/dev")
//            }
        }
    }
}
