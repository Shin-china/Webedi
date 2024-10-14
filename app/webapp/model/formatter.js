sap.ui.define([], function () {
    "use strict";
    return {
      /******** 可否编辑状态取得 ********/
  
      /*==============================
          ==============================*/
      _isHDeleted: function (hDeleteFlag) {
        if (undefined != hDeleteFlag && hDeleteFlag != null) {
          return hDeleteFlag !== "Y";
        } else return true;
      },
  
      /*==============================
          ==============================*/
      _isDWaitPost(dPostStatus) {
        return dPostStatus == 0;
      },
  
      /*==============================
          ==============================*/
      _isDPosted(dPostStatus) {
        //this.formatter._setPosted(!this.formatter._isDWaitPost(dPostStatus)); //格式化时设置整单发送状态
        return dPostStatus === "P";
      },
  
      /*==============================
          ==============================*/
      _isDCancelled(dPostStatus) {
        return dPostStatus === "C";
      },
      /**
       *
       * @param {是否为空判断} obj
       */
      _isNull: function (obj) {
        if (undefined != obj && obj != null) {
          return false;
        } else return true;
      },
  
      //判断字符串是否为空或undefined
      isUndefinedOrEmpty: function (value) {
        return typeof value === "undefined" || value === null || value === "";
      },
      //空或unde时返回false,否则返回原值
      isnullToFalseReturnValue: function (_value) {
        if (this.formatter.isUndefinedOrEmpty(_value)) return false;
        return _value;
      },
      /**
       * 公用，各业务画面
       * 编辑中+有权编辑+未删除 按钮显示。
       * @param {是否在编辑} isHEditing
       * @param {是否有权编辑} isJurisdiction
       * @param {单据头删除标记} hDeleteFlag
       * @returns
       */
      getEditableCommon: function (isHEditing, isJurisdiction, hDeleteFlag) {
        return isHEditing && isJurisdiction && this._isHDeleted(hDeleteFlag);
      },
      /**
       * 公用，各业务画面,无删除flag
       * 编辑中+有权编辑 按钮显示。
       * @param {是否在编辑} isHEditing
       * @param {是否有权编辑} isJurisdiction
       * @returns
       */
      getEditableSaveCommon: function (isHEditing, isJurisdiction) {
        return isHEditing && isJurisdiction;
      },
      /**
       * 公用，各业务画面,无删除flag
       * 非编辑中+有权编辑 按钮显示。
       * @param {是否在编辑} isHEditing
       * @param {是否有权编辑} isJurisdiction
       * @returns
       */
      getEditableDCommon: function (isHEditing, isJurisdiction) {
        return !isHEditing && isJurisdiction;
      },
      /**
       * 公用，各业务画面,无删除flag
       * 编辑中+有权编辑+新创建 按钮显示。
       * @param {是否在编辑} isHEditing
       * @param {是否有权编辑} isJurisdiction
       * @returns
       */
      getEditableSaveCreateCommon: function (isHEditing, isJurisdiction, isCreate) {
        return isHEditing && isJurisdiction && isCreate;
      },
      /**
       * 公用，各业务画面,无删除flag
       * 非编辑中+有权编辑+新创建 按钮显示。
       * @param {是否在编辑} isHEditing
       * @param {是否有权编辑} isJurisdiction
       * @returns
       */
      getEditableCreateCommon: function (isHEditing, isJurisdiction, isCreate) {
        return !isHEditing && isJurisdiction && isCreate;
      },
      /**
       * 照会+有权编辑+未删除 按钮显示。
       * @param {是否在编辑} isHEditing
       * @param {是否有权编辑} isJurisdiction
       * @param {单据头删除标记} hDeleteFlag
       * @returns
       */
      getViewCommon: function (isHEditing, isJurisdiction, hDeleteFlag) {
        return !isHEditing && isJurisdiction && this._isHDeleted(hDeleteFlag);
      },
  
      /**
       * 明细字段控制编辑
       * @param {*} isHEditing
       * @param {*} isJurisdiction
       * @param {*} hDeleteFlag
       * @returns
       */
      getDEditableCommon: function (isHEditing, isJurisdiction, hDeleteFlag) {
        return this.formatter.getEditableCommon(isHEditing, isJurisdiction, hDeleteFlag);
      },
      /**
       * 编辑comm + 非只读
       * @param {*是否只读} isBE_CHANGE
       * @param {*} isHEditing
       * @param {*} isJurisdiction
       * @param {*} hDeleteFlag
       */
      _sys03GetEditableHButton: function (isBE_CHANGE, isHEditing, isJurisdiction) {
        let hDeleteFlag = true;
  
        return isBE_CHANGE && this.formatter.getEditableCommon(isHEditing, isJurisdiction, hDeleteFlag);
      },
      /**
       * 编辑comm + 新规
       * @param {*头表单号是否存在} hCode
       * @param {*} isHEditing
       * @param {*} isJurisdiction
       * @param {*} hDeleteFlag
       */
      _sys03GetEditableHCreat: function (hCode, isHEditing, isJurisdiction) {
        let hDeleteFlag = true;
        return this.formatter.getEditableCommon(isHEditing, isJurisdiction, hDeleteFlag);
      },
  
      _sys03GetCloseButton: function (isBE_CHANGE, isHEditing, isJurisdiction, isCreate) {
        let hDeleteFlag = true;
  
        return isBE_CHANGE && !isCreate && this.formatter.getEditableCommon(isHEditing, isJurisdiction, hDeleteFlag);
      },
  
      /**
       * 照会comm + 非只读
       * @param {*是否只读} isBE_CHANGE
       * @param {*} isHEditing
       * @param {*} isJurisdiction
       * @param {*} hDeleteFlag
       */
      _sys03GetViewHButton: function (isBE_CHANGE, isHEditing, isJurisdiction) {
        let hDeleteFlag = true;
        return isBE_CHANGE && this.formatter.getViewCommon(isHEditing, isJurisdiction, hDeleteFlag);
      },
  
      getRowNo: function (ID) {
        if (ID == null) return;
        if (this._rowNoMap == null) {
          this._rowNoMap = [];
        }
        let key = ID;
        if (this._rowNoMap[key] == null) {
          let firstVRow = this.byId("detailTable").getFirstVisibleRow();
          let contexts = this.byId("detailTable").getBinding().getCurrentContexts();
          for (let c = 0; c < contexts.length; c++) {
            let cObject = contexts[c].getObject();
            this._rowNoMap[cObject.ID] = c + firstVRow + 1;
          }
        }
        return this._rowNoMap[key];
      },
      // 过账履历 No 专用
      getPostRowNo: function (ID) {
        if (ID == null) return;
        if (this._rowNoMap == null) {
          this._rowNoMap = [];
        }
        let key = ID;
        if (this._rowNoMap[key] == null) {
          let firstVRow = this.byId("postLogDetailTable").getFirstVisibleRow();
          let contexts = this.byId("postLogDetailTable").getBinding().getCurrentContexts();
          for (let c = 0; c < contexts.length; c++) {
            let cObject = contexts[c].getObject();
            this._rowNoMap[cObject.ID] = c + firstVRow + 1;
          }
        }
        return this._rowNoMap[key];
      },
  
      formatDate: function (date) {
        if (date != null) {
          const year = date.getFullYear();  
          const month = ('0' + (date.getMonth() + 1)).slice(-2);  
          const day = ('0' + date.getDate()).slice(-2);  
          const hours = ('0' + date.getHours()).slice(-2);  
          const minutes = ('0' + date.getMinutes()).slice(-2);  
          const seconds = ('0' + date.getSeconds()).slice(-2);  
          return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`; 
        }
       
      },
  
      /**
       * 组合2个参数
       * @param {*} date1 
       * @param {*} date2 
       * @returns 
       */
      combination: function (date1,date2) {
        var re = ""
        if (date1 ) {
          re =re+date1;
        }
        if (date2 ) {
          re =re+date2;
        }
        return re;
      },

      /**
       * 进行单价计算 除法
       * @param {*} date1  除数
       * @param {*} date2  被除数
       * @returns 
       */
      getPrice: function (date1,date2) {
        var re = 0.000
        if(date1 &&  date2){
          var qty = date1/date2
          return qty.toFixed(3);
        }
        return re;
      },
      		/*++++++++++++++++++++++++++++++
		日期格式转换
		参数：
		1、datetime：编辑日期
		++++++++++++++++++++++++++++++*/
		dateFormatDateTime: function (datetime) {
			var dateStr = "";
			if (datetime == null || datetime == "") {

				dateStr = datetime;

			} else {

				var y = datetime.getFullYear();
				var mon = datetime.getMonth() + 1;
				var d = datetime.getDate();
				var h = datetime.getHours();
				var min = datetime.getMinutes();
				var s = datetime.getSeconds();
				dateStr = y + '-' + mon + '-' + d + '-' + h + '-' + min + '-' + s;
			}

			return dateStr;

		},
    };
  });
  