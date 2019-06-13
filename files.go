package main

import "os"
import "io"
import "os/user"
import "io/ioutil"
import "crypto/sha1"
import "crypto/sha256"
import "encoding/hex"

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

func WriteFile(Data []byte, Path string) {
  // Deletes the file before writing to it
  if CheckForFile(Path) {
    os.Remove(Path)
  }
  ioutil.WriteFile(Path, Data, os.ModePerm)
}

func Sha1Sum(Path string) (string) {
  f, _ := os.Open(Path)
  defer f.Close()
  h := sha1.New()
  io.Copy(h, f)
  return hex.EncodeToString(h.Sum(nil))
}

func Sha256Sum(Path string) (string) {
  f, _ := os.Open(Path)
  defer f.Close()
  h := sha256.New()
  io.Copy(h, f)
  return hex.EncodeToString(h.Sum(nil))
}
