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
        // excel上传
        onFileChange: function (oEvent) {
          this._viewCreateSet();
          this._setEditable(true);
          var oFileUploader = this.byId("fileUploader");

          var that = this;
          var view = this.getView();
          var jsonModel = view.getModel("workInfo");
          if (!jsonModel) {
            jsonModel = new sap.ui.model.json.JSONModel();
            view.setModel(jsonModel, "workInfo");
          }
          var oFile = oFileUploader.FUEl.files[0];
          var oReader = new FileReader();
          oReader.onload = function (oFileData) {
            var sResult = oFileData.target.result;
            var oWB = XLSX.read(sResult, {
            type: "binary",
            });
            //获得 sheet
            var oSheet = oWB.Sheets[oWB.SheetNames[0]];
            //设置头
            var header = ["H_CODE","H_NAME","BP_ID","EMAIL_ADDRESSY","EMAIL_ADDRESS_NAME","PLANT_ID"];   //,"CUSTOMER_MAT" 存疑
            // 通过 XLSX 将sheet转为json  要转的oSheet，header标题，range起始行（1：第二行开始）
            var jsonS = XLSX.utils.sheet_to_json(oSheet,{header: header, range: 1});

            
            
            jsonModel.setData(jsonS);
          };
          oReader.readAsBinaryString(oFile);
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
          if (jsonModel.oData.length == undefined|| jsonModel.oData.length == 0) {
            sap.m.MessageBox.alert(that.MessageTools._getI18nTextInModel("com", "Message_01", this.getView()));
            that.getView().setBusy(false);
            return;
          }
          this._callCdsAction("/SYS07_CHECK_DATA",this.getData(),this).then(
            function (oData) {
              // 取数据
              var arr = oData.SYS07_CHECK_DATA;
              var myArray = JSON.parse(arr);

              //设置画面上总结
              // that._setCnt(myArray.reTxt);

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
          json: a,
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
        this.byId("_IDGenButton2").setEnabled(true);
        this.byId("_IDGenButton3").setEnabled(false);
        this.byId("_IDGenButton4").setEnabled(false);
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
      onSampleExport: function () {


  
  
        var downfilename = "メール送信マスタ.xlsx";
        var aCols = this.createSampleConfig();
        var json = [{
          H_CODE: "",
          H_NAME: "",
          BP_ID: "",
          EMAIL_ADDRESS: "",
          EMAIL_ADDRESS_NAME: "",
        }];
  
        var oModel = new sap.ui.model.json.JSONModel(json);
        var aProducts = oModel.getProperty("/");
        var oSettings = {
          workbook: { columns: aCols },
          dataSource: aProducts,
          fileName: downfilename
        };
        var oSheet = new sap.ui.export.Spreadsheet(oSettings);
        oSheet.build()
          .then(function () {
          }).finally(function () {
            oSheet.destroy();
          });
      },


      	//文件模板下载
		createSampleConfig: function () {
			var EdmType = exportLibrary.EdmType;
			var aCols = [];

			var H_CODE = "業務区分";
			var H_NAME = "業務名";
			var BP_ID = "仕入先";
			var EMAIL_ADDRESS = "メールアドレス";
			var EMAIL_ADDRESS_NAME ="担当者";
		


			aCols.push({
				label: H_NAME,
				property: H_NAME,
				type: EdmType.String
			});

			aCols.push({
				label: H_CODE,
				property: H_CODE,
				width: 20,
				type: EdmType.String
			});

			aCols.push({
				label: BP_ID,
				property: BP_ID,
				type: EdmType.String
			});

			aCols.push({
				label: EMAIL_ADDRESS,
				property: EMAIL_ADDRESS,
				type: EdmType.String
			});

			aCols.push({
				label: EMAIL_ADDRESS_NAME,
				property: EMAIL_ADDRESS_NAME,
				type: EdmType.String
			});

	


			return aCols;
		},
    
    });
  }
);
