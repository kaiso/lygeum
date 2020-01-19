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
  <aps-layout :title="$t('settings.system')+' '+$t('settings.info')">
    <v-container slot="mainContent" grid-list-md text-xs-center style="height:100%">
      <v-card>
        <v-card-title primary-title>
          <div>
            <h3 class="headline mb-0">{{ $t('settings.general')}}</h3>
            <h5><pre>

              Lygeum Server Information</pre></h5>
            <div><pre>
            {{ $t('settings.version') }} : {{ sysInfo.version }}</pre></div>
            <h5><pre>
              Copyright &copy; Kais OMRI 2020</pre></h5>
          </div>
        </v-card-title>
      </v-card>
      <v-card>
        <v-card-title primary-title>
          <div>
            <h3 class="headline mb-0">{{ $t('settings.system')}}</h3>
            <div><pre> {{ sysInfo.jvm }} </pre></div>
          </div>
        </v-card-title>
      </v-card>
      <v-card>
        <v-card-title primary-title>
          <div>
            <h3 class="headline mb-0">{{ $t('settings.datasource')}}</h3>
            <div><pre> {{ sysInfo.database }} </pre></div>
          </div>
        </v-card-title>
      </v-card>
    </v-container>
  </aps-layout>
</template>
<script>
import Layout from '@/components/layout/Layout'
import * as api from '@/js/api/api'
export default {
  name: 'info-component',
  components: { 'aps-layout': Layout },
  data: () => ({
    sysInfo: {}
  }),
  beforeMount: function () {
    const context = this
    api.getSystemInformation(context).then(function(response) {
      context.sysInfo = response.data
    }).catch(error => {
      console.log(error)
    })
  }
}
</script>
<style scoped>
pre {
  text-align: left;
}
div {
  text-align: left;
}
</style>
