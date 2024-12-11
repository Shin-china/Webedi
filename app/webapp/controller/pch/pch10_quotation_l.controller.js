sap.ui.define(["umc/app/Controller/BaseController", "sap/m/MessageToast"], function (Controller, MessageToast) {
  "use strict";
  return Controller.extend("umc.app.controller.pch.pch10_quotation_l", {

    onInit: function () {
      //  设置版本号
      this._setOnInitNo("PCH10", "20241029");
      //显示页面
      this.getRouter().getRoute("RouteList_pch10").attachPatternMatched(this._onObjectMatched, this);
      this._localModel = new sap.ui.model.json.JSONModel();
      this._localModel.setData({
        "show": true,
        "save": false
      });
      this.getView().setModel(this._localModel, "localModel");
      this._BusyDialog = new sap.m.BusyDialog();
      this._PchResourceBundle = this.getOwnerComponent().getModel("pch").getResourceBundle();
    },

    onRebind: function (oEvent) {

      var select = this.byId("sch_INITIAL_OBJ").getSelected();

      var mBindingParams = oEvent.getParameter("bindingParams");

      var newFilter;

      if (select === true) {
        newFilter = new sap.ui.model.Filter("INITIAL_OBJ", sap.ui.model.FilterOperator.EQ, "1");
      } else {
        newFilter = new sap.ui.model.Filter("INITIAL_OBJ", sap.ui.model.FilterOperator.EQ, "");
      }
      
      mBindingParams.filters.push(newFilter);
      this._rowNoMap == null;
      let sorts = ["QUO_NUMBER"];
      let ascs = [false]; //true desc false asc
      //手动添加排序
      this._onListRebinSort(oEvent, sorts, ascs);

    },

 //调用接口
 onLink: function (oEvent) {
  //获取数据
  this.getView().setBusy(true);
  var that = this;

  var selectedIndices = that._TableList("detailTable10"); // 获取选中行 
  if( selectedIndices != undefined){
    var txt = that.MessageTools._getI18nTextInModel("com", "Dialog", that.getView())
      
    sap.m.MessageBox.confirm(txt, {
      actions: ["YES", "NO"],
      emphasizedAction: "YES",
      onClose: function (sAction) {
        if (sAction == "YES") {
        // selectedIndices.forEach((data) => {
        //   if("B" !== data.STATUS_SALES) {
        //   this.getView().setBusy(true);
        //   sap.m.MessageBox.alert(that.MessageTools._getI18nText("SD022_UWEB_STATUS_ERROR",that.getView()));
        //   return;
        //   }
        // });
  
        //调用转寄接口

        that._invoQums(selectedIndices);
        
        }
      },
      });
  }
},
         /**
     *调用po接口
     * @param {*Odata}tableOdata
     */
     _invoQums(aSelectedData) {
      var that =this;
        var oParams = this._buildParams(aSelectedData);
        var par = {json:JSON.stringify(oParams)};
     
        $.ajax({
          url: "srv/odata/v4/Common/pch06BatchSending",
          type: "POST",
          contentType: "application/json; charset=utf-8",
          dataType: "json",
          async: false,
          crossDomain: true,
          responseType: 'blob',
          data: JSON.stringify(par),
          success: function (oData) {
            that.getView().setBusy(false);
            sap.m.MessageToast.show(oData.value, null, 1, that.getView())
            that.MessageTools._addMessages(oData.value, null, 1, that.getView());

          
          }
        })

      },

        /**
       * ->uqms接口辅助方法
       * @param {} aSelectedData 
       * @returns 
       */
                _buildParams: function (aSelectedData) {
                  // 根据选中的数据构建参数
                  return aSelectedData.map(function (oData) {
                      // 格式化交货日期为 YYYY-MM-DD
                      return {
                        QUO_NUMBER: oData.QUO_NUMBER,                                   // 采购订单号
                      };
                  });
              },
   
    onEdit: function () {
      this._localModel.setProperty("/show", false);
        
      this._localModel.setProperty("/save", true);
    },

    onSave: function (oEvent) {
      var that = this;
      that._setBusy(true);
      var oTable = this.byId("detailTable10");
      var aSelectedIndices = oTable.getSelectedIndices();

      if (aSelectedIndices.length === 0) {
        sap.m.MessageToast.show("選択されたデータがありません、データを選択してください。"); // 提示未选择数据
        oEvent.preventDefault(); // 取消导出操作
        that._setBusy(false);
        return;
      }

      //因为http param 长度有限制，必须循环一条条调用，不要一次性提交
      var oPostData = {}, oPostList = {}, aPostItem = [], iDoCount = 0, bError;
      aSelectedIndices.forEach(iIndex => {
        aPostItem = [];
        oPostData = {};
        oPostList = {};
        var oItem = oTable.getContextByIndex(iIndex).getObject();
        oItem.VALIDATE_START = that.__formatDate(oItem.VALIDATE_START);
        oItem.VALIDATE_END = that.__formatDate(oItem.VALIDATE_END);
        // oItem.TIME = that.__formatDate(oItem.TIME);
        aPostItem.push(oItem);
        oPostList = JSON.stringify({
          "list": aPostItem
        });
        oPostData = {
          "str": oPostList    
        };        
        that._callCdsAction("/PCH10_L_SAVE_DATA", oPostData, that).then(         
          (oData) => {
            iDoCount++;
            var oResult = JSON.parse(oData.PCH10_L_SAVE_DATA);

            if (oResult.err) {
              bError = true;
            }

            if (iDoCount === aSelectedIndices.length) {

              if (bError) {
                that._localModel.setProperty("/show", false);
                that._localModel.setProperty("/save", true);
                that.MessageTools._addMessage(that.MessageTools._getI18nTextInModel("pch", oResult.reTxt, this.getView()), null, 1, this.getView());
              } else {
                that._localModel.setProperty("/show", true);
                that._localModel.setProperty("/save", false);
                sap.m.MessageToast.show(that._PchResourceBundle.getText("SAVE_SUCCESS"));
              }

              that._setBusy(false);
            }
          });
      });
    },
    
    __formatDate: function (date) {
      if (!date) { 
        return '';
      }
      let newDate = new Date(date);
      return newDate.toISOString().split('T')[0];
    },

    onPress: function (oEvent) {
      let oItem = oEvent.getSource();
      let oContext = oItem.getBindingContext();
      let object = oContext.getObject();
      let QUO_NUMBER = object.QUO_NUMBER || "";  // 如果为空，使用 "EMPTY"
      let QUO_ITEM = object.QUO_ITEM || "";
      let SALESNO = object.SALES_NUMBER || "";
      let QUO_V = object.QUO_VERSION || "";

      let headID = QUO_NUMBER + "|" + QUO_ITEM + "|" + SALESNO + "|" + QUO_V;

      this._onPress(oEvent, "RouteView_pch10", headID);
    },

    onSend: function () {
      var that = this;
      that.getView().setBusy(true);
      
      var oTable = that.byId("detailTable10");

      var aSelectedIndices = oTable.getSelectedIndices();

      // 检查是否有选中的数据
      if (aSelectedIndices.length === 0) {
        MessageToast.show("選択されたデータがありません、データを選択してください。");
        that.getView().setBusy(false);
        return;
      }

      var aSelectedData = aSelectedIndices.map(function (iIndex) {
        return oTable.getContextByIndex(iIndex).getObject();
      })  

      var confirmMsg = "送信しますか？";

      sap.m.MessageBox.confirm(confirmMsg, {
        actions: ["YES", "NO"],
        emphasizedAction: "YES",
        onClose: function (sAction) {
          if (sAction == "YES") {
          // 遍历选中的行，提取所需数据
            var QUO_NUMBERSet = new Set(aSelectedData.map(data => data.QUO_NUMBER));
            var BP_NUMBERSet;

            if (QUO_NUMBERSet.size > 0) { 
              var QUO_NUMBER = Array.from(QUO_NUMBERSet);
              var entity = "/PCH_T07_QUOTATION_D";
              that._readBpNumber(QUO_NUMBER, entity).then((oData) => { 
              BP_NUMBERSet = new Set(oData.results.map(result => result.BP_NUMBER));

          
                if (BP_NUMBERSet.size > 0) { 
                  var H_CODE = "MM0002";
                  var BP_NUMBER = BP_NUMBERSet;
                  var entity = "/SYS_T08_COM_OP_D";
                  that._readHeademail(H_CODE, BP_NUMBER, entity).then((oHeadData) => {
              
              
                    let mail = oHeadData.results && oHeadData.results.length > 0 ? 
                      oHeadData.results.map(result => result.VALUE02).join(", ") : '';  
                    let absama = oHeadData.results && oHeadData.results.length > 0 ? 
                      oHeadData.results.map(result => result.VALUE03 + " 様").join("  ") : '';
                    let mailobj = {
                      emailJson: {
                        TEMPLATE_ID: "UWEB_M002",
                        MAIL_TO: [mail].join(", "), 
                        // MAIL_CC: "購買統括部支援G窓口 <zhao.wang@sh.shin-china.com>",
                        MAIL_BODY: [
                          { object: "vendor", value: supplierName },
                          { object: "absama", value: absama },
                        ]
                      }
                    };

                    that._sendEmail(mailobj);
                    
                    //因为http param 长度有限制，必须循环一条条调用，不要一次性提交
                    var oPostData = {}, oPostList = {}, aPostItem = [], iDoCount = 0, bError;
                    aSelectedIndices.forEach(iIndex => {
                      aPostItem = [];
                      oPostData = {};
                      oPostList = {};
                      var oItem = oTable.getContextByIndex(iIndex).getObject();
                      oItem.VALIDATE_START = that.__formatDate(oItem.VALIDATE_START);
                      oItem.VALIDATE_END = that.__formatDate(oItem.VALIDATE_END);
                      // oItem.TIME = that.__formatDate(oItem.TIME);
                      aPostItem.push(oItem);
                      oPostList = JSON.stringify({
                        "list": aPostItem
                      });
                      oPostData = {
                        "str": oPostList
                      };

                      that._callCdsAction("/PCH10_L_SENDSTATUS", oPostData, that).then(
                        (oData) => {
                          
                          var oResult = JSON.parse(oData.PCH10_L_SENDSTATUS);

                          if (oResult.err) {
                            bError = true;
                          }
                          if (bError) {
                              
                            that.MessageTools._addMessage(that.MessageTools._getI18nTextInModel("pch", oResult.reTxt, this.getView()), null, 1, this.getView());
                            that.getView().setBusy(false);
                          } else {

                            that.byId("smartTable10").rebindTable();

                            that.getView().setBusy(false);
                          }
                        });
                    });
                    
                   
                  });
                }
              });
            }
          
          } else {
            that.getView().setBusy(false);
            return;
          }
        }
      })
      
      // 假设您在这里定义邮件内容模板
      var supplierName = "";
      var year = "";
      var month = "";
      aSelectedData.map(function (data) {
        supplierName = data.NAME1 || "未指定"; // 默认值
        // 使用当前年月
        var currentDate = new Date();
        year = currentDate.getFullYear().toString(); // 当前年份
        month = (currentDate.getMonth() + 1).toString().padStart(2, '0'); // 当前月份，确保是两位数格式
      });
      
    },

    // _changeEmailStatus: function (aSelectedData,oTable) {
    //   var that = this;
    //   that._setBusy(true);
    //   var bSelectedIndices = aSelectedData;

    //   //因为http param 长度有限制，必须循环一条条调用，不要一次性提交
    //   var oPostData = {}, oPostList = {}, aPostItem = [], iDoCount = 0, bError;
    //   aSelectedIndices.forEach(iIndex => {
    //     aPostItem = [];
    //     oPostData = {};
    //     oPostList = {};
    //     var oItem = oTable.getContextByIndex(iIndex).getObject();
    //     oItem.VALIDATE_START = that.__formatDate(oItem.VALIDATE_START);
    //     oItem.VALIDATE_END = that.__formatDate(oItem.VALIDATE_END);
    //     // oItem.TIME = that.__formatDate(oItem.TIME);
    //     aPostItem.push(oItem);
    //     oPostList = JSON.stringify({
    //       "list": aPostItem
    //     });
    //     oPostData = {
    //       "str": oPostList    
    //     };

    //   that._callCdsAction("/PCH10_L_SENDSTATUS", oPostList, that).then(         
    //       (oData) => {
            
    //         var oResult = JSON.parse(oData.PCH10_L_SENDSTATUS);

    //         if (oResult.err) {
    //           bError = true;
    //         }
    //           if (bError) {
                
    //             that.MessageTools._addMessage(that.MessageTools._getI18nTextInModel("pch", oResult.reTxt, this.getView()), null, 1, this.getView());
    //           } else {
    //             sap.m.MessageToast.show(that._PchResourceBundle.getText("SAVE_SUCCESS"));
    //       }
          
    //       });
    //   },
    //   )
    //   },
        
        
        
        

    _readBpNumber(a, entity) {
      var that = this;
      return new Promise(function (resolve, reject) {
        that.getModel().read(entity, {
            filters: [
              
            new sap.ui.model.Filter({
              path: "QUO_NUMBER",
              value1: a,
              operator: sap.ui.model.FilterOperator.EQ,
            }),
            new sap.ui.model.Filter({
              path: "DEL_FLAG",
              value1: "N",
              operator: sap.ui.model.FilterOperator.EQ,
            })  
              ],
              success: function (oData) { 
                resolve(oData); 
              },
              error: function (oError) { 
                reject(oError); 
              },
        });
      });
      },



_readHeademail: function (a, b, entity) {
    var that = this;
    return new Promise(function (resolve, reject) {
        // 如果 b 是一个 Set，将其转换为数组
        var aValues = Array.isArray(b) ? b : Array.from(b);

        // 动态创建 VALUE01 的过滤器
        var aValueFilters = aValues.map(function (sValue) {
            return new sap.ui.model.Filter({
                path: "VALUE01",
                operator: sap.ui.model.FilterOperator.Contains,
                value1: sValue
            });
        });

        // 将多个 VALUE01 的过滤器组合为一个 OR 过滤器
        var oValueFilter = new sap.ui.model.Filter({
            filters: aValueFilters,
            and: false // 使用 OR 逻辑
        });

        // 组合其他过滤器
        var aFilters = [
            new sap.ui.model.Filter({
                path: "H_CODE",
                operator: sap.ui.model.FilterOperator.EQ,
                value1: a
            }),
            oValueFilter, // 动态生成的 OR 过滤器
            new sap.ui.model.Filter({
                path: "DEL_FLAG",
                operator: sap.ui.model.FilterOperator.NE,
                value1: "X"
            })
        ];

        // 执行读操作
        that.getModel().read(entity, {
            filters: aFilters,
            success: function (oData) {
                resolve(oData);
            },
            error: function (oError) {
                reject(oError);
            }
        });
    });
},

    onBeforeExport: function (oEvent) {
			var mExcelSettings = oEvent.getParameter("exportSettings");
      
      // 设置文件名
			var oDate = new Date();
			var sDate = oDate.toISOString().slice(0, 10).replace(/-/g, '');
			var sTime = oDate.toTimeString().slice(0, 8).replace(/:/g, '');
      mExcelSettings.fileName = `購買見積依頼管理_ ${sDate}${sTime}.xlsx`;
      
			mExcelSettings.workbook.columns.forEach(function (oColumn) {
				switch (oColumn.property) {

				default:
					break;
				}
			});
    },
  });
});
