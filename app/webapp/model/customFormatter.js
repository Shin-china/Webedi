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
        if(mimeType === "" || mimeType === null){
          return "";
        }
        return atob(mimeType);
      },

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
      },
    }
});