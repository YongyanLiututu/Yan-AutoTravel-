'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var index = require('../../../hooks/use-delayed-toggle/index.js');
var icon = require('../../../utils/vue/icon.js');
var runtime = require('../../../utils/vue/props/runtime.js');
var objects = require('../../../utils/objects.js');
var types = require('../../../utils/types.js');

const alertEffects = ["light", "dark"];
const alertProps = runtime.buildProps({
  title: {
    type: String,
    default: ""
  },
  description: {
    type: String,
    default: ""
  },
  type: {
    type: String,
    values: objects.keysOf(icon.TypeComponentsMap),
    default: "info"
  },
  closable: {
    type: Boolean,
    default: true
  },
  closeText: {
    type: String,
    default: ""
  },
  showIcon: Boolean,
  center: Boolean,
  effect: {
    type: String,
    values: alertEffects,
    default: "light"
  },
  ...index.useDelayedToggleProps
});
const alertEmits = {
  open: () => true,
  close: (evt) => types.isUndefined(evt) || evt instanceof Event
};

exports.alertEffects = alertEffects;
exports.alertEmits = alertEmits;
exports.alertProps = alertProps;
//# sourceMappingURL=alert.js.map
