# WorldRebuild

Trying to resurrect the WorldRebuild plugin for Bukkit compatible Minecraft servers.

Apparently, it's GPLv3 licensed according to
[https://dev.bukkit.org/projects/worldrebuild](https://dev.bukkit.org/projects/worldrebuild)
([archived 2020-AUG-08](https://web.archive.org/web/20200808063349/https://dev.bukkit.org/projects/worldrebuild)).
I assume the plugin author created that plugin page, and was the one who selected the license.

## TODO/TODONE

- [x] Download the `WordRebuild.jar` file, version 1.2.4 from 2014-FEB-18.
- [x] Decompile `WordRebuild.jar`.
- [x] Create `pom.xml`.
- [x] Update according to new APIs, so it compiles.
- [x] `java -jar WorldRebuild.jar` should output the version and some instructions.
- [x] `java -jar WorldRebuild.jar --version` should output the version.
- [x] Refactor a little, to acquaint myself with the code.
- [x] Declare `api-version: 1.16`. Bump plugin version to `2.0.0` because it's a breaking change according to [semver](https://semver.org/).
- [ ] Upgrade implementations with Java 8 and Apache Commons libs.
- [ ] Make sure it works in an up-to-date Bukkit, Spigot and Paper server.
- [ ] Attempt to contact the original author *tobynextdoor*, to present my findings.
