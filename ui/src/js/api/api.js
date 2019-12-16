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

import client from './restClient'

export function getAllEnvs(context) {
  return client.call(
    context,
    'get',
    '/api/environments'
  )
}

export function createEnv(context, env) {
  return client.call(
    context,
    'post',
    '/api/environments',
    env
  )
}

export function saveEnv(context, env) {
  return client.call(
    context,
    'put',
    '/api/environments/' + env.code,
    env
  )
}

export function deleteEnv(context, env) {
  return client.call(
    context,
    'delete',
    '/api/environments/' + env.code
  )
}

export function getAllApps(context) {
  return client.call(
    context,
    'get',
    '/api/applications'
  )
}

export function createApp(context, app) {
  return client.call(
    context,
    'post',
    '/api/applications',
    app
  )
}

export function saveApp(context, app) {
  return client.call(
    context,
    'put',
    '/api/applications/' + app.code,
    app
  )
}

export function deleteApp(context, app) {
  return client.call(
    context,
    'delete',
    '/api/applications/' + app.code
  )
}

export function getProperties(context, environment, application) {
  return client.call(
    context,
    'get',
    '/api/properties',
    {},
    [],
    {
      env: environment.code,
      app: application.code
    }
  )
}

export function saveProperties(context, environment, application, properties) {
  return client.call(
    context,
    'post',
    '/api/properties',
    properties,
    [],
    {
      env: environment.code,
      app: application.code
    }
  )
}

export function uploadProperties(context, environment, application, file) {
  var data = new FormData()
  data.append('file', file)
  return client.call(
    context,
    'post',
    '/api/properties/upload',
    data,
    { 'Content-Type': 'multipart/form-data' },
    {
      env: environment.code,
      app: application.code
    }
  )
}

export function downloadProperties(context, environment, application, layout) {
  return client.call(
    context,
    'get',
    '/api/properties/download',
    {},
    {},
    {
      env: environment.code,
      app: application.code,
      layout: layout
    },
    'blob'
  )
}

export function deleteProperty(context, prop) {
  return client.call(
    context,
    'delete',
    '/api/properties/' + prop.code
  )
}

export function getRoles(context) {
  return client.call(
    context,
    'get',
    '/api/admin/roles'
  )
}

export function getUsers(context) {
  return client.call(
    context,
    'get',
    '/api/admin/users'
  )
}

export function saveUser(context, user) {
  return client.call(
    context,
    'put',
    `/api/admin/users/${user.code}`,
    user
  )
}

export function createUser(context, user) {
  return client.call(
    context,
    'post',
    `/api/admin/users`,
    user
  )
}

export function getMe(context) {
  return client.call(
    context,
    'get',
    `/api/account/me`
  )
}
