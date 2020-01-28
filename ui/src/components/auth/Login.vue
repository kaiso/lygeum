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
      <div style="display:flex;justify-content:center;margin-top:70px;flex-direction:column">
        <img src="~@/assets/lygeum_logo_orange.png" style="max-width:200px;align-self:center;"/>
        <img src="~@/assets/lygeum_logotext_orange_b.png" style="max-width:300px;align-self:center;margin-bottom:90px;"/>
        <div class="aps-simple-input">
          <input
            type="text"
            class="aps-input-active"
            :placeholder="this.$t('fields.username')"
            :disabled="loading"
            v-model="credentials.username"
          />
        </div>
        <div class="aps-simple-input">
          <input
            type="password"
            class="aps-input-active"
            :placeholder="this.$t('fields.password')"
            :disabled="loading"
            v-model="credentials.password"
          />
        </div>
      </div>
      <div class="aps-buttons-container-line">
        <v-btn class="aps-primary-btn btn" @click="doLogin()" :loading="loading">
          {{$t('actions.login.button')}}
          <!--<template v-slot:loader>
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
import * as auth from '@/js/api/auth.js'
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
    doLogin() {
      const context = this
      context.loading = true
      auth.login(context, context.credentials, '../').then(function (response) {
        context.loading = false
      }).catch(error => {
        context.loading = false
        context.$store.dispatch('notification/open', {
          message: context.$i18n.t('auth.login.error', { error: error }),
          status: 'error'
        })
      })
    }
  }

}
</script>
<style scoped>
.logo_container {
  max-width: 400px;
  max-height: 600px;
  align-content: center;
  display: grid;
  grid-column-gap: 5px;
  grid-template-columns: auto;
  justify-items: center;
}
</style>

