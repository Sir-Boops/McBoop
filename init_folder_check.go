package main

import "os"
import "fmt"
import "os/user"

func init_folder_check() (string) {

  // Get users home dir
  usr, err := user.Current()

  if err != nil {
    fmt.Println(err)
    os.Exit(1)
  }

  usr_home := usr.HomeDir + string(os.PathSeparator)
  mcboop_home_path := usr_home + ".mcboop" + string(os.PathSeparator)

  // Check to make sure .mcboop is inside the home dir
  if _, err := os.Stat(mcboop_home_path); err != nil {
    err := os.MkdirAll(mcboop_home_path, os.ModePerm)
    if err != nil {
      fmt.Println(err)
      os.Exit(1)
    }
  }

  return mcboop_home_path
}
