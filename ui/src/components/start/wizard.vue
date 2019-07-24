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
      <v-stepper v-model="e1">
        <v-stepper-header>
          <v-stepper-step
            class="aps-stepper-step"
            :complete="e1 > 1"
            step="1"
          >{{$t('wizard.stepper.start.header')}}</v-stepper-step>

          <v-divider style="max-width:65%"></v-divider>

          <v-stepper-step
            class="aps-stepper-step"
            :complete="e1 > 2"
            step="2"
          >{{$t('wizard.stepper.admin.header')}}</v-stepper-step>

          <v-divider></v-divider>

          <v-stepper-step
            class="aps-stepper-step"
            step="3"
          >{{$t('wizard.stepper.passphrase.header')}}</v-stepper-step>
        </v-stepper-header>

        <v-stepper-items>
          <v-stepper-content step="1">
            <div class="wizard-panel-container">
              <template v-for="(item,index) in this.$t('wizard.stepper.start.message')">
                <label v-bind:key="index">{{item}}</label>
              </template>
            </div>
            <span class="aps-buttons-container-line">
              <v-btn class="aps-primary-btn" @click="e1 = 2">{{$t('actions.continue')}}</v-btn>
            </span>
          </v-stepper-content>

          <v-stepper-content step="2">
            <div class="wizard-panel-container">
              <label style="margin-bottom:70px;">{{$t('wizard.stepper.admin.message')}}</label>
              <div class="aps-simple-input">
                <input
                  type="password"
                  class="aps-input-active"
                  :placeholder="this.$t('fields.password')"
                  v-model="adminPassword"
                />
              </div>
            </div>

            <span class="aps-buttons-container-line">
              <v-btn class="aps-primary-btn" @click="e1 = 3">{{$t('actions.continue')}}</v-btn>

              <v-btn class="aps-primary-btn" @click="e1 = 1">{{$t('actions.cancel')}}</v-btn>
            </span>
          </v-stepper-content>

          <v-stepper-content step="3">
            <div class="wizard-panel-container">
              <template v-for="(item,index) in this.$t('wizard.stepper.passphrase.message')">
                <label v-bind:key="index">{{item}}</label>
              </template>
              <div class="aps-simple-input" style="margin-top:70px;">
                <input
                  type="password"
                  class="aps-input-active"
                  :placeholder="this.$t('fields.passphrase')"
                  v-model="passphrase"
                />
              </div>
            </div>

            <span class="aps-buttons-container-line">
              <v-btn class="aps-primary-btn" @click="e1 = 3">{{$t('actions.continue')}}</v-btn>

              <v-btn class="aps-primary-btn" @click="e1 = 2">{{$t('actions.cancel')}}</v-btn>
            </span>
          </v-stepper-content>
        </v-stepper-items>
      </v-stepper>
    </v-container>
  </aps-layout>
</template>

<script>
// import auth from '@/js/api/auth.js'
import Layout from '@/components/layout/Layout'
export default {
  components: { 'aps-layout': Layout },
  data() {
    return {
      e1: 0,
      adminPassword: '',
      passphrase: ''
    }
  },
  methods: {}

}
</script>

<style scoped>
.wizard-panel-container {
  display: flex;
  flex-direction: column;
}
</style>
