package customer.bean.sys;

import com.alibaba.fastjson.annotation.JSONField;

import customer.tool.StringTool;
import lombok.Data;

@Data
public class Sys07 {
    @JSONField(name = "SUCCESS")
    private Boolean SUCCESS = true;
    @JSONField(name = "MSG_TEXT")
    private String MSG_TEXT;
    @JSONField(name = "I_CON")
    private String I_CON;
    @JSONField(name = "STATUS")
    private String STATUS;

    public void setError(String _code) {
        SUCCESS = false;
        if (StringTool.isEmpty(MSG_TEXT)) {
            MSG_TEXT = _code;
        } else {
            MSG_TEXT = MSG_TEXT + "\n" + _code;
        }

    }

    @JSONField(name = "H_CODE")
    private String H_CODE;
    @JSONField(name = "H_NAME")
    private String H_NAME;
    @JSONField(name = "BP_ID")
    private String BP_ID;
    @JSONField(name = "EMAIL_ADDRESSY")
    private String EMAIL_ADDRESSY;
    @JSONField(name = "EMAIL_ADDRESS_NAME")
    private String EMAIL_ADDRESS_NAME;
    @JSONField(name = "PLANT_ID")
    private String PLANT_ID;
}