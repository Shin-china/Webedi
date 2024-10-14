sap.ui.define(["umc/app/controller/BaseController"], function (Controller) {
    "use strict";
    return Controller.extend("umc.app.controller.sys.sys02_role_d", {
        onInit: function () {
            // 设置版本号
            this._setOnInitNo("SYS02", ".20240418");
            // 设置自己的 OData模型为默认模型
            // this._setDefaultDataModel("TableService");
            // 设置选中框 无
            // this._setSelectionNone("smartTable");
            const oMessageManager = sap.ui.getCore().getMessageManager();
            this.getView().setModel(oMessageManager.getMessageModel(), "message");
            oMessageManager.registerObject(this.getView(), true);
            this.getRouter().getRoute("RouteCre_sys02").attachPatternMatched(this._onRouteMatchedCreate, this);
            this.getRouter().getRoute("RouteEdit_sys02").attachPatternMatched(this._onRouteMatchedEdit, this);
        },

        _onRouteMatchedCreate: function (oEvent) {
            let that = this;
           
            if (this._id) {
                // 解除绑定
                this.getView().unbindElement();
                this._id = null;
            }
                that.MessageTools._clearMessage();
                this._setEditable(true);
                this._setEditableAuth(true);
                //this._setBusy(true);
                this._setIsCreate(true);
                this.getModel().setDeferredGroups(["createEntry", "changes", "draftFunction"]);
                // 新建
                this.getModel()
                    .metadataLoaded()
                    .then(() => {
                        // 新建
                        const newHeaderContext = that.getModel().createEntry("/SYS_T02_ROLE", {
    
                            groupId: "createEntry",
                        });
                        that.getView().setBindingContext(newHeaderContext);
    
                        that.byId("smartTable").rebindTable();
                        that.byId("smartTable2").rebindTable();
    
                       
                        that._setBusy(false);
                        //console.log(this.getView().byId("ROLE_CODE"))
                      
                    });
                  
          
        
         

        },

        _onRouteMatchedEdit: function (oEvent) {
            let headID = oEvent.getParameter("arguments").headID;
            this._id = headID;
            if (this._id) {
                let that = this;
                this._setEditable(false);
                this._setEditableAuth(true);
                this._setBusy(true);
                this._setIsCreate(false);
                // 编辑
                this.getModel()
                    .metadataLoaded()
                    .then(() => {
                        that._bindViewData(
                            that.getModel().createKey("/SYS_T02_ROLE", {
                                ID: headID,
                                IsActiveEntity: true,
                            }),
                        );
                        that._setBusy(false);
                        that.byId("smartTable").rebindTable();
                        that.byId("smartTable2").rebindTable();
                    });
            }


        },

        // 修正权限列表
        onUpdateAuth: function () {
            // this._setEditable(true);
            let that = this;
            let authTable = this.byId("detailTable");
            // 清除选中状态
            authTable.clearSelection();
            let roleId = this._id;
            this._getRoleAuthData(roleId, true).then((data) => {
                that.authData = data;
                return that._getSys02AuthList();
            }).then((oTable) => {
                // 当前角色id 下的权限列表
                let aData = that.authData.results;
                let allData = oTable.results;
                for (let i = 0; i < allData.length; i++) {
                    let dataX = allData[i];
                    for (let j = 0; j < aData.length; j++) {
                        if (dataX.ID === aData[j].AUTH_ID) {
                            this.byId("detailTable").addSelectionInterval(i, i);
                        }

                    }
                }

            }).catch((oError) => {
                console.log(oError);
            }).finally(() => {
                // that._setBusy1(false);
                that._setBusy(false);
            });


        },

        _getRoleAuthData: function (headId, flag) {
            this._setBusy(true);
            let that = this;
            return new Promise(function (resolve, reject) {
                that.getModel().read("/SYS_T05_ROLE_2_AUTH", {
                    filters: [
                        new sap.ui.model.Filter({
                            path: "ROLE_ID",
                            value1: headId,
                            operator: sap.ui.model.FilterOperator.EQ,
                        }),
                        new sap.ui.model.Filter({
                            path: "IsActiveEntity",
                            value1: flag,
                            operator: sap.ui.model.FilterOperator.EQ,
                        }),
                    ],
                    success: function (oData) {
                        that._setBusy(false);
                        resolve(oData);
                    },
                    error: function (oError) {
                        reject(oError);
                    },
                });
            });
        },

        onRoleUpdate: function () {
            this._setEditable(true);
            this.onUpdateAuth();
        },


        onRoleSave: function () {
            this._setBusy(true);
            let that = this;
            that.MessageTools._clearMessage();
            let flag = that._checkHead();
            if (flag) {
                // var context = this._getHeadBingdingContext();
                let authList = this._getRootId("detailTable", "ID");
                let roleCode = that.byId("ROLE_CODE").getValue();
                let roleName = that.byId("ROLE_NAME").getValue();
                // 有id是更新动作 无id是新增动作
                if (this._id) {
                    let jsonStr = {id:this._id, roleCode: roleCode, roleName: roleName, authList: authList };
                    let params = { roleJson: JSON.stringify(jsonStr)};
                    that.getModel().callFunction("/SYS02_ROLE_editRole",{
                    method:"POST",
                    urlParameters:params,
                    success: function (data) {
                        that._setEditable(false);
                        that._setBusy(false);
                        //that._id = context.ID;
                        that.byId("smartTable2").rebindTable();
                        that._setIsCreate(false);

                    },
                    error: function (oError) {
                        that._setBusy(false);
                      },
                
                })

                } else {
                    let jsonStr = { roleCode: roleCode, roleName: roleName, authList: authList };
                    let params = { roleJson: JSON.stringify(jsonStr)};
                    that.getModel().callFunction("/SYS02_ROLE_addRole", {
                        method: "POST",
                        urlParameters: params,
                        success: function (data) {
                            let json = JSON.parse(data.SYS02_ROLE_addRole);
                            console.log(json)
                            that._setBusy(false);
                            that._setEditable(false);
                            that._id = json.id;
                            //that._id = context.id;
                            //that.byId("smartTable").rebindTable();
                            that.byId("smartTable2").rebindTable();
                            that._setIsCreate(false);
                        },
                        error: function () {
                            that._setBusy(false);
                        }
                    })
                }

            } else {
                this._setBusy(false);
            }


        },

        onRebindAuth2: function (oEvent) {
            // let oBindingParams = oEvent.getParameter("bindingParams");
            oEvent.getParameter("bindingParams").parameters.expand = "TO_MENU";
        },

        onRebindAuth: function (oEvent) {
            //过滤
            let oBindingParams = oEvent.getParameter("bindingParams");
            if (this._id != "" && this._id != null) {
                oBindingParams.filters.push(new sap.ui.model.Filter("ROLE_ID", "EQ", this._id));
                oBindingParams.filters.push(new sap.ui.model.Filter("IsActiveEntity", "EQ", true));
                oEvent.getParameter("bindingParams").parameters.expand = "TO_AUTH,TO_AUTH/TO_MENU";
            } else {
                oBindingParams.filters.push(new sap.ui.model.Filter("ROLE_ID", "EQ", "1111111111111"));
            }
        },

        _getSys02AuthList: function () {
            let that = this;
            return new Promise(function (resolve, reject) {
                that.getModel().read("/SYS_T03_AUTH", {
                    success: function (oData) {
                        resolve(oData);
                    },
                    error: function (oError) {
                        reject(oError);
                    }
                })
            });

        },

        /**
    * 获取字段的某一个id集合
    * @param {画面id} viewId
    * @param {要取的字段ID} filedId
    * @returns
    */
        _getRootId(viewId, filedId) {
            // 获取明细权限数据
            var dataList = this._getByIdObject(viewId);
            var dataID = new Array();
            //设值
            for (var j = 0; j < dataList.length; j++) {
                var data = dataList[j];
                dataID.push(eval("data." + filedId));
            }
            return dataID;
        },

        _checkHead: function () {
            let that = this;
            let checkFlag = true;
            let roleCode = that.byId("ROLE_CODE").getValue();
            let roleName = that.byId("ROLE_NAME").getValue();
            if (roleCode === null || roleCode === "") {
                //let sPath = that.getView().getBindingContext().getPath() + "/ROLE_CODE";
                that.MessageTools._addMessage(this.MessageTools._getI18nTextInModel("sys", "SYS02_MSG_ROLE_CODE", this.getView()), null, 1, that.getView());
                checkFlag = false;
            }
            if (roleName === null || roleName === "") {
                // let sPath = that.getView().getBindingContext().getPath() + "/ROLE_NAME";
                that.MessageTools._addMessage(this.MessageTools._getI18nTextInModel("sys", "SYS02_MSG_ROLE_NAME", this.getView()), null, 1, that.getView());
                checkFlag = false;
            }
            return checkFlag;


        }

    });
});
