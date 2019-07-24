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

<style>
@import url("assets/css/root.css");
@import url("assets/css/global.css");
</style>

<template>
  <v-app dark>
    <router-view></router-view>
    <div id="global_dialog_container">
      <v-dialog
        v-model="dialog"
        hide-overlay
        max-height="150px"
        content-class="global-dialog-content"
        full-width
        origin="top"
        transition="dialog-top-transition"
      >
        <div style="width:100%;height:100%;display:flex;flex-direction:column;">
          <label style="margin:auto;">{{message}}</label>
          <div class="aps-dialog-actions">
            <v-btn class="aps-primary-btn" @click="dialogConfirm()">{{$t('actions.ok')}}</v-btn>
            <v-btn class="aps-primary-btn" @click="dialogCancel()">{{$t('actions.cancel')}}</v-btn>
          </div>
        </div>
      </v-dialog>
    </div>
  </v-app>
</template>

<script>
import { mapState } from 'vuex'
import { CONST_ACTIONS } from '@/js/util/constants'

export default {
  data: () => ({
    drawer: true,
    dialog: false,
    dialog_action: CONST_ACTIONS.CANCEL
  }),
  props: {
    source: String
  },
  computed: {
    ...mapState({
      open: state => state.dialog.open,
      message: state => state.dialog.message
    })
  },
  watch: {
    dialog(val) {
      if (!val) {
        this.$store.dispatch('dialog/close', {
          action: this.dialog_action
        })
      }
    },
    open(val) {
      if (val) {
        this.dialog_action = CONST_ACTIONS.CANCEL
        this.dialog = true
      }
    }
  },
  methods: {
    dialogConfirm() {
      this.dialog = false
      this.dialog_action = CONST_ACTIONS.CONFIRM
    },
    dialogCancel() {
      this.dialog = false
    }
  }
}
</script>



