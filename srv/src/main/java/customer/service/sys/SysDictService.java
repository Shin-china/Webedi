package customer.service.sys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.sap.cds.services.draft.DraftService;
import com.sap.cds.services.messages.Messages;
import cds.gen.sys.T07ComOpH;
import cds.gen.sys.T08ComOpD;
import cds.gen.tableservice.SysT08ComOpD;
import customer.comm.constant.UmcConstants;
import customer.comm.tool.StringTool;
import customer.dao.sys.DictDao;

@Component
public class SysDictService {
  @Autowired
  private DictDao dictDao;

  @Autowired
  Messages messages;

  public boolean checkHcode(String code, String id) {

    if (StringTool.isEmpty(code)) {
      this.messages.error("UPLOAD_EROOR_SNULL", "泛用CODE");
      return true;
    }
    T07ComOpH t07 = dictDao.getByCode(code);
    if (null != t07 && !t07.getId().equals(id)) {
      this.messages.error("ERROR_SYS03_H_CODE_IS_EXIT", code);
      return true;
    }

    return true;
  }

  public void checkItems(List<SysT08ComOpD> items) {
    if (null == items || items.size() == 0) {
      this.messages.error("ERROR_COMMON_NO_ITEMS");
    }
  }

  public void deleteItems(String id, String updateFlag, DraftService adminService) {
    // 新数据
    Map<String, Object> data = dictDao.setUpdateDaMap(UmcConstants.DELETE_YES, null);

    // 条件
    Map<String, Object> keys = new HashMap<>();
    keys.put("ID", id);

    keys.put("IsActiveEntity", Boolean.FALSE);

    dictDao.updateDarftByDelete(id, adminService);
  }

  /**
   * 汎用Hcode，DCODE获取描述
   * 
   * @param hCode
   * @param dCode
   * @return
   */
  public String getDictName(String hCode, String dCode) {
    String dName = "";
    T08ComOpD t08ComOpD = dictDao.get(hCode, dCode);
    if (null != t08ComOpD) {
      dName = t08ComOpD.getDName();
    }
    return dName;
  }

  /*
   * 品目是否设定了UPN采番
   */
  public boolean checkMatUpnSet(String plantId) {
    List<T08ComOpD> list = dictDao.getT08ByHcode("UPN_NUM_01");
    if (null == list || 0 == list.size())
      return true;
    for (T08ComOpD t : list) {
      if (plantId.equals(t.getValue02())) {
        return false;
      }
    }
    return true;

  }

  /*
   * 采番设定检查
   */
  public boolean checkDocSet(String dCode) {
    List<T08ComOpD> list = dictDao.getT08ByHcode("DOCUMENT_NUM_01");
    if (null == list || 0 == list.size())
      return true;
    for (T08ComOpD t : list) {
      if (dCode.equals(t.getDCode()) && !StringTool.isNull(t.getValue01())) {
        return false;
      }
    }
    return true;

  }

}
