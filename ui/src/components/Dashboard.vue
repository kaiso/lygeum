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
  <aps-layout :title="$t('layout.dashboard')">
    <v-container slot="mainContent" fluid grid-list-md>
    <v-layout row wrap>
      <v-flex d-flex xs12 sm6 md4>
        <v-card color="brown lighten-3" dark>
              <v-card-title><h4>{{$tc('envs.env', 2)}}</h4></v-card-title>
              <v-card-text>
                <v-badge color="green">
                  <template v-slot:badge>
                    <span>{{ envCount}}</span>
                  </template>
                  <v-icon
                    large
                    color="white">
                    dns
                  </v-icon>
                </v-badge>
               </v-card-text>
            </v-card>
      </v-flex>
      <v-flex d-flex xs12 sm6 md4>
        <v-card color="brown lighten-3" dark>
              <v-card-title><h4>{{$tc('apps.application', 2)}}</h4></v-card-title>
              <v-card-text>
                <v-badge color="green">
                  <template v-slot:badge>
                    <span>{{ appCount}}</span>
                  </template>
                  <v-icon
                    large
                    color="white">
                    apps
                  </v-icon>
                </v-badge>
              </v-card-text>
            </v-card>
      </v-flex>
    </v-layout>
  </v-container>
  </aps-layout>
</template>
<script>
import Layout from '@/components/layout/Layout'
import * as api from '@/js/api/api'
export default {
  name: 'dashboard-component',
  components: { 'aps-layout': Layout },
  data: () => ({
    envCount: 0,
    appCount: 0
  }),
  beforeMount: function () {
    const context = this
    api.getAllApps(this).then(result => {
      if (result.data != null) {
        context.appCount = result.data.length
      }
    }).catch(error => {
      context.$store.dispatch('notification/open', {
        message: context.$i18n.t('apps.notifications.load.error', { error: error }),
        status: 'error'
      })
    })
    api.getAllEnvs(this).then(result => {
      if (result.data != null) {
        context.envCount = result.data.length
      }
    }).catch(error => {
      context.$store.dispatch('notification/open', {
        message: context.$i18n.t('apps.notifications.load.error', { error: error }),
        status: 'error'
      })
    })
  }
}
</script>

<style scoped>
</style>
