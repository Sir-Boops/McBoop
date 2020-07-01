package main

import "os"
import "fmt"
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

    if os.Args[1] == "--list-fabric-versions" {
      if len(os.Args) <= 2 {
        fmt.Println("Missing Required argument run `./Mcboop --help` for help")
      } else {
        ListFabricVersions(os.Args[2])
      }
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
          fmt.Println(users[i], aurora.Green("<- Default"))
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
      runGame()
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
  fmt.Println("./Mcboop --ram <max amount> => Used with `--run` to set maximum amount of RAM ( Default is 4G )")
  fmt.Println("")
  fmt.Println("./Mcboop --list-profiles => List all profiles")
  fmt.Println("")
  fmt.Println("./McBoop --forge => Used with `--run` will try to install and run the forge installer jar inside the profile directory this file must be named `forge.jar`")
  fmt.Println("")
  fmt.Println("./McBoop --list-fabric-versions => List all Fabric versions")
  fmt.Println("")
  fmt.Println("./McBoop --fabric [fabric version] => Used with `--run` if [fabric version] is not defined will install the latest version for Minecraft")
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
