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
  <aps-layout :title="$t('layout.settings')">
    <v-container slot="mainContent" grid-list-md text-xs-center style="height:100%">
    <v-layout row wrap>
      <v-flex xs6>
        <v-flex xs12>
          <v-avatar color="brown lighten-3" size="100" @click.stop="route('/settings/info')"
          style="cursor:pointer;">
            <v-btn icon>
            <v-icon dark size="70">info</v-icon>
            </v-btn>
          </v-avatar>
        </v-flex>
      <label>{{$tc('settings.system', 2)}} {{$tc('settings.info', 2)}} </label>
      </v-flex>
    </v-layout>
</v-container>
  </aps-layout>
</template>
<script>
import Layout from '@/components/layout/Layout'
export default {
  name: 'settings-component',
  components: { 'aps-layout': Layout },
  methods: {
    route: function (url) {
      this.$router.push({ path: `${url}` })
    }
  }
}
</script>
<style scoped>
</style>
