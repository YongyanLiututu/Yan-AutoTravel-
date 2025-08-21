'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var runtime = require('../../../utils/vue/props/runtime.js');

const splitterPanelProps = runtime.buildProps({
  min: {
    type: [String, Number]
  },
  max: {
    type: [String, Number]
  },
  size: {
    type: [String, Number]
  },
  resizable: {
    type: Boolean,
    default: true
  },
  collapsible: Boolean
});

exports.splitterPanelProps = splitterPanelProps;
//# sourceMappingURL=split-panel2.js.map
