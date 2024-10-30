package customer.service.ifm;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cds.gen.mst.T01SapMat;
import cds.gen.sys.T11IfManager;
import customer.bean.mst.SapMstRoot;
import customer.bean.mst.Value;
import customer.bean.pch.Items;
import customer.bean.pch.SapPchRoot;
import customer.dao.pch.PurchaseDataDao;
import customer.dao.sys.IFSManageDao;
import customer.odata.S4OdataTools;

@Component
public class Ifm03PoService {

    @Autowired
    private IFSManageDao ifsManageDao1;

    @Autowired
    private PurchaseDataDao PchDao;

    @Autowired
    private IFSManageDao ifsManageDao;

    public void syncPo() {

        try {
            // 获取 Web Service 配置信息
            T11IfManager webServiceConfig = ifsManageDao.getByCode("IFM41");

            if (webServiceConfig != null) {
                // 调用 Web Service 的 get 方法
                String response = S4OdataTools.get(webServiceConfig, 1000, null, null);
                System.out.println(response);
            } else {

            }
        } catch (UnsupportedOperationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
