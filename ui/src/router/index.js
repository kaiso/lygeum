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
import Vue from 'vue'
import Router from 'vue-router'
import Dashboard from '@/components/Dashboard'
import Settings from '@/components/Settings'
import Configurations from '@/components/Configurations'
import Administration from '@/components/Administration'
import Properties from '@/components/config/Properties'
import Environments from '@/components/config/Environments'
import Applications from '@/components/config/Applications'
import Users from '@/components/admin/Users'
import UserEdit from '@/components/admin/UserEdit'
import Login from '@/components/auth/Login'
import Wizard from '@/components/start/Wizard'
import Account from '@/components/account/Account'
import Password from '@/components/account/Password'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'dashboard',
      component: Dashboard
    },
    {
      path: '/settings',
      name: 'settings',
      component: Settings
    },
    {
      path: '/configurations',
      name: 'configurations',
      component: Configurations
    },
    {
      path: '/administration',
      name: 'administration',
      component: Administration
    },
    {
      path: '/config/properties',
      name: 'properties',
      component: Properties
    },
    {
      path: '/config/environments',
      name: 'environments',
      component: Environments
    },
    {
      path: '/config/applications',
      name: 'applications',
      component: Applications
    },
    {
      path: '/admin/users',
      name: 'users',
      component: Users
    },
    {
      path: '/admin/usere',
      name: 'useredit',
      component: UserEdit,
      props: true
    },
    {
      path: '/auth/login',
      name: 'login',
      component: Login
    },
    {
      path: '/firstrun',
      name: 'firstrun',
      component: Wizard
    },
    {
      path: '/account',
      name: 'account',
      component: Account,
      props: true
    },
    {
      path: '/password',
      name: 'password',
      component: Password,
      props: true
    }
  ]
})
