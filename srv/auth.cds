using {TableService} from './table';
using {SYS} from '../db/model-sys';


extend service TableService {

    //得到主页的菜单
    entity HOMEPAGE_MENU_LIST as
        select  from SYS.T10_MENU as T10
        left join SYS.T03_AUTH as T03 on ( T03.MENU_ID = T10.MENU_ID )
        distinct{
            key T10.MENU_ID,
            T10.localized.MENU_NAME,
            T10.localized.MENU_SUB_NAME,
            T10.MENU_ICON,
            T10.MENU_MODULE,
            T10.MENU_ROUTE_ID,
            T10.MENU_UNIT,
            T10.MENU_FOOTER,
            T10.MENU_VALUECOLOR,
            T10.MENU_VALUE
        }
        


}