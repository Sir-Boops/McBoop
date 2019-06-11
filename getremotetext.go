package main

import "net/http"
import "io/ioutil"

func GetRemoteText(URL string) (string) {
  resp, _ := http.Get(URL)
  defer resp.Body.Close()
  byte, _ := ioutil.ReadAll(resp.Body)
  return string(byte)
}
