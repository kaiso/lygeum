/**
   * Copyright 2018 Kais OMRI [kais.omri.int@gmail.com] and authors
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
  <aps-layout :title="$t('account.password.update')" style="heigth:100%">
    <v-layout slot="mainContent" column wrap style="height:90vh">
      <div class="aps-form-container">
        <div class="aps-form-line" style="height:7vh;">
          <div
            class="aps-form-label"
            style="width:11vw;height:7vh;line-height: inherit;"
          >{{$t('account.password.old')}}</div>
          <span class="aps-form-input-container">
            <v-text-field
              class="aps-input-active aps-input-password"
              v-model="password.oldPassword"
              :append-icon="showOldPassword ? 'visibility' : 'visibility_off'"
              :type="showOldPassword ? 'text' : 'password'"
              @click:append="showOldPassword = !showOldPassword"
              :error="oldPasswordError"
              :outline="oldPasswordError"
              single-line
              hide-details
            ></v-text-field>
          </span>
        </div>
        <div class="aps-form-line" style="height:7vh;">
          <div
            class="aps-form-label"
            style="width:11vw;height:7vh;line-height: inherit;"
          >{{$t('account.password.new')}}</div>
          <span class="aps-form-input-container">
            <v-text-field
              class="aps-input-active aps-input-password"
              v-model="password.newPassword"
              :append-icon="showNewPassword ? 'visibility' : 'visibility_off'"
              :type="showNewPassword ? 'text' : 'password'"
              @click:append="showNewPassword = !showNewPassword"
               :error="newPasswordError"
              :outline="newPasswordError"
              single-line
              hide-details
            ></v-text-field>
          </span>
        </div>
        <div class="aps-form-line" style="height:7vh;">
          <div
            class="aps-form-label"
            style="width:11vw;height:7vh;line-height: inherit;"
          >{{$t('account.password.confirm')}}</div>
          <span class="aps-form-input-container">
            <v-text-field
              class="aps-input-active aps-input-password"
              v-model="password.confirmPassword"
              :append-icon="showConfirmPassword ? 'visibility' : 'visibility_off'"
              :type="showConfirmPassword ? 'text' : 'password'"
              @click:append="showConfirmPassword = !showConfirmPassword"
              :error="newPasswordError"
              :outline="newPasswordError"
              single-line
              hide-details
            ></v-text-field>
          </span>
        </div>
        <div class="aps-buttons-container-line">
          <v-btn
            :loading="loadingSave"
            class="aps-primary-btn"
            @click="save"
          >{{this.$t('actions.save')}}</v-btn>
          <v-btn class="aps-primary-btn" @click="cancel">{{this.$t('actions.cancel')}}</v-btn>
        </div>
      </div>
    </v-layout>
  </aps-layout>
</template>
<script>
import * as api from '@/js/api/api'
import { validatePassword } from '@/js/util/validator'
import Layout from '@/components/layout/Layout'

export default {
  name: 'account-component',
  components: { 'aps-layout': Layout },
  props: {
    'user': Object
  },
  data: () => ({
    loadingSave: false,
    showOldPassword: false,
    showNewPassword: false,
    showConfirmPassword: false,
    oldPasswordError: false,
    newPasswordError: false,
    password: {
      newPassword: null,
      oldPassword: null,
      confirmPassword: null
    }
  }),
  methods: {
    save() {
      if (!this.validatePassword()) {
        return;
      }
      this.loadingSave = true
      api.updatePassword(this, this.user.code, this.password).then((data) => {
        this.loadingSave = false
        this.$store.dispatch('notification/open', {
          message: this.$i18n.t('account.notifications.save.success', { target: this.user.username }),
          status: 'success'
        })
        this.$router.back()
      })
    },
    cancel() {
      this.loadingSave = false
      this.$router.back()
    },
    validatePassword() {
      this.newPasswordError = !validatePassword(this.password.newPassword, this.password.confirmPassword)
      this.oldPasswordError = this.password.oldPassword === null
      return !(this.newPasswordError || this.oldPasswordError)
    }
  }
}
</script>
