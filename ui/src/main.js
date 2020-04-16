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
import './js/util/config-loader'
import Vue from 'vue'
import App from './App'
import router from './router'
import store from './store'
import Vuetify from 'vuetify'
import VueI18n from 'vue-i18n'
import messages from './i18n'
import axios from 'axios'
import { Vue2Storage } from 'vue2-storage'
import 'vuetify/dist/vuetify.min.css'

Vue.use(Vuetify, {
  theme: {
    primary: '#ee44aa',
    secondary: '#424242',
    accent: '#82B1FF',
    error: '#FF5252',
    info: '#2196F3',
    success: '#4CAF50',
    warning: '#FFC107'
  }
})

Vue.use(VueI18n)

Vue.prototype.$http = axios.create({
  baseURL: window.__ENV['server.url'],
  timeout: 180000,
  headers: { 'X-Lygeum-Caller': 'lygeum' }
})

const i18n = new VueI18n({
  locale: 'en-US',
  fallbackLocale: 'en-US',
  messages
})

Vue.use(Vue2Storage, {
  prefix: 'lygeum_',
  driver: 'local',
  ttl: 60 * 60 * 24 * 1000 // 24 hours
})

Vue.config.productionTip = false

Vue.directive('click-outside', {
  bind: function (el, binding, vNode) {
    el.__vueClickOutside__ = event => {
      // call method provided in v-click-outside value
      let array = el.getElementsByTagName('*')
      let outside = true
      for (let index = 0; index < array.length; index++) {
        if (event.target === array[index]) {
          outside = false
          break
        }
      }
      if (outside) {
        vNode.context[binding.expression](event, vNode)
        event.stopPropagation()
      }
    }
    document.body.addEventListener('click', el.__vueClickOutside__)
  },
  unbind: function (el, binding, vNode) {
    // Remove Event Listeners
    document.removeEventListener('click', el.__vueClickOutside__)
    el.__vueClickOutside__ = null
  }
})


/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  i18n,
  store,
  components: { App },
  template: '<App/>'
})
