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
const messages = {
  generic: {
    nodata: 'No data available'
  },
  wizard: {
    header: 'Lygeum first run',
    title: 'Lygeum first run',
    stepper: {
      start: {
        header: 'Welcome',
        message: ['Welcome to Lygeum, this product is licensed to Kais OMRI under the Apache License version 2.0',
          'The wizard will guide you to configure Lygeum startup settings ',
          'Configuration is no longer a mess, let\'s enjoy it']
      },
      admin: {
        header: 'Lygeum password',
        message: 'Please choose the Lygeum user (super user) password'
      },
      passphrase: {
        header: 'Lygeum master passphrase',
        message: ['Please choose the Lygeum master passphrase',
          'The passphrase will be used to encrypt tokens generated by Lygeum']
      }
    }
  },
  fields: {
    username: 'Username',
    password: 'Password',
    passphrase: 'Passphrase'
  },
  actions: {
    ok: 'Ok',
    confirm: 'Confirm',
    cancel: 'Cancel',
    continue: 'Continue',
    login: {
      title: 'Log In',
      button: 'Login'
    }
  },
  layout: {
    dashboard: 'Dashboard',
    settings: 'Settings',
    configurations: 'Configurations',
    administration: 'Administration'
  },
  envs: {
    env: 'Environment | Environments',
    actions: {
      add_tooltip: 'Add a new environment',
      save_tooltip: 'Save this configuration'
    }
  },
  props: {
    prop: 'Property | Properties',
    actions: {
      download_tooltip: 'Download this configuration',
      upload_tooltip: 'Upload configuration file',
      add_tooltip: 'Add a new property',
      save_tooltip: 'Save this configuration',
      loadall_tooltip: 'Load all'
    }
  },
  apps: {
    application: 'Application | Applications'
  },
  file: {
    choose: 'Choose file to upload',
    chosen: 'File chosen',
    type: 'File type'
  },
  admin: {
    id: 'ID',
    user: 'User | Users',
    role: 'Role | Roles',
    email: 'Email',
    firstName: 'First Name',
    lastName: 'Last Name',
    adduser: 'Add user',
    edituser: 'Edit user',
    createuser: 'Create user',
    assignedroles: 'Assigned roles',
    availableroles: 'Available roles'
  }

}
export default messages
