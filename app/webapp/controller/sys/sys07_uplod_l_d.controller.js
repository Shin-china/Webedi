sap.ui.define(["umc/app/controller/BaseController"], function (Controller) {
    "use strict";
    /**
     * 共通用对象
     */
    var _objectCommData = {
      _entity: "/T16_EMAIL_H", //此本页面操作的对象//绑定的数据源视图
      _itmes: "TO_ITEMS", // 展开数据"TO_ITEMS,TO_XXX,"
      _entityDarftEdit: "/T16_EMAIL_H_draftEdit", //草稿对象 编辑
      _entityDarft: "/T16_EMAIL_H_draftActivate", //激活D012MoveActH_draftActivate
      _entityDetailDarft: "/TO_ITEMS", //草稿对象明细
      _darftTables: "tableservice_t16_email_h_drafts" + "," + "tableservice_t17_email_d_drafts",
    };
    return Controller.extend("umc.app.controller.sys.sys07_uplod_l_d", {
      onInit: function () {
        const oRouter = this.getRouter();
        //显示页面
        this._setOnInitNo("SYS07", ".20240920.01");
        oRouter.getRoute("RouteView_sys07").attachPatternMatched(this._onObjectMatched, this);
        //新规
        oRouter.getRoute("RouteCre_sys07").attachPatternMatched(this._onObjectMatched, this);
      },
  
      _onObjectMatched: function (oEvent) {
        let _isActiveEntity = true;
        let parameters = { BE_CHANGE: true }; //默认属性
        //解除绑定
        this.getView().unbindElement();
        this._setAuthByMenuAndUser("SYS07");
        //初始化 msg
        this.MessageTools._clearMessage();
        this.MessageTools._initoMessageManager(this);
        this._onObjectMatchedCommon(oEvent, _objectCommData._entity, parameters, _objectCommData._itmes, _isActiveEntity, "smdetailTable");
        //获取并设置权限数据
      },
      /*++++++++++++++++++++++++++++++
          编辑数据
          ++++++++++++++++++++++++++++++*/
      onHEdit: function () {
        //消除更改记录
        var that = this;
        var sHeaderObject = this.getView().getBindingContext().getObject();
        let headId = sHeaderObject.ID;
        //进入前先清除草稿数据
  
        let oPrams = {
          parms: sHeaderObject.ID + "," + _objectCommData._darftTables, // table_darft + info_Id
        };
        let _isActiveEntity = sHeaderObject.IsActiveEntity;
  
        this._callCdsAction("/cancelDarft", oPrams, this).then((oData) => {
          if (oData.cancelDarft != "error") {
            // draft激活
            this._editDetail(_objectCommData._entityDarftEdit, _objectCommData._entity, _objectCommData._itmes, headId, _isActiveEntity, "smdetailTable");
          }
        });
      },
      /*++++++++++++++++++++++++++++++
          保存数据
          ++++++++++++++++++++++++++++++*/
      onHSave: function (oEvent) {
        let msg = "";
        this._saveCommon(msg, _objectCommData._entityDarft, _objectCommData._entity, _objectCommData._itmes, "smdetailTable", this);
      },
  
      onHClose: function () {
        //手动关闭 ，清除草稿数据，跳转到照会画面
        var that = this;
        this.MessageTools._clearMessage();
        this.onHCloseForDarft(this, _objectCommData._darftTables, _objectCommData._entityDarftEdit, _objectCommData._entity, _objectCommData._itmes, "smdetailTable");
      },
  
      /*++++++++++++++++++++++++++++++
          新增明细行草稿
          ++++++++++++++++++++++++++++++*/
      onDAdd: function (oEvent) {
        let _properties = {  }; //默认值参数  ORD_NO: ""  字段+value
        this._detailAddRowForDarft(_objectCommData._itmes, _properties, this, "smdetailTable");
      },
  
      /*++++++++++++++++++++++++++++++
          明细行删除
          ++++++++++++++++++++++++++++++*/
      onDDelete: function () {


        var that = this;
        var oTable = this.byId("detailTable");
        var selectedIndices = oTable.getSelectedIndices();
        if (selectedIndices.length === 0) {
          sap.m.MessageBox.alert(that.MessageTools._getI18nText("LABEL_NO_DATA_SELECTED", that.getView()));
          return;
        }

        selectedIndices.forEach((selectedIndex) => {
          var cContext = oTable.getContextByIndex(selectedIndex);
          if(cContext){
            
            that.getModel().remove(cContext.getPath());
          }
          
        });
        
      },
      /*==============================
    删除草稿(ReceiveHead + ReceiveDetail)
    ==============================*/
    _deleteDrafts: function () {
      //删除抬头草稿
      var sHeaderPath = this.getView().getBindingContext().getPath();
      this.getModel().remove(sHeaderPath);

      // 获取明细行草稿
      var detailContexts = this.byId("detailTable").getBinding().getContexts();
      for (var d = 0; d < detailContexts.length; d++) {
        //删除明细草稿
        this.getModel().remove(detailContexts[d].getPath());
      }
    },
      onDetailRebind: function (oEvent) {
        this._rowNoMapD = null;
        let sorts = ["EMAIL_ADDRESS_NAME"];
        let ascs = [false]; //true desc false asc
        this._onDetailRebind(oEvent, sorts, ascs);
      },
    });
  });
  