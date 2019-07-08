package main

import "strings"
import "runtime"
import "github.com/tidwall/gjson"

func GenLaunchCommand(Args []string, Name string, Id string, AssetId string, VersionType string, ProfilePath string) ([]string) {
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
      gameargs = append(gameargs, ProfilePath)
    }

    if Args[i] == "${assets_root}" {
      gameargs = append(gameargs, GetMcBoopDir() + "assets")
    }

    if Args[i] == "${game_assets}" {
      gameargs = append(gameargs, GetMcBoopDir() + "default/resources")
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

    if Args[i] == "${auth_session}" {
      user := GetAccount(Name)
      gameargs = append(gameargs, "token:" + user.AccessToken + ":" + user.UUID)
    }

    if Args[i] == "${user_type}" || Args[i] == "${user_properties}" {
      gameargs = append(gameargs, "{}")
    }

    if Args[i] == "${version_type}" {
      gameargs = append(gameargs, VersionType)
    }

  }

  return gameargs
}
func GenLaunchArgs(VersionMeta string, AccountName string, ProfilePath string, AssetIndex string) ([]string) {

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

  return GenLaunchCommand(game_args, AccountName, gjson.Get(VersionMeta, "id").String(), AssetIndex, gjson.Get(VersionMeta, "type").String(),
   ProfilePath)
}

func FillJVMArgs(Arg string, NativesFolder string) (string) {
  ans := Arg
  if strings.Contains(Arg, "${natives_directory}") {
    ans = strings.ReplaceAll(Arg, "${natives_directory}", NativesFolder)
  }

  if strings.Contains(Arg, "${classpath}") {
    ans = ""
  }

  if strings.Contains(Arg, "${launcher_name}") {
    ans = strings.ReplaceAll(Arg, "${launcher_name}", "McBoop")
  }

  if strings.Contains(Arg, "${launcher_version}") {
    ans = strings.ReplaceAll(Arg, "${launcher_version}", "rolling-release")
  }

  if strings.Contains(Arg, "${natives_directory}") {
    ans = strings.ReplaceAll(Arg, "${natives_directory}", NativesFolder)
  }
  return ans
}
func GenJVMArgs(VersionMeta string, NativesFolder string) ([]string) {
  jvm_args := []string{}
  if gjson.Get(VersionMeta, "arguments.jvm").Exists() {
    args_array := gjson.Get(VersionMeta, "arguments.jvm").Array()
    for i := 0; i < len(args_array); i++ {
      if args_array[i].Get("rules").Exists() {
        // We have an OS based option!
        if args_array[i].Get("rules").Array()[0].Get("os.name").Exists() {
          if args_array[i].Get("rules").Array()[0].Get("os.name").String() == runtime.GOOS {
            jvm_args = append(jvm_args, FillJVMArgs(args_array[i].Get("value").String(), NativesFolder))
          }
        } else if args_array[i].Get("rules").Array()[0].Get("os.arch").Exists() {
          jvm_args = append(jvm_args, FillJVMArgs(args_array[i].Get("value").String(), NativesFolder))
        }
      } else {
        // Generic option just fill it in and be done
        arg := FillJVMArgs(args_array[i].String(), NativesFolder)
        if arg != "" {
          jvm_args = append(jvm_args, arg)
        }
      }
    }
  } else {
    jvm_args = append(jvm_args, []string{"-Djava.library.path=" + NativesFolder, "-cp"}...)
  }
  return jvm_args
}
