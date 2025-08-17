import type { PluginConfig } from "ftc-panels"

export const config: PluginConfig = {
  id: "com.pedropathing.panels.limelightproxy",
  name: "Limelight Proxy",
  letterName: "LLP",
  description: "Limelight Proxy for Panels",
  websiteURL: "https://panels.bylazar.com/docs/com.pedropathing.panels.limelightproxy/",
  version: "0.0.10",
  pluginsCoreVersion: "1.1.20",
  author: "Lazar",
  widgets: [
    {
      name: "CameraStream",
      filepath: "src/widgets/CameraStream.svelte",
    },
    {
      name: "Dashboard",
      filepath: "src/widgets/Dashboard.svelte",
    },
    {
      name: "Stats",
      filepath: "src/widgets/Stats.svelte",
    },
  ],
  navlets: [
    {
      name: "Temperature",
      filepath: "src/navlets/Temperature.svelte",
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
    chapters: [
      {
        name: "DocsPage1",
        filepath: "src/docs/DocsPage1.svelte",
      },
    ],
  },
  templates: [],
}
