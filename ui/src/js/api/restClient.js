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

export default {
  call(context, method, uri, data, headers, params, responseType = 'json') {
    let defaultHeaders = this.getAuthHeader()
    let concreteHeaders = { ...headers, ...defaultHeaders }
    console.log('data ', data)
    return new Promise(function (resolve, reject) {
      context.$http({
        'method': method,
        'url': uri,
        'headers': concreteHeaders,
        'data': data,
        'params': params,
        'responseType': responseType
      }).then(function (response) {
        console.log('REST Client Response ==> ', response)
        resolve(response)
      }).catch(function (error) {
        if (error.response &&
          error.response.data.error_description &&
          error.response.data.error_description.startsWith('Invalid access token')) {
          context.$router.push({ path: '/auth/login' })
          resolve('authentication required')
        } else {
          // Something happened in setting up the request that triggered an Error
          console.log('REST Client Error ==>  ', error.message, '\nDetails: ', error.response)
          reject(error.response ? `status: ${error.response.data.status}, message: ${error.response.data.message}` : error)
        }
      })
    })
  },
  getAuthHeader() {
    let accessToken = sessionStorage.getItem('access_token')
    if (typeof accessToken !== 'undefined') {
      return {
        'Authorization': 'Bearer ' + accessToken
      }
    } else {
      return {}
    }
  }
}
