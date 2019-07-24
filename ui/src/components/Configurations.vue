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
  <aps-layout :title="$t('layout.configurations')">
<v-container slot="mainContent" grid-list-md text-xs-center style="height:100%">
    <v-layout row wrap>
      <v-flex xs6>
        <v-flex xs12>
          <v-avatar color="brown lighten-3" size="100" @click.stop="route('/config/environments')"
          style="cursor:pointer;">
            <v-btn icon>
            <v-icon dark size="70">developer_board</v-icon>
            </v-btn>
          </v-avatar>
        </v-flex>
      <label>{{$tc('envs.env', 2)}} </label>
      </v-flex>
      <v-flex xs6>
        <v-flex xs12>
          <v-avatar color="brown lighten-3" size="100" @click.stop="route('/config/applications')"
          style="cursor:pointer;">
          <v-btn icon @click="''">
            <v-icon dark size="70">apps</v-icon>
          </v-btn>
          </v-avatar>
        </v-flex>
        <label>{{$tc('apps.application',2)}} </label>
      </v-flex>
      <v-flex xs6>
        <v-flex xs12>
          <v-avatar color="brown lighten-3" size="100"  @click.stop="route('/config/properties')"
          style="cursor:pointer;">
            <v-btn icon>
            <v-icon dark size="70">list</v-icon>
            </v-btn>
          </v-avatar>
        </v-flex>
        <label>{{$tc('props.prop',2)}} </label>
      </v-flex>
    </v-layout>
</v-container>
  </aps-layout>
</template>
<script>
import Layout from '@/components/layout/Layout'
export default {
  name: 'configurations-component',
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
