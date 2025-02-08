using {TableService} from './table';
using {SYS} from '../db/model-sys';


extend service TableService {

    //得到主页的菜单
    entity HOMEPAGE_MENU_LIST as
        select  from SYS.T10_MENU as T10
        left join SYS.T03_AUTH as T03 on ( T03.MENU_ID = T10.MENU_ID )
        distinct{
            key T10.MENU_ID,
            T10.MENU_NAME,
            T10.MENU_SUB_NAME,
            T10.MENU_ICON,
            T10.MENU_MODULE,
            T10.MENU_ROUTE_ID,
            T10.MENU_UNIT,
            T10.MENU_FOOTER,
            T10.MENU_VALUECOLOR,
            T10.MENU_VALUE
        }
        where
      T10.MENU_TYPE = 'UI5'
      and (
           T10.MENU_ID || '_VIEW' in (
          select from USER_AUTH_LIST as MAL {
            key MAL.AUTH_ID
          }
        )
        or T10.MENU_ID || '_ALL'  in (
          select from USER_AUTH_LIST as MAL {
            key MAL.AUTH_ID
          }
        )
        or T10.MENU_ID || '_CREAT'  in (
          select from USER_AUTH_LIST as MAL {
            key MAL.AUTH_ID
          }
        )
      );

      
  //用户→权限视图
  entity USER_AUTH_LIST     as
    select from SYS.T03_AUTH as T03
    left join SYS.T05_ROLE_2_AUTH as T05
      on T05.AUTH_ID = T03.ID
    left join SYS.T04_USER_2_ROLE as T04
      on T05.ROLE_ID = T04.ROLE_ID
    left join SYS.T01_USER as T01
      on T04.USER_ID = T01.ID
    {
      key T03.ID as AUTH_ID
    }
    where
      T01.USER_ID = COALESCE(
        $user, 'anonymous'
      );


  //用户→权限→菜单视图，调用上面的视图。
  entity MENU_AUTH_LIST     as
    select from SYS.T03_AUTH as T03
    left join USER_AUTH_LIST as TLL
      on T03.ID = TLL.AUTH_ID
    {
      key T03.ID,
      key T03.MENU_ID,
          (
            case
              when
                TLL.AUTH_ID is null
              then
                FALSE
              else
                TRUE
            end
          ) as AUTH_FLAG : Boolean
    };


//数据权限相关，如果usertype为1则内部员工，2为供应商，内部员工可以查看所有数据，供应商只能查看自己数据
  entity AUTH_USER_BP     as
    select from TableService.SYS_T01_USER as T01
    inner join TableService.SYS_T14_USER_2_BP as T02
      on T01.ID = T02.USER_ID
    
    distinct{
      key T02.BP_ID
    } where
      T01.USER_ID = COALESCE(
        $user, 'anonymous'
      );


  action SYS_USER_HOME_PAGE(hpRuleJson : String) returns String;

}