package customer.handlers.pch;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONObject;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.Header;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;

import org.apache.http.client.HttpClient;
import org.apache.logging.log4j.util.Base64Util;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cds.gen.sys.T11IfManager;
import cds.gen.tableservice.PCH02ConfirmationREQUESTContext;
import cds.gen.tableservice.TableService_;
import customer.bean.ifm.IFLog;
import customer.bean.sys.DeliveryInfo;
import customer.bean.sys.DeliveryInfoList;
import customer.dao.sys.IFSManageDao;
import customer.odata.S4OdataTools;
import customer.service.ifm.Ifm07PoPost;
import customer.service.pch.Pch06Service;
import customer.service.pch.PchService;
import customer.task.JobMonotor;
import customer.tool.Eenvironment;
import customer.tool.StringTool;

import com.sap.cds.reflect.CdsService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

@Component
@ServiceName(TableService_.CDS_NAME)
public class Pch02Handler implements EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(Pch02Handler.class);
    @Autowired
    private PchService pchService;
    @Autowired
    private IFSManageDao ifsManageDao;

    @Autowired
    private Ifm07PoPost ifm07PoPost;

    @On(event = PCH02ConfirmationREQUESTContext.CDS_NAME)
    public void onPCH02ConfirmationREQUEST(PCH02ConfirmationREQUESTContext context) {

        // jobMonotor.poolMonitor3();
        IFLog ifLog = new IFLog(IFSManageDao.IF_S4_PO_POST);
        String poPost = ifm07PoPost.poPost(ifLog,context.getParms());
        context.setResult(poPost);
    }






}
