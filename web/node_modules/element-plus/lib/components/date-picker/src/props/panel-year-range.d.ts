import type { ExtractPropTypes, __ExtractPublicPropTypes } from 'vue';
export declare const panelYearRangeProps: {
    readonly unlinkPanels: BooleanConstructor;
    readonly visible: BooleanConstructor;
    readonly showFooter: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly parsedValue: {
        readonly type: import("vue").PropType<import("dayjs").Dayjs[]>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
};
export declare const panelYearRangeEmits: string[];
export type PanelYearRangeProps = ExtractPropTypes<typeof panelYearRangeProps>;
export type PanelYearRangePropsPublic = __ExtractPublicPropTypes<typeof panelYearRangeProps>;
