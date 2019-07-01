package main

import "os"
import "fmt"
import "strings"
import "os/exec"
import "io/ioutil"
import "encoding/json"
import "path/filepath"
import "github.com/tidwall/gjson"
import "github.com/mholt/archiver"

type VersionMeta struct {
  MainClass string `json:"mainClass"`
  MinecraftArguments string `json:"minecraftArguments"`
  Id string `json:"id"`
  Type string `json:"type"`
}

func CreateForgeJar (ClientJar string, ForgeJar string, TempDir string, CleanMeta string, ProfilePath string) (string, []string) {
  os.MkdirAll(TempDir + "forge-installer/", os.ModePerm)

  // Extract the client
  unzip := archiver.Zip{}

  // Extract forge installer
  unzip.Walk(ForgeJar, func(f archiver.File) error {
    bytes, _ := ioutil.ReadAll(f)
    WriteFile(bytes, TempDir + "forge-installer/" + f.Name())
    return nil
  })

  // Read the install_profile.json or version.json or w/e it's called
  var json_file string
  if !CheckForFile(TempDir + "forge-installer/version.json") {
    json_files, _ := filepath.Glob(TempDir + "forge-installer/*.json")
    json_file = json_files[0]
  } else {
    json_files, _ := filepath.Glob(TempDir + "forge-installer/version.json")
    json_file = json_files[0]
  }

  // Fetch lib list
  var libs []gjson.Result
  if gjson.Get(string(ReadTextFile(json_file)), "libraries").Exists() {
    libs = gjson.Get(string(ReadTextFile(json_file)), "libraries").Array()
  } else {
    libs = gjson.Get(string(ReadTextFile(json_file)), "versionInfo.libraries").Array()
  }

  // Install libs
  var lib_ans []string
  fmt.Println("")
  fmt.Println("==== Downloading and Verfiying Forge Libs ====")
  for i := 0; i < len(libs); i++ {
    if (libs[i].Get("serverreq").Exists() && libs[i].Get("serverreq").Bool()) {
      lib_name, lib_version, lib_path := GetLibDetails(libs[i].Get("name").String())
      if !CheckForFile(GetMcBoopDir() + "libraries/" + lib_path + lib_name + "-" + lib_version + ".jar") {
        DownloadForgeLib(GetMcBoopDir() + "libraries/" + lib_path + lib_name + "-" + lib_version + ".jar",
         "https://git.sergal.org/Sir-Boops/McBoop-Support-Files/raw/branch/master/forge-libs/" + lib_path + lib_name + "-" + lib_version + ".jar", "")
      }
      lib_ans = append(lib_ans, GetMcBoopDir() + "libraries/" + lib_path + lib_name + "-" + lib_version + ".jar")

    } else if libs[i].Get("downloads").Exists() && libs[i].Get("downloads.artifact.url").String() != "" {
      if Sha1Sum(GetMcBoopDir() + "libraries/" + libs[i].Get("downloads.artifact.path").String()) != libs[i].Get("downloads.artifact.sha1").String() {
          DownloadForgeLib(GetMcBoopDir() + "libraries/" + libs[i].Get("downloads.artifact.path").String(),
            libs[i].Get("downloads.artifact.url").String(), libs[i].Get("downloads.artifact.sha1").String())
        }
        lib_ans = append(lib_ans, GetMcBoopDir() + "libraries/" + libs[i].Get("downloads.artifact.path").String())

    } else if strings.Contains(libs[i].Get("name").String(), "net.minecraftforge:forge:") || strings.Contains(libs[i].Get("name").String(), "net.minecraftforge:minecraftforge:") {
      _, _, lib_path := GetLibDetails(libs[i].Get("name").String())
      installer_files, _ := filepath.Glob(TempDir + "forge-installer/*forge*.jar")
      for i := 0; i < len(installer_files); i++ {
        os.MkdirAll(GetMcBoopDir() + "libraries/" + lib_path, os.ModePerm)
        os.Rename(installer_files[i], GetMcBoopDir() + "libraries/" + lib_path + filepath.Base(installer_files[i]))
        lib_ans = append(lib_ans, GetMcBoopDir() + "libraries/" + lib_path + filepath.Base(installer_files[i]))
      }
    }
  }

  // Check if we need to run the installer itself
  version_json, _ := filepath.Glob(TempDir + "forge-installer/install_profile.json")
  if gjson.Get(string(ReadTextFile(version_json[0])), "processors").Exists() {
    lib_name, lib_version, lib_path := GetLibDetails(gjson.Get(string(ReadTextFile(version_json[0])), "path").String())
    if !CheckForFile(GetMcBoopDir() + "libraries/" + lib_path + lib_name + "-" + lib_version + "-client.jar") {
      // We need to run the installer
      fmt.Println("")
      fmt.Println("==== Running Forge installer ====")
      fmt.Println("")
      fmt.Println("When prompted please select `~/.mcboop` as your home directory")
      WriteFile([]byte("{\"profiles\":{}}"), GetMcBoopDir() + "launcher_profiles.json")
      mc := exec.Command(GetMcBoopDir() + "java/bin/java", "-jar", ForgeJar)
      mc.Dir = ProfilePath
      mc.Run()
    }
  }


  // Make sure we have te version meta we're looking for in the forge json
  version_meta := gjson.Get(string(ReadTextFile(json_file)), "versionInfo").String()
  if !gjson.Get(string(ReadTextFile(json_file)), "versionInfo").Exists() {
    version_meta = string(ReadTextFile(json_file))
  }

  // Convert 1.13+ args into simple minecraftArguments for launching
  var minecraft_args string
  if gjson.Get(version_meta, "arguments.game").Exists() {
    var new_args []gjson.Result
    for i := 0; i < len(gjson.Get(CleanMeta, "arguments.game").Array()); i++ {
      if !strings.HasPrefix(gjson.Get(CleanMeta, "arguments.game").Array()[i].String(), "{"){
        new_args = append(new_args, gjson.Get(CleanMeta, "arguments.game").Array()[i])
      }
    }
    new_args = append(new_args, gjson.Get(version_meta, "arguments.game").Array()...)
    for i := 0; i < len(new_args); i++ {
      minecraft_args += new_args[i].String() + " "
    }
  } else {
    minecraft_args = gjson.Get(version_meta, "minecraftArguments").String()
  }

  // Create a new meta 'file'
  meta, _ := json.Marshal(&VersionMeta{
    MainClass: gjson.Get(version_meta, "mainClass").String(),
    MinecraftArguments: minecraft_args,
    Type: gjson.Get(version_meta, "type").String(),
    Id: gjson.Get(version_meta, "id").String()})

  return string(meta), lib_ans
}


func GetLibDetails(Lib string) (string, string, string) {
  lib_name := strings.Split(Lib, ":")[1]
  lib_version := strings.Split(Lib, ":")[2]
  lib_path := strings.ReplaceAll(strings.Split(Lib, ":")[0], ".", "/") + "/" + lib_name + "/" + lib_version + "/"
  return lib_name, lib_version, lib_path
}

func DownloadForgeLib(Path string, URL string, SHA1Sum string) {
  os.MkdirAll(Path, os.ModePerm)
  fmt.Println("Downloading:", filepath.Base(Path))
  lib := ReadRemote(URL)
  r := 0
  for Sha1SumByte(lib) != SHA1Sum && r < 5 && SHA1Sum != "" {
    r = r+1
    lib = ReadRemote(URL)
  }
  if r >= 5 {
    fmt.Println("Failed to download:", filepath.Base(Path), "Tried 5 times, If your connection Ok?")
  }
  WriteFile(lib, Path)
}
