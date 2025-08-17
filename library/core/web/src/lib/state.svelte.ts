import { dev } from "$app/environment"
import {
  GlobalSocket,
  type Notification,
  NotificationsManager,
  type PluginConfig,
  type PluginInfo,
} from "ftc-panels"
import { importFromSource } from "../../../../../../ftcontrol-plugins/cli/core/socket/source"
import { PluginSocket } from "../../../../../../ftcontrol-plugins/cli/core/socket/plugin"
import { deleteValue, readValue, storeValue } from "./indexedDB"
import type { ExtendedTemplateEntry } from "./grid/widgets.svelte"

import pako from "pako"

function decompressDeflate(compressed: Uint8Array): string {
  const decompressed = pako.inflate(compressed)
  return new TextDecoder("utf-8").decode(decompressed)
}

export class GlobalState {
  plugins: PluginInfo[] = $state([])

  notifications: Notification[] = $state([])

  notificationsManager: NotificationsManager = new NotificationsManager()

  allTemplates = $derived.by(() => {
    let data: ExtendedTemplateEntry[] = []

    for (const plugin of this.plugins) {
      for (const t of plugin.details.templates) {
        const usedIDs: Set<string> = new Set([])
        const loadedPluginIDs = new Set(this.plugins.map((p) => p.details.id))

        for (const group of t.widgets) {
          for (const widget of group.widgets) {
            usedIDs.add(widget.pluginID)
          }
        }

        for (const navlet of t.navlets) {
          usedIDs.add(navlet.pluginID)
        }

        const missingPlugins = Array.from(usedIDs).filter(
          (id) => !loadedPluginIDs.has(id)
        )

        data.push({
          ...t,
          pluginID: plugin.details.id,
          missingPlugins,
        })
      }
    }

    return data
  })

  skippedPlugins: PluginConfig[] = $state([])
  socket: GlobalSocket = new GlobalSocket()

  isConnected = $state(false)

  updateInterval: ReturnType<typeof setInterval> | null = null

  private async getFromServer(serverURL: string, path: string): Promise<any> {
    const url = `${serverURL.replace(/\/+$/, "")}/${path.replace(/^\/+/, "")}`

    const timeout = 1000
    const interval = 200
    const maxAttempts = Math.ceil(timeout / interval)
    let attempt = 0

    while (attempt < maxAttempts) {
      try {
        const response = await fetch(url)
        if (!response.ok) {
          throw new Error(
            `Failed to fetch: ${response.status} ${response.statusText}`
          )
        }

        return await response.text()
      } catch (err) {
        attempt++
        if (attempt >= maxAttempts) {
          console.error(`Failed after ${attempt} attempts:`, err)
          throw err
        }
        await new Promise((res) => setTimeout(res, interval))
      }
    }

    throw new Error(`Unexpected error while fetching ${url}`)
  }

  interval: ReturnType<typeof setInterval> | null = null
  reloadIndexes: Record<string, number> = $state({})
  lastVersionNotificationTime: Record<string, number> = $state({})

  changedTimestamps: Record<string, number> = $state({})

  pluginsTemplatesPreviews: Record<string, string> = $state({})

  isPrepared = $derived.by(() => {
    if (!this.isConnected) return false

    for (const plugin of this.plugins) {
      for (const t of plugin.details.templates) {
        if (!this.pluginsTemplatesPreviews[`${plugin.details.id}/${t.name}`]) {
          return false
        }
      }
    }

    return true
  })

  devPlugins: string[] = $state([])

  hasDevServer = $state(false)

  createDevServerInterval() {
    if (this.interval !== null) {
      clearInterval(this.interval)
    }

    this.interval = setInterval(
      async () => {
        await this.updateDevPlugins(true)
      },
      this.hasDevServer ? 1500 : 10000
    )
  }

  private async updateDevPlugins(reloadManager = false) {
    type LiveChangeEntry = {
      id: string
      name: string
      lastChanged: number
    }

    let livePlugins: LiveChangeEntry[]

    try {
      const res = await this.fetchWithRetry(
        "http://localhost:3001/plugins",
        {},
        1
      )
      livePlugins = await res.json()
      if (!this.hasDevServer) {
        this.createDevServerInterval()
      }
      this.hasDevServer = true
    } catch (error) {
      console.error("Failed to fetch live plugins:", error)
      if (this.hasDevServer) {
        this.createDevServerInterval()
      }
      this.hasDevServer = false
      return
    }

    for (const entry of livePlugins) {
      if (!this.devPlugins.includes(entry.id)) {
        this.devPlugins.push(entry.id)
      }
      if (entry.lastChanged != this.changedTimestamps[entry.id]) {
        console.log("Rebuilding", entry.name)

        const res = await this.fetchWithRetry(
          `http://localhost:3001/plugins/${entry.id}`,
          {},
          1
        )
        let details = await res.json()

        if (reloadManager) {
          const { default: Manager } = await importFromSource(
            details.manager.textContent || ""
          )
          const oldStateData = this.socket.pluginManagers[details.id].state.data
          this.socket.pluginManagers[details.id] = new Manager(
            new PluginSocket(details.id, this.socket),
            details,
            this.notificationsManager
          )
          this.socket.pluginManagers[details.id].state.data = oldStateData
          for (const item of Object.values(
            this.socket.pluginManagers[details.id].state.data
          )) {
            for (const callback of item.callbacks) {
              callback(item.value)
            }
          }
          this.socket.pluginManagers[details.id]?.onInit()
        }

        for (const t of details.templates) {
          const cacheKey = `${entry.id}/${t.name}`
          delete this.pluginsTemplatesPreviews[cacheKey]
        }

        for (const plugin of this.plugins) {
          if (plugin.details.id == entry.id) {
            plugin.details = details
          }
        }

        this.reloadIndexes[details.id]++

        console.log("Reloaded plugin", entry.id)

        this.changedTimestamps[entry.id] = entry.lastChanged
      }
    }
  }

  async hasInternetConnection(): Promise<boolean> {
    try {
      const response = await fetch(
        "https://raw.githubusercontent.com/lazarcloud/ftcontrol-maven/refs/heads/main/dev/com/bylazar/panels/maven-metadata.xml",
        {
          cache: "no-cache",
        }
      )
      return response.ok
    } catch (e) {
      return false
    }
  }

  async init() {
    const startTime = Date.now()
    console.log(`[init] Starting initialization...`)

    try {
      this.plugins = []

      this.hasDevServer = false

      this.notifications = []

      this.notificationsManager = new NotificationsManager()

      this.skippedPlugins = []
      this.socket = new GlobalSocket()

      this.notificationsManager.callbacks = []
      this.notificationsManager.onUpdate((newValue) => {
        this.notifications = newValue
      })
      this.notifications = this.notificationsManager.data
      this.isConnected = false

      const t0 = Date.now()
      const data = await this.getPluginsUntilReady()
      console.log(`[init] getPluginsUntilReady() took ${Date.now() - t0}ms`)

      const parsed = JSON.parse(data)

      this.plugins = parsed.data.plugins

      console.log(`[init] Loaded ${this.plugins.length} plugins`)
      this.plugins.forEach((item) => {
        this.reloadIndexes[item.details.id] = 0
        this.changedTimestamps[item.details.id] = 0
        this.lastVersionNotificationTime[item.details.id] = 0
      })

      this.skippedPlugins = parsed.data.skippedPlugins
      console.log(`[init] Skipped ${this.skippedPlugins.length} plugins`)

      const t1 = Date.now()

      if (this.interval !== null) {
        clearInterval(this.interval)
      }

      await this.socket.initPlugins(this.plugins, this.notificationsManager)

      await this.updateDevPlugins(true)

      this.createDevServerInterval()
      console.log(`[init] Dev plugin interval set up`)

      await this.socket.initSocket(async () => {
        // await this.init()
        window.location.reload()
      })
      console.log(`[init] socket.init() took ${Date.now() - t1}ms`)

      this.isConnected = true

      if (this.updateInterval !== null) {
        clearInterval(this.updateInterval)
      }

      this.updateInterval = setInterval(async () => {
        const hasInternet = await this.hasInternetConnection()
        if (!hasInternet) return
        console.log("Has internet")
        await this.checkVersions()
      }, 10000)

      console.log(
        `[init] Initialization complete in ${Date.now() - startTime}ms`
      )

      if (
        this.plugins
          .map((it) => it.details.id)
          .includes("com.pedropathing.panels.exampleplugin")
      ) {
        this.notificationsManager.addAction("Don't use default plugin id", [
          {
            text: "OK",
            task: () => {},
          },
          {
            text: "Details",
            task: () => {},
          },
        ])
      }
    } catch (e) {
      console.error(`[init] Error during initialization:`, e)
      // await this.init()
      window.location.reload()
    }
  }

  close() {
    this.isConnected = false
    if (this.interval) clearInterval(this.interval)
    this.socket.close()
  }

  private async getPluginsUntilReady(): Promise<string> {
    var currentSha = await this.getSha()

    var cachedSha = await readValue("sha256")
    var cachedText = await readValue("plugins")
    var oldPanelsVersion = await readValue("version")

    if (oldPanelsVersion !== this.panelsVersion) {
      await deleteValue("sha256")
      await deleteValue("plugins")
      await storeValue("version", this.panelsVersion)
      throw Error("Panels version changed")
    }

    if (currentSha == cachedSha && cachedText) {
      setTimeout(async () => {
        const extraText = await this.getPlugins()

        if (extraText == null) return
        if (extraText == cachedText) return
        await storeValue("sha256", currentSha)
        await storeValue("plugins", extraText)
        await storeValue("version", this.panelsVersion)
        // await this.init()
        window.location.reload()
      }, 100)
      return cachedText
    }

    await storeValue("sha256", currentSha)

    const text = await this.getPlugins()

    await storeValue("plugins", text)
    await storeValue("version", this.panelsVersion)

    return text
  }

  private fetchWithTimeout(url: string, options = {}, timeout = 5000) {
    const controller = new AbortController()
    const timer = setTimeout(() => controller.abort(), timeout)

    return fetch(url, { ...options, signal: controller.signal }).finally(() =>
      clearTimeout(timer)
    )
  }

  private fetchWithRetry(
    url: string,
    options = {},
    retries = 3,
    timeout = 1000,
    delay = 250
  ): Promise<Response> {
    return this.fetchWithTimeout(url, options, timeout).catch((error) => {
      if (retries > 0) {
        console.warn(`Retrying... (${retries} left), Error: ${error.message}`)
        return new Promise((resolve) => setTimeout(resolve, delay)).then(() =>
          this.fetchWithRetry(url, options, retries - 1, timeout, delay * 2)
        )
      }
      throw error
    })
  }

  private async getSha(attempts = 0): Promise<string> {
    if (attempts > 5) {
      throw Error("Tried too many times.")
    }
    const url = dev ? "http://localhost:8001" : window.location.origin

    try {
      const response = await this.fetchWithRetry(`${url}/api/sha256`, {})

      const sha = await response.text()

      if (sha && sha.trim() != "null") {
        return sha
      }
      return this.getSha(attempts + 1)
    } catch (err) {
      console.warn("Fetch failed, retrying...", err)
      throw err
    }
  }

  private async getPlugins(): Promise<string> {
    const url = dev ? "http://localhost:8001" : window.location.origin

    const response = await this.fetchWithRetry(
      `${url}/api/plugins`,
      {},
      5,
      4000,
      500
    )

    const buffer = await response.arrayBuffer()
    const uint8Array = new Uint8Array(buffer)

    const startTime = performance.now()
    const text = decompressDeflate(uint8Array)
    const endTime = performance.now()
    console.log(`Decompression took ${(endTime - startTime).toFixed(2)} ms`)

    return text
  }

  panelsVersion = "0.0.21"

  async getLatestVersion(): Promise<string> {
    try {
      const response = await fetch(
        `https://raw.githubusercontent.com/lazarcloud/ftcontrol-maven/refs/heads/main/dev/com/bylazar/panels/maven-metadata.xml`
      )
      if (!response.ok) throw new Error(`HTTP ${response.status}`)

      const xmlText = await response.text()
      const parser = new DOMParser()
      const xmlDoc = parser.parseFromString(xmlText, "application/xml")

      const latestVersion = xmlDoc.querySelector("latest")?.textContent

      return latestVersion || ""
    } catch (error) {
      return ""
    }
  }

  async checkVersions() {
    const combinedPlugins = [
      "com.pedropathing.panels.battery",
      "com.pedropathing.panels.capture",
      "com.pedropathing.panels.configurables",
      "com.pedropathing.panels.docs",
      "com.pedropathing.panels.field",
      "com.pedropathing.panels.gamepad",
      "com.pedropathing.panels.limelightproxy",
      "com.pedropathing.panels.opmodecontrol",
      "com.pedropathing.panels.telemetry",
      "com.pedropathing.panels.themes",
      "com.pedropathing.panels.utils",
      "com.pedropathing.panels.pinger",
      //TODO: fill here
    ]
    let isCombined = false
    for (const plugin of this.plugins) {
      if (plugin.details.id == "com.pedropathing.panels.fullpanels") {
        isCombined = true
      }
    }

    console.log("isCombined", isCombined)

    for (const plugin of this.plugins) {
      const id = plugin.details.id

      const lastTime = this.lastVersionNotificationTime[id] ?? 0

      if (Date.now() - lastTime < 15 * 60 * 1000) continue

      if (isCombined && combinedPlugins.includes(id)) continue
      const manager = this.socket.pluginManagers[id]

      console.log("Checking plugin version", id, manager.config.version)

      const version = await manager.getNewVersion()
      var hasVersion = version != manager.config.version
      if (version == "") {
        hasVersion = false
      }

      if (version != "") {
        this.lastVersionNotificationTime[id] = Date.now()
      }

      if (hasVersion) {
        this.notificationsManager.addAction(
          `Plugin ${id} has a new version: ${version}`,
          [
            {
              text: "Check Website",
              task: () => {
                window.open(plugin.details.websiteURL, "_blank")
              },
            },
            {
              text: "Remind me later",
              task: () => {},
            },
          ]
        )
      } else {
        console.log(`Plugin ${id} is latest`)
      }
    }

    if (!isCombined) {
      const lastTime = this.lastVersionNotificationTime["panels"] ?? 0
      if (Date.now() - lastTime < 15 * 60 * 1000) return
      const version = await this.getLatestVersion()
      if (version != "") {
        this.lastVersionNotificationTime["panels"] = Date.now()
      }
      if (version != this.panelsVersion && version != "") {
        this.notificationsManager.addAction(
          `Panels has a new version: ${version}`,
          [
            {
              text: "Check Website",
              task: () => {
                window.open("https://panels.bylazar.com", "_blank")
              },
            },
            {
              text: "Remind me later",
              task: () => {},
            },
          ]
        )
      } else {
        console.log(`Panels is latest`)
      }
    }
  }
}
