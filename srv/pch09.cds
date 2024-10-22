using {TableService as view} from './table';
using {PCH} from '../db/model-pch';

//MM-012 FCレポート

extend service TableService {


    entity PCH09_LIST as
        select from PCH.T09_FORCAST as T01 {
            *
        }
};
