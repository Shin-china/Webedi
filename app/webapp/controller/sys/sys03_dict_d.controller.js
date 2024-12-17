sap.ui.define(["umc/app/controller/BaseController"], function (Controller) {
    "use strict";
    /**
     * 共通用对象
     */
    var _objectCommData = {
      _entity: "/SYS_T07_COM_OP_H", //此本页面操作的对象//绑定的数据源视图
      _itmes: "TO_ITEMS", // 展开数据"TO_ITEMS,TO_XXX,"
      _entityDarftEdit: "/SYS_T07_COM_OP_H_draftEdit", //草稿对象 编辑
      _entityDarft: "/SYS_T07_COM_OP_H_draftActivate", //激活
      _entityDetailDarft: "/TO_ITEMS", //草稿对象明细
      _darftTables: "tableservice_sys_t07_com_op_h_drafts" + "," + "tableservice_sys_t08_com_op_d_drafts",
    };
    return Controller.extend("umc.app.controller.sys.sys03_dict_d", {
      onInit: function () {
        const oRouter = this.getRouter();
        //显示页面
        this._setOnInitNo("SYS03", ".20240920.01");
        oRouter.getRoute("RouteView_sys03").attachPatternMatched(this._onObjectMatched, this);
        //新规
        oRouter.getRoute("RouteCre_sys03").attachPatternMatched(this._onObjectMatched, this);
      },
  
      _onObjectMatched: function (oEvent) {
        let _isActiveEntity = true;
        let parameters = { CAN_CHANGE: "Y" }; //默认属性
        //解除绑定
        this.getView().unbindElement();
        this._setAuthByMenuAndUser("SYS03");
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
        let _properties = { CD_TIME: new Date() }; //默认值参数  ORD_NO: ""  字段+value
        this._detailAddRowForDarft(_objectCommData._itmes, _properties, this, "smdetailTable");
      },
  
      /*++++++++++++++++++++++++++++++
          明细行删除
          ++++++++++++++++++++++++++++++*/
      onDDelete: function () {
        let that = this;
        let idJosn = "";
        this.MessageTools._clearMessage();
        let selectedIndices = this._getSelectedIndicesDatas("detailTable");
        selectedIndices.forEach((index) => {   let receiveDetail = index;
          idJosn = idJosn + "," + receiveDetail.ID;
        });
        if (idJosn != "") {
          let oPrams = { parms: idJosn };
  
          //删除明细行
          that._callCdsAction("/SYS03_DELETE_ITEMS", oPrams, that).then((oData) => {
            if (oData.SYS03_DELETE_ITEMS != "error") {
              //that.byId("detailTable").clearSelection();
              that.getModel().refresh();
            } else {
              //抛错误出来
            }
          });
        }
      },
  
      onDetailRebind: function (oEvent) {
        this._rowNoMapD = null;
        let sorts = ["CD_TIME", "D_NO"];
        let ascs = [false, false]; //true desc false asc
        this._onDetailRebind(oEvent, sorts, ascs);
      },
    });
  });
  