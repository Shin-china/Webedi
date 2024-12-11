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
        if (undefined != obj && obj != null && obj != "") {
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
       * 公用，各业务画面,无删除flagy，有自定义字段
       * 编辑中+有权编辑 按钮显示。
       * @param {是否在编辑} isHEditing
       * @param {是否有权编辑} isJurisdiction
       * @returns
       */
            getEditableSaveCommonData: function (isHEditing, isJurisdiction,data) {
              return isHEditing && isJurisdiction && this.formatter._isNull(data);
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

      //買掛金明細,支付通知判断SHKZG逻辑
        formatPrice: function (price, shkzg) {
            if (price === undefined || shkzg === undefined) {
                return price; // 如果没有值，返回原值
            }

            price = Number(price); // 确保是数字
            
            // 根据 SHKZG 值决定正负
            return shkzg === 'H' ? -Math.abs(price) : Math.abs(price);
        },

        formatTotal: function (total, shkzg) {
            if (total === undefined || shkzg === undefined) {
                return total; // 如果没有值，返回原值
            }

            total = Number(total); // 确保是数字
            
            // 根据 SHKZG 值决定正负
            return shkzg === 'H' ? -Math.abs(total) : Math.abs(total);
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

      formatDate2: function (date) {
        if (date != null) {
          const year = date.getFullYear();  
          const month = ('0' + (date.getMonth() + 1)).slice(-2);  
          const day = ('0' + date.getDate()).slice(-2);  
          
          return `${year}-${month}-${day}`; 
        }
       
      },

      formatTaxRate: function(rate) {
        if (!rate && rate !== 0) return "";  // 如果值为 null 或 undefined 返回空字符串
        return rate + "%";  // 在税率后加上 % 符号
    },

    formatUnitPrice: function (value, currency) {
      // 如果 value 为空或无效，直接返回原值
      if (value == null || isNaN(value)) {
          return value;
      }
  
      // 确保 value 是浮动数字
      value = parseFloat(value);
  
      // 如果是 JPY，保留整数
      if (currency === 'JPY') {
          return Math.floor(value).toString(); // JPY 显示为整数
      }
  
      // 如果是 USD 或 EUR，保留两位小数
      if (currency === 'USD' || currency === 'EUR') {
          return value.toFixed(2); // USD 和 EUR 显示两位小数
      }
  
      // 其他情况：返回原始小数位（不修改小数位数）
      return value.toString();
  },
  
  
  
    formatSendFlag: function(value) {
      if (value === '1') {
          return '1: 未送信';
      } else if (value === '2') {
          return '2: 送信済';
      } else {
          return ''; // 默认返回空字符串
      }
  },

  formatSendFlagPch02: function(value) {
    if (value === '1') {
        return '1: 反映待';
    } else if (value === '2') {
        return '2: 反映済';
    } else {
        return ''; // 默认返回空字符串
    }
      },
      formatStatusPch10: function (value) {

        if (value === '1') {
          return '1: 未送信';
        } else if (value === '2') {
          return '2: 送信済（未回答）';
        } else if (value === '3') {
          return '3: 回答済';
        } else if (value === '4') {
          return '4: 再送信（依頼中）';
        } else if (value === '5') {
          return '5: 完了';
        } else {
          return ''; // 默认返回空字符串
        }
      },

  formatIndex: function(index) {
    return (index + 1).toString(); // 返回行号
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
      getUserTypeText: function (userType) {
        if (userType === '1') {
          return 'Internal User';
        } else if (userType === '2') {
          return 'External User';
        } else {
          return '未知用户';
        }
      },
      getUserStatus:function(userStatus){
        if(userStatus === 'A'){
          return 'Active';
        }else if(userStatus === 'I'){
          return 'Inactive';
        }else{
          return 'Locked';
        }
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

      formatDateForFileName: function () {
          var oDate = new Date();
          var sDate = oDate.toISOString().slice(0, 10).replace(/-/g, '');
          var sTime = oDate.toTimeString().slice(0, 8).replace(/:/g, '');
          return `${sDate}${sTime}.xlsx`;
      },

    pch08GetInputEditable : function (str) {
      console.log(str)
      if (str != null && str != "" && str != undefined) {
        return true;
      }
      return false;
    },
      /**
       * 如果为1为true判断权限用
       * @param {*} isHEditing
       * @param {*} isJurisdiction
       * @param {*} hDeleteFlag
       * @returns
       */
      checkUserPermission1: function (userType) {
            return userType == '1';
          },
          
      /**
       * 如果为2为true判断权限用
       * @param {*} isHEditing
       * @param {*} isJurisdiction
       * @param {*} hDeleteFlag
       * @returns
       */
      checkUserPermission2: function (userType) {
            return  userType == '2';
          },

      /**
       * 格式化mime类型
       * @param {*} mimeType
       * @returns
       * 
       */
      formatMimeType: function(mimeType){
        if(mimeType === null || mimeType === undefined || mimeType === ""){
          return "";
        }
        return atob(mimeType);
      },

      /**
       * 格式化pch08状态
       * @param {*} Status
       * @returns
       */
      formatPch08Status: function(sStatus){
        var sStatusText = "";
        switch(sStatus){
          case "2":
            sStatusText = "送信済（未回答）";
            break;
          case "3":
            sStatusText = "回答済";
            break;
          case "4":
            sStatusText = "再送信（依頼中）";
            break;
          default:
            sStatusText = "";
            break;
        }

        if(sStatusText === ""){
          return sStatusText;
        }else{
          return sStatusText + "（" + sStatus + "）";
        }
      }
    };
  });
  