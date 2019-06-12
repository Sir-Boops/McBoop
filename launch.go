package main

import "strings"
import "github.com/tidwall/gjson"

func GenLaunchCommand(Args []string, Name string, Id string, AssetId string, VersionType string) ([]string) {
  gameargs := []string{}
  for i := 0; i < len(Args); i++ {

    if !strings.HasPrefix(Args[i], "$") {
      gameargs = append(gameargs, Args[i])
    }

    if Args[i] == "${auth_player_name}" {
      gameargs = append(gameargs, Name)
    }

    if Args[i] == "${version_name}" {
      gameargs = append(gameargs, Id)
    }

    if Args[i] == "${game_directory}" {
      gameargs = append(gameargs, GetMcBoopDir() + "default" + "/")
    }

    if Args[i] == "${assets_root}" || Args[i] == "${game_assets}" {
      gameargs = append(gameargs, GetMcBoopDir() + "assets" + "/")
    }

    if Args[i] == "${assets_index_name}" {
      gameargs = append(gameargs, AssetId)
    }

    if Args[i] == "${auth_uuid}" {
      user := GetAccount(Name)
      gameargs = append(gameargs, user.UUID)
    }

    if Args[i] == "${auth_access_token}" {
      user := GetAccount(Name)
      gameargs = append(gameargs, user.AccessToken)
    }

    if Args[i] == "${user_type}" {
      gameargs = append(gameargs, "{}")
    }

    if Args[i] == "${version_type}" {
      gameargs = append(gameargs, VersionType)
    }

  }

  return gameargs
}

func GenLaunchArgs(VersionMeta string, AccountName string) ([]string) {

  game_args := []string{}

  // See if it uses minecraftArguments or arguments.game
  if gjson.Get(VersionMeta, "arguments.game").Exists() {
    for i := 0; i < len(gjson.Get(VersionMeta, "arguments.game").Array()); i++ {
      if !strings.HasPrefix(gjson.Get(VersionMeta, "arguments.game").Array()[i].String(), "{") {
        game_args = append(game_args, gjson.Get(VersionMeta, "arguments.game").Array()[i].String())
      }
    }
  }

  if gjson.Get(VersionMeta, "minecraftArguments").Exists() {
    game_args = append(game_args, strings.Split(gjson.Get(VersionMeta, "minecraftArguments").String(), " ")...)
  }

  return GenLaunchCommand(game_args, AccountName, gjson.Get(VersionMeta, "id").String(), gjson.Get(VersionMeta, "assets").String(), gjson.Get(VersionMeta, "type").String())
}
