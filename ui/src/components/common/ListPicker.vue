<template>
    <div class="lp-container">
    <div class="lp-header">
      <div class="lp-list-header">{{choiceLabel}}</div>
      <div class="lp-list-header">{{sourceLabel}}</div>
    </div>
    <div class="lp-content">
        <div class="lp-list-container">
          <template v-for="(item,index) in choice">
            <div class="lp-list-label" v-bind:key="index" @click="remove(item)">{{item.name}}</div>
          </template>
        </div>
        <div class="lp-list-container">
          <template v-for="(item,index) in source">
            <div class="lp-list-label" v-bind:key="index" @click="add(item)">{{item.name}}</div>
          </template>
        </div>
    </div>
    </div>
</template>
<script>
export default {
  data: function () {
    return {
      active: true
    }
  },
  props: {
    choice: {
      default: [],
      type: Array
    },
    source: {
      default: [],
      type: Array
    },
    choiceLabel: {
      default: '',
      type: String
    },
    sourceLabel: {
      default: '',
      type: String
    }
  },
  methods: {
    add (item) {
      console.log('add item', JSON.stringify(item))
      let index = this.source.indexOf(item)
      this.source.splice(index, 1)
      this.choice.push(item)
    },
    remove (item) {
      console.log('remove item', JSON.stringify(item))
      let index = this.choice.indexOf(item)
      this.choice.splice(index, 1)
      this.source.push(item)
    }
  }
}
</script>
<style scoped>
.lp-container {
  display: flex;
  flex-direction: column;
  min-height: 150px;
  min-width: 400px;
  background-color: var(--input-primary-background-color);
}

.lp-header {
  width: 100%;
  display: flex;
  flex-direction: row;
}

.lp-content {
  width: 100%;
  overflow: scroll;
  display: flex;
  flex-direction: row;
  flex: 1 1 auto;
}

.lp-list-container {
 width: 50%;
 border: 1px solid darkgrey;
 height: 100%;
 overflow-y: scroll;
}

.lp-list-label {
  cursor: pointer;
  background-color: var(--input-primary-background-color);
  box-shadow: 1px 1px black;
  margin-top: 1px;
}

.lp-list-label:hover {
  background: hsla(0,0%,100%,.12);
}

.lp-list-header {
  width: 50%;
  height: 25px;
  box-shadow: 1px 1px black;
  border: 1px solid darkgrey;
  text-align: center;
  background-color: #263238;
  font-weight: bold;
}
</style>


