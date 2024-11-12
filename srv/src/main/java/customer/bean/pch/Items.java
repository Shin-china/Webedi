package customer.bean.pch;

import lombok.Data;

@Data
public class Items {

    private String PrNumber; // 采购请求编号
    private String DNo; // 文档编号
    private String PurGroup; // 采购组
    private String Supplier; // 供应商
    private String Material; // 物料
    private String MaterialText; // 物料文本
    private String DelivaryDays; // 交货天数
    private String ArrangeStartDate; // 预约开始日期
    private String ArrangeEndDate; // 预约结束日期
    private String Plant; // 工厂
    private String ArrangeQty; // 预约数量
    private String Name1; // 名称1
    private String MinDeliveryQty; // 最小交货数量
    private String ManufCode; // 生产商代码
    private String PurGroupName; // 采购组名称
    private String Supplierphonenumber; // 供应商电话
    private String Suppliermaterialnumber; // 供应商物料号

}
