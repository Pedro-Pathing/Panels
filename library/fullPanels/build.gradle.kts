val pluginNamespace = "com.pedropathing.panels.fullpanels"
val pluginVersion = "0.0.40"

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
    compileOnly("org.firstinspires.ftc:Inspection:10.3.0")
    compileOnly("org.firstinspires.ftc:Blocks:10.3.0")
    compileOnly("org.firstinspires.ftc:RobotCore:10.3.0")
    compileOnly("org.firstinspires.ftc:RobotServer:10.3.0")
    compileOnly("org.firstinspires.ftc:OnBotJava:10.3.0")
    compileOnly("org.firstinspires.ftc:Hardware:10.3.0")
    compileOnly("org.firstinspires.ftc:FtcCommon:10.3.0")
    compileOnly("org.firstinspires.ftc:Vision:10.3.0")
    api(project(":core"))
    api(project(":opModeControl"))

    api(project(":telemetry"))
    api(project(":configurables"))
    api(project(":themes"))
    api(project(":capture"))
    api(project(":limelightProxy"))
    api(project(":gamepad"))
    api(project(":field"))
    api(project(":docs"))
    api(project(":battery"))
    api(project(":utils"))
    api(project(":pinger"))
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
                    description.set("All in one toolbox dashboard for FTC with core plugins.")
                    name.set("Full Panels")
                    url.set("https://panels.bylazar.com")

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
                name = "localDevRepo"
                url = uri("file:///C:/Users/lazar/Documents/GitHub/ftcontrol-maven/dev")
            }
        }
    }
}
