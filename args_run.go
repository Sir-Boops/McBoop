package main

import "os"
import "fmt"
import "runtime"
import "os/exec"
import "strings"
import "github.com/tidwall/gjson"

func runGame () {
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

  // Get Custom ram amount
  ram_amount := "4G"
  for i := 0; i < len(os.Args); i++ {
    if os.Args[i] == "--ram" {
      ram_amount = os.Args[i + 1]
    }
  }

  // Check if we should deal with forge or not
  forge := false
  for i := 0; i < len(os.Args); i++ {
    if os.Args[i] == "--forge" {
      forge = true
    }
  }

  // Check if we should deal with fabric or not
  fabric := false
  for i := 0; i < len(os.Args); i++ {
    if os.Args[i] == "--fabric" {
      fabric = true
    }
  }

  // Check if we should verify client jar or not
  verify := true
  for i := 0; i < len(os.Args); i++ {
    if os.Args[i] == "--no-verify-client" {
      verify = false
    }
  }

  // Install and verify java
  InstallJava()

  // Ensure the profile folder is there
  os.MkdirAll(profile_path, os.ModePerm)

  // Now refresh login token
  RefreshAccount(account_name)

  //Get version manifest
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
  InstallClient(gjson.Get(version_meta, "downloads.client"), gjson.Get(version_meta, "id").String(), verify)
  fmt.Println("")

  // Start defining libs
  full_libs_list := []string{}
  full_libs_list = append(full_libs_list, GetMcBoopDir() + "client/" + gjson.Get(version_meta, "id").String() + "/" + gjson.Get(version_meta, "id").String() + ".jar")

  // Define some args that can go missing with forge
  asset_index := gjson.Get(version_meta, "assets").String()

  // Start building launch args
  game_launch_cmd := []string{"-Xmx" + ram_amount}
  game_launch_cmd = append(game_launch_cmd, GenJVMArgs(version_meta, nativesfolder)...)

  // Define the mainCLass
  main_class := gjson.Get(version_meta, "mainClass").String()

  // Install forge, If requested
  if forge {
    var forge_libs []string
    version_meta, forge_libs = CreateForgeJar(GetMcBoopDir() + "client/" + gjson.Get(version_meta, "id").String() + "/" + gjson.Get(version_meta, "id").String() + ".jar",
      profile_path + "forge.jar", nativesfolder, version_meta, profile_path)
    libs_dump := libs
    libs = []string{}
    libs = append(libs, forge_libs...)
    libs = append(libs, libs_dump...)
    main_class = gjson.Get(version_meta, "mainClass").String()
    fmt.Println("")
  }

  // Install Fabric, If requested
  if fabric {
    var FabricVersion string
    // If user defines custom fabric version
    for i := 0; i < len(os.Args); i++ {
      if os.Args[i] == "--fabric" && len(os.Args) > i + 1  {
        if !strings.Contains(os.Args[i + 1], "--") {
          FabricVersion = os.Args[i + 1]
          break
        }
      }
    }

    // If no fabric version is defined
    if FabricVersion == "" {
      FabricVersion = GetLatestFabricVersion(requested_version)
    }

    // Verify fabric version is there and fetch meta
    FabricMeta := FetchFabricMeta(requested_version, FabricVersion)

    // Install Fabric Libs
    fmt.Println("==== Installing/Verifying Fabric Libraries ====")
    libs = append(libs, InstallFabricLibs(gjson.Get(FabricMeta, "launcherMeta.libraries.client").Array())...)
    libs = append(libs, InstallFabricLibs(gjson.Get(FabricMeta, "launcherMeta.libraries.common").Array())...)
    libs = append(libs, InstallFabricLibs(gjson.Parse("[{\"name\":\"" + gjson.Get(FabricMeta, "loader.maven").String() +
      "\",\"url\":\"https://maven.fabricmc.net/\"}]").Array())...)
    libs = append(libs, InstallFabricLibs(gjson.Parse("[{\"name\":\"" + gjson.Get(FabricMeta, "intermediary.maven").String() +
      "\",\"url\":\"https://maven.fabricmc.net/\"}]").Array())...)

    main_class = gjson.Get(FabricMeta, "launcherMeta.mainClass.client").String()
  }

  // Generate Launch Command
  full_libs_list = append(full_libs_list, libs...)
  full_libs_list = append(full_libs_list, nativelibs...)

  // Windows likes to be special
  lib_sep := ":"
  if runtime.GOOS == "windows" {
    lib_sep = ";"
  }

  game_launch_cmd = append(game_launch_cmd, []string{strings.Join(full_libs_list, lib_sep), main_class}...)
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
