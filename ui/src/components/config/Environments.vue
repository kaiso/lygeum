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
    <aps-layout :title="this.$i18n.tc('envs.env',2)">
      <v-layout slot="mainContent" column wrap style="height:100%">
        <v-system-bar window class="system-toolbar">
          <v-tooltip bottom>
            <v-btn icon slot="activator" @click="saveEnvs()" style="cursor: pointer;">
              <v-badge color="yellow" class="aps-badge-small" :value="hasUnsavedChanges()">
                <span slot="badge">!</span>
                <v-icon>save</v-icon>
              </v-badge>
            </v-btn>
            <span>{{$t('envs.actions.save_tooltip')}}</span>
          </v-tooltip>
          <v-tooltip bottom>
            <v-btn icon slot="activator" @click="addEnv()" style="cursor: pointer;">
              <v-icon>add_box</v-icon>
            </v-btn>
            <span>{{$t('envs.actions.add_tooltip')}}</span>
          </v-tooltip>
          <v-spacer></v-spacer>
          <span class="input-container">
            <v-text-field
              style="height:27px;"
              class="aps-input-active"
              append-icon="search"
              @input="triggerEnvsfiltering"
              v-model="envFilter"
              single-line
              hide-details
            ></v-text-field>
          </span>
        </v-system-bar>
        <span v-if="loadingEnvs">
          <v-progress-linear color="blue" indeterminate></v-progress-linear>
        </span>
        <span class="aps-table-container">
          <table>
            <tr
              :key="'tr' + index"
              v-for="(propItem, index) in (filteredEnvs.length > 0 ? filteredEnvs : envs)"
            >
              <td class="regular_cell">
                <input
                  :ref="'name' + index"
                  v-model="propItem.name"
                  type="text"
                  @blur="envChanged(index)"
                  v-bind:class="{ 'aps-input-active': propItem.editing , 'aps-simple-input': true }"
                  :disabled="propItem.editing === undefined ? 'disabled' : false"
                />
              </td>
              <td class="actions_cell">
                <v-btn icon @click="editEnv(index)">
                  <v-icon>edit</v-icon>
                </v-btn>
                <v-btn icon @click="deleteEnv(propItem)">
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
import Layout from '@/components/layout/Layout';
import * as api from '@/js/api/api';
import * as tableUtils from '@/js/util/tableUtils';
import Vue from 'vue';
import { mapState } from 'vuex';
import { CONST_ACTIONS } from '@/js/util/constants';
const DELETE_ENV = 'deleteEnv';

export default {
  name: 'envconfig-component',
  components: { 'aps-layout': Layout },
  data: () => ({
    loadingEnvs: false,
    envs: [],
    filteredEnvs: [],
    envFilter: ''
  }),
  computed: {
    ...mapState({
      mainDialogOpen: state => state.dialog.open
    })
  },
  watch: {
    mainDialogOpen(val) {
      if (val) {
        return;
      }
      let caller = this.$store.state.dialog.caller;
      let consumed = false;
      switch (caller) {
        case this.$router.currentRoute.name + '/' + DELETE_ENV:
          if (this.$store.state.dialog.action === CONST_ACTIONS.CONFIRM) {
            let i = this.envs.indexOf(this.$store.state.dialog.target);
            if (i !== -1) {
              this.envs.splice(i, 1);
            }
          }
          consumed = true;
          break;
        default:
          break;
      }
      if (consumed) {
        this.$store.dispatch('dialog/consume', {
          caller: this.$router.currentRoute.name + '/' + DELETE_ENV
        });
      }
    }
  },
  mounted: function () {
    this.loadEnvs();
  },
  methods: {
    loadEnvs() {
      this.loadingEnvs = true;
      this.envs = [];
      this.filteredEnvs = [];
      api.getAllEnvs(this).then(result => {
        if (result.data != null) {
          result.data.forEach(element => {
            tableUtils.enhanceEditable(element);
            this.envs.push(element);
          });
        }
        this.loadingEnvs = false;
      });
    },
    hasUnsavedChanges() {
      let res = false;
      this.envs.some(element => {
        if (element.hasChanges === true) {
          res = true;
          return true;
        }
      });
      return res;
    },
    envChanged(index) {
      let realIndex = index;
      if (this.filteredEnvs.length > 0) {
        realIndex = this.envs.indexOf(this.filteredEnvs[index]);
      }
      if (!this.envs[realIndex].editing) {
        return;
      }
      Vue.set(
        this.envs[realIndex],
        'hasChanges',
        tableUtils.hasChanges(this.envs[realIndex])
      );
      Vue.set(this.envs[realIndex], 'editing', false);
    },
    editEnv(index) {
      let realIndex = index;
      if (this.filteredEnvs.length > 0) {
        realIndex = this.envs.indexOf(this.filteredEnvs[index]);
      }
      Vue.set(this.envs[realIndex], 'editing', true);
      let self = this;
      Vue.nextTick(function () {
        self.$refs['name' + index][0].focus();
      });
    },
    deleteEnv(item) {
      this.$store.dispatch('dialog/open', {
        message: 'Are you sure to remove the environment',
        caller: this.$router.currentRoute.name + '/' + DELETE_ENV,
        target: item
      });
    },
    async saveEnvs() {
      this.loadingEnvs = true;
      let promises = [];
      let context = this;
      this.envs
        .filter(env => env.hasChanges === true)
        .forEach(val => {
          console.log(JSON.stringify(val));
          if (val.code) {
            console.log('updating env');
            promises.push(api.saveEnv(this, val));
          } else {
            console.log('creating env');
            promises.push(api.createEnv(this, val));
          }
        });
      await Promise.all(promises)
        .then(function (result) {
          console.log('reloading evns')
          context.loadEnvs();
        })
        .catch(function (error) {
          console.log('problem saving envs ', error);
          context.loadingEnvs = false;
        });
    },
    addEnv() {
      this.envFilter = '';
      this.filteredEnvs = [];
      this.envs.unshift({
        code: '',
        name: '',
        editing: true
      });
    },
    triggerEnvsfiltering(event) {
      if (event !== undefined) {
        this.filteredEnvs = this.envs.filter(value => {
          return value.name.toLowerCase().includes(event.toLowerCase());
        });
      }
    }
  }
};
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
