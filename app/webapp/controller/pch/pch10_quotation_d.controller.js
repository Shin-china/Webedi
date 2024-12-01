sap.ui.define(["umc/app/Controller/BaseController", "sap/m/MessageToast"], function (Controller, MessageToast) {
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
      
      // this._localModel = new sap.ui.model.json.JSONModel();
      // this._localModel.setData({
      //     "show": true,
      //     "save": false
      // });
      // this.getView().setModel(this._localModel, "localModel");

      // var view = this.getView();
      // var jsonModel = view.getModel("workInfo");


      // // 设置为不可编辑
      // jsonModel.setData({
      //     "show": true,
      //     "save": false
      // });
      
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

            if (QUO_ITEM) {
                this._aFilters.push(new sap.ui.model.Filter({
                    path: "QUO_ITEM",
                    value1: QUO_ITEM,
                    operator: sap.ui.model.FilterOperator.EQ,
                }));
            }

            if (SALES_NUMBER) {
                this._aFilters.push(new sap.ui.model.Filter({
                    path: "SALES_NUMBER",
                    value1: SALES_NUMBER,
                    operator: sap.ui.model.FilterOperator.EQ,
                }));
            }

            if (QUO_VERSION) {
                this._aFilters.push(new sap.ui.model.Filter({
                    path: "QUO_VERSION",
                    value1: QUO_VERSION,
                    operator: sap.ui.model.FilterOperator.EQ,
                }));
            }

      var view = this.getView();
      var jsonModel = view.getModel("workInfo");

      return new Promise(function (resolve, reject) {

        that.getModel().read("/PCH10_LIST", {
        
          // filters: [

          //   new sap.ui.model.Filter({
            
          //     path: "QUO_NUMBER",
            
          //     value1: headID,
            
          //     operator: sap.ui.model.FilterOperator.EQ,
            
          //   }),

          // ]
          filters: that._aFilters
          ,
          success: function (oData) {
                              oData.results.sort(function(a, b) {
                      return a.SALES_D_NO - b.SALES_D_NO; // 升序排序
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
			// that._setBusy(true);
			var view = this.getView();
			//清除msg
			this.MessageTools._clearMessage();

			if (that._isCheckData()) {
				var jsonModel = view.getModel("workInfo");
				this._callCdsAction("/PCH10_SAVE_DATA", this._getData(), this).then((oData) => {


					var myArray = JSON.parse(oData.PCH10_SAVE_DATA);
					this._setEditable(false);
					if (myArray.err) {
						this._setEditable(true);
						var rt = myArray.reTxt.split("||");
						for (var i = 1; i < rt.length; i++) {

							that.MessageTools._addMessages(this.MessageTools._getI18nTextInModel("pch", rt[i], this.getView()), null, 1, this.getView());
						}

          } else {
            
            return new Promise(function (resolve, reject) {

              that.getModel().read("/PCH10_LIST", {
                
                filters: that._aFilters,
                success: function (oData) {

                  oData.results.sort(function(a, b) {
                      return a.SALES_D_NO - b.SALES_D_NO; // 升序排序
                      // 如果需要降序排序，可以使用 b.SALES_D_NO - a.SALES_D_NO;
                  });

                  jsonModel = new sap.ui.model.json.JSONModel();
                  view.setModel(jsonModel, "workInfo");
                  jsonModel.setData(oData.results);

                },
                error: function (oError) {
                  reject(oError);
                },
              })
            })
					}
					// that._setBusy(false);

				});
			} else {
				// that._setBusy(false);
			}
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
                  return item.SALES_D_NO || 0; // 如果 SALES_D_NO 是 undefined 或 null，使用 0
              })) + 1 ;
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
          copiedIndex.MATERIAL_NUMBER  = null;   
          copiedIndex.CUST_MATERIAL = null;
          copiedIndex.MANUFACT_MATERIAL = null;
          copiedIndex.Attachment = null;
          copiedIndex.Material = null;
          copiedIndex.MAKER = null;
					copiedIndex.CD_BY = null;
          copiedIndex.CD_TIME = null;

          copiedIndex.SALES_D_NO = MaxDN++;
          
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
    }
    
  });
});
