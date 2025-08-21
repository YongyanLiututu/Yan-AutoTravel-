'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var vue = require('vue');

const useDeprecated = ({ from, replacement, scope, version, ref, type = "API" }, condition) => {
  vue.watch(() => vue.unref(condition), (val) => {
  }, {
    immediate: true
  });
};

exports.useDeprecated = useDeprecated;
//# sourceMappingURL=index.js.map
