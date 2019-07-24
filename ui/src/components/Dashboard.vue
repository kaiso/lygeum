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
  <aps-layout>
    <p slot="mainContent">
      <a href="https://github.com/kaiso/relmongo/archive/v1.0.0.zip">download</a>
    </p>
  </aps-layout>
</template>
<script>

import Layout from '@/components/layout/Layout'
export default {
  name: 'dashboard-component',
  components: { 'aps-layout': Layout }
}
</script>

<style scoped>
</style>
