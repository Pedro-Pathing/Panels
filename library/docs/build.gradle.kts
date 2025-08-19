val pluginNamespace = "com.pedropathing.panels.docs"
val pluginVersion = "0.0.7"

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
    id("com.pedropathing.panels.svelte-assets")
}

svelteAssets {
    webAppPath = "web"
    buildDirPath = "dist"
    assetsPath = "web/plugins/$pluginNamespace"
}

android {
    namespace = pluginNamespace

    defaultConfig {
        compileSdk = 35
        minSdk = 24
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
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
    compileOnly("org.firstinspires.ftc:RobotCore:10.3.0")
    compileOnly("org.firstinspires.ftc:Hardware:10.3.0")
    compileOnly("org.firstinspires.ftc:FtcCommon:10.3.0")
    compileOnly(project(":core"))
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
                    description.set("Panels Docs Plugin")
                    name.set("Panels Docs")
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
