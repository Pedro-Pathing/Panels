import type { PluginConfig } from "ftc-panels"

export const config: PluginConfig = {
  id: "com.pedropathing.panels.themes",
  name: "Themes",
  letterName: "T",
  description:
    "Plugin for Panels that implements custom theming with saving and preset sharing.",
  websiteURL: "https://panels.bylazar.com/docs/com.pedropathing.panels.themes/",
  version: "0.0.11",
  pluginsCoreVersion: "1.1.20",
  author: "Lazar",
  widgets: [
    {
      name: "Theme Manager",
      filepath: "src/widgets/Color.svelte",
    },
  ],
  navlets: [
    {
      name: "Colors Preview",
      filepath: "src/navlets/Preview.svelte",
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
