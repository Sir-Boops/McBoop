package main

import "os"
//import "fmt"

func main() {

  // Order of events
  // 0: Check for launcher update
  // 1: Parse commands ( If user dosn't need to login then just run it and quit )
  // 1: Check for ~/.mcboop folder
  // 2: check for and verify java ( Run test java command to make sure it works! )
  // 3: Login user to mojang

  LauncherUpdate()

  ArgsParse(os.Args)
}
