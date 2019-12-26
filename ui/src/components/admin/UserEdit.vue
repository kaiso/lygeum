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
  <aps-layout :title="title" style="heigth:100%">
    <v-layout slot="mainContent" column wrap style="height:90vh">
      <div class="aps-form-container">
        <div class="aps-form-line" style="height:7vh;">
          <div
            class="aps-form-label"
            style="width:11vw;height:7vh;line-height: inherit;"
          >{{$t('admin.id')}}</div>
          <span class="aps-form-input-container">
            <v-text-field
              class="aps-input-active"
              v-model="user.code"
              disabled
              single-line
              hide-details
            ></v-text-field>
          </span>
        </div>
        <div class="aps-form-line" style="height:7vh;">
          <div
            class="aps-form-label"
            style="width:11vw;height:7vh;line-height: inherit;"
          >{{$t('admin.firstName')}}</div>
          <span class="aps-form-input-container">
            <v-text-field
              class="aps-input-active"
              v-model="user.firstName"
              :error="firstNameError"
              :outline="firstNameError"
              single-line
              hide-details
            ></v-text-field>
          </span>
        </div>
        <div class="aps-form-line" style="height:7vh;">
          <div
            class="aps-form-label"
            style="width:11vw;height:7vh;line-height: inherit;"
          >{{$t('admin.lastName')}}</div>
          <span class="aps-form-input-container">
            <v-text-field
              class="aps-input-active"
              v-model="user.lastName"
              single-line
              hide-details
              :error="lastNameError"
              :outline="lastNameError"
            ></v-text-field>
          </span>
        </div>
        <div class="aps-form-line" style="height:7vh;">
          <div
            class="aps-form-label"
            style="width:11vw;height:7vh;line-height: inherit;"
          >{{$t('admin.email')}}</div>
          <span class="aps-form-input-container">
            <v-text-field
              class="aps-input-active"
              v-model="user.username"
              :error="emailError"
              :outline="emailError"
              single-line
              hide-details
            ></v-text-field>
          </span>
        </div>
        <div class="aps-form-line" style="height:7vh;">
          <div
            class="aps-form-label"
            style="width:11vw;height:7vh;line-height: inherit;"
          >{{$t('admin.password')}}</div>
          <span class="aps-form-input-container">
            <v-text-field
              class="aps-input-active aps-input-password"
              v-model="user.password"
              :append-icon="showPassword ? 'visibility' : 'visibility_off'"
              :type="showPassword ? 'text' : 'password'"
              @click:append="showPassword = !showPassword"
              single-line
              hide-details
            ></v-text-field>
          </span>
        </div>
        <div class="aps-form-line" style="height:32vh;margin-top:2vh;">
          <list-picker
            :choice="user.roles"
            :source="roles"
            :sourceLabel="this.$t('admin.availableroles')"
            :choiceLabel="this.$t('admin.assignedroles')"
          ></list-picker>
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
import { validateEmail } from '@/js/util/validator'
import Layout from '@/components/layout/Layout'
import ListPicker from '@/components/common/ListPicker'

export default {
  name: 'useredit-component',
  components: { 'aps-layout': Layout, 'list-picker': ListPicker },
  props: {
    'user': Object
  },
  data: () => ({
    loadingSave: false,
    roles: [],
    showPassword: false,
    firstNameError: false,
    lastNameError: false,
    emailError: false
  }),
  computed: {
    title() {
      return this.$route.params.user ? this.$t('admin.edituser') : this.$t('admin.createuser')
    },
    editMode() {
      return this.$route.params.user !== undefined
    }
  },
  beforeMount: function () {
    let userRoles = this.user.roles.map(r => r.name)
    console.log(userRoles)
    api.getRoles(this).then((result) => {
      this.roles = result.data.filter(r => !userRoles.includes(r.name))
    }).catch(error => {
      this.$store.dispatch('notification/open', {
        message: this.$i18n.t('admin.notifications.roles.load.error', { error: error }),
        status: 'error'
      })
    })
  },
  methods: {
    save() {
      if (!this.validateUser()) {
        return;
      }
      this.loadingSave = true
      if (this.user.code) {
        api.saveUser(this, this.user).then((data) => {
          this.loadingSave = false
          this.$router.push({ name: 'users' })
        })
      } else {
        api.createUser(this, this.user).then((data) => {
          this.loadingSave = false
          this.$router.push({ name: 'users' })
        })
      }
    },
    cancel() {
      this.loadingSave = false
      this.$router.push({ name: 'users' })
    },
    validateUser() {
      this.emailError = !validateEmail(this.user.username)
      this.lastNameError = this.user.lastName === null || this.user.lastName === ''
      this.firstNameError = this.user.firstName === null || this.user.firstName === ''
      return !(this.lastNameError || this.firstNameError || this.emailError)
    }
  }
}
</script>

<style scoped>
.aps-input-password {
  width: 197px;
}
</style>
