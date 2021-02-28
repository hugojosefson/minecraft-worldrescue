# WorldRescue

<img alt="WorldRescue logo" src="https://cdn.jsdelivr.net/gh/hugojosefson/minecraft-worldrescue/logo/vector/default.svg" height="100" align="right">

Plugin for Bukkit compatible Minecraft servers, to easily backup and restore your worlds.

This plugin now works with Minecraft API v1.16.

## Download

Download the latest release:
[WorldRescue.jar](https://github.com/hugojosefson/minecraft-worldrescue/releases/latest/download/WorldRescue.jar).

## Credit

Based on the excellent [WorldRebuild](https://www.curseforge.com/minecraft/bukkit-plugins/worldrebuild) plugin from
2014, by _tobynextdoor_. I try to keep the same commands in WorldRescue, that were available in WorldRebuild. Even the
short-command `/wr` is the same, thanks to the similarity of the two plugins' names :)

## TODO/TODONE

- [x] Download the `WordRebuild.jar` file, version 1.2.4 from 2014-FEB-18.
- [x] Decompile `WordRebuild.jar`.
- [x] Create `pom.xml`.
- [x] Update according to new APIs, so it compiles.
- [x] `java -jar WorldRebuild.jar` should output the version and some instructions.
- [x] `java -jar WorldRebuild.jar --version` should output the version.
- [x] Refactor a little, to acquaint myself with the code.
- [x] Declare `api-version: 1.16`. Bump plugin version to `2.0.0` because it's a breaking change according
  to [semver](https://semver.org/).
- [x] Make IO code more stable.
- [ ] Upgrade implementations with Java 8 and Apache Commons libs.
- [x] Make sure it works in an up-to-date Spigot server.
- [ ] Make sure it works in an up-to-date Bukkit server.
- [x] Make sure it works in an up-to-date Paper server.
- [x] Get OK from the original author *tobynextdoor* of WorldRebuild, to go ahead and use their code as base.
- [x] Rename as different plugin: WorldRescue.
- [x] Make a first release.
