/* eslint-disable */
/**
   * Copyright 2018 Kais OMRI [kais.omri.int@gmail.com]
   *
   * Licensed under the Apache License, Version 2.0 (the "License");
   * you may not use this file except in compliance with the License.
   * You may obtain a copy of the License at
   *
   *     http://www.apache.org/licenses/LICENSE-2.0
   *
   * Unless required by applicable law or agreed to in writing, software
   * distributed under the License is distributed on an "AS IS" BASIS,
   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   * See the License for the specific language governing permissions and
   * limitations under the License.
   */
import qs from 'qs'
import client from './restClient'


// Send a request to the login URL and save the returned JWT
export function login(context, creds, redirect) {
  let data = {
    'grant_type': 'password',
    'scope': 'openid',
    'username': creds.username,
    'password': creds.password,
    'client_id': 'ui'
  }
  return new Promise(function(resolve, reject) {
  client.call(
    context,
    'post',
    'auth/access_token',
    qs.stringify(data),
    { 'content-type': 'application/x-www-form-urlencoded' }
  ).then(function (response) {
    sessionStorage.setItem('access_token', response.data.access_token)
    localStorage.setItem('refresh_token', response.data.refresh_token)
    // Redirect to a specified route
    if (redirect) {
      context.$router.push({ path: `${redirect}` })
    }
    resolve(response)
  }).catch(function (error) {
    console.log('problem')
    reject(error)
  })
 })
}
// To log out, we just need to remove the token
export function logout() {
  sessionStorage.removeItem('access_token')
  localStorage.removeItem('refresh_token')
}
