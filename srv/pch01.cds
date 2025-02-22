using {TableService as view} from './table';
using {MST} from '../db/model-mst';
using {PCH} from '../db/model-pch';
using {SYS} from '../db/model-sys';


extend service TableService {
  //得到主页的菜单
  entity PCH_01_DL          as
    select from PCH.T01_PO_H as T01
    join PCH.T02_PO_D as T02
      on(
        T02.PO_NO = T01.PO_NO
      )
    left join MST.T03_SAP_BP as T03
      on(
        T03.BP_ID = T01.SUPPLIER
      )
            join view.SYS_T01_USER as Tu
                on Tu.USER_ID = COALESCE($user, 'anonymous')
                and (Tu.USER_TYPE = '1' or (T01.SUPPLIER in (select BP_ID from view.AUTH_USER_BP   ) and Tu.USER_TYPE = '2') )


    distinct {

      key T01.PO_NO,
      key T02.D_NO,
          BP_NAME1,
          T01.SUPPLIER,
          T02.MAT_ID,
          T02.PO_D_TXZ01,
          T02.PO_PUR_QTY,
          T02.PO_PUR_UNIT,
          T02.PO_TYPE,
          T01.PO_DATE,
          T02.PO_D_DATE,
          T02.CUSTOMER_MAT,
          T02.STATUS,
          T02.SUPPLIER_MAT
    };

  entity PCH_01_AUTH1       as
    select from view.SYS_T01_USER as T01 {
      key T01.USER_ID,
          T01.USER_TYPE,
          T01.BP_NUMBER
    }
    where
      USER_ID = case
                  when
                    $user is null
                  then
                    'anonymous'
                  else
                    $user
                end;

  entity PCH_01_PLANT_CHECK as
    select from SYS.T09_USER_2_PLANT as T09
    inner join PCH.T02_PO_D as T02
      on T09.PLANT_ID = T02.PLANT_ID
    {
      key T09.PLANT_ID
    }
    where
      T09.USER_ID = case
                      when
                        $user is null
                      then
                        'anonymous'
                      else
                        $user
                    end;
};


annotate TableService.PCH_01_DL with {

  STATUS @(Common: {ValueList: {entity: 'PCH01_STATUS_POP_1', }});

};
