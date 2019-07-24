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
  <span>
    <aps-layout :title="this.$i18n.tc('apps.application',2)">
      <v-layout slot="mainContent" column wrap style="height:100%">
        <v-system-bar window class="system-toolbar">
          <v-tooltip bottom>
            <v-btn icon slot="activator" @click="saveApps()" style="cursor: pointer;">
              <v-badge color="yellow" class="aps-badge-small" :value="hasUnsavedChanges()">
                <span slot="badge">!</span>
                <v-icon>save</v-icon>
              </v-badge>
            </v-btn>
            <span>{{$t('apps.actions.save_tooltip')}}</span>
          </v-tooltip>
          <v-tooltip bottom>
            <v-btn icon slot="activator" @click="addApp()" style="cursor: pointer;">
              <v-icon>add_box</v-icon>
            </v-btn>
            <span>{{$t('apps.actions.add_tooltip')}}</span>
          </v-tooltip>
          <v-spacer></v-spacer>
          <span class="input-container">
            <v-text-field
              style="height:27px;"
              class="aps-input-active"
              append-icon="search"
              @input="triggerAppsfiltering"
              v-model="appFilter"
              single-line
              hide-details
            ></v-text-field>
          </span>
        </v-system-bar>
        <span v-if="loadingApps">
          <v-progress-linear color="blue" indeterminate></v-progress-linear>
        </span>
        <span class="aps-table-container">
          <table>
            <tr
              :key="'tr' + index"
              v-for="(propItem, index) in (filteredApps.length > 0 ? filteredApps : apps)"
            >
              <td class="regular_cell">
                <input
                  :ref="'name' + index"
                  v-model="propItem.name"
                  type="text"
                  @blur="appChanged(index)"
                  v-bind:class="{ 'aps-input-active': propItem.editing , 'aps-simple-input': true }"
                  :disabled="propItem.editing === undefined ? 'disabled' : false"
                />
              </td>
              <td class="actions_cell">
                <v-btn icon @click="editApp(index)">
                  <v-icon>edit</v-icon>
                </v-btn>
                <v-btn icon @click="deleteApp(propItem)">
                  <v-icon>delete</v-icon>
                </v-btn>
              </td>
            </tr>
          </table>
        </span>
      </v-layout>
    </aps-layout>
  </span>
</template>
<script>

import Layout from '@/components/layout/Layout'
import * as api from '@/js/api/api'
import * as tableUtils from '@/js/util/tableUtils'
import Vue from 'vue'
import { mapState } from 'vuex'
import { CONST_ACTIONS } from '@/js/util/constants'
const DELETE_APP = 'deleteApp'

export default {
  name: 'appconfig-component',
  components: { 'aps-layout': Layout },
  data: () => ({
    loadingApps: false,
    apps: [],
    filteredApps: [],
    appFilter: ''
  }),
  computed: {
    ...mapState({
      mainDialogOpen: state => state.dialog.open
    })
  },
  watch: {
    mainDialogOpen(val) {
      if (val) {
        return
      }
      let caller = this.$store.state.dialog.caller
      let consumed = false
      switch (caller) {
        case this.$router.currentRoute.name + '/' + DELETE_APP:
          if (this.$store.state.dialog.action === CONST_ACTIONS.CONFIRM) {
            let i = this.apps.indexOf(this.$store.state.dialog.target)
            if (i !== -1) {
              this.apps.splice(i, 1)
            }
          }
          consumed = true
          break
        default:
          break
      }
      if (consumed) {
        this.$store.dispatch('dialog/consume', {
          caller: this.$router.currentRoute.name + '/' + DELETE_APP
        })
      }
    }
  },
  mounted: function () {
    this.loadingApps = true
    this.apps = []
    this.filteredApps = []
    api.getAllApps().then((result) => {
      if (result != null) {
        result.forEach(element => {
          tableUtils.enhanceEditable(element)
          this.apps.push(element)
        })
      }
      this.loadingApps = false
    })
  },
  methods: {
    hasUnsavedChanges() {
      let res = false
      this.apps.some(element => {
        if (element.hasChanges === true) {
          res = true
          return true
        }
      })
      return res
    },
    appChanged(index) {
      let realIndex = index
      if (this.filteredApps.length > 0) {
        realIndex = this.apps.indexOf(this.filteredApps[index])
      }
      if (!this.apps[realIndex].editing) {
        return
      }
      Vue.set(this.apps[realIndex], 'hasChanges', tableUtils.hasChanges(this.apps[realIndex]))
      Vue.set(this.apps[realIndex], 'editing', false)
    },
    editApp(index) {
      let realIndex = index
      if (this.filteredApps.length > 0) {
        realIndex = this.apps.indexOf(this.filteredApps[index])
      }
      Vue.set(this.apps[realIndex], 'editing', true)
      let self = this
      Vue.nextTick(function () {
        self.$refs['name' + index][0].focus()
      })
    },
    deleteApp(item) {
      this.$store.dispatch('dialog/open', {
        message: 'Are you sure to remove the application',
        caller: this.$router.currentRoute.name + '/' + DELETE_APP,
        target: item
      })
    },
    saveApps() {
      console.log('save apps')
    },
    addApp() {
      this.appFilter = ''
      this.filteredApps = []
      this.apps.unshift({
        code: '',
        name: '',
        editing: true
      })
    },
    triggerAppsfiltering(event) {
      if (event !== undefined) {
        this.filteredApps = this.apps.filter((value) => {
          return value.name.toLowerCase().includes(event.toLowerCase())
        })
      }
    }
  }
}
</script>

<style scoped>
.system-toolbar {
  width: 100%;
  background-color: var(--content-toolbar-bg);
  margin-top: 10px;
  box-shadow: 0 6px 6px -3px rgba(0, 0, 0, 0.2),
    0 10px 14px 1px rgba(0, 0, 0, 0.14), 0 4px 18px 3px rgba(0, 0, 0, 0.12) !important;
}

.input-container {
  width: 20%;
  height: 30px;
  margin-left: 10px;
}

.actions_cell {
  display: flex;
}

.regular_cell {
  width: 87%;
}
</style>
