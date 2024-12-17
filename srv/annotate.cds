using { TableService  } from './table';
annotate TableService.COM_Y_N_DROPDOWN with {
  OP_VALUE @Common.Text: {$value: OP_NAME}
};
