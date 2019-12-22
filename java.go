package main

import "os"
import "fmt"
import "runtime"
import "path/filepath"
import "github.com/tidwall/gjson"
import "github.com/mholt/archiver"

func InstallJava() {

  // Currently supports Linux and Windows
  fmt.Println("==== Installing/Verifying Java ====")

  var hash string
  var url string
  var filename string
  var files_sums gjson.Result
  java_json := GetRemoteText("https://mcboop.boops.org/McBoop/Support-Files/openjdk/sha256sums.json")

  if runtime.GOOS == "linux" {
    hash = gjson.Get(java_json, "linux-sum").String()
    url = "https://mcboop.boops.org/McBoop/Support-Files/openjdk/" + gjson.Get(java_json, "linux-filename").String()
    files_sums = gjson.Parse(GetRemoteText(url + ".json"))
    filename = "java.tar.gz"
  } else if runtime.GOOS == "windows" {
    hash = gjson.Get(java_json, "windows-sum").String()
    url = "https://mcboop.boops.org/McBoop/Support-Files/openjdk/" + gjson.Get(java_json, "windows-filename").String()
    files_sums = gjson.Parse(GetRemoteText(url + ".json"))
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

  // Verify all Java files
  files_sums.ForEach(func(key, value gjson.Result) bool {
    if !CheckForFile(GetMcBoopDir() + "java/" + value.Get("name").String()) || Sha256Sum(GetMcBoopDir() + "java/" + value.Get("name").String()) != value.Get("sha256sum").String() {
      ExtractJava(filename)
      return false
    }
    return true
  })

  fmt.Println("")
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

func ExtractJava(Filename string) {
  fmt.Println("Extracting Java")
  os.RemoveAll(GetMcBoopDir() + "java/")
  archiver.Unarchive(GetMcBoopDir() + Filename, GetMcBoopDir())
  files, _ := filepath.Glob(GetMcBoopDir() + "jdk*")
  os.Rename(files[0], GetMcBoopDir() + "java")
}
