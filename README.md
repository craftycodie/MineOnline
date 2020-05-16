# MineOnline
Launch old versions of minecraft just as you remembered them, only without a browser.

## New: Test MineOnline
If you'd like to play around with this during early stages of development, I'd recommend joining my Discord server, where I regularly post updates and instructions, as well as known issues and planned features.
https://discord.gg/z3HNFH

## What is MineOnline?
MineOnline is a launcher for pre-release Minecraft versions capable of running web applets without a browser, and redirecting old web requests to a new API.

For example, if you wanted to play classic right now, you'd have no way to launch it without a lot of outdated vulnerable software and luck. And even if you pulled it off, you'd be running a stipped down version of the game, with no skins, no server authentication and no online map saving. MineOnline fixes this.

The program can also run regular desktop versions of the game, and even old launchers.

## How do I use MineOnline?
While you can use this launcher just to play applets offline, well It's called MineOnline for a reason.
The application was build for use with [my Minecraft API project](https://github.com/codieradical/Minecraft-API), it might work with other reimplementations too.

1. Select an API domain name.
  - This is the API you want Minecraft to talk to. Defaults to "mineonline.codie.gg".
2. Select your jar.
  - Select a jar using the browse button or by entering the path. Once it's been found, the applet and game classes should fill up.
  - If applet and game class are empty, enter the main class into the relevant box. eg com.mojang.minecraft.MinecraftApplet.
  - Note: in this current prototype release, the launcher expects retro install folders (bin folder containing lwgjl etc and natives).
3. Select a username.
- Authentication
  - If you wish to authenticate to use online features like world saving or online mode servers, enter your password into the password field and press login. This will fetch your Session ID.
  - THIS IS NOT YOUR MOJANG PASSWORD! You are logging into the specified API. If you're confused, click "Need Acccount?".
- Joining Servers
  - If you're using an old applet capable of joining servers, you can enter the details to join.
  - If the server is online, you will need to get a Server Authentication Token. This requires a session ID and a server IP.
- Base URL
  - Some versions of the game require changes to the base URL field. More info on this later.

## Credit

- Applet bootstrap code derived from Videogamer555's MinecraftAppletLauncher.
