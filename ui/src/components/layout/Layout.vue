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
<template>

  <div>
    <v-navigation-drawer
      v-model="drawer"
      v-show="!drawerDisabled"
      clipped
      style="height: calc(100% - 65px);margin-top: 65px;"
      app
    >
      <v-list dense>
        <v-list-tile @click="route('dashboard')">
          <v-list-tile-action>
            <v-icon>dashboard</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>{{ $t("layout.dashboard") }}</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>

        <v-list-tile @click="route('configurations')">
          <v-list-tile-action>
            <v-icon>settings_applications</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>{{ $t("layout.configurations") }}</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>

        <v-list-tile @click="route('administration')">
          <v-list-tile-action>
            <v-icon>chrome_reader_mode</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>{{ $t("layout.administration") }}</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>

        <v-list-tile @click="route('settings')">
          <v-list-tile-action>
            <v-icon>build</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>{{ $t("layout.settings") }}</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
      </v-list>
    </v-navigation-drawer>
    <v-navigation-drawer
      v-model="profileDrawer"
      absolute
      dark
      right
      temporary
    >
      <v-list class="pa-1">
        <v-list-tile>
          <v-list-tile-action>
            <v-btn
              icon
              @click.stop="profileDrawer=!profileDrawer"
            >
            <v-icon>chevron_right</v-icon>
            </v-btn>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>{{ me.username }}</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
        <v-list-tile @click="route('account', {'user': me })">
            <v-list-tile-action>
              <v-icon>home_work</v-icon>
            </v-list-tile-action>
            <v-list-tile-content>
              <v-list-tile-title>{{ $t("account.myaccount") }}</v-list-tile-title>
            </v-list-tile-content>
         </v-list-tile>
        <v-list-tile @click="route('password', {'user': me })">
            <v-list-tile-action>
              <v-icon>vpn_key</v-icon>
            </v-list-tile-action>
            <v-list-tile-content>
              <v-list-tile-title>{{ $t("account.password.update") }}</v-list-tile-title>
            </v-list-tile-content>
         </v-list-tile>
        <v-list-tile
        @click="doLogout">
          <v-list-tile-action>
            <v-icon>exit_to_app</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>{{ $t("actions.logout.button") }}</v-list-tile-title>
          </v-list-tile-content>
         </v-list-tile>
      </v-list>
    </v-navigation-drawer>
    <v-toolbar app fixed clipped-left>
      <v-toolbar-side-icon v-show="!drawerDisabled" @click.stop="drawer = !drawer"></v-toolbar-side-icon>
      <img src="~@/assets/lygeum_logo_orange.png" style="max-width:40px;max-height:40px;"/>
      <img src="~@/assets/lygeum_logotext_orange_b.png" style="max-width:75px;max-height:25px;margin-top:0px;"/>
      <!--<v-toolbar-title style="width:20%;">Lygeum</v-toolbar-title>-->
      <div style="width:100%;">
        <v-toolbar-title style="margin:auto;width:200px">{{title}}</v-toolbar-title>
      </div>
       <v-btn icon v-if="loggedIn" @click.stop="profileDrawer=!profileDrawer">
        <v-icon>account_circle</v-icon>
      </v-btn>
    </v-toolbar>
    <v-content>
      <v-container fluid fill-height>
        <v-layout justify-center align-center>
          <slot name="mainContent"></slot>
        </v-layout>
      </v-container>
    </v-content>
    <v-footer app fixed style="flex-direction:column;">
      <span style="align-self:center;height:100%;line-height:37px">Copyright {{ $t('lygeum.copyright') }}</span>
    </v-footer>
  </div>
</template>

<script>
import { logout } from '@/js/api/auth.js'
import { getMe } from '@/js/api/api'
export default {
  name: 'aps-layout',
  data: () => ({
    profileDrawer: null,
    me: {},
    loggedIn: false
  }),
  props: {
    drawer: { type: Boolean, default: true },
    source: String,
    title: { type: String, default: 'Lygeum' },
    drawerDisabled: { type: Boolean, default: false }
  },
  beforeMount: function () {
    let context = this
    if (!context.$storage.has('user')) {
      getMe(context).then(function (response) {
        context.$storage.set('user', response.data, { ttl: 30 * 60 * 60 * 24 * 1000 })
        context.me = response.data
        context.loggedIn = true
      }).catch(function (error) {
        return error
      })
    } else {
      context.me = context.$storage.get('user')
      context.loggedIn = true
    }
  },
  methods: {
    route: function (name, params = {}) {
      this.$router.push({ name: name, params: params })
    },
    doLogout: function() {
      logout(this)
    }
  }
}
</script>
