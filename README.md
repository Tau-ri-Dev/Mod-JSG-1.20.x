Public things from JSG 1.20


# **Just Stargate Mod**
***
Just Stargate Mod (JSG) is a Stargate-based Minecraft mod. It aims to closely resemble the franchise and be the most immersive Stargate mod out there.

[![Minecraft Versions](https://cf.way2muchnoise.eu/versions/For%20MC_537047_all.svg)](https://www.curseforge.com/minecraft/mc-mods/jsg/files)      [![Discord invite](https://img.shields.io/discord/881802052488011837?style=flat-square&label=%20&logo=discord&color=2D2D2D)](https://discord.justsgmod.eu) [![YouTube](https://img.shields.io/youtube/channel/subscribers/UChxSgOztJWUVqmw7TcZ-uMg?style=flat-square&label=%20&logo=youtube&logoColor=ff2129&color=2d2d2d)](https://www.youtube.com/channel/UChxSgOztJWUVqmw7TcZ-uMg) [![GitHub](https://img.shields.io/badge/-GitHub-2d2d2d?style=flat-square&logo=GitHub&logoColor=white)](https://github.com/MineDragonCZ/JSGMod) [![Website](https://img.shields.io/badge/-Website-2d2d2d?style=flat-square&logo=Pinboard&logoColor=00A95C)](https://justsgmod.eu/)
***
## **What is JSG?**
JSG is a mod originated on famous TV series called Stargate.
This mod adds devices like Stargates or Transport rings into the game.
You also will find big variety of materials for creating deviced mentioned before.
For example: Trinium, Titanium, Naquadah and more.
This mod also adds three Iris variants, so you can protect your base from unwanted guests.
***
## **How JSG works?**
JSG allows you to teleport anywhere in the world, even in other dimensions!
Transport rings work on short distance, like an elevator.
Tutorial can be found [here](https://www.youtube.com/watch?v=Adrj8sjAyC8).
If you prefer reading, you can try our [wiki](https://wiki.justsgmod.eu) (in 6 languages)
***
[![Trailer](https://img.youtube.com/vi/Ip-lWaQ3CnE/0.jpg)](https://www.youtube.com/watch?v=Ip-lWaQ3CnE)
***

> Created and coded by Tau'ri Dev Team<br>
> Models by MarcelMPL, **Harald de Luca** and from Aunis<br>
> Checkout our official website: [JustSGMod.eu](https://justsgmod.eu)<br>
> *Big thanks to MrJake222 for making base of this mod*<br>

# **Creating own JSG addon**
## Adding dependency
To create and addon you need to add our maven repository to your project:
``` gradle
repositories {
    maven {
        name = "jsg-api"
        url = "https://maven.justsgmod.eu/api/"
    }
}
```

And also add dependency:
``` gradle
dependencies {
    compileOnly "dev.tauri:jsg:[version]"
}
```

## Creating Loaders for Models and Textures
You can use our API to load custom OBJ models that are triangulated or custom (even custom sized or with custom format (png/jpg/jpeg)) textures.

Simply register your loaders:
``` java
public static final APIOBJLoader EXAMPLE_OBJ_LOADER = APIOBJLoader.createLoader(your mod id, main class of the mod);
public static final APITextureLoader EXAMPLE_TEXTURE_LOADER = APITextureLoader.createLoader(your mod id, main class of the mod);
```
You can check our example addon mod: https://github.com/Tau-ri-Dev/Example-JSG-1.20.x-Addon