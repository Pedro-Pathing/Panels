plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

group = "com.bylazar"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("svelteAssetsPlugin") {
            id = "com.pedropathing.panels.svelte-assets"
            implementationClass = "com.pedropathing.panels.SvelteAssetsPlugin"
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("plugin") {
                from(components["java"])

                groupId = project.group.toString()
                artifactId = "svelte-assets"
                version = project.version.toString()

                pom {
                    name.set("Svelte Assets Plugin")
                    description.set("A Gradle plugin to bundle Svelte apps into Android libraries.")
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

            maven {
                name = "localDevRepo"
                url = uri("file:///C:/Users/lazar/Documents/GitHub/ftcontrol-maven/dev")
            }
        }
    }
}