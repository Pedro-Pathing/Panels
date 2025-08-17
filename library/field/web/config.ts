import type { PluginConfig } from "ftc-panels"

export const config: PluginConfig = {
  id: "com.pedropathing.panels.field",
  name: "Field",
  letterName: "F",
  description: "Field Drawing for Panels",
  websiteURL: "https://panels.bylazar.com/docs/com.pedropathing.panels.field/",
  version: "0.0.11",
  pluginsCoreVersion: "1.1.20",
  author: "Lazar",
  widgets: [
    {
      name: "Field",
      filepath: "src/widgets/Field.svelte",
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
