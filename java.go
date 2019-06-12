package main

import "os"
import "fmt"
import "path/filepath"
import "github.com/mholt/archiver"

func InstallJava() {

  hash := "96d24d94c022b3e414b612cae8829244329d71ad2cce790f099c020f33247e7e"
  url := "https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk8u212-b04/OpenJDK8U-jre_x64_linux_hotspot_8u212b04.tar.gz"

  if !CheckForFile(GetMcBoopDir() + "java.tar.gz") {
    DownloadJava(url)
  }

  if Sha256Sum(GetMcBoopDir() + "java.tar.gz") != hash {
    DownloadJava(url)
  }

}

func DownloadJava(URL string) {
  fmt.Println("Downloading Java")
  WriteFile(ReadRemote(URL), GetMcBoopDir() + "java.tar.gz")
  os.RemoveAll(GetMcBoopDir() + "java/")
  fmt.Println("Extracting Java")
  archiver.Unarchive(GetMcBoopDir() + "java.tar.gz", GetMcBoopDir())
  files, _ := filepath.Glob(GetMcBoopDir() + "jdk*")
  os.Rename(files[0], GetMcBoopDir() + "java")
}
