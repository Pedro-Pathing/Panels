import type { PluginConfig } from "ftc-panels"

export const config: PluginConfig = {
  id: "com.pedropathing.panels.fullpanels",
  name: "Full Panels",
  letterName: "FP",
  description: "Full Panels Installation",
  websiteURL: "https://panels.bylazar.com/docs/com.pedropathing.panels.fullpanels/",
  version: "0.0.40",
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
    chapters: [],
  },
  templates: [
    {
      name: "Default",
      navlets: [
        { navletID: "Battery", pluginID: "com.pedropathing.panels.battery" },
        { navletID: "Ping", pluginID: "com.pedropathing.panels.pinger" },
      ],
      widgets: [
        {
          x: 0,
          y: 0,
          w: 5,
          h: 4,
          widgets: [
            {
              pluginID: "com.pedropathing.panels.opmodecontrol",
              widgetID: "OpModes Control",
            },
          ],
        },
        {
          x: 5,
          y: 0,
          w: 6,
          h: 12,
          widgets: [
            {
              pluginID: "com.pedropathing.panels.configurables",
              widgetID: "Configurables",
            },
            {
              pluginID: "com.pedropathing.panels.configurables",
              widgetID: "ChangedConfigurables",
            },
          ],
        },
        {
          x: 0,
          y: 4,
          w: 5,
          h: 8,
          widgets: [
            { pluginID: "com.pedropathing.panels.gamepad", widgetID: "Combined Gamepad" },
            { pluginID: "com.pedropathing.panels.capture", widgetID: "Capture" },
          ],
        },
        {
          x: 11,
          y: 0,
          w: 5,
          h: 9,
          widgets: [{ pluginID: "com.pedropathing.panels.field", widgetID: "Field" }],
        },
        {
          x: 11,
          y: 9,
          w: 5,
          h: 3,
          widgets: [
            { pluginID: "com.pedropathing.panels.telemetry", widgetID: "Telemetry" },
          ],
        },
      ],
    },
  ],
}
