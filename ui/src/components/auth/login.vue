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
  <aps-layout :drawerDisabled="true" :drawer="false" :title="''">
    <v-container slot="mainContent" grid-list-md text-xs-center style="height:100%">
      <h2>{{$t('actions.login.title')}}</h2>

      <div style="display:flex;justify-content:center;margin-top:70px;flex-direction:column">
        <div class="aps-simple-input">
          <input
            type="text"
            class="aps-input-active"
            :placeholder="this.$t('fields.username')"
            v-model="credentials.username"
          />
        </div>
        <div class="aps-simple-input">
          <input
            type="password"
            class="aps-input-active"
            :placeholder="this.$t('fields.password')"
            v-model="credentials.password"
          />
        </div>
      </div>
      <div class="aps-buttons-container-line">
        <v-btn class="aps-primary-btn btn" @click="submit()" :loading="loading">
          {{$t('actions.login.button')}}
          <!-- <template v-slot:loader>
                <span class="custom-loader">
                  <v-icon light>cached</v-icon>
                </span>
          </template>-->
        </v-btn>
      </div>
    </v-container>
  </aps-layout>
</template>

<script>
import auth from '@/js/api/auth.js'
import Layout from '@/components/layout/Layout'
export default {
  components: { 'aps-layout': Layout },
  data() {
    return {
      credentials: {
        username: '',
        password: ''
      },
      error: '',
      loading: false
    }
  },
  methods: {
    submit() {
      let context = this
      context.loading = true
      var credentials = {
        username: this.credentials.username,
        password: this.credentials.password
      }
      auth.login(context, credentials, '../').then(function (response) {
        context.loading = false
      })
    }
  }

}
</script>
