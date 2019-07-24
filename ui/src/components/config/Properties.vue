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
    <aps-layout :title="this.$i18n.tc('props.prop',2)">
      <v-layout slot="mainContent" column wrap style="height:100%">
        <v-toolbar dense style="background-color:var(--content-toolbar-bg); height:45px;">
          <!-- <v-toolbar-title class="aps-toolbar-title">
        <v-icon right class="aps-toolbar-title">list</v-icon>
        {{$t('configurations.props')}}
          </v-toolbar-title>-->
          <!-- <v-menu transition="scale-transition">
        <v-toolbar-title slot="activator">
          <span>Environment: </span>
          <span>{{selectedEnv}}</span>
          <v-icon dark>arrow_drop_down</v-icon>
        </v-toolbar-title>
        <v-list>
          <v-list-tile  v-for="item in items" :key="item" @click="selectEnv(item)">
            <v-list-tile-title class="dropdown_element" v-text="item"></v-list-tile-title>
          </v-list-tile>
        </v-list>
          </v-menu>-->
          <v-toolbar-title>
            <span>{{$tc('envs.env',1)}}</span>
          </v-toolbar-title>
          <span class="aps-input-container">
            <v-select
              :items="items"
              class="aps-simple-input, aps-input-active"
              content-class="toolbar-select-content"
            ></v-select>
          </span>
          <v-spacer></v-spacer>
          <v-toolbar-title>
            <span>{{$tc('apps.application',1)}}:</span>
          </v-toolbar-title>
          <span class="aps-input-container">
            <v-select
              id="properties_application_select"
              class="aps-simple-input, aps-input-active"
              content-class="toolbar-select-content"
              @change="appChanged"
              :items="apps"
              :search-input.sync="searchApp"
              v-model="selectedApp"
              autocomplete
              cache-items
            ></v-select>
          </span>
        </v-toolbar>
        <v-system-bar window class="system-toolbar">
          <v-tooltip bottom>
            <v-btn icon slot="activator" @click="downloadProps()" style="cursor: pointer;">
              <v-icon>cloud_download</v-icon>
            </v-btn>
            <span>{{$t('props.actions.download_tooltip')}}</span>
          </v-tooltip>
          <v-tooltip bottom>
            <v-btn
              icon
              slot="activator"
              @click.stop="dialogFileUpload=true"
              style="cursor: pointer;"
            >
              <v-icon>cloud_upload</v-icon>
            </v-btn>
            <span>{{$t('props.actions.upload_tooltip')}}</span>
          </v-tooltip>
          <v-tooltip bottom>
            <v-btn icon slot="activator" @click="saveAppProps()" style="cursor: pointer;">
              <v-badge color="yellow" class="aps-badge-small" :value="hasUnsavedChanges()">
                <span slot="badge">!</span>
                <v-icon>save</v-icon>
              </v-badge>
            </v-btn>
            <span>{{$t('props.actions.save_tooltip')}}</span>
          </v-tooltip>
          <v-tooltip bottom>
            <v-btn icon slot="activator" @click="addAppProperty()" style="cursor: pointer;">
              <v-icon>add_box</v-icon>
            </v-btn>
            <span>{{$t('props.actions.add_tooltip')}}</span>
          </v-tooltip>
          <v-spacer></v-spacer>
          <span class="aps-input-container">
            <v-text-field
              class="aps-input-active"
              append-icon="search"
              @input="triggerAppPropsfiltering"
              single-line
              hide-details
            ></v-text-field>
          </span>
        </v-system-bar>
        <span v-if="loadingApps || loadingProps">
          <v-progress-linear color="blue" indeterminate></v-progress-linear>
        </span>
        <span class="aps-table-container">
          <table>
            <tr
              :key="'tr' + index"
              v-for="(propItem, index) in (filteredAppProperties.length > 0 ? filteredAppProperties : appProperties)"
            >
              <td class="regular_cell">
                <input
                  v-model="propItem.key"
                  type="text"
                  @blur="propChanged(index, $event)"
                  v-bind:class="{ 'aps-input-active': propItem.editing , 'aps-simple-input': true }"
                  :disabled="propItem.editing === undefined ? 'disabled' : false"
                />
              </td>
              <td class="regular_cell">
                <input
                  v-model="propItem.value"
                  type="text"
                  :ref="'name' + index"
                  @blur="propChanged(index, $event)"
                  v-bind:class="{ 'aps-input-active': propItem.editing , 'aps-simple-input': true }"
                  :disabled="propItem.editing === undefined ? 'disabled' : false"
                />
              </td>
              <td class="actions_cell">
                <v-btn icon @click="editAppProperty(index)">
                  <v-icon>edit</v-icon>
                </v-btn>
                <v-btn icon @click="deleteAppProperty(propItem)">
                  <v-icon>delete</v-icon>
                </v-btn>
              </td>
            </tr>
          </table>
        </span>
      </v-layout>
    </aps-layout>
    <v-dialog v-model="dialogFileUpload" max-width="500px" max-height="500px">
      <v-layout class="aps-dialog-layout">
        <v-system-bar style="width:100%">
          <v-spacer />
          <v-btn icon @click.stop="cancelUploadedFile">
            <v-icon>close</v-icon>
          </v-btn>
        </v-system-bar>
        <div class="aps-dialog-content">
          <span v-if="fileUpload.file">{{$t('file.chosen')}}: {{fileUpload.filename}}</span>
          <div v-else>{{$t('file.choose')}}</div>
          <span v-if="fileUpload.file">{{$t('file.type')}}: {{fileUpload.filetype}}</span>
          <div class="aps-dialog-actions">
            <v-btn
              :loading="loadingUpload"
              v-if="fileUpload.file"
              class="upload-btn"
              @click="submitUploadedFile"
            >submit file</v-btn>
            <label v-else class="upload-btn-wrapper">
              <div class="aps-primary-btn">
                <v-icon dark>backup</v-icon>
              </div>
              <input type="file" @change="fileUploaded" accept=".properties, .yml, .yaml" />
            </label>
            <v-btn class="aps-primary-btn" @click="cancelUploadedFile">cancel</v-btn>
          </div>
        </div>
      </v-layout>
    </v-dialog>
  </span>
</template>
<script>

import Layout from '@/components/layout/Layout'
import * as api from '@/js/api/api'
import * as tableUtils from '@/js/util/tableUtils'
import Vue from 'vue'
import { mapState } from 'vuex'
import { CONST_ACTIONS } from '@/js/util/constants'
const DELETE_PROP = 'deleteAppProperty'

export default {
  name: 'appconfig-component',
  components: { 'aps-layout': Layout },
  data: () => ({
    loadingApps: false,
    loadingProps: false,
    loadingUpload: false,
    dialogFileUpload: false,
    fileUpload: {
      file: null,
      filename: '',
      filetype: ''
    },
    apps: [],
    searchApp: null,
    items: [
      'Production', 'Development'
    ],
    selectedEnv: '',
    selectedApp: '',
    appProperties: [],
    filteredAppProperties: []
  }),
  computed: {
    ...mapState({
      mainDialogOpen: state => state.dialog.open
    })
  },
  watch: {
    searchApp(val) {
      val && this.queryAppSelections(val)
    },
    mainDialogOpen(val) {
      if (val) {
        return
      }
      let caller = this.$store.state.dialog.caller
      let consumed = false
      switch (caller) {
        case this.$router.currentRoute.name + '/' + DELETE_PROP:
          if (this.$store.state.dialog.action === CONST_ACTIONS.CONFIRM) {
            let i = this.appProperties.indexOf(this.$store.state.dialog.target)
            if (i !== -1) {
              this.appProperties.splice(i, 1)
            }
          }
          consumed = true
          break
        default:
          break
      }
      if (consumed) {
        this.$store.dispatch('dialog/consume', {
          caller: this.$router.currentRoute.name + '/' + DELETE_PROP
        })
      }
    }
  },
  methods: {
    selectEnv: function (env) {
      this.selectedEnv = env
    },
    queryAppSelections(v) {
      this.loadingApps = true
      // Simulated ajax query
      setTimeout(() => {
        this.apps = ['RelMongo', 'Hystrix']
        this.loadingApps = false
      }, 500)
    },
    hasUnsavedChanges() {
      let res = false
      this.appProperties.some(element => {
        if (element.hasChanges === true) {
          res = true
          return true
        }
      })
      return res
    },
    propChanged(index, e) {
      let target = e.relatedTarget ? e.relatedTarget.value : undefined
      let realIndex = index
      if (this.filteredAppProperties.length > 0) {
        realIndex = this.appProperties.indexOf(this.filteredAppProperties[index])
      }
      if (!this.appProperties[realIndex].editing) {
        return
      }
      Vue.set(this.appProperties[realIndex], 'hasChanges', tableUtils.hasChanges(this.appProperties[realIndex]))
      if (target && (this.appProperties[realIndex].key === target ||
        this.appProperties[realIndex].value === target)) {
        return
      }
      Vue.set(this.appProperties[realIndex], 'editing', false)
    },
    editAppProperty(index) {
      let realIndex = index
      if (this.filteredAppProperties.length > 0) {
        realIndex = this.appProperties.indexOf(this.filteredAppProperties[index])
      }
      Vue.set(this.appProperties[realIndex], 'editing', true)
      let self = this
      Vue.nextTick(function () {
        self.$refs['name' + index][0].focus()
      })
    },
    deleteAppProperty(item) {
      this.$store.dispatch('dialog/open', {
        message: 'Are you sure to remove the property',
        caller: this.$router.currentRoute.name + '/' + DELETE_PROP,
        target: item
      })
    },
    downloadAppProps() {
      console.log('download clicked')
    },
    saveAppProps() {
      console.log('save props')
    },
    addAppProperty() {
      this.filteredAppProperties = []
      this.appProperties.unshift({
        key: '',
        value: '',
        editing: true
      })
    },
    appChanged(val) {
      let newApp = JSON.parse(JSON.stringify(val))
      if (newApp === this.selectedApp || !this.apps.includes(newApp)) {
        return
      }
      this.loadingProps = true
      this.searchApp = newApp
      this.appProperties = []
      this.filteredAppProperties = []
      api.getProperties(this.selectedApp).then((result) => {
        if (result != null) {
          result.forEach((entry) => {
            tableUtils.enhanceEditable(entry)
            this.appProperties.push(entry)
          })
        }
        this.loadingProps = false
      })
    },
    fileUploaded(event) {
      this.fileUpload.file = event.target.files[0]
      this.fileUpload.filename = this.fileUpload.file.name
      if (this.fileUpload.file.type.startsWith('application/')) {
        this.fileUpload.filetype = this.fileUpload.file.type.substring(12)
      } else {
        this.fileUpload.filetype = this.fileUpload.file.name.split('.').pop()
        if (['yml', 'yaml'].includes(this.fileUpload.filetype.toLowerCase())) {
          this.fileUpload.filetype = 'yaml'
        }
      }
      this.fileUpload.filetype = this.fileUpload.filetype.toUpperCase()
    },
    submitUploadedFile() {
      this.loadingUpload = true
      console.log('submit uploaded file')
      setTimeout(() => (this.loadingUpload = false), 3000)
    },
    cancelUploadedFile() {
      this.dialogFileUpload = false
      this.fileUpload.file = null
      this.fileUpload.filename = ''
      this.fileUpload.filetype = ''
    },
    triggerAppPropsfiltering(event) {
      if (event !== undefined) {
        this.filteredAppProperties = this.appProperties.filter((value) => {
          return value.key.toLowerCase().includes(event.toLowerCase()) ||
            value.value.toLowerCase().includes(event.toLowerCase())
        })
      }
    }
  }
}
</script>

<style scoped>
.content .toolbar__title {
  font-family: Roboto, sans-serif;
  line-height: 2;
  font-size: 15px;
}

.dropdown_element {
  font-family: Roboto, sans-serif !important;
  line-height: 1.5;
  font-size: 12px !important;
}

.system-toolbar {
  width: 100%;
  background-color: var(--content-toolbar-bg);
  margin-top: 10px;
  box-shadow: 0 6px 6px -3px rgba(0, 0, 0, 0.2),
    0 10px 14px 1px rgba(0, 0, 0, 0.14), 0 4px 18px 3px rgba(0, 0, 0, 0.12) !important;
}

.toolbar-select-content {
  background-color: var(--input-primary-background-color) !important;
}

.aps-toolbar-title {
  /* color: rgb(31, 146, 146); */
  color: blueviolet;
  font-size: 28px;
  text-shadow: 1px 0 indigo, 0 1px indigo, 1px 0 indigo, 0 1px indigo;
}

.actions_cell {
  display: flex;
}

.regular_cell {
  width: 47%;
}

.aps-dialog-layout {
  display: flex;
  width: 100%;
  height: 100%;
  flex-direction: column;
  background-color: rgb(48, 48, 48);
}

.aps-dialog-content {
  height: 150px;
  width: 100%;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.aps-dialog-content div,
.aps-dialog-conten span {
  margin: auto;
}

.upload-btn-wrapper {
  margin-left: 20px;
  margin-top: auto;
  margin-right: auto;
  margin-bottom: auto;
}

.upload-btn-wrapper input[type="file"] {
  display: none;
}
</style>
