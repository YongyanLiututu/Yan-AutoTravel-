'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

class ElementPlusError extends Error {
  constructor(m) {
    super(m);
    this.name = "ElementPlusError";
  }
}
function throwError(scope, m) {
  throw new ElementPlusError(`[${scope}] ${m}`);
}
function debugWarn(scope, message) {
}

exports.debugWarn = debugWarn;
exports.throwError = throwError;
//# sourceMappingURL=error.js.map
