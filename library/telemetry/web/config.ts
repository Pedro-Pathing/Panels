import type { PluginConfig } from "ftc-panels"

export const config: PluginConfig = {
  id: "com.pedropathing.panels.telemetry",
  name: "Telemetry",
  letterName: "T",
  description: "Text-Based Telemetry",
  websiteURL: "https://panels.bylazar.com/docs/com.pedropathing.panels.telemetry/",
  version: "0.0.8",
  pluginsCoreVersion: "1.1.20",
  author: "Lazar",
  widgets: [
    {
      name: "Telemetry",
      filepath: "src/widgets/Telemetry.svelte",
    },
  ],
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
        name: "DocsPage1",
        filepath: "src/docs/DocsPage1.svelte",
      },
    ],
  },
  templates: [],
}
