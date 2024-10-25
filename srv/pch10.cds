using {TableService as view} from './table';
using {PCH} from '../db/model-pch';

//MM-012 FCレポート

extend service TableService {


    entity PCH10_Header as
        select from PCH.T06_QUOTATION_H as T01 distinct {
            *
        }

};
