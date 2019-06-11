package main

import "os"
import "fmt"
import "github.com/tidwall/gjson"
import "github.com/logrusorgru/aurora"

func ArgsParse(Args []string) {

  // If Nonthing is set then default to --help
  if len(Args) <= 1 {
    ArgsParse_Help()
  } else {
    // Args is 1 or longer so parse time!
    if Args[1] == "--help" {
      ArgsParse_Help()
    }

    if Args[1] == "--status" {
      ArgsParse_Status()
    }
    
  }
}

func ArgsParse_Help() {
  // System will exit when you call this!
  fmt.Println("")
  fmt.Println("McBoop Help")
  fmt.Println("")
  fmt.Println("McBoop --help => Shows this page")
  fmt.Println("")
  fmt.Println("McBoop --status => Shows mojang status")
  os.Exit(0)
}

func ArgsParse_Status() {
  // System will exit upon calling this
  status := gjson.Parse(GetRemoteText("https://status.mojang.com/check"))
  fmt.Println(status)
  fmt.Println("")
  fmt.Println("Minecraft server status")
  fmt.Println("")
  fmt.Println("Minecraft.net status:", ArgsParse_Status_Color(status.Get("#.minecraft\\.net").Array()[0].String()))
  fmt.Println("Session.Minecraft.net status:", ArgsParse_Status_Color(status.Get("#.session\\.minecraft\\.net").Array()[0].String()))
  fmt.Println("Textures.Minecraft.net Status:", ArgsParse_Status_Color(status.Get("#.textures\\.minecraft\\.net").Array()[0].String()))
  fmt.Println("Mojang.com status:", ArgsParse_Status_Color(status.Get("#.mojang\\.com").Array()[0].String()))
  fmt.Println("Account.Mojang.com status:", ArgsParse_Status_Color(status.Get("#.account\\.mojang\\.com").Array()[0].String()))
  fmt.Println("Authserver.Mojang.com status:", ArgsParse_Status_Color(status.Get("#.authserver\\.mojang\\.com").Array()[0].String()))
  fmt.Println("Sessionserver.Mojang.com status:", ArgsParse_Status_Color(status.Get("#.sessionserver\\.mojang\\.com").Array()[0].String()))
  fmt.Println("Api.Mojang.com status:", ArgsParse_Status_Color(status.Get("#.api\\.mojang\\.com").Array()[0].String()))
  os.Exit(0)
}

func ArgsParse_Status_Color(Status string) (aurora.Value){
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
