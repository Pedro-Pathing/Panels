import type { PluginConfig } from "ftc-panels"

export const config: PluginConfig = {
  id: "com.pedropathing.panels.utils",
  name: "Utils",
  letterName: "U",
  description: "Utils for Panels",
  websiteURL: "https://panels.bylazar.com/docs/com.pedropathing.panels.utils/",
  version: "0.0.10",
  pluginsCoreVersion: "1.1.20",
  author: "Lazar",
  widgets: [],
  navlets: [],
  manager: {
    name: "Manager",
    filepath: "src/manager.ts",
  },
  docs: {
    homepage: {
      name: "Homepage",
      filepath: "src/docs/Homepage.svelte",
    },
    chapters: [
      {
        name: "Loop Timer",
        filepath: "src/docs/LoopTimer.svelte",
      },
      {
        name: "Moving Average",
        filepath: "src/docs/MovingAverage.svelte",
      },
    ],
  },
  templates: [],
}
