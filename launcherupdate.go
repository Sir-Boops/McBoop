package main

import "os"
import "fmt"
import "path/filepath"

func LauncherUpdate() {

  expath, _ := filepath.Abs(os.Args[0])
  local_sum := Sha256Sum(expath)
  remote_sum := GetRemoteText("https://boops-deploy.s3.amazonaws.com/McBoop/McBoop.sha256")

  if local_sum != remote_sum {
    fmt.Println("A launcher update has been found and will now be downloaded")
    WriteFile(ReadRemote("https://boops-deploy.s3.amazonaws.com/McBoop/McBoop"), expath)
    fmt.Println("Update done, re-run your last command and enjoy! :)")
    os.Exit(0)
  }

  fmt.Println(remote_sum, expath)
}
