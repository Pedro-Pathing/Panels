import type { PluginConfig } from "ftc-panels"

export const config: PluginConfig = {
  id: "com.pedropathing.panels.battery",
  name: "Battery",
  letterName: "B",
  description: "Battery Utils for Panels",
  websiteURL: "https://panels.bylazar.com/docs/com.pedropathing.panels.battery/",
  version: "0.0.10",
  pluginsCoreVersion: "1.1.20",
  author: "Lazar",
  widgets: [],
  navlets: [
    {
      name: "Battery",
      filepath: "src/navlets/Battery.svelte",
    },
  ],
  manager: {
    name: "Manager",
    filepath: "src/manager.ts",
  },
  docs: {
    homepage: {
      name: "Homepage",
      filepath: "src/docs/Homepage.svelte",
    },
    chapters: [],
  },
  templates: [],
}
