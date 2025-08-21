'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var runtime = require('../../../utils/vue/props/runtime.js');

const splitterProps = runtime.buildProps({
  layout: {
    type: String,
    default: "horizontal",
    values: ["horizontal", "vertical"]
  }
});

exports.splitterProps = splitterProps;
//# sourceMappingURL=splitter.js.map
