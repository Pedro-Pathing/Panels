import type { PluginConfig } from "ftc-panels"

export const config: PluginConfig = {
  id: "com.pedropathing.panels.exampleplugin",
  name: "Example Plugin",
  letterName: "EP",
  description: "Example Plugin for Panels",
  websiteURL: "",
  version: "0.0.8",
  pluginsCoreVersion: "1.1.20",
  author: "Lazar",
  widgets: [
    {
      name: "Counter",
      filepath: "src/widgets/Counter.svelte",
    },
  ],
  navlets: [],
  manager: {
    name: "Manager",
    filepath: "src/manager.ts",
  },
  docs: {
    homepage: {
      name: "Overview",
      filepath: "src/docs/Overview.svelte",
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
