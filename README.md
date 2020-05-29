![logo](mineonlinelogo.png)

Launch old versions of minecraft just as you remembered them, only without a browser.

## New: Test MineOnline
If you'd like to play around with this during early stages of development, I'd recommend joining my Discord server, where I regularly post updates and instructions, as well as known issues and planned features.
https://discord.gg/z3HNFH

## What is MineOnline?
MineOnline is a launcher for pre-release Minecraft versions capable of running web applets without a browser, and redirecting old web requests to a new API.

For example, if you wanted to play classic right now, you'd have no way to launch it without a lot of outdated vulnerable software and luck. And even if you pulled it off, you'd be running a stipped down version of the game, with no skins, no server authentication and no online map saving. MineOnline fixes this.

The program can also run regular desktop versions of the game, and even old launchers.

## Features
These are features MineOnline will bring to pre-release Minecraft.

- Launcher Authentication and Updates

- Server Authentication (online-mode)

- Skins and Cloaks

- Classic Server List

- Online World Saves

- Resource Files (Sounds)

- Resizable Applets

- Screenshots (F2)

## How do I use MineOnline?
While you can use this launcher just to play applets offline, well It's called MineOnline for a reason.
The application was build for use with [my Minecraft API project](https://github.com/codieradical/Minecraft-API), it might work with other reimplementations too.

1. Register
  - You can do this at http://mineonline.codie.gg/register.jsp
  - if you're planning on using a different API endpoint you don't need to register here.
2. Download a release and login.
3. Select your jar.
  - Settings -> Game Configuration to add Minecraft jars.
3. Play
  - Press play on the main screen to launch single player or go to the join server screen to play multiplayer.

## Launching Servers
If you'd like to launch a server to authenticate using a different API, you can do so with a simple tweak to the typical launch command.
Add `-cp <MineOnline.jar path> gg.codie.mineonline.Server <server jar path>` after "java". Eg.

```java -Xmx1024M -Xms1024M -jar minecraft_server.jar nogui```

becomes

```java -cp MineOnline.jar gg.codie.mineonline.Server minecraft_server.jar -Xmx1024M -Xms1024M -jar minecraft_server.jar nogui```
