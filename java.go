package main

import "os"
import "fmt"
import "runtime"
import "path/filepath"
import "github.com/tidwall/gjson"
import "github.com/mholt/archiver"

func InstallJava() {

  // Currently supports Linux and Windows

  var hash string
  var url string
  var filename string
  java_json := GetRemoteText("https://boops-deploy.s3.amazonaws.com/McBoop/Support-Files/openjdk/sha256sums.json")

  if runtime.GOOS == "linux" {
    hash = gjson.Get(java_json, "linux-sum").String()
    url = "https://boops-deploy.s3.amazonaws.com/McBoop/Support-Files/openjdk/" + gjson.Get(java_json, "linux-filename").String()
    filename = "java.tar.gz"
  } else if runtime.GOOS == "windows" {
    hash = gjson.Get(java_json, "windows-sum").String()
    url = "https://boops-deploy.s3.amazonaws.com/McBoop/Support-Files/openjdk/" + gjson.Get(java_json, "windows-filename").String()
    filename = "java.zip"
  }

  if !CheckForFile(GetMcBoopDir() + filename) {
    fmt.Println("Installing Java, This can take a bit")
    DownloadJava(url, filename)
  }

  if Sha256Sum(GetMcBoopDir() + filename) != hash {
    fmt.Println("Updating Java, This can take a bit")
    DownloadJava(url, filename)
  }

}

func DownloadJava(URL string, Filename string) {
  fmt.Println("Downloading Java")
  WriteFile(ReadRemote(URL), GetMcBoopDir() + Filename)
  os.RemoveAll(GetMcBoopDir() + "java/")
  fmt.Println("Extracting Java")
  archiver.Unarchive(GetMcBoopDir() + Filename, GetMcBoopDir())
  files, _ := filepath.Glob(GetMcBoopDir() + "jdk*")
  os.Rename(files[0], GetMcBoopDir() + "java")
}
