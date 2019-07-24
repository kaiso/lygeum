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
      <list-picker :choice="choice" :source="source" sourceLabel="Available" choiceLabel="Roles"></list-picker>
    </p>
  </aps-layout>
</template>
<script>
import Layout from '@/components/layout/Layout'
import ListPicker from '@/components/common/ListPicker'
export default {
  name: 'settings-component',
  components: { 'aps-layout': Layout,
    'list-picker': ListPicker },
  data: () => ({
    source: [
      { key: 'name', label: 'label1' },
      { key: 'name1', label: 'label1' },
      { key: 'name2', label: 'label1' },
      { key: 'name3', label: 'label1' },
      { key: 'name4', label: 'label1' },
      { key: 'name5', label: 'label1' },
      { key: 'name6', label: 'label1' },
      { key: 'name6', label: 'label1' },
      { key: 'name7', label: 'label1' }
    ],
    choice: [{ key: 'name', label: 'label2' }]
  })
}
</script>
<style scoped>
</style>
