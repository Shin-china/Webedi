sap.ui.define([], function () {
    "use strict";
    return {
      /**
       * 格式化mime类型
       * @param {*} mimeType
       * @returns
       * 
       */
      formatMimeType: function(mimeType){
        return atob(mimeType);
      }
    }
});