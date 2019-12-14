package main

import "os"
import "fmt"
import "runtime"
import "path/filepath"
import "github.com/mholt/archiver"

func InstallJava() {

  // Currently supports Linux and Windows

  var hash string
  var url string
  var filename string

  if runtime.GOOS == "linux" {
    hash = "bd06b84a1fc10e0a555431bc49a84e86df45de0be93c8ee4d09d13513219843b"
    url = "https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk8u232-b09/OpenJDK8U-jre_x64_linux_hotspot_8u232b09.tar.gz"
    filename = "java.tar.gz"
  } else if runtime.GOOS == "windows" {
    hash = "3e56345e39d5ee04cae8d9a253d2529f553b14041a22c23fb2730504015202a9"
    url = "https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk8u232-b09/OpenJDK8U-jre_x64_windows_hotspot_8u232b09.zip"
    filename = "java.zip"
  }

  if hash == "" {
    // Unsupported OS
    fmt.Println("Please use Linux or Windows or open an issue!")
    os.Exit(0)
  }

  if !CheckForFile(GetMcBoopDir() + filename) {
    DownloadJava(url, filename)
  }

  if Sha256Sum(GetMcBoopDir() + filename) != hash {
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
