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

import client from './restClient';

export function getAllEnvs(context) {
  return client.call(context, 'get', '/api/environments');
}

export function createEnv(context, env) {
  return client.call(context, 'post', '/api/environments', env);
}

export function saveEnv(context, env) {
  return client.call(context, 'put', '/api/environments/' + env.code, env);
}

export function deleteEnv(context, env) {
  return client.call(context, 'delete', '/api/environments/' + env.code);
}

export function getAllApps(context) {
  return client.call(context, 'get', '/api/applications');
}

export function createApp(context, app) {
  return client.call(context, 'post', '/api/applications', app);
}

export function saveApp(context, app) {
  return client.call(context, 'put', '/api/applications/' + app.code, app);
}

export function deleteApp(context, app) {
  return client.call(context, 'delete', '/api/applications/' + app.code);
}

export function getProperties(context, environment, application) {
  return client.call(context, 'get', '/api/properties', {}, [], {
    env: environment.code,
    app: application.code
  });
}

export function getCommitHistory(context, environment, application) {
  return client.call(context, 'get', '/api/properties/commits', {}, [], {
    env: environment.code,
    app: application.code
  });
}

export function saveProperties(context, environment, application, properties) {
  return client.call(context, 'post', '/api/properties', properties, [], {
    env: environment.code,
    app: application.code
  });
}

export function uploadProperties(context, environment, application, file) {
  var data = new FormData();
  data.append('file', file);
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
  );
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
  );
}

export function deleteProperty(context, prop, environment) {
  return client.call(
    context,
    'delete',
    '/api/properties/' + prop.code,
    {},
    [],
    {
      env: environment.code
    }
  );
}

export function getRoles(context) {
  return client.call(context, 'get', '/api/admin/roles');
}

export function getUsers(context, pattern) {
  return client.call(context, 'get', `/api/admin/users?search=${pattern}`);
}

export function saveUser(context, user) {
  return client.call(context, 'put', `/api/admin/users/${user.code}`, user);
}

export function createUser(context, user) {
  return client.call(context, 'post', `/api/admin/users`, user);
}

export function deleteUser(context, user) {
  return client.call(context, 'delete', `/api/admin/users/${user.code}`);
}

export function getClients(context) {
  return client.call(context, 'get', '/api/admin/clients');
}

export function saveClient(context, arg) {
  return client.call(context, 'put', `/api/admin/clients/${arg.code}`, arg);
}

export function createClient(context, arg) {
  return client.call(context, 'post', `/api/admin/clients`, arg);
}

export function deleteClient(context, arg) {
  return client.call(context, 'delete', `/api/admin/clients/${arg.code}`);
}

export function getMe(context) {
  return client.call(context, 'get', `/api/accounts/me`);
}

export function updateMe(context, user) {
  return client.call(context, 'put', `/api/accounts/${user.code}`, user);
}

export function updatePassword(context, code, password) {
  return client.call(
    context,
    'put',
    `/api/accounts/${code}/password`,
    password
  );
}

export function getSystemInformation(context) {
  return client.call(context, 'get', '/api/system/info');
}
