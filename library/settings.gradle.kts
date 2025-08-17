pluginManagement {
    includeBuild("plugin-svelte-assets")
    repositories {
        gradlePluginPortal()
    }
}

include(":opModeControl")
include(":examplePlugin")
include(":telemetry")
include(":configurables")
include(":themes")
include(":capture")
include(":limelightProxy")
include(":field")
include(":gamepad")
include(":docs")
include(":battery")
include(":fullPanels")
include(":core")
include(":utils")
include(":pinger")
