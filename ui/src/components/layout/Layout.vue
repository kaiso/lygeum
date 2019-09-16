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
        <v-list-tile @click="route('/')">
          <v-list-tile-action>
            <v-icon>dashboard</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>{{ $t("layout.dashboard") }}</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>

        <v-list-tile @click="route('/configurations')">
          <v-list-tile-action>
            <v-icon>settings_applications</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>{{ $t("layout.configurations") }}</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>

        <v-list-tile @click="route('/administration')">
          <v-list-tile-action>
            <v-icon>chrome_reader_mode</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>{{ $t("layout.administration") }}</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>

        <v-list-tile @click="route('/settings')">
          <v-list-tile-action>
            <v-icon>build</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>{{ $t("layout.settings") }}</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
      </v-list>
    </v-navigation-drawer>
    <v-toolbar app fixed clipped-left>
      <v-toolbar-side-icon v-show="!drawerDisabled" @click.stop="drawer = !drawer"></v-toolbar-side-icon>
      <v-toolbar-title style="width:20%;">Lygeum</v-toolbar-title>
      <div style="width:100%;">
        <v-toolbar-title style="margin:auto;width:200px">{{title}}</v-toolbar-title>
      </div>
    </v-toolbar>
    <v-content>
      <v-container fluid fill-height>
        <v-layout justify-center align-center>
          <slot name="mainContent"></slot>
        </v-layout>
      </v-container>
    </v-content>
    <v-footer app fixed>
      <span>&copy; Lygeum - Kais OMRI - 2019</span>
    </v-footer>
  </div>
</template>

<script>
export default {
  name: 'aps-layout',
  props: {
    drawer: { type: Boolean, default: true },
    source: String,
    title: { type: String, default: 'Lygeum' },
    drawerDisabled: { type: Boolean, default: false }
  },
  methods: {
    route: function (url) {
      this.$router.push({ path: `${url}` })
    }
  }
}
</script>
