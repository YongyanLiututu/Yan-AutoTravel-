import { buildProps, definePropType } from '../../../utils/vue/props/runtime.mjs';

const descriptionsRowProps = buildProps({
  row: {
    type: definePropType(Array),
    default: () => []
  }
});

export { descriptionsRowProps };
//# sourceMappingURL=descriptions-row2.mjs.map
