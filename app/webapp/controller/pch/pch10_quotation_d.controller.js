sap.ui.define(["umc/app/Controller/BaseController", "sap/m/MessageToast"], function (Controller, MessageToast) {
  "use strict";

  var _objectCommData = {
		_entity: "/PCH10_LIST", //此本页面操作的对象//绑定的数据源视图
		_aFilters: undefined

	};
  var myMap = new Map();
  
  // let _objectCommData = {
  //   _entity: "/PCH10_LIST", //此本页面操作的对象//绑定的数据源视图
  //   _entity_d: "PCH10_LIST",
  //   _items: "PCH10_LIST", // 展开数据"TO_ITEMS,TO_XXX,"
  //   // _entityDarftEdit: "/INV_T09_MOVE_draftEdit", //草稿对象 编辑
  //   // _entityDarft: "/INV_T09_MOVE_draftActivate", //激活
  //   // _entityDetailDarft: "/TO_ITEMS", //草稿对象明细
  //   // _darftTables: "tableservice_inv_T09_MOVE_drafts" + "," + "tableservice_inv_t08_re_d_drafts",
  // };

  return Controller.extend("umc.app.controller.pch.pch10_quotation_d", {
    onInit: function () {
      //  设置版本号
      this._setOnInitNo("PCH10", "20241024");
      this.getRouter().getRoute("RouteView_pch10").attachPatternMatched(this._onRouteMatched, this)
      this._PchResourceBundle = this.getOwnerComponent().getModel("pch").getResourceBundle();
      
      this._localModel = new sap.ui.model.json.JSONModel();
      this._localModel.setData({
          "show": true,
          "save": false
      });
      this.getView().setModel(this._localModel, "localModel");

      
    },

    _onRouteMatched: function (oEvent) {

      var that = this;
      that._setBusy(true);
      
      let headID = oEvent.getParameter("arguments").headID;
      var aFilters = headID;
			_objectCommData._aFilters = aFilters;
			var view = this.getView();
			var jsonModel = view.getModel("workInfo");
			view.setModel(jsonModel, undefined);
			if (aFilters.length == 0) {
				that._setBusy(false);
				return;
			}
			this._readEntryByServiceAndEntity(_objectCommData._entity, aFilters, null).then((oData) => {

				if (!jsonModel) {
					jsonModel = new sap.ui.model.json.JSONModel();
					view.setModel(jsonModel, "workInfo");
        }
        
				jsonModel.setData(oData.results);
				//更新seq数据
				that._getSeq();
				that._setBusy(false);
        console.log(oData.results)
        });





      // //获取并设置权限数据
      // // this._setAuthByMenuAndUser("INV34");
      // let that = this;
      // let headID = oEvent.getParameter("arguments").headID;
      // console.log(headID);
      // this.getView().unbindElement();
      // this._headID = headID;
      // this._setEditable(false);
      // this._setEditableAuth(true);
      // this._setBusy(true);
      // this._setIsCreate(false);
      // // //初始化 msg
      // // this.MessageTools._clearMessage();
      // // this.MessageTools._initoMessageManager(this);
      // // // 编辑
      // this._onObjectMatchedCommon(oEvent, _objectCommData._entity, {}, _objectCommData._items, true, "smartTable");
      // // //this.byId("smartTable2").rebindTable();
      // // that.getModel().refresh(true);
    },

    onRebind: function (oEvent) {
      this._rowNoMap == null;
      let sorts = ["SALES_D_NO","QUO_ITEM"];
      let ascs = [false]; //true desc false asc
      //手动添加排序
      this._onListRebinSort(oEvent, sorts, ascs);
    },


    getDetail: function (oEvent) { 

        let that = this;
        let selectedData = this._getSelectedIndicesDatasByTable("detailTable");
        if (selectedData.length == 0) {
            MessageToast.show("選択されたデータがありません、データを選択してください。");
            return false;
        }
        let para = [];
      selectedData.forEach(item => {

          let key1 = item.QUO_NUMBER;
        
            para.push(key1);
        });
        this._onPress(oEvent,"RouteView_pch10_d", this.unique(para).join(","));
      
        // let params = { param: para };
        // that._callCdsAction("/PCH08_SHOW_DETAIL", params, that).then((oData) => {
        //     let json = JSON.parse(oData.PCH08_SHOW_DETAIL);
        //     console.log(json)
        //   });
    },

    onEdit: function () {
      this._localModel.setProperty("/show", false);
      this._localModel.setProperty("/save", true);
    },

    
    unique: function (arr) {
        return [...new Set(arr)];
      },  

    // onDelete: function () {
    //   this.MessageTools._clearMessage();
    //   let selectedIndices = this._getSelectedIndicesDatasByTable("detailTable");
    //   if (selectedIndices.length === 0) {
    //     sap.m.MessageBox.alert(this.MessageTools._getI18nText("MSG_ERR_NO_DATA_SELECTED", this.getView()));
    //     this._setBusy(false);
    //     return false;
    //   }
    //   let that = this;
    //   let idJosn = "";
    //   var sHeaderObject = this.getView().getBindingContext().getObject();
    //   _objectCommData._headId = sHeaderObject.ID;
    //   that._setBusy(true);
    //   that._saveChanges().then(() => {
    //     let selectedIndices = this._getSelectedIndicesDatasByTable("detailTable");
    //     selectedIndices.forEach((index) => {
    //       let receiveDetail = index;
    //       //已发行UPN
    //       idJosn = idJosn + "," + receiveDetail.ID;
    //     });
    //     let oPrams = { idStr: idJosn };
    //     //删除明细行
    //     if (selectedIndices.length > 0) {
    //       this._callCdsAction("/INV34_deleteItems", oPrams, this).then((oData) => {
    //         if (oData.INV34_deleteItems != "error") {
    //           this.byId("detailTable").clearSelection();

    //           that._bindViewDataByCreateKey(_objectCommData._headId, _objectCommData._entity, _objectCommData._items, false, "smartTable");
    //           this.getModel().refresh(true); //刷新数据
    //         }
    //       });
    //     }
    //   });
    //   that._setBusy(false);
    // },

    // //品目情報显示
    // onDetailMatRebind: function (oEvent) {
    //   let headID = this._headID;
    //   var oBindingParams = oEvent.getParameter("bindingParams");
    //   var aFilters = oBindingParams.filters;
    //   var Filter = new sap.ui.model.Filter("H_ID", "EQ", headID);
    //   aFilters.push(Filter);
    //   oBindingParams.filters = aFilters;
    // },

    // sendSapFunction: function (isPost) {
    //   this.MessageTools._clearMessage();
    //   var that = this;
    //   this._setBusy(true);
    //   var sHeaderObject = this.getView().getBindingContext().getObject();
    //   var headId = sHeaderObject.ID;
    //   this.onSendDetailCommon("detailTable", "INV34", "/INV34_GR_SEND", isPost).then((oData) => {
    //     //刷新数据
    //     if (oData.INV34_GR_SEND === "success") {
    //       //that._bindViewDataByCreateKey(headId, _objectCommData._entity, _objectCommData._itmes, true, "smdetailTable");
    //       that.getModel().refresh(true); //刷新数据
    //       this.byId("detailTable").clearSelection();
    //       //that.MessageTools._addMessage(that.MessageTools._getI18nText("MSG_SUCCESS_SEND", that.getView()), null, 3, that.getView());
    //     }
    //     that._setBusy(false);
    //   });
    // },
    // /**
    //  * 过账，取消过账
    //  * @param {} oEvent
    //  */
    // onSend: function (oEvent) {
    //   this.MessageTools._clearMessage();
    //   this.onSendDetailCommon("detailTable", "INV34", "/INV34_GR_SEND", true, "smartTable");
    // },
    // onCancel: function (oEvent) {
    //   this.MessageTools._clearMessage();
    //   this.onSendDetailCommon("detailTable", "INV34", "/INV34_GR_SEND", false, "smartTable");
    // },
  });
});
