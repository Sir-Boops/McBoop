package main

import "os"
import "fmt"
import "runtime"

func main() {

  // Order of events
  // 0: Check for launcher update
  // 1: Parse commands ( If user dosn't need to login then just run it and quit )
  // 2: check for and verify java ( Run test java command to make sure it works! )
  // 3: Login user to mojang

  if runtime.GOOS != "linux" && runtime.GOOS != "windows" {
    fmt.Println("Sorry McBoop only currently supports Linux and Windows")
    os.Exit(0)
  }

  LauncherUpdate()

  // Make sure the ~/.mcboop dir is present
  if !CheckForFile(GetMcBoopDir()) {
    os.MkdirAll(GetMcBoopDir(), os.ModePerm)
  }

  ArgsParse()
}
