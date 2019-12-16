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
    nodata: 'No data available',
    confirm_delete: 'Are you sure to delete {target} ?'
  },
  auth: {
    login: {
      error: 'Failed to login, cause: [<i>{error}</i>]'
    }
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
    close: 'Close',
    save: 'Save',
    cancel: 'Cancel',
    continue: 'Continue',
    login: {
      title: 'Log In',
      button: 'Login'
    },
    logout: {
      title: 'Log Out',
      button: 'Logout'
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
    },
    notifications: {
      save: {
        success: 'Environments have been successfully saved',
        error: 'Failed to save environments, cause: [<i>{error}</i>]'
      },
      delete: {
        success: 'Environment <b>{target}</b> has been deleted successfully',
        error: 'Failed to delete environment <b>{target}</b>, cause: [<i>{error}</i>]'
      },
      load: {
        error: 'Failed to load environments, cause: [<i>{error}</i>]'
      }
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
    },
    notifications: {
      save: {
        success: 'Properties have been successfully saved',
        error: 'Failed to save properties, cause: [<i>{error}</i>]'
      },
      delete: {
        success: 'Property <b>{target}</b> has been deleted successfully',
        error: 'Failed to delete property <b>{target}</b>, cause: [<i>{error}</i>]'
      },
      load: {
        error: 'Failed to load properties, cause: [<i>{error}</i>]'
      }
    }
  },
  apps: {
    application: 'Application | Applications',
    actions: {
      add_tooltip: 'Add a new application',
      save_tooltip: 'Save this configuration'
    },
    notifications: {
      save: {
        success: 'Applications have been successfully saved',
        error: 'Failed to save applications, cause: [<i>{error}</i>]'
      },
      delete: {
        success: 'Application <b>{target}</b> has been deleted successfully',
        error: 'Failed to delete application <b>{target}</b>, cause: [<i>{error}</i>]'
      },
      load: {
        error: 'Failed to load applications, cause: [<i>{error}</i>]'
      }
    }
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
    password: 'Password',
    firstName: 'First Name',
    lastName: 'Last Name',
    adduser: 'Add user',
    edituser: 'Edit user',
    createuser: 'Create user',
    assignedroles: 'Assigned roles',
    availableroles: 'Available roles',
    notifications: {
      roles: {
        load: {
          error: 'Failed to load roles, cause: [<i>{error}</i>]'
        }
      }
    }
  }

}
export default messages
