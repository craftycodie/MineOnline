# MineOnline
Launch old versions of minecraft just as you remembered them, only without a browser.

## New: Test MineOnline
If you'd like to play around with this during early stages of development, I'd recommend joining my Discord server, where I regularly post updates and instructions, as well as known issues and planned features.
https://discord.codie.gg/

## What is MineOnline?
MineOnline is a launcher for pre-release Minecraft versions capable of running web applets without a browser, and redirecting old web requests to a new API.

For example, if you wanted to play classic right now, you'd have no way to launch it without a lot of outdated vulnerable software and luck. And even if you pulled it off, you'd be running a stipped down version of the game, with no skins, no server authentication and no online map saving. MineOnline fixes this.

The program can also run regular desktop versions of the game, and even old launchers.

## Features
These are features MineOnline will bring to pre-release Minecraft.

- Launcher Authentication and Updates

- Server Authentication (online-mode)

- Skins and Cloaks

- Server List

- Online World Saves

- Resource Files (Sounds)

- Resizable Applets

- Screenshots (F2)

## Launching Servers
If you'd like to launch a server to authenticate using a different API, you can do so with a simple tweak to the typical launch command.
Add `-cp <MineOnline.jar path> gg.codie.mineonline.Server` after "java". Eg.

```java -Xmx1024M -Xms1024M -jar minecraft_server.jar nogui```

becomes

```java -cp MineOnline.jar gg.codie.mineonline.Server -Xmx1024M -Xms1024M -jar minecraft_server.jar nogui```

## Credit

- I wouldn't have been able to make this LWJGL UI without [ThinMatrix](https://twitter.com/thinmatrix?lang=en)'s LWJGL 2 tutorial.
- Version information retrieved from Omniarchive.
