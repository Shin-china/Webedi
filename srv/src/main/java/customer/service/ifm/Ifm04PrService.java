package customer.service.ifm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import cds.gen.pch.T09Forcast;
import cds.gen.sys.T11IfManager;
import customer.bean.pch.Items;
import customer.bean.pch.SapPrRoot;
import customer.dao.pch.PurchaseDataDao;
import customer.dao.sys.IFSManageDao;
import customer.odata.S4OdataTools;

@Component
public class Ifm04PrService {

    @Autowired
    private IFSManageDao ifsManageDao;

    @Autowired
    private PurchaseDataDao PchDao;

    public String syncPr() {

        try {

            // 获取 Web Service 配置信息
            T11IfManager webServiceConfig = ifsManageDao.getByCode("IFM65");

            if (webServiceConfig != null) {

                String response = S4OdataTools.get(webServiceConfig, 1000, null, null);

                SapPrRoot sapPrRoot = JSON.parseObject(response, SapPrRoot.class);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

                for (Items item : sapPrRoot.getItems()) {

                    // 处理单个 PR 数据
                    T09Forcast o = T09Forcast.create();
                    o.setPrNumber(item.getPrNumber());
                    o.setDNo(Integer.parseInt(item.getDNo())); // 假设 item.getDNo() 返回的是 String 类型
                    o.setPurGroup(item.getPurGroup());
                    o.setSupplier(item.getSupplier());
                    o.setMaterial(item.getMaterial());
                    o.setMaterialText(item.getMaterialText());
                    o.setDelivaryDays(Integer.parseInt(item.getDelivaryDays())); // 假设 item.getDelivaryDays() 返回的是
                                                                                 // String 类型
                    o.setArrangeStartDate(LocalDate.parse(item.getArrangeStartDate(), formatter)); // 假设
                                                                                                   // item.getArrangeStartDate()
                                                                                                   // // 为字符串
                    o.setArrangeEndDate(LocalDate.parse(item.getArrangeEndDate(), formatter)); // 假设
                                                                                               // item.getArrangeEndDate()
                                                                                               // 为字符串
                    o.setPlant(item.getPlant());
                    o.setArrangeQty(new BigDecimal(item.getArrangeQty())); // 假设 item.getArrangeQty() 返回的是 String 类型
                    o.setName1(item.getName1());
                    o.setMinDeliveryQty(new BigDecimal(item.getMinDeliveryQty())); // 假设 item.getMinDeliveryQty() 返回
                                                                                   // String 类型
                    o.setManufCode(item.getManufCode());
                    o.setPurGroupName(item.getPurGroupName());

                    PchDao.modify3(o);

                }

                return "success";
            } else {

                return "error";
            }

        } catch (Exception e) {

            return e.getMessage();

        }

    }

}
