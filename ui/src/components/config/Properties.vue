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
          <v-toolbar-title>
            <span>{{$tc('envs.env',1)}}</span>
          </v-toolbar-title>
          <span class="aps-input-container">
            <v-select
              :items="envs"
              item-text="name"
              :return-object="true"
              @change="selectEnv"
              v-model="selectedEnv"
              style="height: inherit;"
              class="aps-simple-input, aps-input-active"
              :menu-props="{contentClass:'toolbar-select-content'}"
            ></v-select>
          </span>
          <v-spacer></v-spacer>
          <v-toolbar-title>
            <span>{{$tc('apps.application',1)}}:</span>
          </v-toolbar-title>
          <span class="aps-input-container">
            <v-autocomplete
              id="properties_application_select"
              style="height: inherit;"
              class="aps-simple-input, aps-input-active"
              :menu-props="{contentClass:'toolbar-select-content'}"
              @change="appChanged"
              item-text="name"
              :items="apps"
              :return-object="true"
              :search-input.sync="searchApp"
              v-model="selectedApp"
              hide-no-data
            ></v-autocomplete>
          </span>
        </v-toolbar>
        <v-system-bar window class="system-toolbar">
          <v-tooltip bottom>
            <v-btn icon slot="activator" @click="openDownloadDialog()" style="cursor: pointer;">
              <v-icon>cloud_download</v-icon>
            </v-btn>
            <span>{{$t('props.actions.download_tooltip')}}</span>
          </v-tooltip>
          <v-tooltip bottom>
            <v-btn
              icon
              slot="activator"
              @click.stop="openUploadDialog()"
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
          <span v-if="lastUpdatedInfo.date">{{$t('generic.lastModifiedOn')}} {{lastUpdatedInfo.date}} {{$t('generic.by')}} {{lastUpdatedInfo.user}}</span>
          <v-spacer></v-spacer>
          <span class="aps-input-container">
            <v-text-field
              class="aps-input-active search-input"
              append-icon="search"
              @input="triggerAppPropsfiltering"
              v-model="filter"
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
              :key="propItem.displayIdentifier"
              v-click-outside="clickOutside"
              v-bind:style="{display:propItem.hidden ? 'none' : 'revert'}"
              v-for="(propItem, index) in appProperties"
            >
              <td class="regular_cell" v-bind:class="{ 'aps-error': propItem.errorAttributes.includes('name')}">
                <input
                  v-model="propItem.name"
                  type="text"
                  :ref="'nameinput' + index"
                  @blur="propChanged(index, $event)"
                  v-bind:class="{ 'aps-input-active': propItem.editing , 'aps-simple-input': true }"
                  :disabled="(propItem.editing === undefined || propItem.editing === false)? true : false"
                />
              </td>
              <td class="regular_cell" v-bind:class="{ 'aps-error': propItem.errorAttributes.includes('value')}">
                <input
                  v-model="propItem.value"
                  type="text"
                  :ref="'valueinput' + index"
                  @blur="propChanged(index, $event)"
                  v-bind:class="{ 'aps-input-active': propItem.editing , 'aps-simple-input': true }"
                  :disabled="(propItem.editing === undefined || propItem.editing === false)? true : false"
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
              :loading="loadingUpload || !fileUpload.ready"
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
    <v-dialog v-model="dialogFileDownload" max-width="500px" max-height="500px">
      <v-layout class="aps-dialog-layout">
        <v-system-bar style="width:100%">
          <v-spacer />
          <v-btn icon @click.stop="dialogFileDownload=false">
            <v-icon>close</v-icon>
          </v-btn>
        </v-system-bar>
        <div class="aps-dialog-content">
          <v-radio-group v-model="selectedLayout" row>
            <v-radio label="Yaml" value="yaml" style="margin-right:5px;"></v-radio>
            <v-radio label="Properties" value="properties" style="margin-right:5px;"></v-radio>
            <v-radio label="Json" value="json"></v-radio>
          </v-radio-group>
          <div class="aps-dialog-actions">
            <v-btn :loading="loadingDownload" class="upload-btn" @click="downloadAppProps">Download</v-btn>
            <v-btn class="aps-primary-btn" @click="cancelFileDownload">cancel</v-btn>
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
    loadingDownload: false,
    dialogFileUpload: false,
    dialogFileDownload: false,
    fileUpload: {
      file: null,
      filename: '',
      filetype: '',
      ready: false
    },
    apps: [],
    searchApp: null,
    envs: [],
    lastUpdatedInfo: {},
    selectedLayout: 'yaml',
    selectedEnv: {},
    selectedApp: {},
    appProperties: [],
    filter: null
  }),
  mounted: function () {
    this.loadEnvs()
    this.queryAppSelections(null)
  },
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
          try {
            if (this.$store.state.dialog.action === CONST_ACTIONS.CONFIRM) {
              let context = this
              let target = this.$store.state.dialog.target
              api.deleteProperty(context, target, context.selectedEnv).then(function (result) {
                let i = context.appProperties.indexOf(target)
                if (i !== -1) {
                  context.appProperties.splice(i, 1)
                }
                context.$store.dispatch('notification/open', {
                  message: context.$i18n.t('props.notifications.delete.success', { target: target.name }),
                  status: 'success'
                })
                context.loadEnvs()
              }).catch(function (error) {
                context.$store.dispatch('notification/open', {
                  message: context.$i18n.t('props.notifications.delete.error', { target: target.name, error: error }),
                  status: 'error'
                })
              })
            }
          } finally {
            consumed = true
          }
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
    loadEnvs() {
      this.envs = []
      let context = this
      api.getAllEnvs(this).then(result => {
        if (result.data != null) {
          result.data.forEach(element => {
            this.envs.push(element)
          })
        }
      }).catch(error => {
        context.$store.dispatch('notification/open', {
          message: context.$i18n.t('envs.notifications.load.error', { error: error }),
          status: 'error'
        })
      })
    },
    loadProps() {
      this.loadingProps = true
      this.appProperties = []
      this.filteredAppProperties = []
      this.filter = null
      this.lastUpdatedInfo = {}
      api.getProperties(this, this.selectedEnv, this.selectedApp).then((result) => {
        if (result.data != null) {
          result.data.sort((a, b) => a.name.localeCompare(b.name, 'en', { sensitivity: 'base' }))
          let lastModifiedDate = null
          let lastModifiedBy = null
          result.data.forEach((entry) => {
            try {
              if (new Date(entry.lastModifiedDate) > lastModifiedDate || lastModifiedDate === null) {
                lastModifiedDate = new Date(entry.lastModifiedDate)
                lastModifiedBy = entry.lastModifiedBy
              }
            } finally {
              tableUtils.enhanceEditable(entry)
              this.appProperties.push(entry)
            }
          })
          if (lastModifiedDate) {
            let options = { dateStyle: 'medium', timeStyle: 'short' }
            this.lastUpdatedInfo.date = lastModifiedDate.toLocaleString(this.$i18n.locale, options)
            this.lastUpdatedInfo.user = lastModifiedBy
          }
        }
      }).catch(error => {
        this.$store.dispatch('notification/open', {
          message: this.$i18n.t('props.notifications.load.error', { error: error }),
          status: 'error'
        })
      }).finally(() => {
        this.loadingProps = false
      })
    },
    validate() {
      let isValid = true
      this.appProperties.forEach(element => {
        if (element.name === '') {
          if (!element.errorAttributes.includes('name')) {
            element.errorAttributes.push('name')
          }
          isValid = false
        }
        if (element.value === '') {
          if (!element.errorAttributes.includes('value')) {
            element.errorAttributes.push('value')
          }
          isValid = false
        }
      })
      return isValid
    },
    selectEnv: function (env) {
      if (!this.selectedApp.code) {
        return
      }
      this.loadProps()
    },
    queryAppSelections(v) {
      this.loadingApps = true
      let context = this
      api.getAllApps(this).then(result => {
        if (result.data != null) {
          result.data.forEach(element => {
            this.apps.push(element)
          })
        }
      }).catch(error => {
        context.$store.dispatch('notification/open', {
          message: context.$i18n.t('apps.notifications.load.error', { error: error }),
          status: 'error'
        })
      }).finally(() => {
        this.loadingApps = false
      })
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
      if (!this.appProperties[index].editing) {
        return
      }
      Vue.set(this.appProperties[index], 'hasChanges', tableUtils.hasChanges(this.appProperties[index]))
      if (target && (this.appProperties[index].name === target ||
        this.appProperties[index].value === target)) {
      }
    },
    editAppProperty(index) {
      Vue.set(this.appProperties[index], 'editing', true)
      Vue.set(this.appProperties[index], 'errorAttributes', [])
      let self = this
      Vue.nextTick(function () {
        self.$refs['valueinput' + index][0].focus()
      })
    },
    deleteAppProperty(item) {
      if (!item.code) {
        this.appProperties = this.appProperties.filter(p => p !== item)
        return
      }
      this.$store.dispatch('dialog/open', {
        message: this.$i18n.t('generic.confirm_delete', { target: item.name }),
        caller: this.$router.currentRoute.name + '/' + DELETE_PROP,
        target: item
      })
    },
    downloadAppProps() {
      this.loadingDownload = true
      api.downloadProperties(this, this.selectedEnv, this.selectedApp, this.selectedLayout).then((result) => {
        const url = window.URL.createObjectURL(new Blob([result.data]))
        const link = document.createElement('a')
        link.href = url
        link.setAttribute('download', `${this.selectedApp.name}-${this.selectedEnv.name}.${this.selectedLayout}`)
        document.body.appendChild(link)
        this.dialogFileDownload = false
        this.loadingDownload = false
        link.click()
      })
    },
    openDownloadDialog() {
      if (!this.selectedApp.code || !this.selectedEnv) {
        return
      }
      this.dialogFileDownload = true
    },
    cancelFileDownload() {
      this.dialogFileDownload = false
      this.loadingDownload = false
    },
    saveAppProps() {
      if (!this.hasUnsavedChanges() || !this.validate()) {
        return
      }
      this.loadingProps = true;
      let context = this
      api.saveProperties(context, context.selectedEnv, context.selectedApp, context.appProperties.filter(element => element.hasChanges))
        .then(function (result) {
          context.$store.dispatch('notification/open', {
            message: context.$i18n.t('props.notifications.save.success'),
            status: 'success'
          });
          context.loadProps()
        })
        .catch(function (error) {
          context.$store.dispatch('notification/open', {
            message: context.$i18n.t('props.notifications.save.error', { error: error }),
            status: 'error'
          })
          context.loadingProps = false
        })
    },
    addAppProperty() {
      if (!this.selectedApp.code || !this.selectedEnv) {
        return
      }
      this.triggerAppPropsfiltering('')
      let entry = {
        name: '',
        value: ''
      }
      tableUtils.enhanceEditable(entry)
      this.appProperties.unshift(entry)
    },
    appChanged(val) {
      if (!this.selectedEnv.code) {
        return
      }
      this.loadProps()
    },
    openUploadDialog() {
      if (!this.selectedApp.code || !this.selectedEnv) {
        return
      }
      this.dialogFileUpload = true
    },
    async fileUploaded(event) {
      this.fileUpload.ready = false
      this.fileUpload.file = event.target.files[0]
      this.fileUpload.filename = this.fileUpload.file.name
      let mimeType
      this.fileUpload.filetype = this.fileUpload.file.name.split('.').pop()
      if (['yml', 'yaml'].includes(this.fileUpload.filetype.toLowerCase())) {
        this.fileUpload.filetype = 'yaml'
        mimeType = 'text/x-yaml'
      } else if ('properties'.includes(this.fileUpload.filetype.toLowerCase())) {
        this.fileUpload.filetype = 'properties'
        mimeType = 'text/x-java-properties'
      }
      let context = this
      let reader = new FileReader();
      reader.onload = function (e) {
        var file = new File([reader.result], context.fileUpload.file.name, {
          type: mimeType
        })
        context.fileUpload.file = file
        context.fileUpload.filetype = context.fileUpload.filetype.toUpperCase()
        setTimeout(() => {
          context.fileUpload.ready = true
        }, 1000)
      }
      await reader.readAsText(this.fileUpload.file);
    },
    submitUploadedFile() {
      this.loadingUpload = true
      let context = this
      api.uploadProperties(context, context.selectedEnv, context.selectedApp, context.fileUpload.file)
        .then(function (result) {
          context.$store.dispatch('notification/open', {
            message: context.$i18n.t('props.notifications.save.success'),
            status: 'success'
          });
          context.cancelUploadedFile()
          context.loadingUpload = false
          context.loadProps()
        })
        .catch(function (error) {
          context.cancelUploadedFile()
          context.$store.dispatch('notification/open', {
            message: context.$i18n.t('props.notifications.save.error', { error: error }),
            status: 'error'
          })
        })
    },
    cancelUploadedFile() {
      this.dialogFileUpload = false
      this.fileUpload.file = null
      this.fileUpload.filename = ''
      this.fileUpload.filetype = ''
    },
    triggerAppPropsfiltering(event) {
      if (event !== undefined) {
        this.filter = event
        this.appProperties.forEach(function(element) {
          if (element.name.toLowerCase().includes(event.toLowerCase()) ||
            (element.value !== null && element.value.toLowerCase().includes(event.toLowerCase()))) {
            Vue.set(element, 'hidden', false)
          } else {
            Vue.set(element, 'hidden', true)
          }
        })
      }
    },
    clickOutside: function (event, vNode) {
      this.appProperties.forEach(function(element, index) {
        if (index === 0 && event.target.innerHTML === 'add_box') {
          Vue.set(element, 'editing', true)
          Vue.nextTick(function () {
            vNode.context.$refs['nameinput' + index][0].focus()
          })
          return
        }
        if (element.displayIdentifier === vNode.data.key) {
          Vue.set(element, 'editing', false)
        }
      })
    }
  }
}
</script>

<style scoped>
.search-input {
    max-height: 27px;
}

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
