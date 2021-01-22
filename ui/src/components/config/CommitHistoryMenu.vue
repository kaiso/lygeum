/** * Copyright 2018 Kais OMRI [kais.omri.int@gmail.com] * * Licensed under the
Apache License, Version 2.0 (the "License"); * you may not use this file except
in compliance with the License. * You may obtain a copy of the License at * *
http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law
or agreed to in writing, software * distributed under the License is distributed
on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
express or implied. * See the License for the specific language governing
permissions and * limitations under the License. */

<template>
  <v-menu
    v-model="menu"
    :disabled="!(app.code && env.code)"
    :close-on-content-click="false"
    :nudge-width="400"
    offset-x
  >
    <template v-slot:activator="{ on }">
      <v-tooltip bottom>
        <v-btn icon slot="activator" v-on="on" style="cursor: pointer;">
          <v-icon>history</v-icon>
        </v-btn>
        <span>{{ $t('props.actions.show_history_tooltip') }}</span>
      </v-tooltip>
    </template>

    <v-card max-width="600">
      <v-expansion-panel v-model="panel" expand>
        <v-expansion-panel-content v-for="(item, i) in commits" :key="i">
          <template v-slot:header>
            <div>{{ item.date }} - {{ item.author }}</div>
          </template>
          <v-card>
            <v-card-text>
              <ul v-if="item.changes.length > 0">
                <li v-for="(change, j) in item.changes" :key="j">
                  {{ $t('props.commits.key') }}: {{ change.key }},
                  {{ $t('props.commits.value') }}: {{ change.value }}
                  {{ $t('props.commits.type.' + change.type.toLowerCase()) }}
                </li>
              </ul>
              <span v-else>
                {{ $t('props.commits.no_changes') }}
              </span>
            </v-card-text>
          </v-card>
        </v-expansion-panel-content>
      </v-expansion-panel>
    </v-card>
  </v-menu>
</template>
<script>
import Vue from 'vue';
import * as api from '@/js/api/api';

export default {
  name: 'commitHistoryMenu-component',
  data: () => ({
    menu: false,
    panel: [],
    commits: []
  }),
  props: {
    env: Object,
    app: Object,
    random: Number
  },
  watch: {
    env: function(val, oldVal) {
      this.loadCommitHistory();
    },
    app: function(val, oldVal) {
      this.loadCommitHistory();
    },
    random: function(val, oldVal) {
      this.loadCommitHistory();
    },
    menu: function(val, oldVal) {
      this.panel = [];
    }
  },
  methods: {
    loadCommitHistory() {
      if (this.app.code && this.env.code) {
        api.getCommitHistory(this, this.env, this.app).then((result) => {
          if (result.data != null) {
            this.commits = result.data
              .map((e) => {
                e.date = new Date(e.date);
                return e;
              })
              .sort((a, b) => b.date - a.date);
            this.commits.forEach((item) => {
              let options = { dateStyle: 'short', timeStyle: 'short' };
              Vue.set(
                item,
                'date',
                item.date.toLocaleString(this.$i18n.locale, options)
              );
            });
          }
        });
      }
    }
  }
};
</script>
<style scoped>
.v-card__text {
  background-color: var(--table-bg-primary);
}
</style>
