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

export function getProperties (application) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      let appProperties = []
      appProperties.push({ 'key': 'app.locale', 'value': 'US-en' })
      appProperties.push({ 'key': 'app.author', 'value': 'Kais OMRI' })
      appProperties.push({ 'key': 'app.region', 'value': 'West Europe' })
      appProperties.push({ 'key': 'app.isActive', 'value': 'true' })
      resolve(appProperties)
    }, 1000)
  })
}

export function getAllEnvs (context) {
  return client.call(
    context,
    'get',
    '/api/environments'
  )
}

export function createEnv (context, env) {
  return client.call(
    context,
    'post',
    '/api/environments',
    env
  )
}

export function saveEnv (context, env) {
  return client.call(
    context,
    'put',
    '/api/environments/' + env.code,
    env
  )
}

export function getAllApps () {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      let envs = []
      envs.push({ 'code': 'APP01', 'name': 'Relmongo' })
      envs.push({ 'code': 'APP02', 'name': 'Hystrix' })
      envs.push({ 'code': 'APP03', 'name': 'Docker' })
      envs.push({ 'code': 'APP04', 'name': 'Jenkins' })
      resolve(envs)
    }, 1000)
  })
}

export function getUsers (pattern) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      let data = {
        users: [],
        total: 500
      }
      data.users.push({ 'id': 'USR01', 'email': 'email@test.com', 'firstName': 'first', 'lastName': 'lasto' })
      data.users.push({ 'id': 'USR02', 'email': 'email@test.com', 'firstName': 'first', 'lastName': 'last' })
      data.users.push({ 'id': 'USR03', 'email': 'email@test.com', 'firstName': 'first', 'lastName': 'last' })
      data.users.push({ 'id': 'USR04', 'email': 'email@test.com', 'firstName': 'first', 'lastName': 'last' })
      resolve(data)
    }, 1000)
  })
}
