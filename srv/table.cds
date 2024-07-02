using {SYS} from '../db/model-sys';
//V2的缺点：不能接收基本类型以外的参数。
//V4的缺点：UI5中 smarttable 不认v4的odata

@endpoints: [{
  path    : 'TableService',
  protocol: 'odata-v2'
}]
service TableService {

    entity SYS_T01_USER as projection on SYS.T01_USER;
    entity SYS_T02_ROLE as projection on SYS.T02_ROLE;
    entity SYS_T04_USER_2_ROLE as projection on SYS.T04_USER_2_ROLE;
    entity SYS_T06_DOC_NO as projection on SYS.T06_DOC_NO;



}