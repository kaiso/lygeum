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
  <aps-layout :title="$tc('admin.user', 2)">
    <v-layout slot="mainContent" column wrap style="height:100%">
      <v-system-bar window class="system-toolbar">
        <v-tooltip bottom>
          <v-btn icon slot="activator" @click="'#'" style="cursor: pointer;">
            <v-icon>add_box</v-icon>
          </v-btn>
          <span>{{$t('admin.adduser')}}</span>
        </v-tooltip>
        <v-tooltip bottom>
          <v-btn icon slot="activator" @click="loadAll()" style="cursor: pointer;">
            <v-icon>reply_all</v-icon>
          </v-btn>
          <span>{{$t('props.actions.loadall_tooltip')}}</span>
        </v-tooltip>
        <v-spacer></v-spacer>
        <span class="input-container">
          <v-text-field
            style="height:27px;"
            class="aps-input-active"
            append-icon="search"
            @input="triggerUserSearch(userSearch)"
            v-model="userSearch"
            single-line
            hide-details
          ></v-text-field>
        </span>
      </v-system-bar>
      <span v-if="loading">
        <v-progress-linear color="blue" indeterminate></v-progress-linear>
      </span>
      <div style="margin-top:5px;">
        <v-data-table
          :headers="headers"
          :items="users"
          :pagination.sync="pagination"
          :total-items="totalUsers"
          :no-data-text="this.$t('generic.nodata')"
          class="elevation-1"
        >
          <template slot="items" slot-scope="props">
            <td>{{ props.item.email }}</td>
            <td>{{ props.item.firstName }}</td>
            <td>{{ props.item.lastName }}</td>
            <td class="justify-center layout px-0">
              <v-icon style="cursor:pointer;" @click="editItem(props.item)">edit</v-icon>
              <v-icon style="cursor:pointer;" @click="deleteItem(props.item)">delete</v-icon>
            </td>
          </template>
        </v-data-table>
      </div>
    </v-layout>
  </aps-layout>
</template>
<script>
import * as api from '@/js/api/api'
import debounce from '@/js/util/debounce'
import Layout from '@/components/layout/Layout'
import ListPicker from '@/components/common/ListPicker'

let search = debounce(function (target, param) {
  target.getDataFromApi(param).then((data) => {
    target.users = data.items
    target.totalUsers = data.total
  })
}, 1000)

export default {
  name: 'settings-component',
  components: { 'aps-layout': Layout,
    'list-picker': ListPicker },
  data: () => ({
    totalUsers: 0,
    users: [],
    userSearch: '',
    loading: true,
    pagination: {},
    headers: [
      {
        i18nKey: 'admin.email',
        text: '1',
        align: 'left',
        sortable: false,
        value: 'email'
      },
      { i18nKey: 'admin.firstName', text: '2', value: 'firstName' },
      { i18nKey: 'admin.lastName', text: '3', value: 'lastName' }
    ]
  }),
  watch: {
    pagination: {
      handler() {
        this.getDataFromApi().then((data) => {
          this.users = data.items
          this.totalUsers = data.total
        })
      },
      deep: true
    }
  },
  mounted() {
    this.headers.forEach((header) => {
      header.text = this.$t(header.i18nKey)
    })
  },
  methods: {
    loadAll() {
      this.getDataFromApi().then((data) => {
        this.users = data.items
        this.totalUsers = data.total
        console.log('load all from users', data.items)
      })
    },
    triggerUserSearch(param) {
      search(this, this.userSearch)
    },
    getDataFromApi(pattern) {
      this.loading = true
      return new Promise((resolve, reject) => {
        const { sortBy, descending, page, rowsPerPage } = this.pagination
        api.getUsers(pattern).then((data) => {
          let items = data.users
          const total = data.total

          if (this.pagination.sortBy) {
            items = items.sort((a, b) => {
              const sortA = a[sortBy]
              const sortB = b[sortBy]

              if (descending) {
                if (sortA < sortB) return 1
                if (sortA > sortB) return -1
                return 0
              } else {
                if (sortA < sortB) return -1
                if (sortA > sortB) return 1
                return 0
              }
            })
          }

          if (rowsPerPage > 0) {
            items = items.slice((page - 1) * rowsPerPage, page * rowsPerPage)
          }

          this.loading = false
          resolve({
            items,
            total
          })
        })
      })
    }
  }
}
</script>
<style scoped>
</style>
