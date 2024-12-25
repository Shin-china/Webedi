jQuery.sap.require("umc/app/lib/xlsx");
sap.ui.define(
  ["umc/app/controller/BaseController", "sap/ui/model/odata/v2/ODataModel", "sap/ui/export/library", "sap/ui/model/json/JSONModel","sap/ui/export/Spreadsheet"],
  function (Controller, ODataModel, exportLibrary, JSONModel) {
    "use strict";
    return Controller.extend("umc.app.controller.sys.sys07_uplod_d", {
      /*++++++++++++++++++++++++++++++
		  初始化
		  ++++++++++++++++++++++++++++++*/
      onInit: function (oEvent) { 
        // Message Init
        //初始化 msg
        this.MessageTools._clearMessage();
        // this.getRouter().getRoute("RouteCre_SYS07").attachPatternMatched(this._onRouteMatched, this);
        //  设置版本号
        this._setOnInitNo("SYS07", ".20241225.01");
      },

      uploadButtonPress(oEvent) {
        this._viewCreateSet();
        // const model = this.getView().getModel("workInfo");
        // model.setData(oEvent.getParameter("rawData"));
      },
      /*==============================
		==============================*/
      _onRouteMatched: function (oEvent) {
        this._setAuthByMenuAndUser("SYS07");
        this._viewCreateSet();

      },

      /*++++++++++++++++++++++++++++++
		  检查当前页面数据
		  ++++++++++++++++++++++++++++++*/
      onCheck: function () {
        var flgHeand = true;
        var checkResult = true;

        var that = this;
        that.getView().setBusy(true);
        

        if (checkResult && flgHeand) {
          var jsonModel = that.getModel("workInfo");
          // 模板里面没有数据时的提示msg
          if (jsonModel.oData.length == undefined) {
            sap.m.MessageBox.alert(that.MessageTools._getI18nTextInModel("com", "SD01_Message_01", this.getView()));
            that.getView().setBusy(false);
            return;
          }
          this._callCdsAction("/SYS07_CHECK_DATA",this.getData(),this).then(
            function (oData) {
              // 取数据
              var arr = oData.SYS07_CHECK_DATA;
              var myArray = JSON.parse(arr);

              //设置画面上总结
              that._setCnt(myArray.reTxt);

              // 画面上的mode
              var jsonModel = that.getModel("workInfo");

              jsonModel.setData(myArray.list);

              if (!myArray.err) {
                that.byId("_IDGenButton2").setEnabled(false);
                that.byId("_IDGenButton3").setEnabled(true);
              }
              that.getView().setBusy(false);
            },
            function (error) {
              that.getView().setBusy(false);
            }
          )
        }
      },

      getData: function () {
        var jsondata = this.getModel("workInfo").getData();
        var a = JSON.stringify({ list: jsondata });
        var oPrams = {
          SYS07Json: a,
        };
        return oPrams;
      },
      /*++++++++++++++++++++++++++++++
		  保存棚帆
		  ++++++++++++++++++++++++++++++*/
      onSave: function () {
        var that = this;
        that.getView().setBusy(true);

        this._callCdsAction("/SYS07_SAVE_DATA", this.getData(), this).then((oData) => {
          var myArray = JSON.parse(oData.SYS07_SAVE_DATA);
          that._setCnt(myArray.reTxt);
          var jsonModel = that.getModel("workInfo");

          
          if (!myArray.err) {
            that.byId("_IDGenButton2").setEnabled(false);
            that.byId("_IDGenButton3").setEnabled(false);
          }
          jsonModel.setData(myArray.list);
          that.getView().setBusy(false);
        });
      },

      /*==============================
			设置画面显示数量
			==============================*/
      _setCnt: function (reTxt) {
        this.getModel("localModel").setProperty("/isReCnt", reTxt);
      },
      /**
       * 画面初始化调用
       */
      _viewCreateSet() {
        this.byId("_IDGenButton2").setEnabled(false);
        this.byId("_IDGenButton3").setEnabled(false);
        this.byId("_IDGenButton4").setEnabled(true);
        this._setEditable(true);
        this._setIsCreate(true);
        this._setEditableAuth(true);
        //清空mode
        var oTableModel = new JSONModel();
        this.getView().setModel(oTableModel, "workInfo");
        //设置画面上总结
        this._setCnt("");
        this.getModel().refresh(true);
      },
      
    });
  }
);
