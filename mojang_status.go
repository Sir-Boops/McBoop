package main

import "os"
import "fmt"
import "net/http"
import "io/ioutil"
import "github.com/tidwall/gjson"

type StatusArr struct {

}

func mojang_status() {

  resp, err := http.Get("https://status.mojang.com/check")
  if err != nil {
    fmt.Println(err)
    os.Exit(1)
  }

  defer resp.Body.Close()

  body_bytes, _ := ioutil.ReadAll(resp.Body)
  body_string := string(body_bytes)

  fmt.Println("")
  fmt.Println("mojang.com status:", gjson.Get(body_string, "#.mojang\\.com").Array()[0])
  fmt.Println("api.mojang.com status:", gjson.Get(body_string, "#.api\\.mojang\\.com").Array()[0])
  fmt.Println("account.mojang.com status:", gjson.Get(body_string, "#.account\\.mojang\\.com").Array()[0])
  fmt.Println("authserver.mojang.com status:", gjson.Get(body_string, "#.authserver\\.mojang\\.com").Array()[0])
  fmt.Println("sessionserver.mojang.com status:", gjson.Get(body_string, "#.sessionserver\\.mojang\\.com").Array()[0])
  fmt.Println("")
  fmt.Println("minecraft.net status:", gjson.Get(body_string, "#.minecraft\\.net").Array()[0])
  fmt.Println("session.minecraft.net status:", gjson.Get(body_string, "#.session\\.minecraft\\.net").Array()[0])
  fmt.Println("textures.minecraft.net status:", gjson.Get(body_string, "#.textures\\.minecraft\\.net").Array()[0])
  fmt.Println("")

}
