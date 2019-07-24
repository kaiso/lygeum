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

import Vue from 'vue'
import { deepCopy, equal } from '@/js/util/util'

/**
 * add element to object to detect
 * whether it was edited
 *
 * @param {Object} editableObject
 * @return {Object} the enhanced object
 */
export function enhanceEditable (editableObject) {
  Vue.set(editableObject, 'original', deepCopy(editableObject))
  Vue.set(editableObject, 'editing', false)
  Vue.set(editableObject, 'hasChanges', false)
}

/**
 * detect whether object
 * has changed or not
 *
 * @param {Object} obj
 * @return {Boolean} the result
 */
export function hasChanges (obj) {
  let o = obj.original
  let c = deepCopy(obj)
  delete c.editing
  delete c.original
  delete c.hasChanges
  return !equal(o, c)
}
