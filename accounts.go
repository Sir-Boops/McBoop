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
  AccessToken string `json:"accessToken"`
  ClientToken string `json:"clientToken"`
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
  WriteFile(new_file_data, GetMcBoopDir() + "accounts.json")
}

func ListAccounts() ([]string) {
  var user_array []UserAccount
  var users []string
  json.Unmarshal(ReadTextFile(GetMcBoopDir() + "accounts.json"), &user_array)

  for i := 0; i < len(user_array); i++ {
    users = append(users, user_array[i].Username)
  }

  return users
}

func GetDefaultAccount() (string) {
  var user_array []UserAccount
  default_account := ""
  json.Unmarshal(ReadTextFile(GetMcBoopDir() + "accounts.json"), &user_array)

  for i := 0; i < len(user_array); i++ {
    if user_array[i].Default {
      // Found the default account!
      default_account = user_array[i].Username
    }
  }

  return default_account
}

func GetAccount(Username string) (UserAccount) {
  //Returns a whole UserAccount
  var user_array []UserAccount
  var ans UserAccount
  json.Unmarshal(ReadTextFile(GetMcBoopDir() + "accounts.json"), &user_array)

  for i := 0; i < len(user_array); i++ {
    if user_array[i].Username == Username {
      ans = user_array[i]
    }
  }

  return ans
}

func RefreshAccount(Username string) {

  user := GetAccount(Username)

  refresh_request, _ := json.Marshal(&RefreshRequest{
    AccessToken: user.AccessToken,
    ClientToken: user.ClientToken})

  resp := PostRemote(refresh_request, "https://authserver.mojang.com/refresh")
  UpdateAccount(user.Username, gjson.Get(resp, "accessToken").String(), gjson.Get(resp, "clientToken").String(), user.UUID, user.Default)
}
