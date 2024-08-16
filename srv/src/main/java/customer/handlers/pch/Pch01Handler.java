package customer.handlers.pch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.tableservice.PCH01CheckDATAContext;
import cds.gen.tableservice.PCH01SaveDATAContext;
import cds.gen.tableservice.TableService_;

import com.sap.cds.services.handler.EventHandler;

@Component
@ServiceName(TableService_.CDS_NAME)
public class Pch01Handler implements EventHandler {

    @Autowired
    ResourceBundleMessageSource rbms;
  
    // check数据
    @On(event = "PCH01_CHECK_DATA")
    public void checkData(PCH01CheckDATAContext context) {

    }

}
