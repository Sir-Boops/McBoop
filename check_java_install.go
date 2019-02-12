package main

import "os"
import "io"
import "fmt"
import "time"
import "strings"
import "strconv"
import "os/exec"
import "net/http"
import "encoding/hex"
import "crypto/sha256"
import "github.com/mholt/archiver"

func check_java_install(mcboop_home_path string) (string) {

  // When bumping java versions be sure to bump
  // All 3 hashes!
  java_path := mcboop_home_path + "java" + string(os.PathSeparator)
  java_version := "11.0.2"
  java_url := "https://download.java.net/java/GA/jdk11/9/GPL/openjdk-11.0.2_linux-x64_bin.tar.gz"
  java_hash := "99be79935354f5c0df1ad293620ea36d13f48ec3ea870c838f20c504c9668b57"

  // The final calucated java path
  ans := ""

  // Check if the java path if present
  if _, err := os.Stat(java_path + "jdk-" + java_version); err != nil {
    // We need to install java

    // Download the archive
    // Open the connection
    resp, err := http.Get(java_url)
    if err != nil {
      fmt.Println(err)
      os.Exit(1)
    }
    defer resp.Body.Close()

    // Create the file we are saving to
    file_name := strings.Split(java_url, "/")[len(strings.Split(java_url, "/")) - 1]
    out, err := os.Create(mcboop_home_path + file_name)
    if err != nil {
      fmt.Println(err)
      os.Exit(1)
    }
    defer out.Close()

    // Get file length
    file_length, err := strconv.ParseInt(resp.Header["Content-Length"][0], 10, 64)
    if err != nil {
      fmt.Println(err)
      os.Exit(1)
    }

    // fork off and save the file
    go io.Copy(out, resp.Body)

    // Wait for background DL to finish and print progress
    done := false
    for !done {
      fi, _ := os.Stat(mcboop_home_path + file_name)
      if fi.Size() >= file_length {
        done = true
        fmt.Println(fi.Size() / 1048576, "of", file_length / 1048576, "Mbytes downloaded")
      } else {
        fmt.Printf("%v of %v Mbytes downloaded \r", fi.Size() / 1048576, file_length / 1048576)
        time.Sleep(20 * time.Millisecond)
      }
    }

    // Now check the hash of the downloaded file
    // Open the file for reading
    dld_file, err := os.Open(mcboop_home_path + file_name)
    if err != nil {
      fmt.Println(err)
      os.Exit(1)
    }
    defer dld_file.Close()

    // Create a new hash and read the file into it
    hash := sha256.New()
    if _, err := io.Copy(hash, dld_file); err != nil {
      fmt.Println(err)
      os.Exit(1)
    }

    // Get the hash
    sum := hex.EncodeToString(hash.Sum(nil))

    // Compare the sums
    if sum != java_hash {
      fmt.Println("Bad download please try downloading again!")
      os.Exit(0)
    } else {

      // Download is done and confirmed
      // Now extract and verify that it works
      err = archiver.Unarchive(mcboop_home_path + file_name, mcboop_home_path + "java")
      if err != nil {
        fmt.Println(err)
        os.Exit(1)
      }

      java_bin := mcboop_home_path + "java" + string(os.PathSeparator) + "jdk-" + java_version + string(os.PathSeparator) +
      "bin" + string(os.PathSeparator) + "java"

      // Now run it to make sure it works
      cmd := exec.Command(java_bin, "--version")
      err := cmd.Run()
      if err != nil {
        fmt.Println(err)
        os.Exit(1)
      }
      ans = java_bin
    }


  } else {
    // Check that java works

    java_bin := mcboop_home_path + "java" + string(os.PathSeparator) + "jdk-" + java_version + string(os.PathSeparator) +
    "bin" + string(os.PathSeparator) + "java"

    // Now run it to make sure it works
    cmd := exec.Command(java_bin, "--version")
    err := cmd.Run()
    if err != nil {
      fmt.Println(err)
      os.Exit(1)
    }
    ans = java_bin
  }

  return ans
}
