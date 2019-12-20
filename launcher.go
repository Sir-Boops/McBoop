package main

import "os"
import "fmt"
import "runtime"
import "path/filepath"

func LauncherUpdate() {

  expath, _ := filepath.Abs(os.Args[0])
  local_sum := Sha256Sum(expath)
  remote_sum := GetRemoteText("https://mcboop.boops.org/McBoop/" + filepath.Base(expath) + ".sha256")

  // Auto clean old binary if there
  if runtime.GOOS == "windows" && CheckForFile(expath + ".old") {
    os.RemoveAll(expath + ".old")
  }

  if local_sum != remote_sum {
    fmt.Println("A launcher update has been found and will now be downloaded")
    if runtime.GOOS == "windows" {
      os.Rename(expath, expath + ".old")
    }
    WriteFile(ReadRemote("https://mcboop.boops.org/McBoop/" + filepath.Base(expath)), expath)
    fmt.Println("Update done, re-run your last command and enjoy! :)")
    os.Exit(0)
  }
}
