![logo](mineonlinelogo.png)

Launch old versions of minecraft just as you remembered them, only without a browser.

## What is MineOnline?
MineOnline is a launcher for pre-release Minecraft versions capable of running web applets without a browser, and redirecting old web requests to a new API.

For example, if you wanted to play classic right now, you'd have no way to launch it without a lot of outdated vulnerable software and luck. And even if you pulled it off, you'd be running a stipped down version of the game, with no skins, no server authentication and no online map saving. MineOnline fixes this.

The program can also run regular desktop versions of the game, and even old launchers.

[Download](https://github.com/codieradical/MineOnline/releases)

## Features
These are features MineOnline will bring to pre-release Minecraft.

- Launcher Authentication and Updates

- Server Authentication (online-mode)

- Skins and Cloaks

- Server List

- Online World Saves

- Resource Files (Sounds)

- Resizable & Fullscreenable Applets

- Screenshots (F2)

![launcher](launcherdemo.png)

## Launching Servers
If you'd like to launch a server to authenticate using a different API, you can do so with a simple tweak to the typical launch command.
Add `-cp <MineOnline.jar path> gg.codie.mineonline.Server <server jar path>` after "java". Eg.

```java -Xmx1024M -Xms1024M -jar minecraft_server.jar nogui```

becomes

```java -cp MineOnline.jar gg.codie.mineonline.Server minecraft_server.jar -Xmx1024M -Xms1024M -jar minecraft_server.jar nogui```

If you don't want your server to appear on the list, set `dont-list: true` in `server.properties`.

## For Modders
You can add custom version information to the launcher by creating the file `.minecraft\mineonline\custom-version-info.json`.
This file should contain an array of JSON versions, like this:

```json
[
  { "name": "Skylands 0.1", "md5": "F8F78A4ED4033547CC1EA28C776DA7AE", "type": "client", "info": "Beta 1.7.3 mod" },
  { "name": "Skylands 0.1", "md5": "22D6B302995BA88549C98ED1996A5430", "type": "server", "info": "Beta 1.7.3 mod", "clientName": "Beta 1.7.3", "clientMd5s": ["F8F78A4ED4033547CC1EA28C776DA7AE", "EAE3353FDAA7E10A59B4CB5B45BFA10D"] }
]
```

If you would like a version to be added to the main list, contact me (Codie) and I can add it to the API.

## For Developers
As per the license you are welcome to use the launcher code under non-commercial conditions.

Note that the graphics code is VERY rough. I've never used LWJGL before, I've also never written game engine code on that level.
I might tidy things up in future, if I get time.

The GUI and menu code shall not be used for 'clone' projects (for instance, copying my API and pointing the launcher to it).

I also request that forks remain up to date for security.


## Credit

- I wouldn't have been able to make this LWJGL UI without [ThinMatrix](https://twitter.com/thinmatrix)'s LWJGL 2 tutorial.
