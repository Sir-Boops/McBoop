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
    hash = "96d24d94c022b3e414b612cae8829244329d71ad2cce790f099c020f33247e7e"
    url = "https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk8u212-b04/OpenJDK8U-jre_x64_linux_hotspot_8u212b04.tar.gz"
    filename = "java.tar.gz"
  } else if runtime.GOOS == "windows" {
    hash = "a83d8ed72766308d28932cbe65012b526cc977b669832f8868e46c1450b44748"
    url = "https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk8u212-b04/OpenJDK8U-jre_x86-32_windows_hotspot_8u212b04.zip"
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
