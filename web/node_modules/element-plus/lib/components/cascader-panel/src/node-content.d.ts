import type { PropType } from 'vue';
import type { CascaderNode } from './types';
declare const _default: import("vue").DefineComponent<{
    node: {
        type: PropType<CascaderNode>;
        required: true;
    };
    disabled: BooleanConstructor;
}, () => JSX.Element, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    node: {
        type: PropType<CascaderNode>;
        required: true;
    };
    disabled: BooleanConstructor;
}>>, {
    disabled: boolean;
}>;
export default _default;
