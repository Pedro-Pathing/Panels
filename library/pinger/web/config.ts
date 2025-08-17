import type { PluginConfig } from "ftc-panels"

export const config: PluginConfig = {
  id: "com.pedropathing.panels.pinger",
  name: "Pinger",
  letterName: "EP",
  description: "Latency Test for Panels",
  websiteURL: "https://panels.bylazar.com/docs/com.pedropathing.panels.pinger/",
  version: "0.0.6",
  pluginsCoreVersion: "1.1.20",
  author: "Lazar",
  widgets: [],
  navlets: [
    {
      name: "Ping",
      filepath: "src/navlets/Ping.svelte",
    },
  ],
  manager: {
    name: "Manager",
    filepath: "src/manager.ts",
  },
  docs: {
    homepage: {
      name: "Overview",
      filepath: "src/docs/Overview.svelte",
    },
    chapters: [],
  },
  templates: [],
}
