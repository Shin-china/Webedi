sap.ui.define(["umc/app/controller/BaseController", "sap/m/MessageToast", "sap/m/BusyDialog",], function (Controller, MessageToast, BusyDialog) {
  "use strict";

  var _objectCommData = {

		_entity: "/PCH10_LIST", //此本页面操作的对象//绑定的数据源视图
		// _aFilters: []

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
      this._setDefaultDataModel("TableService");

      this.getRouter().getRoute("RouteView_pch10").attachPatternMatched(this._onRouteMatched, this)
      this._PchResourceBundle = this.getOwnerComponent().getModel("pch").getResourceBundle();

      this.MessageTools._clearMessage();
      this.MessageTools._initoMessageManager(this);

      this._BusyDialog = new sap.m.BusyDialog();
    },

    _onRouteMatched: function (oEvent) {

      var that = this;
      // that._setBusy(true);

      this._setEditable(false);

      let headID = oEvent.getParameter("arguments").headID;
      // 拆分 headID 字符串
      let [QUO_NUMBER, QUO_ITEM, SALES_NUMBER, QUO_VERSION] = headID.split("|");

      QUO_NUMBER = (QUO_NUMBER === "") ? null : QUO_NUMBER;
      QUO_ITEM = (QUO_ITEM === "") ? null : QUO_ITEM;
      SALES_NUMBER = (SALES_NUMBER === "") ? null : SALES_NUMBER;
      QUO_VERSION = (QUO_VERSION === "") ? null : QUO_VERSION;

      this._aFilters = [];
        if (QUO_NUMBER) {
            this._aFilters.push(new sap.ui.model.Filter({
                path: "QUO_NUMBER",
                value1: QUO_NUMBER,
                operator: sap.ui.model.FilterOperator.EQ,
            }));
        }
        
      var view = this.getView();
      var jsonModel = view.getModel("workInfo");

      return new Promise(function (resolve, reject) {

        that.getModel().read("/PCH10_LIST", {
        

          filters: that._aFilters
          ,
          success: function (oData) {
                              oData.results.sort(function(a, b) {
                      return a.QUO_ITEM - b.QUO_ITEM; // 升序排序
                      // 如果需要降序排序，可以使用 b.SALES_D_NO - a.SALES_D_NO;
                  });

            jsonModel = new sap.ui.model.json.JSONModel();
            view.setModel(jsonModel, "workInfo");
            jsonModel.setData(oData.results);

            resolve(oData);

          },
          error: function (oError) {
            reject(oError);
          },
        })
      })
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
            var selectedIndices = this._TableList("tableUploadData"); // 获取选中行
            if (selectedIndices.length == 0) {
                sap.m.MessageToast.show("選択されたデータがありません、データを選択してください。");
                return false;
            }
        let para = [];
      selectedIndices.forEach(item => {
                let key = item.QUO_NUMBER + '-' + item.QUO_ITEM;
                para.push(key);
      });
      
      this._onPress(oEvent, "RouteView_pch10_d", this.unique(para).join(","));
      
    },

    /*==============================
     编辑
     ==============================*/
    onEdit: function () {

			var that = this;
			that._setEditable(true);
      
    },

    /*==============================
    保存
    ==============================*/
      onSave: function (oEvent) {
         var that = this;
         that._setBusy(true);
          var oTable = this.byId("tableUploadData");
          var aSelectedIndices = oTable.getSelectedIndices();
          if (aSelectedIndices.length === 0) {
              sap.m.MessageToast.show("選択されたデータがありません、データを選択してください。"); // 提示未选择数据
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
              
              aPostItem.push(oItem);

              oPostList = JSON.stringify({
                  "list": aPostItem
              });

              oPostData = {
                  "json": oPostList
              };


              this._callCdsAction("/PCH10_SAVE_DATA", oPostData, this).then((oData) => {
                  
                  iDoCount++;

                  var oResult = JSON.parse(oData.PCH10_SAVE_DATA);
                  if (oResult.err) {   
                      bError = true;
                  }

                  if (iDoCount === aSelectedIndices.length) {
                      if (bError) {
                          that.MessageTools._addMessage(that.MessageTools._getI18nTextInModel("pch", oResult.reTxt, this.getView()), null, 1, this.getView());
                      } else {
                          that._setEditable(false);
                          sap.m.MessageToast.show(that._PchResourceBundle.getText("PCH10_SAVE_SUCCESS"));
                      }
                      that._setBusy(false);
                  }
              });
                
          });

        // var that = this;
        // // that._setBusy(true);
        // var view = this.getView();
        // //清除msg
        // this.MessageTools._clearMessage();
        // var oTable = this.byId("tableUploadData");


        // var aSelectedIndices = this._TableList("tableUploadData"); // 获取选中行

        //     if (aSelectedIndices.length == 0) {
        //         sap.m.MessageToast.show("選択されたデータがありません、データを選択してください。");
        //         return false;
        //     }

        //     //因为http param 长度有限制，必须循环一条条调用，不要一次性提交
        // var oPostData = {}, oPostList = {}, aPostItem = [], iDoCount = 0, bError;
              
        // aSelectedIndices.forEach(iIndex => {
             
        //     aPostItem = [];
        //     oPostData = {};
        //     oPostList = {};

        //     var oItem = oTable.getContextByIndex(iIndex).getObject();             
        //     aPostItem.push(oItem);

        //     oPostList = JSON.stringify({
        //         "list": aPostItem
        //     });

        //     oPostData = {
        //         "json": oPostList
        //     };
        
        //     }
        // );


        // if (that._isCheckData()) {
        //     var jsonModel = view.getModel("workInfo");
        //     this._callCdsAction("/PCH10_SAVE_DATA", oPostData, this).then((oData) => {


        //         var myArray = JSON.parse(oData.PCH10_SAVE_DATA);
        //         this._setEditable(false);
        //         if (myArray.err) {
        //             this._setEditable(true);
        //             var rt = myArray.reTxt.split("||");
        //             for (var i = 1; i < rt.length; i++) {

        //                 that.MessageTools._addMessages(this.MessageTools._getI18nTextInModel("pch", rt[i], this.getView()), null, 1, this.getView());
        //             }

        // } else {
        
        // return new Promise(function (resolve, reject) {

        //     that.getModel().read("/PCH10_LIST", {
            
        //     filters: that._aFilters,
        //     success: function (oData) {

        //         oData.results.sort(function(a, b) {
        //             return a.QUO_ITEM - b.QUO_ITEM; // 升序排序
        //             // 如果需要降序排序，可以使用 b.SALES_D_NO - a.SALES_D_NO;
        //         });

        //         jsonModel = new sap.ui.model.json.JSONModel();
        //         view.setModel(jsonModel, "workInfo");
        //         jsonModel.setData(oData.results);

        //     },
        //     error: function (oError) {
        //         reject(oError);
        //     },
        //     })
        // })
        //         }
        //         // that._setBusy(false);

        //     });
        // } else {
        //     // that._setBusy(false);
        // }
      },
      
      /*==============================
    change email status
    ==============================*/
      _sendEmailStatus: function (oEvent) {
         var that = this;
         that._setBusy(true);
          var oTable = this.byId("tableUploadData");

          var aSelectedIndices = oTable.getSelectedIndices();
            
          if (aSelectedIndices.length === 0) {
                
              sap.m.MessageToast.show("選択されたデータがありません、データを選択してください。"); // 提示未选择数据
                
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
              
              aPostItem.push(oItem);

              oPostList = JSON.stringify({
                  "list": aPostItem
              });

              oPostData = {
                  "str": oPostList
              };


              this._callCdsAction("/PCH10_D_SENDSTATUS", oPostData, this).then((oData) => {
                  
                  iDoCount++;

                  var oResult = JSON.parse(oData.PCH10_D_SENDSTATUS);
                  if (oResult.err) {   
                      bError = true;
                  }

                 
                      if (bError) {
                          that.MessageTools._addMessage(that.MessageTools._getI18nTextInModel("pch", oResult.reTxt, this.getView()), null, 1, this.getView());
                      } else {

                      }
                      that._setBusy(false);
                  
              });
                
          });

        // var that = this;
        // // that._setBusy(true);
        // var view = this.getView();
        // //清除msg
        // this.MessageTools._clearMessage();
        // var oTable = this.byId("tableUploadData");


        // var aSelectedIndices = this._TableList("tableUploadData"); // 获取选中行

        //     if (aSelectedIndices.length == 0) {
        //         sap.m.MessageToast.show("選択されたデータがありません、データを選択してください。");
        //         return false;
        //     }

        //     //因为http param 长度有限制，必须循环一条条调用，不要一次性提交
        // var oPostData = {}, oPostList = {}, aPostItem = [], iDoCount = 0, bError;
              
        // aSelectedIndices.forEach(iIndex => {
             
        //     aPostItem = [];
        //     oPostData = {};
        //     oPostList = {};

        //     var oItem = oTable.getContextByIndex(iIndex).getObject();             
        //     aPostItem.push(oItem);

        //     oPostList = JSON.stringify({
        //         "list": aPostItem
        //     });

        //     oPostData = {
        //         "json": oPostList
        //     };
        
        //     }
        // );


        // if (that._isCheckData()) {
        //     var jsonModel = view.getModel("workInfo");
        //     this._callCdsAction("/PCH10_SAVE_DATA", oPostData, this).then((oData) => {


        //         var myArray = JSON.parse(oData.PCH10_SAVE_DATA);
        //         this._setEditable(false);
        //         if (myArray.err) {
        //             this._setEditable(true);
        //             var rt = myArray.reTxt.split("||");
        //             for (var i = 1; i < rt.length; i++) {

        //                 that.MessageTools._addMessages(this.MessageTools._getI18nTextInModel("pch", rt[i], this.getView()), null, 1, this.getView());
        //             }

        // } else {
        
        // return new Promise(function (resolve, reject) {

        //     that.getModel().read("/PCH10_LIST", {
            
        //     filters: that._aFilters,
        //     success: function (oData) {

        //         oData.results.sort(function(a, b) {
        //             return a.QUO_ITEM - b.QUO_ITEM; // 升序排序
        //             // 如果需要降序排序，可以使用 b.SALES_D_NO - a.SALES_D_NO;
        //         });

        //         jsonModel = new sap.ui.model.json.JSONModel();
        //         view.setModel(jsonModel, "workInfo");
        //         jsonModel.setData(oData.results);

        //     },
        //     error: function (oError) {
        //         reject(oError);
        //     },
        //     })
        // })
        //         }
        //         // that._setBusy(false);

        //     });
        // } else {
        //     // that._setBusy(false);
        // }
    },

    /*==============================
    删除
    ==============================*/
    onBPDel: function (oEvent) {
        var that = this;
        var view = this.getView();
        var selectedIndices = this._TableList("tableUploadData"); // 获取选中行
        var jsonModel = view.getModel("workInfo");

        var datas = jsonModel.getData();
        if (selectedIndices) {
            selectedIndices.forEach((selectedIndex) => {
                //如果删除只能删除新追加的
        if (!selectedIndex.CD_BY) {
            var T02_ID = selectedIndex.T02_ID
            //根据id删除list
            datas = datas.filter(odata =>
            odata.T02_ID !== T02_ID
            )
            
        } else {
            that.MessageTools._addMessages(this.MessageTools._getI18nTextInModel("pch", "PCH_10_ERROR_MSG1", this.getView()), null, 1, this.getView());
        }
        });

            jsonModel.setData(datas);
        }
    },
        
      /*=  
      发信
      */
      onSend: function (oEvent) {
          var that = this;
          that._setBusy(true);

          var oTable = that.byId("tableUploadData");
        var selectedIndices = this._TableList("tableUploadData"); // 获取选中行
        if (selectedIndices.length == 0) {
            sap.m.MessageToast.show("選択されたデータがありません、データを選択してください。");
            return false;
          }
          
          var confirmMsg = "送信しますか？";
          sap.m.MessageBox.confirm(confirmMsg, {
        
              actions: ["YES", "NO"],
      
              emphasizedAction: "YES",
     
              onClose: function (sAction) {
        
                  if (sAction == "YES") {
                      var BP_NUMBERset = new Set(selectedIndices.map(item => item.BP_NUMBER));
        
                      if (BP_NUMBERset.size > 0) {
                          var H_CODE = "MM0002";
                          var BP_NUMBER = BP_NUMBERset;
                          var entity = "/SYS_T08_COM_OP_D";
                          var supplierName = "";
            
                          that._readHeademail(H_CODE, BP_NUMBER, entity).then((oHeadData) => {
                              if (oHeadData.results.length == 0) {
                                  that._setBusy(false);
                                  sap.m.MessageToast.show("BPに対してメールが設定されていません、送信できません。");
                                  return;
                                  
                          }
                              
                              // 多人邮件地址（，分隔隔开）
                              let mail = oHeadData.results && oHeadData.results.length > 0 ?
                                  oHeadData.results.map(result => result.VALUE02).join(", ") : '';
                              
                              //担当着姓名 替换邮件模板中{absama}
                              let absama = oHeadData.results && oHeadData.results.length > 0 ? 
                                  oHeadData.results.map(result => result.VALUE03 + " 様").join("  ") : '';
                              
                              // 邮件obj
                              let mailobj = {
                                  emailJson: {
                                      TEMPLATE_ID: "UWEB_M002",
                                      MAIL_TO: [mail].join(", "),
                                      MAIL_BODY: [
                                          { object: "absama", value: absama },
                                      ]

                                  }
                              };
                              
                              that._sendEmail(mailobj)
                              that._sendEmailStatus();
                              that._refreshTable();
                              that._setBusy(false);

                         
                          });
                          }
                              
                      else {
                          that._setBusy(false);
                      }
                  }
              }
              });
      },  

      onBP: function (oEvent) {
          var that = this;

          var oTable = this.byId("tableUploadData");
          var aSelectedIndices = this.byId("tableUploadData"); // 获取选中行

          if (aSelectedIndices.length === 0) {
                
              sap.m.MessageToast.show("選択されたデータがありません、データを選択してください。"); // 提示未选择数据
                
              return;

          }

          //因为http param 长度有限制，必须循环一条条调用，不要一次性提交
            var oPostData = {}, oPostList = {}, aPostItem = [], iDoCount = 0, bError;
              aSelectedIndices.forEach(iIndex => {
              aPostItem = [];
              oPostData = {};
              oPostList = {};

              var oItem = oTable.getContextByIndex(iIndex).getObject();

                  if (oItem.BP_NUMBER == null) {
                      sap.m.MessageToast.show("BP为空，无法调用接口"); // 提示未选择数据
                                  that._setBusy(false);
                  return;

                  }
                  
                  aPostItem.push(oItem);

              oPostData = JSON.stringify({
                  "str": oPostList
              });

              oPostData = {
                  "str": oPostList
                  };

                  this._callCdsAction("/PCH10_BPTQ", oPostData, this).then((oData) => {
                  
                  
                  iDoCount++;
                        var oResult = JSON.parse(oData.PCH10_GMTQ);

                        if (oResult.err) {
                            bError = true;
                        }

                        if (iDoCount === aSelectedIndices.length) {
                            if (bError) {
                                that.MessageTools._addMessage(that.MessageTools._getI18nTextInModel("pch", oResult.reTxt, this.getView()), null, 1, this.getView());
                            } else {

                                sap.m.MessageToast.show(that._PchResourceBundle.getText("PCH10_SAVE_SUCCESS"));
                            }
                            that._setBusy(false);
                  }
              });
                
          });

      },

      onGM: function (oEvent) {
          var that = this;
         that._setBusy(true);
          var oTable = this.byId("tableUploadData");

          var aSelectedIndices = oTable.getSelectedIndices();
            
          if (aSelectedIndices.length === 0) {
                
              sap.m.MessageToast.show("選択されたデータがありません、データを選択してください。"); // 提示未选择数据
                
              return;

          }
          //因为http param 长度有限制，必须循环一条条调用，不要一次性提交
            var oPostData = {}, oPostList = {}, aPostItem = [], iDoCount = 0, bError;
              aSelectedIndices.forEach(iIndex => {
              aPostItem = [];
              oPostData = {};
              oPostList = {};

              var oItem = oTable.getContextByIndex(iIndex).getObject();

                  if (oItem.MATERIAL_NUMBER == null) {
                      sap.m.MessageToast.show("物料为空，无法调用接口"); // 提示未选择数据
                                  that._setBusy(false);
                      return;
                  
              }

                  if (oItem.BP_NUMBER == null) {
                      sap.m.MessageToast.show("BP为空，无法调用接口"); // 提示未选择数据
                                  that._setBusy(false);
                  return;

              }
                  if (oItem.PLANT_ID == null) {   
                      sap.m.MessageToast.show("工厂为空，无法调用接口"); // 提示未选择数据
                                  that._setBusy(false);
                  return;

              }
              
              aPostItem.push(oItem);

              oPostList = JSON.stringify({
                  "list": aPostItem
              });

              oPostData = {
                  "str": oPostList
              };


              this._callCdsAction("/PCH10_GMTQ", oPostData, this).then((oData) => {
                  
                  
                  iDoCount++;
                        var oResult = JSON.parse(oData.PCH10_GMTQ);

                        if (oResult.err) {
                            bError = true;
                        }

                        if (iDoCount === aSelectedIndices.length) {
                            if (bError) {
                                that.MessageTools._addMessage(that.MessageTools._getI18nTextInModel("pch", oResult.reTxt, this.getView()), null, 1, this.getView());
                            } else {
                                sap.m.MessageToast.show(that._PchResourceBundle.getText("GMSUCCESS_MSG"));
                            }
                            that._setBusy(false);
                  }
              });
                
          });
          },

      getData: function () {
            
          var jsondata = this.getModel("tableUploadData").getData();
          
            
          var a = JSON.stringify({ list: jsondata });
            
            
          var oPrams = {
                
              shelfJson: a,
          
          };
          
          return oPrams;
          
        
      },

      
      _readHeademail: function (a,b, entity) {
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
              that.getModel().read(entity, {
                  filters: aFilters,
                  
                
              success: function (oData) {
                resolve(oData);
              },
              error: function (oError) {
                reject(oError);
              },
            });
          });
        },
    
    /*==============================
		复制
		==============================*/
		onBPAdd: function (oEvent) {
			var that = this;
            var view = this.getView();
            that._setEditable(true);
            var selectedIndices = this._TableList("tableUploadData"); // 获取选中行
            var jsonModel = view.getModel("workInfo");
            var datas = jsonModel.getData();
            var MaxDN = 0;  // 默认从 1 开始
         
            if (datas && datas.length > 0) {
             
                MaxDN = Math.max(...datas.map(function (item) {
                 
                    return item.QUO_ITEM || 0; // 如果 SALES_D_NO 是 undefined 或 null，使用 0
                }));

                MaxDN++;
                
            }
			if (selectedIndices) {
                selectedIndices.forEach((selectedIndex) => {
                    let copiedIndex = JSON.parse(JSON.stringify(selectedIndex));
          // copiedIndex.QUO_NUMBER = null;     
          // copiedIndex.QUO_ITEM = null;
          // copiedIndex.SALES_NUMBER = null;
          // copiedIndex.QUO_VERSION = null;
          // copiedIndex.NO  = null;            
          // copiedIndex.REFRENCE_NO    = null;   
        //   copiedIndex.MATERIAL_NUMBER  = null;   
        //   copiedIndex.CUST_MATERIAL = null;
        //   copiedIndex.MANUFACT_MATERIAL = null;
        //   copiedIndex.Attachment = null;
        //   copiedIndex.Material = null;
        //   copiedIndex.MAKER = null;
                copiedIndex.CD_BY = null;
                copiedIndex.CD_TIME = null;
                    copiedIndex.COPYBY = selectedIndex.QUO_ITEM;

                copiedIndex.QUO_ITEM = MaxDN++;
                
                const uuid = this.generateUUID();
                copiedIndex.T02_ID = uuid;
          
					datas.push(copiedIndex);
				});
				jsonModel.setData(datas);
				//更新seq数据
				// that._getSeq();
			}
		},

    unique: function (arr) {
        return [...new Set(arr)];
    },  
    
    // 生成UUID
    generateUUID() {
      return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
          const r = Math.random() * 16 | 0;
          const v = c === 'x' ? r : (r & 0x3 | 0x8);
          return v.toString(16);
      });
    },

    /**
		 * 判断保存前check,数据必输check
		 * @returns 
		 */
		_isCheckData: function () {
			var jsondata = this.getModel("workInfo").getData();
			var boo = true;
			if(jsondata.length){
				jsondata.forEach((odata) => {
					if (this.formatter._isNull(odata.QTY)) {
						this.MessageTools._addMessages(this.MessageTools._getI18nTextInModel("pch", "PCH_06_ERROR_MSG4", this.getView()), null, 1, this.getView());
						boo = false;
					}
					// if (this.formatter._isNull(odata.DELIVERY_DATE)) {
					// 	this.MessageTools._addMessages(this.MessageTools._getI18nTextInModel("pch", "PCH_06_ERROR_MSG5", this.getView()), null, 1, this.getView());
					// 	boo = false;
					// }
	
				})
			}else{boo =false;}
		
			return boo;
    },

    		/**
		 * 
		 * @returns 
		 */
		_getData: function () {
			var jsondata = this.getModel("workInfo").getData();
			var a = JSON.stringify({ list: jsondata });
			var oPrams = {
				json: a,
			};
			return oPrams;
      },
        


    fetchMaxSalesDNo() {
      return new Promise(function (resolve, reject) {
        var that = this;
        const model = that.getModel(); // 假设 `that` 是全局上下文
        const entitySet = "PCH10_LIST";
        const filterKey = "QUO_NUMBER";
        const filterValue = that.headID; // 假设 `headID` 是全局变量

        model.read(`/${entitySet}`, {
            filters: this._aFilters,
            success: function (oData) {
                if (oData && oData.results) {
                    // 找到 `sales_d_no` 最大值
                    const maxSalesDNo = oData.results.reduce((max, current) => {
                        return Math.max(max, parseInt(current.sales_d_no, 10) || 0);
                    }, 0); // 初始化为 0，防止空值

                    resolve(maxSalesDNo);
                } else {
                    reject(new Error("No data returned or results are empty."));
                }
            },
            error: function (oError) {
                reject(oError);
            },
        });
    });
    },

    onCloseAttachmentDialog: function () {
            this._closeDialog(this, 'idAttachmentDialog');
        },

        onFileChange: function (oEvent) {
            var oFile = oEvent.getParameter("files")[0];
            if (!oFile) {
                return;
            }

            var oAttachmentModel = this.getView().getModel("attachment");

            if (!oAttachmentModel) {
                return;
            }

            var sQuoNumber = oAttachmentModel.getProperty("/QUO_NUMBER");
            if (!sQuoNumber) {
                sap.m.MessageToast.show(this._PchResourceBundle.getText("QUO_NUMBER_IS_NULL"));
                return;
            }

            let sQuoItem = oAttachmentModel.getProperty("/QUO_ITEM");
            let sObject = sQuoNumber + '_' + sQuoItem;

            var oReader = new FileReader();
            oReader.readAsDataURL(oFile);
            this._BusyDialog.open();
            var that = this;
            oReader.onload = function (e) {
                let oFileData = e.target.result;
                let sContent = oFileData.substring(oFileData.indexOf("base64,") + 7);
                var oUploadData = {};
                oUploadData = {
                    "object": sObject,
                    "object_type": "PCH08",
                    "file_type": btoa(oFile.type),
                    "file_name": oFile.name,
                    "value": sContent
                };
                let oAttachmentObj = {
                    "attachmentJson": oUploadData
                };

                $.ajax({
                    url: "srv/odata/v4/Common/s3uploadAttachment",
                    type: "POST",
                    contentType: "application/json; charset=utf-8",
                    dataType: "json",
                    async: false,
                    crossDomain: true,
                    responseType: 'blob',
                    data: JSON.stringify(oAttachmentObj),
                    success: function (base64) {
                        that.getView().getModel().refresh();
                        console.log("上传成功");
                    }
                })


                this.byId("idFileUploader").clear();
                this._BusyDialog.close();
            }.bind(this);
        },

        onDestroyAttachmentDialog: function (oEvent) {
            this._destroyDialog(oEvent);
        },

        onOpenAttachmentDialog: function () {
            var oTable = this.byId("tableUploadData");

            if (!oTable) {
                return;
            }

            const aIndex = oTable.getSelectedIndices();

            if (aIndex.length !== 1) {
                sap.m.MessageToast.show(this._PchResourceBundle.getText("SELECT_ONE_RECORD"));
                return;
            }

            let oSelectData = oTable.getContextByIndex(aIndex[0]).getObject();

            var sQuoNumber = oSelectData.QUO_NUMBER;
            if (!sQuoNumber) {
                sap.m.MessageToast.show(this._PchResourceBundle.getText("QUO_NUMBER_IS_NULL"));
                return;
            }

            var oAttachmentModel = new sap.ui.model.json.JSONModel(oSelectData);

            this.getView().setModel(oAttachmentModel, "attachment");

            var oBindingContext = oTable.getContextByIndex(aIndex[0]);

            this.loadFragment({
                name: 'umc.app.view.pch.pch08_list_a'
            }).then(function (oDialog) {
                oDialog.setBindingContext(oBindingContext);
                oDialog.open();
            });
        },

        onDeleteAttachment: function (oEvent) {
            var oTable = this.byId("id.AttachmentTable");

            if (!oTable) {
                return;
            }

            var aIndex = oTable.getSelectedIndices();

            if (aIndex.length < 1) {
                sap.m.MessageToast.show(this._PchResourceBundle.getText("SELECT_AT_LEAST_ONE_RECORD"));
                return;
            }

            var that = this;
            var aKey = [];
            for (var i = 0; i < aIndex.length; i++) {
                var oBindingContext = oTable.getContextByIndex(aIndex[i]);
                var oData = oBindingContext.getObject();
                var sAttachmentId = oData.ID;
                aKey.push(sAttachmentId);
            }

            for (var i = 0; i < aKey.length; i++) {
                var oAttachmentObj = {
                    "key": aKey[i]
                };

                $.ajax({
                    url: "srv/odata/v4/Common/deleteS3Object",
                    type: "POST",
                    contentType: "application/json; charset=utf-8",
                    dataType: "json",
                    async: false,
                    crossDomain: true,
                    data: JSON.stringify(oAttachmentObj),
                    success: function (base64) {
                        that.getView().getModel().refresh();
                    }
                })

            }
        },

        _closeDialog: function (oContext, sDialogId) {
            var oDialog = oContext.getView().byId(sDialogId);
            oDialog.close();
            oDialog.destroy();
        },

        _destroyDialog: function (oEvent) {
            var oSource = oEvent.getSource();
            if (oSource) {
                oSource.destroy();
            }
        },

        onBeforeBindAttachment: function (oEvent) {
            var oParameters = oEvent.getParameter("bindingParams");
            var oAttachmentModel = this.getView().getModel("attachment");

            //Filter
            if (oAttachmentModel) {
                let sQuoNumber = oAttachmentModel.getProperty("/QUO_NUMBER");
                let sQuoItem = oAttachmentModel.getProperty("/QUO_ITEM");
                let sFilter = sQuoNumber + '_' + sQuoItem;
                if (sQuoNumber !== '') {
                    oParameters.filters.push(new sap.ui.model.Filter(
                        "OBJECT",
                        sap.ui.model.FilterOperator.EQ,
                        sFilter
                    ))
                }
            }

            oParameters.filters.push(new sap.ui.model.Filter(
                "OBJECT_TYPE",
                sap.ui.model.FilterOperator.EQ,
                "PCH08"
            ));

            oParameters.parameters.select = oParameters.parameters.select + ",FILE_TYPE,ID,OBJECT_LINK,OBJECT_TYPE";

        },

        onDownloadAttachment: function (oEvent) {
            var oTable = this.byId("id.AttachmentTable");
            var that = this;

            if (!oTable) {
                return;
            }


            var oBindingContext = oEvent.oSource.getBindingContext();
            var oAttachment = oBindingContext.getObject();
            var sId = oAttachment.ID;
            var sType = '';
            if (sId) {
                var downloadJson = {
                    attachmentJson: [{
                        object: "download",
                        value: oAttachment.OBJECT_LINK
                    }]
                }
                sType = oAttachment.FILE_TYPE;
            }


            $.ajax({
                url: "srv/odata/v4/Common/s3DownloadAttachment",
                type: "POST",
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                async: false,
                crossDomain: true,
                responseType: 'blob',
                data: JSON.stringify(downloadJson),
                success: function (base64) {
                    const downloadLink = document.createElement("a");
                    const blob = that._base64Blob(base64.value, sType);
                    const blobUrl = URL.createObjectURL(blob);
                    downloadLink.href = blobUrl;
                    downloadLink.download = oAttachment.FILE_NAME;
                    downloadLink.click();
                }
            })
        },

        onBeforeRebindList: function (oEvent) {

            var oModel = this.getView().getModel();
            if (oModel.hasPendingChanges()) {
                oModel.resetChanges();
            }

            var oParameters = oEvent.getParameter("bindingParams");
            var oComboStatus = this.byId("idStatusMultiComboBox");

            //Filter
            if (oComboStatus) {
                var aKeys = oComboStatus.getSelectedKeys();
                if (aKeys && aKeys.length > 0) {
                    aKeys.forEach(e => {
                        oParameters.filters.push(new sap.ui.model.Filter(
                            "STATUS",
                            sap.ui.model.FilterOperator.EQ,
                            e
                        ))
                    });

                }
            }

      },
        
      _refreshTable: function () {
          var that = this;
        var view = this.getView();
      var jsonModel = view.getModel("workInfo");

      return new Promise(function (resolve, reject) {

        that.getModel().read("/PCH10_LIST", {
        

          filters: that._aFilters
          ,
          success: function (oData) {
                              oData.results.sort(function(a, b) {
                      return a.QUO_ITEM - b.QUO_ITEM; // 升序排序
                      // 如果需要降序排序，可以使用 b.SALES_D_NO - a.SALES_D_NO;
                  });

            jsonModel = new sap.ui.model.json.JSONModel();
            view.setModel(jsonModel, "workInfo");
            jsonModel.setData(oData.results);

            resolve(oData);

          },
          error: function (oError) {
            reject(oError);
          },
        })
      })
          },

  });
});
