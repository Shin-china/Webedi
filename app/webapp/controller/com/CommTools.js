sap.ui.define(["sap/base/i18n/ResourceBundle", "sap/ui/model/resource/ResourceModel"], function (ResourceBundle, ResourceModel) {
  "use strict";

  return {
    _test: function () {
      this.MessageTools._bindResourceModel(this.getView(), "com"); //动态绑定资源文件

      // JS直接操作 odata测试
      let serviceUrl = "/srv/odata/v2/TableService/";

      var odata = new sap.ui.model.odata.v2.ODataModel({
        serviceUrl: serviceUrl,
      });

      var path = "/SYS_T01_USER(ID=guid'0f6c4b7a-8928-4048-a0a6-f8234b965d09',IsActiveEntity=true)";
      odata.read(path, {
        success: function (oData, oResponse) {
          //alert(oData.USER_NAME);   //访问属性
          console.log("Response", oResponse);
        },
        error: function (oError) {
          console.log("Error", oError);
        },
      });
    },
      /**
       *将日期格式化为YYYYMMDD
       * @param {日期} _date
       */
       _formatToYYYYMMDD: function (_date) {
        const year = _date.getFullYear();
        const month = String(_date.getMonth() + 1).padStart(2, '0'); // 月份从0开始，加1后补零
        const day = String(_date.getDate()).padStart(2, '0'); // 补零
 
        return `${year}/${month}/${day}`;
      },
    //导出Excel-日期时间格式化
    _setExcelFormatDateTime(setting, i, fieldId) {
      if (fieldId === setting.workbook.columns[i].property) {
        setting.workbook.columns[i].type = "DateTime";
        setting.workbook.columns[i].width = 20;
        setting.workbook.columns[i].utc = false;
      }
    },
    //导出Excel-日期格式化
    _setExcelFormatDate(setting, i, fieldId) {
      if (fieldId === setting.workbook.columns[i].property) {
        setting.workbook.columns[i].type = "Date";
        setting.workbook.columns[i].width = 15;
        setting.workbook.columns[i].utc = false;
      }
    },

    /**
     * 将日期格式化
     */
    getDate: function (datetime) {
      var myyear = datetime.getFullYear();
      var mymonth = datetime.getMonth() + 1;
      var myweekday = datetime.getDate();
      if (mymonth < 10) {
        mymonth = "0" + mymonth;
      }
      if (myweekday < 10) {
        myweekday = "0" + myweekday;
      }
      return new Date(myyear + "-" + mymonth + "-" + myweekday + "T00:00:00Z");
    },
    /**
     * 获取当前时间的YYYYMMDD時分秒
     */
     getCurrentTimeFormatted:function() {
      const now = new Date();
   
      const year = now.getFullYear();
      const month = String(now.getMonth() + 1).padStart(2, '0'); // 月份从0开始，所以要加1
      const day = String(now.getDate()).padStart(2, '0');
      const hours = String(now.getHours()).padStart(2, '0');
      const minutes = String(now.getMinutes()).padStart(2, '0');
      const seconds = String(now.getSeconds()).padStart(2, '0');
   
      const formattedTime = `${year}${month}${day}${hours}${minutes}${seconds}`;
      return formattedTime;
  },
    //修改画面日期格式->传后台时使用
    _getDateYYYYMMDD: function (accdate) {
      var date = new Date(accdate);
      var y = date.getFullYear();
      var m = date.getMonth() + 1;
      m = m < 10 ? "0" + m : m;
      var d = date.getDate();
      d = d < 10 ? "0" + d : d;
      const time = y + "-" + m + "-" + d;
      return time;
    },
    _ajax: function (_that, ajaxdate, url, successfun, errorfun) {
      //that._setBusy(true);
      let that = _that;
      //通用AJAX函数
      //MaskUtil.mask();
      $.ajax({
        type: "post",
        async: true,
        data: ajaxdate,
        url: url,
        success: function (data) {
          //var rs = eval("(" + data + ")"); // change the JSON string to
          // javascript object
          if (data.error) {
            // 错误的时候，弹出错误提示
            errorfun(data);
          } else if (data.success) {
            // 成功的时候
            successfun(data);
          }
        },
        error: function (error) {
          if(error.status=="502"){
            that.MessageTools._addMessage(that.MessageTools._getI18nText("MSG_ERR_SERVER_EXCEPTION", that.getView()), null, 1, that.getView());
          }
          that._setBusy(false);
          //MaskUtil.unmask();
        },
        complete: function () {
          that._setBusy(false);
          //MaskUtil.unmask();
        },
      });
    },
  };
});
