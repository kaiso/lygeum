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
    context.$storage.set('access_token', response.data.access_token, { ttl: 30 * 60 * 60 * 24 * 1000 })
    context.$storage.set('refresh_token', response.data.refresh_token, { ttl: 0 })
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
export function logout(context) {
  client.call(
    context,
    'get',
    'auth/logout'
  ).then(function (response) {
    context.$router.push({ path: '/auth/login' })
  }).catch (function (error) {
    console.log('failed to logout', error)
  })
  context.$storage.remove('user')
  context.$storage.remove('access_token')
  context.$storage.remove('refresh_token')
}
