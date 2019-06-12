package main

import "os"
import "fmt"
import "path"
import "github.com/tidwall/gjson"

func InstallAssets(AssetIndex string) {
  assets := gjson.Get(AssetIndex, "objects")
  assets.ForEach(func(key, value gjson.Result) (bool) {

    // Now we are looping over a list of assets
    // key is the filename
    // value is the object

    assetpath := GetMcBoopDir() + "assets/objects/" + string(value.Get("hash").String()[0:2]) + "/"

    // Check to see if asset is there
    if !CheckForFile(assetpath + value.Get("hash").String()) {
      DownloadAsset("https://resources.download.minecraft.net/" + string(value.Get("hash").String()[0:2]) + "/" + value.Get("hash").String(),
        assetpath, value.Get("hash").String(), key.String())
    } else if Sha1Sum(assetpath + value.Get("hash").String()) != value.Get("hash").String() {
      // Hash is wrong redownload
      DownloadAsset("https://resources.download.minecraft.net/" + string(value.Get("hash").String()[0:2]) + "/" + value.Get("hash").String(),
        assetpath, value.Get("hash").String(), key.String())
    }


    return true
  })
}
func DownloadAsset(URL string, Path string, FileName string, PrettyFileName string) {
  os.MkdirAll(Path, os.ModePerm)
  os.MkdirAll(GetMcBoopDir() + "assets/virtual/legacy/" + path.Dir(PrettyFileName), os.ModePerm)
  fmt.Println("Downloading:", PrettyFileName)
  asset := ReadRemote(URL)
  WriteFile(asset, Path + FileName)
  WriteFile(asset, GetMcBoopDir() + "assets/virtual/legacy/" + PrettyFileName)
}
