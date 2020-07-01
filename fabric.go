package main

import "os"
import "fmt"
import "strings"
import "github.com/tidwall/gjson"

func ListFabricVersions(VersionID string) {
  // Fatch the Version list
  VersionList := gjson.Parse(GetRemoteText("https://meta.fabricmc.net/v2/versions/loader/" + VersionID)).Array()

  // Check for an actual answer
  if len(VersionList) < 1 {
    fmt.Println("No versions of Fabric found for:", VersionID)
    os.Exit(0)
  }

  // Print all versions
  fmt.Println("Fabric Versions for:", VersionID)
  fmt.Println("")
  for i := 0; i < (len(VersionList)); i++ {
    fmt.Println(VersionList[i].Get("loader.version"))
  }
  fmt.Println("")

  // At the bottom print the newest
  fmt.Println("List is in order newest ( top ) to oldest ( bottom )")
  fmt.Println("")
  fmt.Println("Latest Fabric Version:", VersionList[0].Get("loader.version"))
}

func GetLatestFabricVersion(VersionID string) (string) {
  VersionList := gjson.Parse(GetRemoteText("https://meta.fabricmc.net/v2/versions/loader/" + VersionID)).Array()
  if len(VersionList) < 1 {
    fmt.Println("No versions of Fabric found for:", VersionID)
    os.Exit(0)
  }
  return VersionList[0].Get("loader.version").String()
}

func FetchFabricMeta(VersionID string, FabricVersion string) (string) {
  VersionMeta := GetRemoteText("https://meta.fabricmc.net/v2/versions/loader/" + VersionID + "/" + FabricVersion)
    // Make sure we have some sort of answer
  if !strings.Contains(VersionMeta, "{") {
    fmt.Println("Could not find Fabric version:", FabricVersion, "For:", VersionID)
    os.Exit(0)
  }
  return VersionMeta
}

func InstallFabricLibs(Libs []gjson.Result) ([]string) {
  var LibList []string
  for i := 0; i < len(Libs); i++ {
    SplitName := strings.Split(Libs[i].Get("name").String(), ":")
    LibPath := strings.ReplaceAll(SplitName[0], ".", "/") + "/" + SplitName[1] + "/" + SplitName[2]
    FileName := SplitName[1] + "-" + SplitName[2] + ".jar"

    // Try create lib path
    os.MkdirAll(GetMcBoopDir() + "libraries/" + LibPath, os.ModePerm)

    // Check if file is there or not
    LibSHA1Sum := GetRemoteText(Libs[i].Get("url").String() + LibPath + "/" + FileName + ".sha1")
    if !CheckForFile(GetMcBoopDir() + "libraries/" + LibPath + "/" + FileName) || Sha1Sum(GetMcBoopDir() + "libraries/" + LibPath + "/" + FileName) != LibSHA1Sum {
      // Redownload/Download file
      fmt.Println("Downloading:", FileName)
      lib := ReadRemote(Libs[i].Get("url").String() + LibPath + "/" + FileName)
      r := 0

      for Sha1SumByte(lib) != LibSHA1Sum && r < 5 {
        r = r+1
        lib = ReadRemote(Libs[i].Get("url").String() + LibPath + "/" + FileName)
      }

      if r >= 5 {
        fmt.Println("Failed to download:", FileName, "Tried 5 times, If your connection Ok?")
      }

      WriteFile(lib, GetMcBoopDir() + "libraries/" + LibPath + "/" + FileName)
    }
    LibList = append(LibList, GetMcBoopDir() + "libraries/" + LibPath + "/" + FileName)
  }
  return LibList
}
