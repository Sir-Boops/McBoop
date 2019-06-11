package main

import "os"
import "os/user"
import "io/ioutil"

func GetMcBoopDir() (string) {
  usr, _ := user.Current();
  return usr.HomeDir + "/" + ".mcboop" + "/"
}

func CheckForFile(Path string) (bool) {
  // True if found
  // False if not
  ans := false
  if _, err := os.Stat(Path); err == nil {
    // File/Folder found!
    ans = true
  }
  return ans
}

func ReadTextFile(Path string) ([]byte) {
  bytes, _ := ioutil.ReadFile(Path)
  return bytes
}

func WriteTextFile(Data []byte, Path string) {
  // Deletes the file before writing to it
  if CheckForFile(Path) {
    os.Remove(Path)
  }
  ioutil.WriteFile(Path, Data, os.ModePerm)
}
