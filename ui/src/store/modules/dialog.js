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

const state = {
  open: false,
  message: '',
  caller: '',
  action: '',
  target: '',
  consumption: ''
}

const actions = {
  open ({ commit, state }, payload) {
    if (state.consumption === 'pending') {
      throw new Error('main dialog state was not consumed yet')
    }
    commit('open', { payload })
  },
  close ({ commit, state }, payload) {
    commit('close', { payload })
  },
  consume ({ commit, state }, payload) {
    commit('consume', { payload })
  }
}

const mutations = {
  open (state, { payload }) {
    state.open = true
    state.consumption = 'pending'
    state.message = payload.message
    state.caller = payload.caller
    state.target = payload.target
  },
  close (state, { payload }) {
    state.open = false
    state.action = payload.action
  },
  consume (state, { payload }) {
    if (payload.caller === state.caller) {
      state.consumption = ''
      state.caller = ''
      state.action = ''
      state.message = ''
      state.target = ''
    }
  }
}

export default {
  namespaced: true,
  state,
  actions,
  mutations
}
