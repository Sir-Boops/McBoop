package main

import "os"
import "fmt"
import "os/exec"
import "strings"
import "io/ioutil"
import "github.com/tidwall/gjson"
import "github.com/logrusorgru/aurora"

func ArgsParse() {

  // If Nonthing is set then default to --help
  if len(os.Args) <= 1 {
    Help()
  } else {
    // Args is 1 or longer so parse time!
    // Order here is important!

    if os.Args[1] == "--help" {
      Help()
    }
    if os.Args[1] == "--status" {
      Status()
    }
    if os.Args[1] == "--list-profiles" {
      ListProfiles()
    }

    if os.Args[1] == "--add-account" {
      // Make sure there are enough args
      if len(os.Args) <= 2 {
        // Not enough Args to add an account
        fmt.Println("Run `./Mcboop --help` for help")
      } else {
        // Enough args to add an account
        auth := AuthAccount(os.Args[2], os.Args[3])
        AddAccount(auth)
        fmt.Println("Added account:", gjson.Get(auth, "selectedProfile.name"))
      }
    }
    if os.Args[1] == "--list-accounts" {
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
        if users[i] == GetDefaultAccount() {
          fmt.Println(users[i], aurora.Green("Default"))
        } else {
          fmt.Println(users[i])
        }
      }
      fmt.Println("================")
      fmt.Println("")
    }

    if os.Args[1] == "--list-mc-versions" {
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
    if os.Args[1] == "--run" {
      account_name := GetDefaultAccount()

      // Check for optional account name
      for i := 0; i < len(os.Args); i++ {
        if os.Args[i] == "--user" {
          account_name = os.Args[i + 1]
          break
        }
      }

      // Get custom profile path
      profile_path := GetMcBoopDir() + "profiles/default/"
      for i := 0; i < len(os.Args); i++ {
        if os.Args[i] == "--profile" {
          profile_path = GetMcBoopDir() + "profiles/" + os.Args[i + 1] + "/"
        }
      }

      // Check if we should deal with forge or not
      forge := false
      for i := 0; i < len(os.Args); i++ {
        if os.Args[i] == "--forge" {
          forge = true
        }
      }

      // Ensure the profile folder is there
      os.MkdirAll(profile_path, os.ModePerm)

      // Now refresh login token
      RefreshAccount(account_name)

      //Get version meta
      manifest := GetRemoteText("https://launchermeta.mojang.com/mc/game/version_manifest.json")
      requested_version := os.Args[2]
      if os.Args[2] == "stable" || os.Args[2] == "snapshot" {
        if os.Args[2] == "stable" {
          requested_version = gjson.Get(manifest, "latest.release").String()
        }
        if os.Args[2] == "snapshot" {
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
      fmt.Println("==== Installing/Verfiying Assets ====")
      InstallAssets(gjson.Get(version_meta, "assetIndex.url").String(), gjson.Get(version_meta, "assets").String(), profile_path)
      fmt.Println("")

      // Now install/verify Libs
      fmt.Println("==== Installing/Verifying Libraries ====")
      libs, nativelibs := InstallLibs(gjson.Get(version_meta, "libraries").Array())
      fmt.Println("")

      // Extract natives
      fmt.Println("==== Extracting Native Libraries ====")
      nativesfolder := ExtractNatives(nativelibs)
      fmt.Println("")

      // Download the client jar
      fmt.Println("==== Installing/Verifying Client ====")
      InstallClient(gjson.Get(version_meta, "downloads.client"), gjson.Get(version_meta, "id").String())
      fmt.Println("")

      // Start defining libs
      full_libs_list := []string{}
      full_libs_list = append(full_libs_list, GetMcBoopDir() + "client/" + gjson.Get(version_meta, "id").String() + "/" + gjson.Get(version_meta, "id").String() + ".jar")

      // Define some args that can go missing with forge
      asset_index := gjson.Get(version_meta, "assets").String()

      // Install forge
      if forge {
        var forge_libs []string
        version_meta, forge_libs = CreateForgeJar(GetMcBoopDir() + "client/" + gjson.Get(version_meta, "id").String() + "/" + gjson.Get(version_meta, "id").String() + ".jar",
          profile_path + "forge.jar", nativesfolder, version_meta, profile_path)
        libs_dump := libs
        libs = []string{}
        libs = append(libs, forge_libs...)
        libs = append(libs, libs_dump...)
        fmt.Println("")
      }

      // Generate Launch Command
      full_libs_list = append(full_libs_list, libs...)
      full_libs_list = append(full_libs_list, nativelibs...)

      game_launch_cmd := []string{"-Djava.library.path=" + nativesfolder, "-cp", strings.Join(full_libs_list, ":"), gjson.Get(version_meta, "mainClass").String()}
      game_launch_cmd = append(game_launch_cmd, GenLaunchArgs(version_meta, account_name, profile_path, asset_index)...)

      // Run the game!
      mc := exec.Command(GetMcBoopDir() + "java/bin/java", game_launch_cmd...)
      mc.Stdout = os.Stdout
      mc.Stderr = os.Stderr
      mc.Dir = profile_path

      fmt.Println("")
      fmt.Println("==== Game logging starts here ====")
      fmt.Println("")
      mc.Run()


      os.RemoveAll(nativesfolder)
    }
  }
}

func Help() {
  // System will exit when you call this!
  fmt.Println("")
  fmt.Println("==== McBoop Help ====")
  fmt.Println("")
  fmt.Println("<> Signify required args, [] Signify optional args")
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
  fmt.Println("")
  fmt.Println("./Mcboop --user <username> => Used with `--run` to select a spefic user to run the game with")
  fmt.Println("")
  fmt.Println("./Mcboop --profile <profile name> => Used with `--run` to run in a non-default profile")
  fmt.Println("")
  fmt.Println("./Mcboop --list-profiles => List all profiles")
  fmt.Println("")
  fmt.Println("./McBoop --forge => Used with `--run` will try to install and run the forge installer jar inside the profile directory this file must be named `forge.jar`")
  fmt.Println("")
  os.Exit(0)
}

func Status() {
  // System will exit upon calling this
  status := gjson.Parse(GetRemoteText("https://status.mojang.com/check"))
  fmt.Println("")
  fmt.Println("==== Minecraft server status ====")
  fmt.Println("")
  fmt.Println("Minecraft.net status:", StatusColor(status.Get("#.minecraft\\.net").Array()[0].String()))
  fmt.Println("Session.Minecraft.net status:", StatusColor(status.Get("#.session\\.minecraft\\.net").Array()[0].String()))
  fmt.Println("Textures.Minecraft.net Status:", StatusColor(status.Get("#.textures\\.minecraft\\.net").Array()[0].String()))
  fmt.Println("Mojang.com status:", StatusColor(status.Get("#.mojang\\.com").Array()[0].String()))
  fmt.Println("Account.Mojang.com status:", StatusColor(status.Get("#.account\\.mojang\\.com").Array()[0].String()))
  fmt.Println("Authserver.Mojang.com status:", StatusColor(status.Get("#.authserver\\.mojang\\.com").Array()[0].String()))
  fmt.Println("Sessionserver.Mojang.com status:", StatusColor(status.Get("#.sessionserver\\.mojang\\.com").Array()[0].String()))
  fmt.Println("Api.Mojang.com status:", StatusColor(status.Get("#.api\\.mojang\\.com").Array()[0].String()))
  fmt.Println("")
  os.Exit(0)
}
func StatusColor(Status string) (aurora.Value){
  // Add some color to the status page
  ans := aurora.White(Status)
  if Status == "green" {
    ans = aurora.Green("Green")
  } else if Status == "yellow" {
    ans = aurora.Yellow("Yellow")
  } else if Status == "red" {
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
func ListProfiles() {
  files, _ := ioutil.ReadDir(GetMcBoopDir() + "profiles")
  fmt.Println("")
  fmt.Println("==== Currently installed profiles ====")
  for i := 0; i < len(files); i++ {
    fmt.Println("\"" + files[i].Name() + "\"")
  }
  fmt.Println("")
  os.Exit(0)
}
