package main

import "bytes"
import "net/http"
import "io/ioutil"

func GetRemoteText(URL string) (string) {
  client := &http.Client{}
  req, _ := http.NewRequest("GET", URL, nil)
  req.Header.Add("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:67.0) Gecko/20100101 Firefox/67.0")
  resp, _ := client.Do(req)
  defer resp.Body.Close()
  byte, _ := ioutil.ReadAll(resp.Body)
  return string(byte)
}

func ReadRemote(URL string) ([]byte) {
  client := &http.Client{}
  req, _ := http.NewRequest("GET", URL, nil)
  req.Header.Add("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:67.0) Gecko/20100101 Firefox/67.0")
  resp, _ := client.Do(req)
  defer resp.Body.Close()
  byte, _ := ioutil.ReadAll(resp.Body)
  return byte
}

func PostRemote(Data []byte, URL string) (string) {
  client := &http.Client{}
  req, _ := http.NewRequest("POST", URL, bytes.NewBuffer(Data))
  req.Header.Add("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:67.0) Gecko/20100101 Firefox/67.0")
  resp, _ := client.Do(req)
  defer resp.Body.Close()
  byte, _ := ioutil.ReadAll(resp.Body)
  return string(byte)
}
