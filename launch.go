package main

import "strings"
import "github.com/tidwall/gjson"

func GenLaunchCommand(Args []gjson.Result, Name string, Id string, AssetId string, VersionType string) ([]string) {
  gameargs := []string{}
  for i := 0; i < len(Args); i++ {

    if strings.HasPrefix(Args[i].String(), "--") {
      gameargs = append(gameargs, Args[i].String())
    }

    if Args[i].String() == "${auth_player_name}" {
      gameargs = append(gameargs, Name)
    }

    if Args[i].String() == "${version_name}" {
      gameargs = append(gameargs, Id)
    }

    if Args[i].String() == "${game_directory}" {
      gameargs = append(gameargs, GetMcBoopDir() + "default" + "/")
    }

    if Args[i].String() == "${assets_root}" {
      gameargs = append(gameargs, GetMcBoopDir() + "assets" + "/")
    }

    if Args[i].String() == "${assets_index_name}" {
      gameargs = append(gameargs, AssetId)
    }

    if Args[i].String() == "${auth_uuid}" {
      user := GetAccount(Name)
      gameargs = append(gameargs, user.UUID)
    }

    if Args[i].String() == "${auth_access_token}" {
      user := GetAccount(Name)
      gameargs = append(gameargs, user.AccessToken)
    }

    if Args[i].String() == "${user_type}" {
      gameargs = append(gameargs, "{}")
    }

    if Args[i].String() == "${version_type}" {
      gameargs = append(gameargs, VersionType)
    }

  }

  return gameargs
}
