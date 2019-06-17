package main

import "os"
import "fmt"
import "os/exec"
import "strings"
import "github.com/tidwall/gjson"
import "github.com/logrusorgru/aurora"

func ArgsParse(Args []string) {

  // If Nonthing is set then default to --help
  if len(Args) <= 1 {
    Help()
  } else {
    // Args is 1 or longer so parse time!
    if Args[1] == "--help" {
      Help()
    }
    if Args[1] == "--status" {
      Status()
    }

    if Args[1] == "--add-account" {
      // Make sure there are enough args
      if len(Args) <= 2 {
        // Not enough Args to add an account
        fmt.Println("Run `./Mcboop --help` for help")
      } else {
        // Enough args to add an account
        AddAccount(AuthAccount(Args[2], Args[3]))
      }
    }
    if Args[1] == "--list-accounts" {
      // Verify that accounts.json is there!
      if !CheckForFile(GetMcBoopDir() + "accounts.json") {
        fmt.Println("Accounts json not found!")
        os.Exit(0)
      }
      users := ListAccounts()
      fmt.Println("")
      fmt.Println("Current added accounts")
      fmt.Println("================")
      for i := 0; i < len(users); i++ {
        fmt.Println(users[i])
      }
      fmt.Println("================")
      fmt.Println("")
    }

    if Args[1] == "--list-mc-versions" {
      release, snapshot, list := ListMCVersions()
      fmt.Println("")
      fmt.Println("Here are a list of playable versions")
      fmt.Println("")
      for i := 0; i < len(list); i++ {
        if (i + 1) >= len(list) {
          fmt.Print(list[i])
        } else {
          fmt.Print(list[i] + ", ")
        }
      }
      fmt.Println("")
      fmt.Println("")
      fmt.Println("Current", aurora.Green("stable"), "is:", aurora.Green(release))
      fmt.Println("Current", aurora.Yellow("snapshot"), "is:", aurora.Yellow(snapshot))
      fmt.Println("")
    }
    if Args[1] == "--run" {
      account_name := GetDefaultAccount()

      // Now refresh login token
      RefreshAccount(account_name)

      //Get version meta
      manifest := GetRemoteText("https://launchermeta.mojang.com/mc/game/version_manifest.json")
      requested_version := Args[2]
      if Args[2] == "stable" || Args[2] == "snapshot" {
        if Args[2] == "stable" {
          requested_version = gjson.Get(manifest, "latest.release").String()
        }
        if Args[2] == "snapshot" {
          requested_version = gjson.Get(manifest, "latest.snapshot").String()
        }
      }

      // Get version meta
      version_meta := ""
      for i := 0; i < len(gjson.Get(manifest, "versions").Array()); i++ {
        if gjson.Get(manifest, "versions").Array()[i].Get("id").String() == requested_version {
          version_meta = GetRemoteText(gjson.Get(manifest, "versions").Array()[i].Get("url").String())
        }
      }

      // Now install/verify assets
      InstallAssets(gjson.Get(version_meta, "assetIndex.url").String(), gjson.Get(version_meta, "assets").String())

      // Now install/verify Libs
      libs, nativelibs := InstallLibs(gjson.Get(version_meta, "libraries").Array())

      // Download the client jar
      InstallClient(gjson.Get(version_meta, "downloads.client"), gjson.Get(version_meta, "id").String())

      // Extract natives
      nativesfolder := ExtractNatives(nativelibs)

      // Generate Launch Command
      full_libs_list := []string{}
      full_libs_list = append(full_libs_list, GetMcBoopDir() + "client/" + gjson.Get(version_meta, "id").String() + "/" + gjson.Get(version_meta, "id").String() + ".jar")
      full_libs_list = append(full_libs_list, libs...)
      full_libs_list = append(full_libs_list, nativelibs...)

      game_launch_cmd := []string{"-Djava.library.path=" + nativesfolder, "-cp", strings.Join(full_libs_list, ":"), gjson.Get(version_meta, "mainClass").String()}
      game_launch_cmd = append(game_launch_cmd, GenLaunchArgs(version_meta, account_name)...)

      // Ensure the profile folder is there
      os.MkdirAll(GetMcBoopDir() + "default/", os.ModePerm)

      // Run the game!
      mc := exec.Command(GetMcBoopDir() + "java/bin/java", game_launch_cmd...)
      mc.Stdout = os.Stdout
      mc.Stderr = os.Stderr
      mc.Dir = GetMcBoopDir() + "default/"

      fmt.Println("")
      fmt.Println("Game logging starts here")
      fmt.Println("")
      mc.Run()


      os.RemoveAll(nativesfolder)
    }
  }
}

func Help() {
  // System will exit when you call this!
  fmt.Println("")
  fmt.Println("McBoop Help")
  fmt.Println("")
  fmt.Println("./McBoop --help => Shows this page.")
  fmt.Println("")
  fmt.Println("./McBoop --status => Shows mojang status.")
  fmt.Println("")
  fmt.Println("./McBoop --add-account <username> <password> => Add a new account, The first account added becomes the default. ( This can be changed later ) ")
  fmt.Println("")
  fmt.Println("./Mcboop --list-accounts => List all saved accounts.")
  fmt.Println("")
  fmt.Println("./McBoop --list-mc-versions => List all playable MC versions.")
  fmt.Println("")
  fmt.Println("./Mcboop --run <version> => Runs Minecraft <version>.")
  os.Exit(0)
}

func Status() {
  // System will exit upon calling this
  status := gjson.Parse(GetRemoteText("https://status.mojang.com/check"))
  fmt.Println("")
  fmt.Println("Minecraft server status")
  fmt.Println("")
  fmt.Println("Minecraft.net status:", StatusColor(status.Get("#.minecraft\\.net").Array()[0].String()))
  fmt.Println("Session.Minecraft.net status:", StatusColor(status.Get("#.session\\.minecraft\\.net").Array()[0].String()))
  fmt.Println("Textures.Minecraft.net Status:", StatusColor(status.Get("#.textures\\.minecraft\\.net").Array()[0].String()))
  fmt.Println("Mojang.com status:", StatusColor(status.Get("#.mojang\\.com").Array()[0].String()))
  fmt.Println("Account.Mojang.com status:", StatusColor(status.Get("#.account\\.mojang\\.com").Array()[0].String()))
  fmt.Println("Authserver.Mojang.com status:", StatusColor(status.Get("#.authserver\\.mojang\\.com").Array()[0].String()))
  fmt.Println("Sessionserver.Mojang.com status:", StatusColor(status.Get("#.sessionserver\\.mojang\\.com").Array()[0].String()))
  fmt.Println("Api.Mojang.com status:", StatusColor(status.Get("#.api\\.mojang\\.com").Array()[0].String()))
  os.Exit(0)
}
func StatusColor(Status string) (aurora.Value){
  // Add some color to the status page
  ans := aurora.White(Status)
  if Status == "green" {
    ans = aurora.Green("Green")
  }

  if Status == "yellow" {
    ans = aurora.Yellow("Yellow")
  }

  if Status == "red" {
    ans = aurora.Red("Red")
  }

  return ans
}

func ListMCVersions() (string, string, []string) {
  versions := gjson.Parse(GetRemoteText("https://launchermeta.mojang.com/mc/game/version_manifest.json"))
  var list []string
  for i := 0; i < len(versions.Get("versions.#.id").Array()); i++ {
    list = append(list, versions.Get("versions.#.id").Array()[i].String())
  }
  return versions.Get("latest.release").String(), versions.Get("latest.snapshot").String(), list
}
