package main

import "os"
import "fmt"
import "path"
import "sync"
import "time"
import "strings"
import "io/ioutil"
import "path/filepath"
import "github.com/tidwall/gjson"
import "github.com/mholt/archiver"
import "github.com/satori/go.uuid"

func InstallAssets(URL string, Id string) {
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
        time.Sleep(250 * time.Millisecond)
      }

      wg.Add(1)
      go func() {
        defer wg.Done()
        defer func() {
          r = r-1
        }()
        DownloadAsset("https://resources.download.minecraft.net/" + string(value.Get("hash").String()[0:2]) + "/" + value.Get("hash").String(),
          assetpath, value.Get("hash").String(), key.String(), value.Get("hash").String())
      }()
    }

    return true
  })

// Make sure everything is downloaded before going on
  wg.Wait()

  if Id == "pre-1.6" || Id == "legacy" {
    if !CheckForFile(GetMcBoopDir() + "default/resources/old_sounds.zip") {
      fmt.Println("Downloading: old_sounds.zip")
      os.MkdirAll(GetMcBoopDir() + "default/resources/", os.ModePerm)
      WriteFile(ReadRemote("https://git.sergal.org/Sir-Boops/McBoop-Support-Files/raw/branch/master/mojang_files/old_sounds.zip"),
        GetMcBoopDir() + "default/resources/old_sounds.zip")
      fmt.Println("Extracting: old_sounds.zip")
      archiver.Unarchive(GetMcBoopDir() + "default/resources/old_sounds.zip", GetMcBoopDir() + "default/resources/")
    }
  }
}

func DownloadAsset(URL string, Path string, FileName string, PrettyFileName string, sha1sum string) {
  os.MkdirAll(Path, os.ModePerm)
  os.MkdirAll(GetMcBoopDir() + "default/resources/" + path.Dir(PrettyFileName), os.ModePerm)
  fmt.Println("Downloading:", PrettyFileName)
  var asset []byte
  t := 0
  for Sha1SumByte(asset) != sha1sum && t < 5 {
    t = t+1
    asset = ReadRemote(URL)
  }
  if t >= 5 {
    fmt.Println("Failed to download:", PrettyFileName, "Five times is your connection ok?")
  }
  WriteFile(asset, Path + FileName)
  WriteFile(asset, GetMcBoopDir() + "default/resources/" + PrettyFileName)
}

func InstallLibs(LibIndex []gjson.Result) ([]string, []string) {
  // Returns []libs []nativelibs
  libs := []string{}
  nativelibs := []string{}

  for i := 0; i < len(LibIndex); i++ {
    if LibIndex[i].Get("downloads.artifact.path").Exists() {

      libpath := GetMcBoopDir() + "libraries/" + LibIndex[i].Get("downloads.artifact.path").String()
      libs = append(libs, libpath)

      if !CheckForFile(libpath) {
        DownloadLib(libpath, LibIndex[i].Get("downloads.artifact.url").String())
      }

      if Sha1Sum(libpath) != LibIndex[i].Get("downloads.artifact.sha1").String() {
        DownloadLib(libpath, LibIndex[i].Get("downloads.artifact.url").String())
      }
    }

    if LibIndex[i].Get("natives.linux").Exists() {
      if LibIndex[i].Get("downloads.classifiers." + LibIndex[i].Get("natives.linux").String()).Exists() {

        nativelibpath := GetMcBoopDir() + "libraries/" + LibIndex[i].Get("downloads.classifiers." + LibIndex[i].Get("natives.linux").String() + ".path").String()
        nativelibs = append(nativelibs, nativelibpath)

        if !CheckForFile(nativelibpath) {
          DownloadLib(nativelibpath, LibIndex[i].Get("downloads.classifiers." + LibIndex[i].Get("natives.linux").String() + ".url").String())
        }

        if Sha1Sum(nativelibpath) != LibIndex[i].Get("downloads.classifiers." + LibIndex[i].Get("natives.linux").String() + ".sha1").String() {
          DownloadLib(nativelibpath, LibIndex[i].Get("downloads.classifiers." + LibIndex[i].Get("natives.linux").String() + ".url").String())
        }

      }
    }
  }
  return libs, nativelibs
}
func DownloadLib(Path string, URL string) {
  os.MkdirAll(Path, os.ModePerm)
  fmt.Println("Downloading:", filepath.Base(Path))
  WriteFile(ReadRemote(URL), Path)
}

func InstallClient(Meta gjson.Result, Id string) {
  path := GetMcBoopDir() + "client/" + Id + "/"
  filename := Id + ".jar"

  if !CheckForFile(path + filename) {
    DownloadClient(Meta.Get("url").String(), path + filename)
  }

  if Sha1Sum(path + filename) != Meta.Get("sha1").String() {
    DownloadClient(Meta.Get("url").String(), path + filename)
  }
}
func DownloadClient(URL string, Path string) {
  os.MkdirAll(Path, os.ModePerm)
  fmt.Println("Downloading:", filepath.Base(Path))
  WriteFile(ReadRemote(URL), Path)
}

func ExtractNatives(Natives []string) (string) {
  zip := archiver.Zip{}
  new_uuid, _ := uuid.NewV4()
  nativesfolder := GetMcBoopDir() + new_uuid.String() + "/"
  os.MkdirAll(nativesfolder, os.ModePerm)

  for i := 0; i < len(Natives); i++ {
    zip.Walk(Natives[i], func(f archiver.File) error {
      if strings.HasSuffix(f.Name(), ".so") {
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
