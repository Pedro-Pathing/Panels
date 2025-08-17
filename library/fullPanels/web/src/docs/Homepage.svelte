<script lang="ts">
    import type {PluginConfig} from "ftc-panels"
    import {Title, Image, Paragraph, Accent, OrderedList, ListItem} from "ftc-panels/docs"

    let {
        info,
    }: {
        info: PluginConfig
    } = $props()

    import image from "./image.png"

    import Manager from "./../manager"
</script>

<Title level={1}>Installation</Title>

{#await Manager.getNewVersion()}
    <Paragraph>Loading</Paragraph>
{:then data}
    <Paragraph>Replace basic Panels Installation with:</Paragraph>
    <Paragraph><Accent>{info.id}:{data}</Accent></Paragraph>
{:catch error}
    <Paragraph>Please check you connection: {error}</Paragraph>
{/await}

<Title  level={1}>What's FullPanels?</Title>

<Paragraph>
    This plugin conveniently bundles all the essential core plugins, simplifying installation and setup.
</Paragraph>

<OrderedList>
    <ListItem>com.pedropathing.panels.battery</ListItem>
    <ListItem>com.pedropathing.panels.capture</ListItem>
    <ListItem>com.pedropathing.panels.configurables</ListItem>
    <ListItem>com.pedropathing.panels.docs</ListItem>
    <ListItem>com.pedropathing.panels.exampleplugin</ListItem>
    <ListItem>com.pedropathing.panels.field</ListItem>
    <ListItem>com.pedropathing.panels.gamepad</ListItem>
    <ListItem>com.pedropathing.panels.limelightproxy</ListItem>
    <ListItem>com.pedropathing.panels.opmodecontrol</ListItem>
    <ListItem>com.pedropathing.panels.telemetry</ListItem>
    <ListItem>com.pedropathing.panels.themes</ListItem>
    <ListItem>com.pedropathing.panels.utils</ListItem>
    <ListItem>com.pedropathing.panels.pinger</ListItem>
</OrderedList>

<Title level={1}>Templates</Title>

<Paragraph>
    It also includes core templates alongside the essential plugins, so you don’t have to start from scratch when designing your UI.
</Paragraph>

<Image src={image} alt={"Templates"}/>