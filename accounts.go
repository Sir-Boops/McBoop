package main

import "encoding/json"
import "github.com/tidwall/gjson"

type AuthRequest struct {
  Username string `json:"username"`
  Password string `json:"password"`
  Agent AuthRequestAgent `json:"agent"`
}
type AuthRequestAgent struct {
  Name string `json:"name"`
  Version int `json:version`
}
type RefreshRequest struct {
  Access string `json:"accessToken"`
  Client string `json:"clientToken"`
}
type UserAccount struct {
  Username string `json:"username"`
  AccessToken string `json:"accessToken"`
  ClientToken string `json:"clientToken"`
  UUID string `json:"uuid"`
  Default bool `json:"default"`
}

func AuthAccount(Username string, Password string) (string) {
  auth_agent := &AuthRequestAgent{
    Name: "Minecraft",
    Version: 1}

  auth_request, _ := json.Marshal(&AuthRequest{
    Username: Username,
    Password: Password,
    Agent: *auth_agent})

  return PostRemote(auth_request, "https://authserver.mojang.com/authenticate")
}

func AddAccount(Auth_JSON string) {
  // Now Check for accounts.json
  if !CheckForFile(GetMcBoopDir() + "accounts.json") {
    // File not found so this is a new account
    // So add and set default flag
    UpdateAccount(gjson.Get(Auth_JSON, "selectedProfile.name").String(), gjson.Get(Auth_JSON, "accessToken").String(),
      gjson.Get(Auth_JSON, "clientToken").String(), gjson.Get(Auth_JSON, "selectedProfile.id").String(), true)
  } else {
    // File found so just add
    UpdateAccount(gjson.Get(Auth_JSON, "selectedProfile.name").String(), gjson.Get(Auth_JSON, "accessToken").String(),
      gjson.Get(Auth_JSON, "clientToken").String(), gjson.Get(Auth_JSON, "selectedProfile.id").String(), false)
  }
}

func UpdateAccount(Username string, AccessToken string, ClientToken string, UUID string, Default bool) {

  // If there is not file nothing is loaded but an empty array else
  // objects are loaded into the array
  var user_array []UserAccount

  // Make sure accounts.json is there
  if CheckForFile(GetMcBoopDir() + "accounts.json") {
    // File found
    json.Unmarshal(ReadTextFile(GetMcBoopDir() + "accounts.json"), &user_array)
  }

  // Check to see if the user we are updating is inside the accounts.json file or not
  if len(user_array) >= 1 {
    // Remove that account so we can easly re-add it
    for i := 0; i < len(user_array); i++ {
      if user_array[i].Username == Username {
        user_array[i] = user_array[len(user_array)-1]
        user_array[len(user_array)-1] = UserAccount{}
        user_array = user_array[:len(user_array)-1]
      }
    }
  }

  user_array = append(user_array, UserAccount{
    Username: Username,
    AccessToken: AccessToken,
    ClientToken: ClientToken,
    UUID: UUID,
    Default: Default})

  new_file_data, _ := json.Marshal(user_array)
  WriteTextFile(new_file_data, GetMcBoopDir() + "accounts.json")
}
