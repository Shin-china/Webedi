package customer.handlers.pch;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.services.handler.annotations.On;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.tableservice.PCH10GrSENDContext;
import cds.gen.tableservice.TableService_;

import customer.service.pch.Pch10Service;

@Component
@ServiceName(TableService_.CDS_NAME)

public class Pch10Handler implements EventHandler {

    @Autowired
    private Pch10Service pch10Service;

    @On(event = "PCH10_GR_SEND")
    public void Send(PCH10GrSENDContext context) throws IOException {

        JSONArray jsonArray = JSONArray.parseArray(context.getParams());

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String quo_no = jsonObject.getString("Quo_No");

            pch10Service.setQuostatus(quo_no);

            context.setResult("1");

        }
    }
}
