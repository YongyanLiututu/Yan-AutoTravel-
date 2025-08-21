'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');
var core = require('@vueuse/core');
var index = require('../use-namespace/index.js');

const defaultIdInjection = {
  prefix: Math.floor(Math.random() * 1e4),
  current: 0
};
const ID_INJECTION_KEY = Symbol("elIdInjection");
const useIdInjection = () => {
  return vue.getCurrentInstance() ? vue.inject(ID_INJECTION_KEY, defaultIdInjection) : defaultIdInjection;
};
const useId = (deterministicId) => {
  const idInjection = useIdInjection();
  const namespace = index.useGetDerivedNamespace();
  const idRef = core.computedEager(() => vue.unref(deterministicId) || `${namespace.value}-id-${idInjection.prefix}-${idInjection.current++}`);
  return idRef;
};

exports.ID_INJECTION_KEY = ID_INJECTION_KEY;
exports.useId = useId;
exports.useIdInjection = useIdInjection;
//# sourceMappingURL=index.js.map
