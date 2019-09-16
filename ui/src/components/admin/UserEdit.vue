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
        <div class="aps-form-line" style="height:30px;">
          <div
            class="aps-form-label"
            style="width:150px;height: 35px;line-height: inherit;"
          >{{$t('admin.id')}}</div>
          <span class="input-container">
            <v-text-field
              class="aps-input-active"
              v-model="user.code"
              disabled
              single-line
              hide-details
            ></v-text-field>
          </span>
        </div>
        <div class="aps-form-line" style="height:30px;">
          <div
            class="aps-form-label"
            style="width:150px;height: 35px;line-height: inherit;"
          >{{$t('admin.firstName')}}</div>
          <span class="input-container">
            <v-text-field class="aps-input-active" v-model="user.firstName" single-line hide-details></v-text-field>
          </span>
        </div>
        <div class="aps-form-line" style="height:30px;">
          <div
            class="aps-form-label"
            style="width:150px;height: 35px;line-height: inherit;"
          >{{$t('admin.lastName')}}</div>
          <span class="input-container">
            <v-text-field class="aps-input-active" v-model="user.lastName" single-line hide-details></v-text-field>
          </span>
        </div>
        <div class="aps-form-line" style="height:30px;">
          <div
            class="aps-form-label"
            style="width:150px;height: 35px;line-height: inherit;"
          >{{$t('admin.email')}}</div>
          <span class="input-container">
            <v-text-field class="aps-input-active" v-model="user.email" single-line hide-details></v-text-field>
          </span>
        </div>
        <div class="aps-form-line" style="height:150px;margin-top:20px;">
          <list-picker
            :choice="choice"
            :source="source"
            :sourceLabel="this.$t('admin.availableroles')"
            :choiceLabel="this.$t('admin.assignedroles')"
          ></list-picker>
        </div>
      </div>
    </v-layout>
  </aps-layout>
</template>
<script>
import * as api from '@/js/api/api'
import Layout from '@/components/layout/Layout'
import ListPicker from '@/components/common/ListPicker'

export default {
  name: 'useredit-component',
  components: { 'aps-layout': Layout,
    'list-picker': ListPicker },
  props: {
    'user': Object
  },
  computed: {
    title() {
      return this.$route.params.user ? this.$t('admin.edituser') : this.$t('admin.createuser')
    }
  },
  methods: {
    loadAll() {
      api.getUsers().then((data) => {
        console.log('load all from users', data.items)
      })
    }
  }
}
</script>
<style scoped>
</style>
