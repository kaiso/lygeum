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
              v-model="client.code"
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
          >{{$t('admin.clientname')}}</div>
          <span class="aps-form-input-container">
            <v-text-field
              class="aps-input-active"
              v-model="client.name"
              :error="nameError"
              :outline="nameError"
              single-line
              hide-details
            ></v-text-field>
          </span>
        </div>
        <div class="aps-form-line" style="height:7vh;">
          <div
            class="aps-form-label"
            style="width:11vw;height:7vh;line-height: inherit;"
          >{{$t('admin.clientsecret')}}</div>
          <span class="aps-form-input-container">
            <v-text-field
              class="aps-input-active aps-input-password"
              v-model="client.clientSecret"
              :append-icon="showPassword ? 'visibility' : 'visibility_off'"
              :type="showPassword ? 'text' : 'password'"
              @click:append="showPassword = !showPassword"
              single-line
              hide-details
            ></v-text-field>
          </span>
          <v-btn flat icon color="white" @click="renewSecret">
            <v-icon>autorenew</v-icon>
          </v-btn>
        </div>
        <div class="aps-form-line" style="height:32vh;margin-top:2vh;">
          <list-picker
            :choice="client.roles"
            :source="roles"
            :sourceLabel="this.$t('admin.availableauthorities')"
            :choiceLabel="this.$t('admin.assignedauthorities')"
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
import { generateSecret } from '@/js/util/crypto'
import Layout from '@/components/layout/Layout'
import ListPicker from '@/components/common/ListPicker'

export default {
  name: 'clientedit-component',
  components: { 'aps-layout': Layout, 'list-picker': ListPicker },
  props: {
    'client': Object
  },
  data: () => ({
    loadingSave: false,
    roles: [],
    showPassword: false,
    nameError: false
  }),
  computed: {
    title() {
      return this.$route.params.client ? this.$t('admin.editclient') : this.$t('admin.createclient')
    },
    editMode() {
      let createMode = !(this.$route.params.client)
      return !createMode
    }
  },
  beforeMount: function () {
    let clientRoles = this.client.roles.map(r => r.name)
    api.getRoles(this).then((result) => {
      this.roles = result.data.filter(r => !clientRoles.includes(r.name))
    }).catch(error => {
      this.$store.dispatch('notification/open', {
        message: this.$i18n.t('admin.notifications.roles.load.error', { error: error }),
        status: 'error'
      })
    })
  },
  methods: {
    save() {
      if (!this.validateclient()) {
        return;
      }
      this.loadingSave = true
      if (this.client.code) {
        api.saveClient(this, this.client).then((data) => {
          this.loadingSave = false
          this.$router.push({ name: 'clients' })
        })
      } else {
        api.createClient(this, this.client).then((data) => {
          this.loadingSave = false
          this.$router.push({ name: 'clients' })
        })
      }
    },
    cancel() {
      this.loadingSave = false
      this.$router.push({ name: 'clients' })
    },
    validateclient() {
      this.nameError = !(this.client.name)
      return !(this.nameError)
    },
    renewSecret() {
      this.client.clientSecret = generateSecret()
    }
  }
}
</script>

<style scoped>
.aps-input-password {
  width: 197px;
}
</style>
