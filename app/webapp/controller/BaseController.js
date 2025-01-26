sap.ui.define([
    "sap/ui/core/mvc/Controller",
    "sap/ui/core/routing/History",
    "sap/ui/core/UIComponent",
    "umc/app/model/formatter",
    "sap/ui/core/Fragment",
    "sap/ui/model/odata/v2/ODataModel",
    "umc/app/controller/com/MessageTools",
    "umc/app/controller/com/CommTools",
	"sap/ui/model/odata/type/Boolean",
    "sap/m/MessageToast",
    "umc/app/controller/com/CheckTools",
    "umc/app/controller/com/PrintTool",
    "sap/ui/model/Filter",
    "sap/ui/model/FilterOperator",

], function(
	Controller,
	History,
	UIComponent,
	formatter,
	Fragment,
	ODataModel,
	MessageTools,
	CommTools,
	Boolean,
	MessageToast, CheckTools, PrintTool,Filter,FilterOperator
) {
	"use strict";

	return Controller.extend("umc.app.controller.BaseController", {
        formatter: formatter,
        MessageTools: MessageTools,
        CommTools: CommTools,
        CheckTools: CheckTools,
        PrintTool: PrintTool,
        //获取model如果为空则设置一个mode
        getModel: function (sName) {
          var jsonModel = this.getView().getModel(sName);
          if (!jsonModel) {
            jsonModel = new sap.ui.model.json.JSONModel();
            this.getView().setModel(jsonModel, sName);
          }
          return jsonModel;
        },
        //设定model
        setModel: function (oModel, sName) {
          return this.getView().setModel(oModel, sName);
        },
  
        //设定全局参数locl
        setGlobProperty: function (_propertyKey, _propertyValue) {
          this.getModel("localModel").setProperty("/" + _propertyKey, _propertyValue);
        },
        //获取全局参数locl
        getGlobProperty: function (_propertyKey) {
          return this.getModel("localModel").getProperty("/" + _propertyKey);
        },
  
        //得到资源文件
        getResourceBundle: function (_i18n) {
          return this.getOwnerComponent().getModel(_i18n).getResourceBundle();
        },
        /**
         * Method for navigation to specific view
         * @public
         * @param {string} psTarget Parameter containing the string for the target navigation
         * @param {Object.<string, string>} pmParameters? Parameters for navigation
         * @param {boolean} pbReplace? Defines if the hash should be replaced (no browser history entry) or set (browser history entry)
         */
        navTo: function (psTarget, pmParameters, pbReplace) {
          this.getRouter().navTo(psTarget, pmParameters, pbReplace);
        },
        _setCreateAble:function(isCreateAble){
          this.setGlobProperty("createable",isCreateAble )
        },
        /**
         * 默认跳转到菜单页面
         */
        navToHome: function () {
          this.setGlobProperty("refrashTimes", "1");
          this.getRouter().navTo("RouteMainView");
        },
        //跳转到指定页面并重新加载页面
        /**
         * 调用此方法，需要在跳转到的页面controller中init追加 Route 监听 
         * 监听中执行如下方法
         *  if (this.getGlobProperty("refrashTimes") === "1") {
          this.setGlobProperty("refrashTimes", "0");
          window.location.reload();
        }
         * @param {*} _RouteId 
         */
        navToPageView: function (_RouteId) {
          this.setGlobProperty("refrashTimes", "1");
          this.getRouter().navTo(_RouteId);
        },
        getRouter: function () {
          return UIComponent.getRouterFor(this);
        },
  
        onNavBack: function () {
          const sPreviousHash = History.getInstance().getPreviousHash();
  
          if (sPreviousHash !== undefined) {
            window.history.back();
          } else {
            this.getRouter().navTo("appHome", {}, true /* no history*/);
          }
        },
        navToMySelf: function (psTarget, pmParameters, pbReplace) {
          alert("xxxx");
          //this.getRouter().push(psTarget, pmParameters, pbReplace);
          //window.open(psTarget, '_blank');
          this.getRouter().getTargets(psTarget).display();
        },
        /**
         * 获取manifest中配置的 service 除默认外
         * 参数：model name
         */
        _getOwnerModel: function (_mName) {
          //var om = this.getOwnerComponent().getModel(_mName);
          return this.getOwnerComponent().getModel(_mName);
        },
        /**自定义设置默认数据 */
        _setDefaultDataModel: function (_mName) {
          this.setModel(this._getOwnerModel(_mName));
        },

        _onPressNav:function(oEvent,_navTo,_infoId){
          this.getRouter().navTo(_navTo, { headID: _infoId });
      },
  
        /**
         * 初始化画面时设置 画面中的按钮权限
         * @param {* 当前菜单ID} _menuId
         */
        _setAuthByMenuAndUser: function (_menuId) {
          var that = this;
          let _entity = "/MENU_AUTH_LIST";
          let filters = [
            new sap.ui.model.Filter({
              path: "MENU_ID",
              value1: _menuId,
              operator: sap.ui.model.FilterOperator.EQ,
            }),
          ];
          this._readEntryByServiceAndEntity(_entity, filters).then((oData) => {
            oData.results.forEach(function (result) {
              that.setGlobProperty(result.ID, result.AUTH_FLAG);
            });
          });
        },
        /**
         * 初始化画面时设置 系统配置信息获取
         */
        setSysConFig: function () {
          var that = this;
          let _entity = "/SYS_T12_CONFIG";
          let filters = [];
          return new Promise(function (resolve, reject) {
            that._readEntryByServiceAndEntity(_entity, filters, null).then((oData) => {
              oData.results.forEach(function (result) {
                that.setGlobProperty(result.CON_CODE, result.CON_VALUE);
              });
              resolve(true);
            });
          });
        },
  
        /**
         * 使用草稿表enetiy作为一览画面数据时默认过滤掉草稿数据显示
         * @param {*} oEvent
         * @param {*}是否显示草稿数据 是 false,
         */
        _onListRebindDarft: function (oEvent, _isActiveEntity) {
          var oBindingParams = oEvent.getParameter("bindingParams");
          var aFilters = oBindingParams.filters;
          //一览画面 只显示非草稿数据
          var oDarftFilter = new sap.ui.model.Filter("IsActiveEntity", "EQ", _isActiveEntity);
          aFilters.push(oDarftFilter);
          oBindingParams.filters = aFilters;
        },
        /**
         *
         * @param {*} oEvent
         * @param {*排序} sorters
         * @param {*}正序、倒序
         */
        _onListRebinSort: function (oEvent, _sorters, _ascs) {
          var oBindingParams = oEvent.getParameter("bindingParams");
          var aSorters = oBindingParams.sorter;
          if (null != _sorters && _sorters.length > 0) {
            let sorter = null;
            for (let i = 0; i < _sorters.length; i++) {
              sorter = new sap.ui.model.Sorter(_sorters[i], _ascs[i]);
              aSorters.push(sorter);
            }
            oBindingParams.sorters = aSorters;
          }
        },
  
        _onListRebinFilter: function (oEvent, _filedId, _value) {
          var oBindingParams = oEvent.getParameter("bindingParams");
          var aFilters = oBindingParams.filters;
          var oDarftFilter = new sap.ui.model.Filter(_filedId, "EQ", _value);
          aFilters.push(oDarftFilter);
          oBindingParams.filters = aFilters;
        },
        /**
         *
         * @param {*} oEvent
         * @param {*} _sorters
         * @param {*} _ascs
         */
        _onDetailRebind: function (oEvent, _sorters, _ascs) {
          var oBindingParams = oEvent.getParameter("bindingParams");
  
          var aSorters = oBindingParams.sorter;
          if (null != _sorters && _sorters.length > 0) {
            let sorter = null;
            for (let i = 0; i < _sorters.length; i++) {
              sorter = new sap.ui.model.Sorter(_sorters[i], _ascs[i]);
              aSorters.push(sorter);
            }
  
            oBindingParams.sorters = aSorters;
          }
        },
  
        /**一览画面自定义函数 */
        _setOnInitNo: function (oEvent, _initno) {
          console.log("%c version =======" + oEvent + _initno);
          console.log("color: #ffffff; font-style: italic; background-color: #20B2AA;padding: 2px 4px");
        },
        /** 设置 smattable 表格 checkbox有无，默认有 */
        _setSelectionNone: function (detailTableId) {
          const oTable = this.byId(detailTableId);
          oTable.setSelectionMode("None");
        },
  
        /**
         * 非草稿模式处理选中数据
         * @param {} _tableId
         */
        _getSelectedIndicesDatasByTable: function (_tableId) {
          let that = this;
          let detailTable = this.byId(_tableId);
          let selectedIndices = detailTable.getSelectedIndices(); // 获取选中行
          let idArr = new Array();
          if (selectedIndices.length === 0) {
            return idArr;
          }
          let i = 0;
          selectedIndices.forEach((index) => {
            let checkContext = detailTable.getContextByIndex(index);
            if(checkContext){
              if (null != checkContext) {
                let receiveDetail = checkContext.getObject();
                idArr[i] = receiveDetail;
                i++;
              }

            }
           
          });
  
          return idArr;
        },
        /**
         * 草稿模式下获取勾选的表格数据，
         * @param {表格ID}} _tableId
         * @returns
         */
        _getSelectedIndicesDatas: function (_tableId) {
          let that = this;
          let detailTable = this.byId(_tableId);
          let selectedIndices = detailTable.getSelectedIndices(); // 获取选中行
          let idArr = new Array();
          if (selectedIndices.length === 0) {
            sap.m.MessageBox.alert(this.MessageTools._getI18nText("MSG_ERR_NO_DATA_SELECTED", this.getView()));
            return;
          }
          let i = 0;
          selectedIndices.forEach((index) => {
            let deleteContext = detailTable.getContextByIndex(index);
            if (deleteContext != null) {
              let receiveDetail = deleteContext.getObject();
              let HasActiveEntity = receiveDetail.HasActiveEntity;
              //整理参数
              if (HasActiveEntity) {
                idArr[i] = receiveDetail;
                i++;
              } else {
                that.getModel().remove(deleteContext.getPath());
              }
            }
          });
  
          return idArr;
        },
        /**
         *  一栏画面跳转到明细页面
         * @param {*} oEvent
         * @param {*} _navTo  要跳转路径
         * @param {*} _infoId  参数
         */
        _onPress: function (oEvent, _navTo, _infoId) {
          this.getRouter().navTo(_navTo, { headID: _infoId });
        },
        /**
         * 草稿明细画面 新规、照会画面 通用
         * manifest rote命名 ： RouteView_菜单ID ，例如 RouteView_sys03
         * @param {*} oEvent
         * @param {*草稿对象} _entity
         * @param {*创建草稿时默认属性} _properties
         * @param {*草稿对象明细} _items
         * @param {*是否活动实体} _isActiveEntity
         * @param {*表格ID} _smartTableId
         */
        _onObjectMatchedCommon: function (oEvent, _entity, _properties, _items, _isActiveEntity, _smartTableId) {
          let roteName = oEvent.getParameter("name").split("_");
          let _headID = oEvent.getParameter("arguments").headID;
          //删除MGS
          this.MessageTools._clearMessage();
          let isActiveEntity = true;
          if ("RouteView" == roteName[0]) {
            //照会画面
            //设置页面不可编辑
            this._setEditable(false);
            //设置编辑权限
            this._setEditableAuth(true);
            this._setIsCreate(false);
            this._bindViewDataByCreateKey(_headID, _entity, _items, isActiveEntity, _smartTableId);
          } else if ("RouteCre" == roteName[0]) {
            //新规页面
            this.getModel().setDeferredGroups(["createEntry", "changes", "draftFunction"]);
            //设置页面可编辑
            this._setEditable(true);
            this._setEditableAuth(true);
            this._setIsCreate(true);
            this._editDarftByCreate(_entity, _properties, _items, _smartTableId);
          }
        },
        /**一览画面跳转明细 初始化数据 */
        /**
         *
         * @param {*} oEvent
         * @param {跳转路径} _path
         * @param {数据展开对象items} items
         * @param {草稿数据标记 } _isActiveEntity
         * @param {获取数据对应的表格ID} _smartTableId
         */
        _bindViewDataByCreateKey: function (_headID, _path, items, _isActiveEntity, _smartTableId) {
          //获取路由名称
          var that = this;
          var _parameters = { ID: _headID, IsActiveEntity: _isActiveEntity };
          that._bindViewData(this._getbindViewDataPath(_path, _parameters), items);
  
          that.byId(_smartTableId).rebindTable(); //参数统一共通smdetailTable
  
          that._setBusy(false);
        },
        //显示数据path
        /**
         * 生成path
         * @param {对象 } _entity
         * @param {*参数} _parameters
         * @returns
         */
        _getbindViewDataPath: function (_entity, _parameters) {
          let that = this;
          let sPath = that.getModel().createKey(_entity, _parameters);
          return sPath;
        },
        /**
         *
         * @param {*service路径} sPath
         * @param {*展开对象} items
         */
        _bindViewData(sPath, items) {
          var that = this;
          this.getView().bindElement({
            path: sPath,
            parameters: {
              expand: items,
            },
            events: {
              _datarecived: function () {
                that.setBusy(true);
              },
            },
          });
        },
        /**
         * 新规页面打开时新建草稿
         * @param {*草稿对象"/table"} _entity
         * @param {*新建草稿对象中的默认属性} _properties
         * * @param {*草稿明细表对象"TO_ITEMS"} _items
         */
        _editDarftByCreate: function (_entity, _properties, _items, _smartTableId) {
          let that = this;
          let isActiveEntity = true;
          let headID = "";
          var newHeaderContext = this.getModel().createEntry(_entity, {
            properties: _properties,
            groupId: "createEntry",
          });
          this.getView().setBindingContext(newHeaderContext);
  
          // 更新抬头draft
          this.getModel().submitChanges({
            groupId: "createEntry",
            success: function (oData) {
              var oHeaderContext = that.getView().getBindingContext();
              that._sPath = oHeaderContext.sPath;
              that.byId(_smartTableId).rebindTable();
            },
          });
        },
        //获取 表头的数据,考虑共用，key  entity 参数
        _readHead: function (headID, entity) {
          var that = this;
          return new Promise(function (resolve, reject) {
            that.getModel().read(entity, {
              filters: [
                new sap.ui.model.Filter({
                  path: "ID",
                  value1: headID,
                  operator: sap.ui.model.FilterOperator.EQ,
                }),
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
          //获取 表头的数据,考虑共用，key  entity 参数
          _readSys: function (headID, entity) {
            var that = this;
            return new Promise(function (resolve, reject) {
              that.getModel().read(entity, {
                filters: [
                  new sap.ui.model.Filter({
                    path: "D_CODE",
                    value1: headID,
                    operator: sap.ui.model.FilterOperator.EQ,
                  }),
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
        /**
         * 草稿类型的照会画面点击编辑按钮
         * @param {*草稿对象entity} _entityDraft
         * @param {*数据对象} _entity
         * @param {*展开数据} _items
         * @param {*参数统一共通headID} _headID
         * @param {*草稿标记false/true} _isActiveEntity
         * @param {*表格ID} _smartTableId
         */
        _editDetail: function (_entityDraft, _entity, _items, _headID, _isActiveEntity, _smartTableId) {
          var that = this;
  
          let isActiveEntity = true;
          let headID = "";
          this._setBusy(true);
          let _view = this.getView();
  
          //检查是否有草稿
          this.getModel()
            .metadataLoaded()
            .then(() => {
              this._readHead(_headID, _entity).then((oData) => {
                oData.results.forEach(function (result) {
                  if (result.IsActiveEntity === false) {
                    isActiveEntity = result.IsActiveEntity;
                    headID = result.ID;
                  }
                });
                //有草稿，显示
                if (!isActiveEntity) {
                  let _parameters = { ID: headID, IsActiveEntity: isActiveEntity };
                  this._bindViewData(this._getbindViewDataPath(_entity, _parameters), _items);
                  this.byId(_smartTableId).rebindTable();
                  that._setEditable(true);
                  that._setEditableAuth(true);
                  this._setBusy(false);
                } else {
                  //消除更改记录
  
                  this.getModel().resetChanges();
                  that
                    ._editDraft(_entityDraft, _headID, _isActiveEntity)
                    .then((oData) => {
                      let _parameters = { ID: oData.ID, IsActiveEntity: oData.IsActiveEntity };
                      that._bindViewData(this._getbindViewDataPath(_entity, _parameters), _items);
  
                      that.getModel().refresh(true);
  
                      that._setEditable(true);
                      that._setEditableAuth(true);
                      that._setBusy(false);
                      that.byId(_smartTableId).rebindTable();
                    })
                    .catch((oError) => {
                      this.MessageTools._clearMessage();
                      if (oError.statusCode === "409" && oError.statusText === "Conflict") {
                        //已有非自己的草稿数据
                        that.MessageTools._addMessage(that.MessageTools._getI18nText("MSG_EDIT_DARFT", that.getView()), null, 1, that.getView());
                      } else {
                        that.MessageTools._addMessage(that.MessageTools._getI18nText("MSG_ERR_EDIT_EXCEPTION", _view), null, 1, _view);
                      }
  
                      that._setEditable(false);
                    })
                    .finally(() => {
                      that._setBusy(false);
                    });
                }
              });
            });
        },
        /*==============================
          编辑生效版本并生成新版本草稿
          ==============================*/
        /**
         *
         * @param {*草稿表对应的草稿Table_draftEdit} _editDraftPath
         * @param {*头表ID} _headId
         * @param {*是否为草稿} _isActiveEntity
         * @returns
         */
        _editDraft: function (_editDraftPath, _headId, _isActiveEntity) {
          let that = this;
  
          return new Promise(function (resolve, reject) {
            that.getModel().callFunction(_editDraftPath, {
              method: "POST",
              urlParameters: {
                ID: _headId,
                IsActiveEntity: _isActiveEntity,
              },
              success: function (oData) {
                resolve(oData);
              },
              error: function (oError) {
                console.log("%c version =======" + oError.statusCode + oError.statusText);
                reject(oError);
              },
            });
          });
        },
        /**
         *
         * @param {*保存msg} msg
         * @param {*草稿对象"/table_draftActivate"} _activateDraftEntity
         * @param {*当前操作的表"/table"} _entity
         * @param {*明细 TO_ITEMS} _items
         * @param {*表格ID 刷新用} _smartTableId
         * @param {*} _that
         */
        _saveCommon: function (msg, _activateDraftEntity, _entity, _items, _smartTableId, _that) {
          //保存当前页面更改
          var that = this;
          this._setBusy(true);
          this.MessageTools._clearMessage(); //清除已有的msg
          this.getModel().setDeferredGroups(["createEntry", "changes", "draftFunction"]);
          this._saveChanges()
            .then((oData) => {
              // draft激活
              return that._activateDraft(_activateDraftEntity);
            })
            .then((result) => {
              // 获取新Key
  
              let _parameters = { ID: result.ID, IsActiveEntity: result.IsActiveEntity };
              that._bindViewData(this._getbindViewDataPath(_entity, _parameters), _items);
              that._setEditable(false);
              that.getModel().refresh(true);
              that.byId(_smartTableId).rebindTable();
              that._setBusy(false);
            })
            .catch((oError) => {
              if(msg)
              that.MessageTools._addMessage(that.MessageTools._getI18nText(msg, this.getView()), null, 1, this.getView());
              that._setBusy(false);
              that._setEditable(true);
            })
            .finally(() => {
              that._setBusy(false);
            });
        },
        /**
         * 保存更新数据
         */
        _saveChanges: function (groupID) {
          var that = this;
          return new Promise(function (resolve, reject) {
            that.getModel().submitChanges({
              groupId: groupID,
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
          激活草稿
          草稿数据删除，数据保存到正式表
          ==============================*/
        /**
         *
         * @param {*"/table_draftEdit"} _activateDraftEntity
         * @returns
         */
        _activateDraft: function (_activateDraftEntity) {
          var that = this;
          var sHeaderObject = this.getView().getBindingContext().getObject();
          return new Promise(function (resolve, reject) {
            that.getModel().callFunction(_activateDraftEntity, {
              method: "POST",
              urlParameters: {
                ID: sHeaderObject.ID,
                IsActiveEntity: false,
              },
              success: function (oData) {
                resolve(oData);
                //that.getModel().refresh(true);
              },
              error: function (oError) {
                that._setBusy(false);
                reject(oError);
              },
            });
          });
        },
        /**
         * 通用获取头绑定数据
         */
        _getHeadBingdingContext() {
          var oHeaderContext = this.getView().getBindingContext();
          return oHeaderContext.getObject();
        },
        /**
         * 通用根据ID获取所有选中数据
         */
        _getByIdObject(id) {
          // 获取明细权限数据
          var roleTableTable = this.byId(id);
          if (!roleTableTable) {
            return undefined;
          }
          var roleTableIndices = roleTableTable.getSelectedIndices(); // 获取选中行
          var dataList = new Array();
          //设值
          for (var j = 0; j < roleTableIndices.length; j++) {
            var deleteContext = roleTableTable.getContextByIndex(roleTableIndices[j]);
            if(deleteContext){
              var receiveDetail = deleteContext.getObject();
              dataList.push(receiveDetail);
            }
            
          }
          return dataList;
        },
  
        /**
         *
         * @param {*调用的service action} _callFunction
         * @param {*参数} _oPrams
         * @param {*} _that
         * @param {*} 未指定service 取默认
         * @param {*} 成功回调方法
         * @param {*} 失败回调方法
         * @returns
         */
        _callCdsAction: function (_callFunction, _oPrams, _that, entityInModelID, _callBackWhenSuccess, _callBackWhenFaild) {
          let that = _that;
          var oModel = entityInModelID == null || entityInModelID == "" ? that.getModel("TableService") : that.getModel(entityInModelID);
          return new Promise(function (resolve, reject) {
            oModel.callFunction(_callFunction, {
              method: "POST",
              urlParameters: _oPrams,
              success: function (oData) {
                resolve(oData);
                if (_callBackWhenSuccess) {
                  _callBackWhenSuccess(_that, oData);
                }
              },
              error: function (oError) {
                reject(oError);
                if (_callBackWhenFaild) {
                  _callBackWhenFaild(_that, oError);
                }
                that._setBusy(false);
              },
            });
          });
        },
        /**
         *
         * @param {*调用的service action} _callFunction
         * @param {*参数} _oPrams
         * @param {*} _that
         * @returns
         */
        _callCdsActionFromService: function (_callFunction, _oPrams, _that, _service, _callBackWhenSuccess, _callBackWhenFaild) {
          let that = _that;
  
          return new Promise(function (resolve, reject) {
            that.getModel(_service).callFunction(_callFunction, {
              method: "POST",
              urlParameters: _oPrams,
              success: function (oData) {
                resolve(oData);
                if (_callBackWhenSuccess) {
                  _callBackWhenSuccess(_that, oData);
                }
              },
              error: function (oError) {
                reject(oError);
                if (_callBackWhenFaild) {
                  _callBackWhenFaild(_that, oError);
                }
                that._setBusy(false);
              },
            });
          });
        },
  
        /*新规草稿数据
        },
        /*新规草稿数据
          * p1:草稿对象“/pch001
          * p2:操作的数据smart 表格ID
          * p3: 默认参数{}
          ==============================
          */
        _creNewDarft: function (_entity, _smartTableId, _properties, _that) {
          let that = _that;
          this.getModel().setDeferredGroups(["createEntry", "changes", "draftFunction"]);
          let newHeaderContext = that.getModel().createEntry(_entity, {
            properties: _properties,
            groupId: "createEntry",
          });
          that.getView().setBindingContext(newHeaderContext);
  
          // 更新抬头draft
          that.getModel().submitChanges({
            groupId: "createEntry",
            success: function (oData) {
              var oHeaderContext = that.getView().getBindingContext();
              that._sPath = oHeaderContext.sPath;
              that.byId(_smartTableId).rebindTable();
            },
            error: function (oError) {},
          });
        },
        /**
         *  //获取草稿表所有可用的草稿数据
         * @param {*"/table"} _entityDarft
         * @returns
         */
        _readDarft: function (_entityDarft) {
          var that = this;
          return new Promise(function (resolve, reject) {
            that.getModel().read(_entityDarft, {
              success: function (oData) {
                resolve(oData);
              },
              error: function (oError) {
                reject(oError);
              },
            });
          });
        },
        /**
         * 一览弹出框画面
         * @param {*} oEvent
         * @param {*当前视图} _view
         * @param {*调用的model} _entity
         * @param {*调用model 参数} _parameters
         */
        _onPopCreate: function (_view, _entity) {
          var oView = this.getView();
          var that = this;
          if (!this._pPopover) {
            this._pPopover = Fragment.load({
              id: oView.getId(),
              name: _view,
              controller: this,
            }).then(function (oPopover) {
              oView.addDependent(oPopover);
              if (null != _entity) {
                // 新建
                const sPath = that.getModel().createEntry(_entity, {});
  
                oPopover.unbindElement();
                oPopover.bindElement({
                  path: sPath.sPath,
                });
              }
  
              return oPopover;
            });
          }
          this._pPopover.then(
            function (oPopover) {
              oPopover.open();
            }.bind(this)
          );
        },
        /**
         * 一览弹出框画面
         * @param {*} oEvent
         * @param {*当前视图} _view
         * @param {*调用的model} _entity
         * @param {*调用model 参数} _parameters
         */
        _onPopPress: function (oEvent, _view, _entity, _parameters, _items) {
          var oView = this.getView();
          var that = this;
          if (!this._pPopover) {
            this._pPopover = Fragment.load({
              id: oView.getId(),
              name: _view,
              controller: this,
            }).then(function (oPopover) {
              oView.addDependent(oPopover);
              if (null != _entity) {
                let sPath = oView.getModel().createKey(_entity, _parameters);
                oPopover.unbindElement();
                oPopover.bindElement({
                  path: sPath,
                  parameters: {
                    expand: _items,
                  },
                });
              }
  
              return oPopover;
            });
          }
          this._pPopover.then(
            function (oPopover) {
              oPopover.open();
            }.bind(this)
          );
        },
  
        //==============
        //画面状态设置相关
        /*设置画面是否可编辑权限控制
          ==============================
          */
        _setEditableAuth: function (isEditable) {
          this.setGlobProperty("jurisdiction", isEditable);
        },
        /*==============================
        设置画面是否可编辑
        ==============================*/
        _setEditable: function (isEditable) {
          this.setGlobProperty("editable", isEditable);
        },
  
        /*==============================
        设置画面是否新规
        ==============================*/
        _setIsCreate: function (isCreate) {
          this.setGlobProperty("isCreate", isCreate);
        },
  
        //设置页面忙碌
        _setBusy: function (isBusy) {
          this.setGlobProperty("isBusy", isBusy);
        },
  
        //=============
        //画面取数据相关
  
        //获取单表的全部数据,考虑共用
        _readEntryData: function (entity) {
          var that = this;
          return new Promise(function (resolve, reject) {
            that.getModel().read(entity, {
              success: function (oData) {
                resolve(oData);
              },
              error: function (oError) {
                reject(oError);
              },
            });
          });
        },
        
        /**
         * 指定service 获取CDS 数据
         * @param {* 对应的view} _entity
         * @param {* where 参数 过滤器} _filters
         * @returns
         */
        _readEntryByServiceAndEntity: function (_entity, _filters, entityInModelID) {
          var that = this;
          var oModel = entityInModelID == null || entityInModelID == "" ? that.getModel("TableService") : that.getModel(entityInModelID);
          return new Promise(function (resolve, reject) {
            oModel.read(_entity, {
              filters: _filters,
              success: function (oData) {
                resolve(oData);
              },
              error: function (oError) {
                reject(oError);
              },
            });
          });
        },
          /**
         * 指定service 获取CDS 数据
         * @param {* 对应的view} _entity
         * @param {* where 参数 过滤器} _filters
         * @returns
         */
          _getDataInfo:  function (_that, selectedIndices, entityPath4Print, propertyInEntity4Filter, entityInModelID) {
            var that = _that;
            // get filter
            var filtersOr = [];
            for (var i = 0; i < selectedIndices.length; i++) {
              var j = selectedIndices[i];
              filtersOr.push(
                new Filter({
                  path: propertyInEntity4Filter,
                  operator: FilterOperator.EQ,
                  value1: j,
                })
              );
            }
            // Query Print Information
            return new Promise(function (resolve, reject) {
              var sPath = entityPath4Print;
              // filters
              var mParameter = {
                success: function (oData) {
                  resolve(oData);
                },
                error: function (oError) {
                  reject(oError);
                },
                filters: [
                  new Filter({
                    filters: filtersOr,
                    and: false,
                  }),
                ],
              };
              var oModel = entityInModelID == null || entityInModelID == "" ? that.getModel() : that.getModel(entityInModelID);
              oModel.read(sPath, mParameter);
            });
          },
        //通过id获取单表的全部非草稿数据,考虑共用
        _readEntryByIdData: function (entity, id) {
          var that = this;
          return new Promise(function (resolve, reject) {
            that.getModel().read(entity, {
              success: function (oData) {
                resolve(oData);
              },
              error: function (oError) {
                reject(oError);
              },
            });
          });
        },
        /**
         * MSG弹出框调用
         * @param {*} oEvent
         */
        onClickMessagePopover: function (oEvent) {
          this.MessageTools._onClickMessagePopover(oEvent, this.getView());
        },
        //table 绑定json model 表格的操作
        setInputValueState: function (oEvent, _type) {
          var oInput = oEvent.getSource();
          oInput.setValueState(_type);
        },
        /**
         * 清除平铺类型input 控件内容
         * @param {*} _allInputs
         */
        clearHeadPageInputData: function (_allInputs, _that) {
          _allInputs.forEach(function (oInput) {
            _that.byId(oInput).setValue("");
          });
        },
        /**
         * 清除表格内容
         * @param {*} _that
         */
        clearTable: function (_that) {
          let oModel = _that.getView().getModel();
          var datale = oModel.oData.dataList;
          oModel.oData.dataList.splice(0, datale.length);
          oModel.refresh(true);
          _that.getView().setModel(oModel);
        },
  
        /**
         * 根据单据明细ID 弹出 过账履历画面 共用
         * @param {*} oEvent
         */
        onPostLogPopPress: function (oEvent) {
          var oView = this.getView();
          var that = this;
          let popView = "umc.app.view.PostLogPopover";
          var oItem = oEvent.getSource();
          var oContext = oItem.getBindingContext();
          let id = oContext.getObject().ID;
          that.getModel("localModel").setProperty("/RId", id);
          let _parameters = { R_ID: id };
          let _items = "";
          if (!this._pPopover) {
            this._pPopover = Fragment.load({
              id: oView.getId(),
              name: popView,
              controller: this,
            }).then(function (oPopover) {
              oView.addDependent(oPopover);
  
             
  
              return oPopover;
            });
          }
          this._pPopover.then(
            function (oPopover) {
              oPopover.open();
            }.bind(this)
          );
        },
        /**
         * 转送履历显示画面的 显示过滤。
         * @param {*} oEvent
         */
        onPostDetailRebind: function (oEvent) {
          this._rowNoMapD = null;
          var did = this.getModel("localModel").oData.RId;
          this._onListRebinFilter(oEvent, "R_ID", did);
          this.MessageTools._clearMessage();
        },
  
        /**
         * 根据单据明细ID 弹出 过账履历画面 关闭 共用
         * @param {*} oEvent
         */
        onPopPostLogClose: function (oEvent) {
          var that = this;
          that._pPopover.then(function (oPopover) {
            oPopover.close();
            oPopover.destroy();
            oPopover = null;
          });
          that._pPopover = null;
        },
        /**
         * 过账通用方法
         * @param {*画面明细表格ID} _tableId
         * @param {*当前菜单功能ID*PCH02} _menuId
         * @param {*过账方法} _action
         * @param {*true 过账，false 取消过账} isPost
         * @returns
         */
        onSendDetailCommon: function (_tableId, _menuId, _action, isPost) {
          var that = this;
          this._setBusy(true);
          let selectedIndices = this._getSelectedIndicesDatasByTable(_tableId);
          if (selectedIndices.length === 0) {
            sap.m.MessageBox.alert(this.MessageTools._getI18nText("MSG_ERR_NO_DATA_SELECTED", this.getView()));
            this._setBusy(false);
            return false;
          }
  
          var _parameters = [];
          selectedIndices.forEach((index) => {
            let receiveDetail = index;
            var params = { id: receiveDetail.ID, isPost: isPost, menuId: _menuId, accountDate: receiveDetail.POST_ACCOUNT_DATE_FORMAT, cancelAccountDate: receiveDetail.CANCEL_ACCOUNT_DATE_FORMAT };
            _parameters.push(params);
          });
          var _parametersString = { parms: JSON.stringify(_parameters) };
  
          this._callCdsAction(_action, _parametersString, this).then((oData) => {
            let result = _action.replace("/", "");
            //刷新数据
            if (oData[result] === "success") {
              that.getModel().refresh(true); //刷新数据
  
              that.byId(_tableId).clearSelection();
            }
            that._setBusy(false);
          });
  
          return true;
        },
  
        /**
         * 日期控件 datepicker ,返回 format日期格式 添加到对应model
         * 返回名字要与当前 字段不一致。
         * 使用时 更换参数
         * 例如过账日： POST_ACCOUNT_DATE  画面 key
         * 转换后给出参数:POST_ACCOUNT_DATE_FORMAT
         * 调用时 model.POST_ACCOUNT_DATE_FORMAT
         * @param {*} oEvent
         * @param {*} modelKey
         */
        onPosthandleDateChangeCommon: function (oEvent, isPost) {
          var oDatePicker = oEvent.getSource();
          var oCtx = oDatePicker.getBindingContext();
          var sPath = oCtx.getPath();
          var oModel = this.getView().getModel();
          var sNewDate = oDatePicker.getFormFormattedValue(); // 获取选择的日期
  
          // 更新模型中的日期，过账取消过账使用
  
          if (isPost) {
            oModel.setProperty(sPath + "/POST_ACCOUNT_DATE_FORMAT", sNewDate);
            oModel.setProperty(sPath + "/CANCEL_ACCOUNT_DATE_FORMAT", "");
          } else {
            oModel.setProperty(sPath + "/POST_ACCOUNT_DATE_FORMAT", "");
            oModel.setProperty(sPath + "/CANCEL_ACCOUNT_DATE_FORMAT", sNewDate);
          }
        },
        /**
         * Table 中 日期控件的使用oDatePicker
         * @param {*} oEvent
         */
        onPosthandleDateChange: function (oEvent) {
          this.onPosthandleDateChangeCommon(oEvent, true);
        },
        onPostCelhandleDateChange: function (oEvent) {
          this.onPosthandleDateChangeCommon(oEvent, false);
        },
        /**
         * UPN 打印 全体
         * @param {*} _this
         * @param {*数据源表格 包含参数UPN_NO} _detailTable
         * @param {*} _smartTableId
         * @returns
         */
        onUpnPrintCom: function (_this, _detailTable, _smartTableId) {
          //Set Busy
          var that = _this;
          that._setBusy(true);
          var upnData = []; //传入key 参数 用与 获取打印数据、回写打印方法
          let selectedIndices = that._getSelectedIndicesDatasByTable(_detailTable);
          if (selectedIndices.length === 0) {
            sap.m.MessageBox.alert(this.MessageTools._getI18nText("MSG_ERR_NO_DATA_SELECTED", that.getView()));
            that._setBusy(false);
            return;
          }
          selectedIndices.forEach((index) => {
            let receiveDetail = index;
            upnData.push({UPN_NO:receiveDetail.UPN_NO,P_NUM:1});
          });
          //打印执行
  
          that.PrintTool.printUpnCommonByUpnDatas(that, upnData, _smartTableId).then((sta) => {
            if (sta) {
              that._setBusy(false);
            }
          });
        },
  
        /** 关于草稿头+明细相关共通操作
         * 标准的头+明细 草稿操作
         *
         */
        onHEditForDarft: function (_this, _darftTables, _entityDarftEdit, _entity, _itmes, _smartTableId) {
          //消除更改记录
          var that = _this;
          var sHeaderObject = that.getView().getBindingContext().getObject();
          let headId = sHeaderObject.ID;
          //进入前先清除草稿数据
          let oPrams = { parms: sHeaderObject.ID + "," + _darftTables }; // table_darft + info_Id
          let _isActiveEntity = sHeaderObject.IsActiveEntity;
          this._callCdsAction("/cancelDarft", oPrams, that).then((oData) => {
            if (oData.cancelDarft != "error") {
              // draft激活
              this._editDetail(_entityDarftEdit, _entity, _itmes, headId, _isActiveEntity, _smartTableId);
            }
          });
        },
        /**
         * smarttable 草稿模式追加明细行
         * p1:草稿对象entity "/PCH01"
         * p2:参数{属性：value}
         * p3:smarttable ID
         */
        _detailAddRowForDarft: function (_entity, _properties, _this, _smartTableId) {
          var that = _this;
          var oHeaderContext = that.getView().getBindingContext();
          that.getModel().createEntry(_entity, {
            properties: _properties,
            context: oHeaderContext,
            groupId: "createEntry",
          });
          that.getModel().submitChanges({
            groupId: "createEntry",
            success: function (oData) {
              that.byId(_smartTableId).rebindTable();
            },
            error: function (oError) {},
          });
        },
        /**
         *  画面草稿编辑状态关闭功能（删除草稿数据、恢复照会状态）
         * @param {*} _this
         * @param {*} _darftTables
         * @param {*} _entityDarftEdit
         * @param {*} _entity
         * @param {*} _itmes
         * @param {*} _smartTableId
         */
        onHCloseForDarft: function (_this, _darftTables, _entityDarftEdit, _entity, _itmes, _smartTableId) {
          //手动关闭 ，清除草稿数据，跳转到照会画面
          var that = _this;
          var sHeaderObject = that.getView().getBindingContext().getObject();
          var headId = sHeaderObject.ID;
          let oPrams = {
            parms: headId + "," + _darftTables, // table_darft + info_Id
          };
          this._callCdsAction("/cancelDarft", oPrams, that).then((oData) => {
            if (oData.cancelDarft != "error") {
              //设置页面不可编辑
              that._setEditable(false);
              //设置编辑权限
              //this._setEditableAuth(true);
              that._setIsCreate(false);
              that._bindViewDataByCreateKey(headId, _entity, _itmes, true, _smartTableId);
            }
          });
          that.MessageTools._clearMessage();
        },
        /**
         * 草稿表格明细删除处理
         * @param {*} _this
         * @param {*} _headId
         * @param {* 明细表格ID获取勾选数据} _detailTable
         * @param {* 删除function} _callFunction
         * @param {* 头表对象} _entity
         * @param {* 明细对象} _itmes
         * @param {* 智能表格ID，完成后刷新数据} _smartTableId
         */
        onDDelForDarft: function (_this, _headId, _detailTable, _callFunction, _entity, _itmes, _smartTableId) {
          /** 关于草稿头+明细相关共通操作 */
          let that = _this;
          var resultProperty = _callFunction.replace("/", "");
          let idJosn = [];
          var sHeaderObject = that.getView().getBindingContext().getObject();
          _headId = sHeaderObject.ID;
          that._setBusy(true);
          that._saveChanges().then(() => {
            let selectedIndices = this._getSelectedIndicesDatas(_detailTable);
            selectedIndices.forEach((index) => {
              let receiveDetail = index;
              idJosn.push(receiveDetail.ID);
            });
            let oPrams = { parms: idJosn };
            //删除明细行
            if (selectedIndices.length > 0) {
              this._callCdsAction(_callFunction, oPrams, that, null).then((oData) => {
                if (oData[resultProperty] == "success") {
                  that.byId(_detailTable).clearSelection();
                  that._bindViewDataByCreateKey(_headId, _entity, _itmes, false, _smartTableId);
                  that.getModel().refresh(true); //刷新数据
                }
              });
            }
          });
          that._setBusy(false);
        },
        /**
         * 照会画面头表headId 操作
         * @param {*当前页面} _that
         * @param {*调用的fun} _callFunction
         * @param {*页面对象} _entity
         * @param {*展开对象} _itmes
         * @param {*完成后刷新表格} _smdetailTable
         */
        headPostFunction: function (_that, _callFunction, _entity, _itmes, _smdetailTable) {
          var that = _that;
          var sHeaderObject = that.getView().getBindingContext().getObject();
          var headId = sHeaderObject.ID;
          let oPrams = { parms: headId }; //  info_Id
          var resultProperty = _callFunction.replace("/", "");
          that._setBusy(true);
          that.MessageTools._clearMessage();
          this._callCdsAction(_callFunction, oPrams, that).then((oData) => {
            if (oData[resultProperty] == "success") {
              //设置页面不可编辑
              that._setEditable(false);
              that._setIsCreate(false);
              that._bindViewDataByCreateKey(headId, _entity, _itmes, true, _smdetailTable);
              that.getModel().refresh();
              that._setBusy(false);
            }
          });
          that._setBusy(false);
        },
        /**
         *  账票照会画面，明细ID操作
         * @param {*} _that
         * @param {*} _callFunction
         * @param {*} _entity
         * @param {*} _itmes
         * @param {*} _smdetailTable
         * @returns
         */
        detalPostFunction: function (_that, _callFunction, _entity, _itmes, _detailTable, _smdetailTable) {
          var that = _that;
  
          let selectedIndices = thithats._getSelectedIndicesDatasByTable(_detailTable);
          if (selectedIndices.length === 0) {
            sap.m.MessageBox.alert(that.MessageTools._getI18nText("MSG_ERR_NO_DATA_SELECTED", that.getView()));
            that._setBusy(false);
            return;
          }
          selectedIndices.forEach((index) => {
            let receiveDetail = index;
            idJosn = idJosn + "," + receiveDetail.ID;
          });
          if (idJosn != "") {
            oPrams = { parms: idJosn };
          }
          var resultProperty = _callFunction.replace("/", "");
          that._setBusy(true);
          that.MessageTools._clearMessage();
          this._callCdsAction(_callFunction, oPrams, that).then((oData) => {
            /*for (var property in oData) {
              if (oData.hasOwnProperty(property)) {
                  if(oData[property]=="success"){
                  }
              }
            }*/
            if (oData[resultProperty] == "success") {
              //设置页面不可编辑
              that._setEditable(false);
              that._setIsCreate(false);
              that._bindViewDataByCreateKey(headId, _entity, _itmes, true, _smdetailTable);
              that.getModel().refresh();
              that._setBusy(false);
            }
          });
          that._setBusy(false);
        },
  
        /*
         * 下载时格式化
         * 创建时间，修改时间
         * @param {*} oEvent
         */
        onBeforeExportCommon: function (oEvent) {
          var mExcelSettings = oEvent.getParameter("exportSettings");
  
          for (var i = 0; i < mExcelSettings.workbook.columns.length; i++) {
            this.CommTools._setExcelFormatDateTime(mExcelSettings, i, "CD_TIME");
            this.CommTools._setExcelFormatDateTime(mExcelSettings, i, "UP_TIME");
          }
        },
      
           /**
         *获取选中表格的一个固定数据集
         * @param {*表格名字} tableNmae
         * @param {*数据明字} dataName
         */
         _TableDataList(tableNmae,dataName) {
          var that = this;
          var oTable = this.byId(tableNmae);
          var IdList = [];
          var b = dataName;
          var selectedIndices = oTable.getSelectedIndices();
          if (selectedIndices.length === 0) {
            sap.m.MessageBox.alert(that.MessageTools._getI18nText("LABEL_NO_DATA_SELECTED", that.getView()));
            return;
          }
          selectedIndices.forEach((selectedIndex) => {
            var a = dataName;
            var cContext = oTable.getContextByIndex(selectedIndex);
            if(cContext){
              var data = cContext.getObject();
              var ID =data[a];
              IdList.push(ID)
            }
            
          });
          return IdList;
        },
     
       /**
         *获取选中数据集
         * @param {*表格名字} tableNmae
         */
         _TableList(tableNmae) {
          var that = this;
          var oTable = this.byId(tableNmae);
          var dataList = [];
          var selectedIndices = oTable.getSelectedIndices();
          if (selectedIndices.length === 0) {
            sap.m.MessageBox.alert(that.MessageTools._getI18nText("LABEL_NO_DATA_SELECTED", that.getView()));
            return;
          }
          selectedIndices.forEach((selectedIndex) => {
            var cContext = oTable.getContextByIndex(selectedIndex);
            if(cContext){
              var data = cContext.getObject();
              
              dataList.push(data)
            }
            
          });
          return dataList;
        },

          /**
         *调用po接口
         * @param {*Odata}tableOdata
         */
         _invoPo(aSelectedData) {
          var that =this;
            var oParams = this._buildParams(aSelectedData);
            var par = {parms:JSON.stringify(oParams)};
            this._callCdsAction("/PCH02_CONFIRMATION_REQUEST", par, this).then(
              function (oData) {
                var str = oData.PCH02_CONFIRMATION_REQUEST;
                that._setBusy(false);
                sap.m.MessageToast.show(str);
              },
              function (error) {
                that._setBusy(false);
                sap.m.MessageToast.show(error);
              }
            );

          },

          /**发送邮件 */
          _sendEmail: function (mailobj) {
            var that = this;
            $.ajax({
              url: "srv/odata/v4/Common/sendEmail",
              type: "POST",
              contentType: "application/json; charset=utf-8",
              dataType: "json",
              async: false,
              crossDomain: true,
              responseType: 'blob',
              data: JSON.stringify(mailobj),
              success: function (data) {
                sap.m.MessageToast.show(that.MessageTools._getI18nTextInModel("com", "email_msg_s", that.getView()),{
                  duration: 3000
                })
              },
              error: function (error) {
                sap.m.MessageToast.show("error");
              },
            })

          },
          _readHeadEmail: function (a, b,entity) {
            var that = this;
            return new Promise(function (resolve, reject) {
              that.getModel().read(entity, {
                filters: [
                    
                  new sap.ui.model.Filter({
                    path: "H_CODE",
                    value1: a,
                    operator: sap.ui.model.FilterOperator.EQ,
                  }),
                  new sap.ui.model.Filter({
                    path: "BP_ID",
                    value1: b,
                    operator: sap.ui.model.FilterOperator.EQ,
                  }),
  
   
  
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
      

          /**
           * po接口辅助方法
           * @param {} aSelectedData 
           * @returns 
           */
          _buildParams: function (aSelectedData) {
            // 根据选中的数据构建参数
            return aSelectedData.map(function (oData) {
                // 格式化交货日期为 YYYY-MM-DD
              var oDate = new Date(oData.DELIVERY_DATE);
              var sFormattedDate = oDate.getFullYear() + '-' + 
                                  String(oDate.getMonth() + 1).padStart(2, '0') + '-' + 
                                  String(oDate.getDate()).padStart(2, '0');

                return {
                    PONO: oData.PO_NO,                                   // 采购订单号
                    DNO: String(oData.D_NO).padStart(5, '0'),            // 明细行号，转换为字符串并补足 5 位
                    SEQ: String(oData.SEQ).padStart(4, '0'),             // 序号，转换为字符串并补足 4 位
                    DELIVERYDATE: sFormattedDate,                        // 交货日期，格式为 YYYY-MM-DD
                    QUANTITY: String(oData.QUANTITY).padStart(13, '0'),  // 交货数量，转换为字符串并补足 13 位
                    DELFLAG: oData.DEL_FLAG,                             // 删除标识，确保为字符串
                    EXTNUMBER: oData.ExtNumber                           // 参照
                };
            });
        },
        _base64Blob:function(base64,mimeType){
          const byteCharacters = atob(base64);
          const byteArrays = [];
          
          for (let offset = 0; offset < byteCharacters.length; offset += 512) {
            const slice = byteCharacters.slice(offset, offset + 512);
            const byteNumbers = new Array(slice.length);
            
            for (let i = 0; i < slice.length; i++) {
              byteNumbers[i] = slice.charCodeAt(i);
            }
            
            const byteArray = new Uint8Array(byteNumbers);
            byteArrays.push(byteArray);
          }
          
          return new Blob(byteArrays, {type: mimeType}); 
      },
      _getDialogParm(selectedIndices){
        var pList = Array();
        selectedIndices.forEach((odata) => {
            var p = {
            po: odata.PO_NO,
            dNo: odata.D_NO,
          }
          pList.push(p);
        })
        return {parms: JSON.stringify(pList)};
        
      },
    _setDialog: function (boo, data) {
      var that = this;
      return new Promise(function (resolve, reject) {
        //返回发送过邮件的po号
        that._callCdsAction("/PCH03_LOGDATA", that._getDialogParm(data), that).then(

          function (odata) {
            //判断dialog是的提示消息
            var txt = that.MessageTools._getI18nTextInModel("com", "Dialog", that.getView())
            var list = [];

            var po = JSON.parse(odata.PCH03_LOGDATA);
            if (po != undefined) {
            if(po.length > 0){
              if ('1' == data[0].USER_TYPE) {
                
                po.forEach((item) => {
                  
                      list.push(item)
                  })
  
                  if (boo) {
                    if (list.length > 0) {
                      txt = that.MessageTools._getI18nMessage("Dialog2", list, that.getView())
                    }
                  }
                }
              }
            }




            sap.m.MessageBox.confirm(txt, {
              icon: sap.m.MessageBox.Icon.INFORMATION,
              title: "Confirmation",
              actions: [sap.m.MessageBox.Action.OK, sap.m.MessageBox.Action.CANCEL],
              emphasizedAction: sap.m.MessageBox.Action.OK,
              onClose: function (sAction) {
                if (sAction === sap.m.MessageBox.Action.OK) {
                  resolve();
                } else {
                  reject();
                  that._setBusy(false);
                }
              }
            })
          },
          function (error) {
            that._setBusy(false);

          }
        )
      })
    },
        //需要从明细取数据且弹提示框的所有前置
        _AfterDigLogCheck(boo) {
          var that = this;
          this._setBusy(true);
          return new Promise(function (resolve, reject) {
            var selectedIndices = that._TableList("detailTable");
            if (selectedIndices) {
              //设置通用dialog
              that._setDialog(boo,selectedIndices).then((oDialog) => {
                resolve(selectedIndices);
              });
            }else {
              reject();
              that._setBusy(false);
            }
          });
        },


           // 辅助函数：将 Excel 日期序列号转换为实际日期
			_convertExcelDate: function (serial,format) {
        // 辅助函数：将 Excel 日期序列号转换为实际日期（基于1899年12月31日作为基准日期）
  
          // 注意：这里使用1899年12月30日作为基准日期可能会导致不准确的结果
          // 如果你的Excel实际上使用的是1899年12月31日作为基准，应该使用下面的代码（但已经按照你的要求使用了31日）
          // 然而，由于历史原因，有时Excel的基准日期被视为1899年12月30日
          // 在这种情况下，你应该将下面的日期改为1899, 11, 29（但这里我们保持31日来匹配你的要求）
          var excelEpoch = new Date(Date.UTC(1899, 11, 31)); // 使用UTC来避免时区问题
          excelEpoch.setUTCHours(0, 0, 0, 0); // 确保时间是当天的开始
        
          // 由于Excel日期序列号是以天为单位的浮点数（可能包含时间部分），
          // 我们需要将序列号乘以一天的毫秒数（24小时 * 60分钟 * 60秒 * 1000毫秒）
          var convertedDate = new Date(excelEpoch.getTime() + (serial - 1) * 24 * 60 * 60 * 1000);
          // 注意：这里减去1是因为Excel的序列号通常从0开始（即1899年12月31日是序列号0，如果是30日则不需要减1）
          // 但由于你指定了31日作为基准，且希望45656对应正确的日期，我们需要根据实际情况调整
          // 如果你的Excel实际上是从1899年12月30日开始计数的，那么上面的代码中的excelEpoch应该是1899, 11, 29，并且不需要减1
        
          // 提取年、月、日并格式化为 YYYY/MM/DD
          var year = convertedDate.getUTCFullYear(); // 使用UTC方法来避免时区影响
          var month = String(convertedDate.getUTCMonth() + 1).padStart(2, '0');
          var day = String(convertedDate.getUTCDate()).padStart(2, '0');
        
                  // 根据格式要求拼接日期字符串
              if (format === 'YYYY-MM-DD') {
                return `${year}-${month}-${day}`;
              } else if (format === 'YYYY/MM/DD') {
                return `${year}/${month}/${day}`;
              } 
  
        },

    /**
     * 共通下载s3文件
     * @param {文件名称} name 
     */

    _onExportTemple(name){
      
			var that = this;
			var testJson={attachmentJson:[{
				object:"template",
				value:name
				}]}
			$.ajax({
				url: "srv/odata/v4/Common/s3DownloadAttachment",
				type: "POST",
				contentType: "application/json; charset=utf-8",
				dataType: "json",
				async: false,
				crossDomain: true,
				responseType: 'blob',
				data: JSON.stringify(testJson),
				success: function (base64) {
					const downloadLink = document.createElement("a");
					const blob = that._base64Blob(base64.value, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
					const blobUrl = URL.createObjectURL(blob);
					downloadLink.href = blobUrl;
					downloadLink.download = name;//data.FILE_NAME + "." + data.FILE_TYPE;
					downloadLink.click();
				}
			})
    },
      },
      


    )
});