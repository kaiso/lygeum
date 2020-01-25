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
    let defaultHeaders = this.getAuthHeader(context)
    let concreteHeaders = { ...headers, ...defaultHeaders }
    return new Promise(function (resolve, reject) {
      context.$http({
        'method': method,
        'url': uri,
        'headers': concreteHeaders,
        'data': data,
        'params': params,
        'responseType': responseType
      }).then(function (response) {
        resolve(response)
      }).catch(function (error) {
        if (error.response && error.response.status === 401) {
          context.$storage.remove('user')
          context.$storage.remove('access_token')
          context.$storage.remove('refresh_token')
          context.$router.push({ path: '/auth/login' })
        } else if (error.response && error.response.status === 403) {
          context.$store.dispatch('notification/open', {
            message: context.$i18n.t('auth.access.error', { error: error }),
            status: 'error'
          })
        } else {
          console.log('HTTP error ==>  ', error.response.status, '\nDetails: ', JSON.stringify(error.response.data))
        }
        reject(error)
      })
    })
  },
  getAuthHeader(context) {
    let accessToken = context.$storage.get('access_token')
    if (typeof accessToken !== 'undefined') {
      return {
        'Authorization': 'Bearer ' + accessToken
      }
    } else {
      return {}
    }
  }
}
