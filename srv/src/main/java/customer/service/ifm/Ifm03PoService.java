package customer.service.ifm;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSON;
import cds.gen.pch.T01PoH;
import cds.gen.pch.T02PoD;
import cds.gen.pch.T03PoC;
import cds.gen.sys.T11IfManager;
import customer.bean.pch.Item;
import customer.bean.pch.SapPchRoot;
import customer.dao.pch.PurchaseDataDao;
import customer.dao.sys.IFSManageDao;
import customer.odata.S4OdataTools;

import java.math.RoundingMode;

@Component
public class Ifm03PoService {

    @Autowired
    private PurchaseDataDao PchDao;

    @Autowired
    private IFSManageDao ifsManageDao;

    public String syncPo() {

        try {
            // 获取 Web Service 配置信息
            T11IfManager webServiceConfig = ifsManageDao.getByCode("IFM41");

            if (webServiceConfig != null) {
                // 调用 Web Service 的 get 方法
                String response = S4OdataTools.get(webServiceConfig, 1000, null, null);

                System.out.println(response);

                SapPchRoot sapPchRoot = JSON.parseObject(response, SapPchRoot.class);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

                Boolean dele = false;

                for (Item Items : sapPchRoot.getItems()) {

                    T01PoH o = T01PoH.create();
                    o.setPoNo(Items.getPurchaseorder());
                    o.setPoBukrs(Items.getCompanycode());

                    try {

                        LocalDate poDate = LocalDate.parse(Items.getPurchaseorderdate(), formatter); // 转换字符串为 LocalDate
                        o.setPoDate(poDate);

                    } catch (DateTimeParseException e) {

                        System.out.println("日期格式无效: " + e.getMessage()); // 处理格式错误

                    }
                    // o.setSupplier(Items.getSupplier());
                    // 去除前导 0
                    o.setSupplier(Items.getSupplier().replaceFirst("^0+(?!$)", ""));

                    PchDao.modify(o);

                    T02PoD o2 = T02PoD.create();
                    o2.setPoNo(Items.getPurchaseorder());

                    int number = Integer.parseInt(Items.getPurchaseorderitem());
                    o2.setDNo(number);

                    o2.setMatId(Items.getMaterial());

                    o2.setPlantId(Items.getPlant());
                    o2.setPoDTxz01(Items.getPurchaseorderitemtext());
                    Items.getOrderquantity();

                    o2.setPoPurQty(new BigDecimal(Items.getOrderquantity()));
                    o2.setPoPurUnit(Items.getPurchaseorderquantityunit());

                    o2.setCurrency(Items.getDocumentcurrency());

                    try {

                        LocalDate deliveryDate = LocalDate.parse(Items.getSchedulelinedeliverydate(), formatter);
                        o2.setPoDDate(deliveryDate);

                    } catch (DateTimeParseException e) {
                        e.printStackTrace();

                    }

                    try {
                        // 将字符串转换为 BigDecimal
                        BigDecimal netpriceAmount = new BigDecimal(Items.getNetpriceamount());
                        BigDecimal netPriceQuantity = new BigDecimal(Items.getNetpricequantity());

                        // 检查 netPriceQuantity 是否为 0，以避免除以 0 的情况
                        if (netPriceQuantity.compareTo(BigDecimal.ZERO) != 0) {
                            // 计算并设置价格，使用指定为具有两位小数的舍入模式
                            BigDecimal delPrice = netpriceAmount.divide(netPriceQuantity, 2, RoundingMode.HALF_UP);
                            o2.setDelPrice(delPrice);
                        } else {
                            // 处理除以 0 的情况，例如设置为 0 或抛出异常
                            o2.setDelPrice(BigDecimal.ZERO); // 或其他逻辑
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace(); // 处理转换错误
                    } catch (Exception e) {
                        e.printStackTrace(); // 处理其他可能的异常
                    }

                    o2.setDelAmount(new BigDecimal(Items.getNetamount()));
                    o2.setUnitPrice(new BigDecimal(Items.getNetpricequantity()));
                    o2.setStorageLoc(Items.getStoragelocation());
                    o2.setStorageTxt(Items.getStoragelocationname());
                    o2.setMemo(Items.getPlainlongtext());

                    o2.setSupplierMat(Items.getSupplierMaterialNumber());

                    if (Items.getPurchasingdocumentdeletioncode() == "X") {

                        dele = true;
                    }

                    PchDao.modify2(o2, dele);

                    // T03PoC o3 = T03PoC.create();
                    // o3.setPoNo(Items.getPurchaseorder());
                    // o3.setDNo(number);

                }

            } else {

                return "同步失败";

            }
        } catch (Exception e) {

            e.printStackTrace();

            return e.getMessage();

        }

        return "同步成功";

    }

}
