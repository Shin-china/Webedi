package customer.bean.sys;

import lombok.Data;

public class Sys005Mail {
    String TEMPLATE_ID;
    String MAIL_NAME;
    String MAIL_TITLE;
    String MAIL_CONTENT;
    String MAIL_CC;

    public String getMAIL_CC() {
        return MAIL_CC;
    }

    public void setMAIL_CC(String mAIL_CC) {
        MAIL_CC = mAIL_CC;
    }

    public String getTEMPLATE_ID() {
        return TEMPLATE_ID;
    }

    public void setTEMPLATE_ID(String tEMPLATE_ID) {
        TEMPLATE_ID = tEMPLATE_ID;
    }

    public String getMAIL_NAME() {
        return MAIL_NAME;
    }

    public void setMAIL_NAME(String mAIL_NAME) {
        MAIL_NAME = mAIL_NAME;
    }

    public String getMAIL_TITLE() {
        return MAIL_TITLE;
    }

    public void setMAIL_TITLE(String mAIL_TITLE) {
        MAIL_TITLE = mAIL_TITLE;
    }

    public String getMAIL_CONTENT() {
        return MAIL_CONTENT;
    }

    public void setMAIL_CONTENT(String mAIL_CONTENT) {
        MAIL_CONTENT = mAIL_CONTENT;
    }

}
