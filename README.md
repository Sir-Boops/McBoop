# McBoop

The fastest ( I've seen ) Minecraft launcher

This launcher is designed around using commands, This allows easy scripting letting you to have for example a shell script/batch file on your desktop that when run would always launch the latest snapshot/stable version even as it get updated!

This launcher also fully supports forge for MC 1.6.4+ and profiles allowing you to easily run your or your friends modpacks without having to use the cursed launcher that shall not be named! ( For pulling packs off curseforge see [CurseDownloader](https://git.sergal.org/Sir-Boops/CurseDownloader) )

Things the launcher can do:
* Run any version of MC offically listed
* Use multiple accounts at once
* Run multiple copies of Minecraft at once
* Run using predefined profiles
* Run Forge modpacks

How to use
---
* Clone the repo `git clone https://git.sergal.org/Sir-Boops/McBoop.git`
* enter the repo and build the launcher `cd McBoop && ./gradlew build`
* Add the default user to the launcher `java -jar build/libs/McBoop.jar --add-account <email> <password>`
* Finally run a version of Minecraft! `java -jar build/libs/McBoop.jar --run stable`
* More help can be viewed by running `java -jar build/libs/McBoop.jar --help`
