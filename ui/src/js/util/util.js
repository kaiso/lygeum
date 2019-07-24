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

/* eslint-disable curly, eqeqeq */
/**
 * Get the first item that pass the test
 * by second argument function
 *
 * @param {Array} list
 * @param {Function} f
 * @return {*}
 */
export function find (list, f) {
  return list.filter(f)[0]
}

/**
 * Deep copy the given object considering circular structure.
 * This function caches all nested objects and its copies.
 * If it detects circular structure, use cached copy to avoid infinite loop.
 *
 * @param {*} obj
 * @param {Array<Object>} cache
 * @return {*}
 */
export function deepCopy (obj, cache = []) {
  // just return if obj is immutable value
  if (obj === null || typeof obj !== 'object') {
    return obj
  }

  // if obj is hit, it is in circular structure
  const hit = find(cache, c => c.original === obj)
  if (hit) {
    return hit.copy
  }

  const copy = Array.isArray(obj) ? [] : {}
  // put the copy into cache at first
  // because we want to refer it in recursive deepCopy
  cache.push({
    original: obj,
    copy
  })

  Object.keys(obj).forEach(key => {
    copy[key] = deepCopy(obj[key], cache)
  })

  return copy
}

/**
 * forEach for object
 */
export function forEachValue (obj, fn) {
  Object.keys(obj).forEach(key => fn(obj[key], key))
}

export function isObject (obj) {
  return obj !== null && typeof obj === 'object'
}

export function isPromise (val) {
  return val && typeof val.then === 'function'
}

export function assert (condition, msg) {
  if (!condition) throw new Error(`[vuex] ${msg}`)
}

// Returns the object's class, Array, Date, RegExp, Object are of interest to us
var getClass = function (val) {
  return Object.prototype.toString.call(val)
    .match(/^\[object\s(.*)\]$/)[1]
}

// Defines the type of the value, extended typeof
var whatis = function (val) {
  if (val === undefined)
    return 'undefined'
  if (val === null)
    return 'null'

  var type = typeof val

  if (type === 'object')
    type = getClass(val).toLowerCase()

  if (type === 'number') {
    if (val.toString().indexOf('.') > 0)
      return 'float'
    else
      return 'integer'
  }

  return type
}

var compareObjects = function (a, b) {
  if (a === b)
    return true
  for (var i in a) {
    if (b.hasOwnProperty(i)) {
      if (!equal(a[i], b[i])) return false
    }
  }
  for (var j in b) {
    if (!a.hasOwnProperty(j)) {
      return false
    }
  }
  return true
}

var compareArrays = function (a, b) {
  if (a === b)
    return true
  if (a.length !== b.length)
    return false
  for (var i = 0; i < a.length; i++) {
    if (!equal(a[i], b[i])) return false
  };
  return true
}

var _equal = {}
_equal.array = compareArrays
_equal.object = compareObjects
_equal.date = function (a, b) {
  return a.getTime() === b.getTime()
}
_equal.regexp = function (a, b) {
  return a.toString() === b.toString()
}

/*
 * Are two values equal, deep compare for objects and arrays.
 * @param a {any}
 * @param b {any}
 * @return {boolean} Are equal?
 */
export function equal (a, b) {
  if (a !== b) {
    var atype = whatis(a)
    var btype = whatis(b)
    if (atype === btype) {
      return _equal.hasOwnProperty(atype) ? _equal[atype](a, b) : a == b
    }
    return false
  }
  return true
}
