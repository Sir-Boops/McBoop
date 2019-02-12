package main

import "os"

func args_parse(args []string) {
  for i := 1; i < len(args); i++ {
    if args[i] == "--status" {
      mojang_status()
      os.Exit(0)
    }
  }
}
