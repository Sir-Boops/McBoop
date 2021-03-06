package main

import "os"
import "fmt"
import "path"
import "sync"
import "time"
import "runtime"
import "strings"
import "io/ioutil"
import "path/filepath"
import "github.com/tidwall/gjson"
import "github.com/mholt/archiver"
import "github.com/satori/go.uuid"

func InstallAssets(URL string, Id string, ProfilePath string) {
  AssetIndex := GetRemoteText(URL)
  os.MkdirAll(GetMcBoopDir() + "assets/indexes/", os.ModePerm)
  WriteFile([]byte(AssetIndex), GetMcBoopDir() + "assets/indexes/" + Id + ".json")
  assets := gjson.Get(AssetIndex, "objects")
  var wg sync.WaitGroup
  r := 0
  assets.ForEach(func(key, value gjson.Result) (bool) {
    // Now we are looping over a list of assets
    // key is the filename
    // value is the object

    // If we have to download it thread it else don't bother with threads
    assetpath := GetMcBoopDir() + "assets/objects/" + string(value.Get("hash").String()[0:2]) + "/"
    if !CheckForFile(assetpath + value.Get("hash").String()) || Sha1Sum(assetpath + value.Get("hash").String()) != value.Get("hash").String() {
      r = r+1

      // Limit to 10 download threads
      for r > 10 {
        time.Sleep(100 * time.Millisecond)
      }

      wg.Add(1)
      go func() {
        defer wg.Done()
        defer func() {
          r = r-1
        }()
        DownloadAsset("https://resources.download.minecraft.net/" + string(value.Get("hash").String()[0:2]) + "/" + value.Get("hash").String(),
          assetpath, value.Get("hash").String(), key.String(), value.Get("hash").String(), ProfilePath)
      }()
    }

    return true
  })

// Make sure everything is downloaded before going on
  wg.Wait()

  if Id == "pre-1.6" || Id == "legacy" {
    // Check old_sounds.zip sum
    if gjson.Get(GetRemoteText("https://mcboop.boops.org/McBoop/Support-Files/mojang_files/sha256sums.json"), "old_sounds\\.zip").String() != Sha256Sum(GetMcBoopDir() + "old_sounds.zip") {
      fmt.Println("Downloading old_sounds.zip")
      WriteFile(ReadRemote("https://mcboop.boops.org/McBoop/Support-Files/mojang_files/old_sounds.zip"), GetMcBoopDir() + "old_sounds.zip")
    }
    os.MkdirAll(ProfilePath + "resources", os.ModePerm)
    os.MkdirAll(GetMcBoopDir() + "default/resources", os.ModePerm)
    // Make sure old sounds are still good
    old_sounds_json := GetRemoteText("https://mcboop.boops.org/McBoop/Support-Files/mojang_files/old_sounds.zip.json")
    old_assets := gjson.Parse(old_sounds_json)
    old_assets.ForEach(func(key, value gjson.Result) bool {
      // Check inside the profile
      return CheckSoundFile(ProfilePath + "resources/", value.Get("name").String(), value.Get("sha256sum").String())
    })
    old_assets.ForEach(func(key, value gjson.Result) bool {
      // Check inside the default folder
      return CheckSoundFile(GetMcBoopDir() + "default/", value.Get("name").String(), value.Get("sha256sum").String())
    })
  }
}

func CheckSoundFile(Path string, Object string, RemoteSum string) (bool) {
  if !CheckForFile(Path + Object) || Sha256Sum(Path + Object) != RemoteSum {
    fmt.Println("Extracting old_sounds.zip to", Path)
    os.RemoveAll(Path)
    archiver.Unarchive(GetMcBoopDir() + "old_sounds.zip", Path)
    return false
  }
  return true
}

func DownloadAsset(URL string, Path string, FileName string, PrettyFileName string, sha1sum string, ProfilePath string) {
  os.MkdirAll(Path, os.ModePerm)
  os.MkdirAll(ProfilePath + "resources/" + path.Dir(PrettyFileName), os.ModePerm)
  fmt.Println("Downloading:", PrettyFileName)
  asset := ReadRemote(URL)
  t := 0
  for Sha1SumByte(asset) != sha1sum && t < 5 {
    t = t+1
    asset = ReadRemote(URL)
  }
  if t >= 5 {
    fmt.Println("Failed to download:", PrettyFileName, "Five times is your connection ok?")
  }
  WriteFile(asset, Path + FileName)
  WriteFile(asset, ProfilePath + "resources/" + PrettyFileName)
}

func InstallLibs(LibIndex []gjson.Result) ([]string, []string) {
  // Returns []libs []nativelibs
  libs := []string{}
  nativelibs := []string{}

  for i := 0; i < len(LibIndex); i++ {
    if LibIndex[i].Get("downloads.artifact.path").Exists() {
      libpath := GetMcBoopDir() + "libraries/" + LibIndex[i].Get("downloads.artifact.path").String()
      if (LibIndex[i].Get("rules").Exists() && CheckForAllow(LibIndex[i].Get("rules").Array())) || !LibIndex[i].Get("rules").Exists() && !LibIndex[i].Get("natives").Exists() {
        libs = append(libs, libpath)
        if !CheckForFile(libpath) || Sha1Sum(libpath) != LibIndex[i].Get("downloads.artifact.sha1").String() {
          DownloadLib(libpath, LibIndex[i].Get("downloads.artifact.url").String(), LibIndex[i].Get("downloads.artifact.sha1").String())
        }
      }
    }

    if LibIndex[i].Get("natives." + runtime.GOOS).Exists() {
      if LibIndex[i].Get("downloads.classifiers." + LibIndex[i].Get("natives." + runtime.GOOS).String()).Exists() { // Make find the natives for this OS
        if !LibIndex[i].Get("rules").Exists() || CheckForAllow(LibIndex[i].Get("rules").Array()) {
          nativelibpath := GetMcBoopDir() + "libraries/" + LibIndex[i].Get("downloads.classifiers." + LibIndex[i].Get("natives." + runtime.GOOS).String() + ".path").String()
          nativelibs = append(nativelibs, nativelibpath)

          if !CheckForFile(nativelibpath) || Sha1Sum(nativelibpath) != LibIndex[i].Get("downloads.classifiers." + LibIndex[i].Get("natives." + runtime.GOOS).String() +
            ".sha1").String() {
            DownloadLib(nativelibpath, LibIndex[i].Get("downloads.classifiers." + LibIndex[i].Get("natives." + runtime.GOOS).String() + ".url").String(),
              LibIndex[i].Get("downloads.classifiers." + LibIndex[i].Get("natives." + runtime.GOOS).String() + ".sha1").String())
          }
        }
      }
    }
  }
  return libs, nativelibs
}
func DownloadLib(Path string, URL string, SHA1Sum string) {
  os.MkdirAll(Path, os.ModePerm)
  fmt.Println("Downloading:", filepath.Base(Path))
  lib := ReadRemote(URL)
  r := 0
  for Sha1SumByte(lib) != SHA1Sum && r < 5 {
    r = r+1
    lib = ReadRemote(URL)
  }
  if r >= 5 {
    fmt.Println("Failed to download:", filepath.Base(Path), "Tried 5 times, If your connection Ok?")
  }
  WriteFile(lib, Path)
}

func InstallClient(Meta gjson.Result, Id string, Verify bool) {
  path := GetMcBoopDir() + "client/" + Id + "/"
  filename := Id + ".jar"

  if !Verify {
    fmt.Println("Warning, Not verifying client jar hash")
  }

  if !CheckForFile(path + filename) || (Sha1Sum(path + filename) != Meta.Get("sha1").String() && Verify) {
    DownloadClient(Meta.Get("url").String(), path + filename, Meta.Get("sha1").String())
  }
}
func DownloadClient(URL string, Path string, Sha1Sum string) {
  os.MkdirAll(Path, os.ModePerm)
  fmt.Println("Downloading:", filepath.Base(Path))
  client := ReadRemote(URL)
  r := 0
  for Sha1SumByte(client) != Sha1Sum && r < 5 {
    r = r+1
    client = ReadRemote(URL)
  }
  if r >= 5 {
    fmt.Println("Failed to download:", filepath.Base(Path), "5 Times is your connection ok?")
  }
  WriteFile(ReadRemote(URL), Path)
}

func ExtractNatives(Natives []string) (string) {
  zip := archiver.Zip{}
  new_uuid := uuid.NewV4()
  nativesfolder := GetMcBoopDir() + new_uuid.String() + "/"
  os.MkdirAll(nativesfolder, os.ModePerm)

  for i := 0; i < len(Natives); i++ {
    zip.Walk(Natives[i], func(f archiver.File) error {
      if strings.HasSuffix(f.Name(), ".so") || strings.HasSuffix(f.Name(), ".dll") {
        // Found a lib to extract
        fmt.Println("Extracting native file:", f.Name())
        bytes, _ := ioutil.ReadAll(f)
        WriteFile(bytes, nativesfolder + f.Name())
      }
      return nil
    })
  }

  return nativesfolder
}

func CheckForAllow(Arr []gjson.Result) (bool) {
  ans := false
  if Arr != nil {
    for i := 0; i < len(Arr); i++ {
      if Arr[i].Get("action").String() == "allow" && !Arr[i].Get("os").Exists() {
        ans = true
        break
      }
    }
  }
  return ans
}
