package customer.service.ifm;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cds.gen.mst.T01SapMat;
import cds.gen.sys.T11IfManager;
import customer.bean.mst.Value;
import customer.bean.mst._ProductDescription;
import customer.bean.mst.SapMstRoot;
import customer.dao.mst.MaterialDataDao;
import customer.dao.sys.IFSManageDao;
import customer.odata.S4OdataTools;

import java.util.ArrayList;

@Component
public class Ifm02MstService {

    @Autowired
    private IFSManageDao ifsManageDao1;

    @Autowired
    private MaterialDataDao MSTDao;

    public String tt;

    public void syncMst() {

        T11IfManager interfaceConfig = ifsManageDao1.getByCode("IFM39");

        try {

            String response = S4OdataTools.get(interfaceConfig, null, null, null);
            SapMstRoot sapMstRoot = JSON.parseObject(response, SapMstRoot.class);

            for (Value value : sapMstRoot.getValue()) {

                T01SapMat o = T01SapMat.create();

                o.setMatId(value.getProduct());
                o.setCdBy(value.getCreatedByUser());
                o.setUpBy(value.getLastChangedByUser());
                o.setMatUnit(value.getBaseUnit());
                o.setMatType(value.getProductType());
                o.setMatGroup(value.getProductGroup());
                // o.setMatStatus(value.getCrossPlantStatus());
                // o.setMatName(value.getBaseUnit());
                // o.setManuCode(value.getBaseUnit());
                // o.setManuMaterial(value.getBaseUnit());
                MSTDao.modify(o);

            }

            // ArrayList<T01SapMatTexts> textList = new ArrayList<T01SapMatTexts>();
            // mat.setTexts(textList);
            // if (s4.get_ProductDescription() != null) { // 物料多语言
            // int index = 0;
            // for (_ProductDescription a : s4.get_ProductDescription()) {
            // T01SapMatTexts text = T01SapMatTexts.create();
            // text.setLocale(OdateValueTool.getLocaleCode(a.getLanguage()));
            // text.setMatId(matId);
            // text.setMatName(a.getProductDescription());
            // textList.add(text);

            // if (index == 0 || ConfigConstants.USER_LANG_CODE.equals(text.getLocale())) {
            // //任意给一个。或者直接给日文
            // mat.setMatName(a.getProductDescription());
            // }
            // index++;
            // }
            // }

        } catch (UnsupportedOperationException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

}
