using {TableService as view} from './table';
using {MST} from '../db/model-mst';
using {PCH} from '../db/model-pch';


extend service TableService {
    //得到主页的菜单
    entity PCH_01_DL as
        select  from PCH.PCH_T01_PO_H as T01
        left join PCH.PCH_T02_PO_D as T02 on ( T02.PO_NO = T01.PO_NO )
        left join MST.MST_T03_SAP_BP     as T03 on ( T03.BP_ID = T01.SUPPLIER)
        distinct{
            key T01.SUPPLIER,       
            T03.BP_NAME1,                   
            T01.PO_NO,              
            T02.MAT_ID,                 
            T02.PO_TYPE,                                        
            T01.PO_DATE,                    
            T02.PO_D_DATE,                  
            T02.CUSTOMER_MAT, 
            T02.STATUS ,                 
            T02.SUPPLIER_MAT                    
        }




}


annotate TableService.PCH_01_DL with {
  
  STATUS @(Common: {ValueList: {entity: 'PCH01_STATUS_POP_1', }});

};