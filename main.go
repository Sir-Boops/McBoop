package main

import "fmt"

func main() () {

  // TODO: Add support for windows

  // Init folder check
  // if $HOME/.mcboop is not there create it
  // Has tailing slash attached already!
  mcboop_home_path := init_folder_check() // Returns: mcboop_home_path
  fmt.Println(mcboop_home_path)

  // Check the java install
  java_bin := check_java_install(mcboop_home_path) // Returns java bin
  fmt.Println(java_bin)

}
