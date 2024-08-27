sap.ui.define(["sap/base/i18n/ResourceBundle", "sap/ui/model/resource/ResourceModel","umc/app/controller/com/MessageTools",], function (ResourceBundle, ResourceModel,MessageTools) {
  "use strict";


  var _objectCommData = {
      _delete_y:"Y",
      _delete_n:"N",
      _post_status_w:"W",
      _post_status_p:"P",
      _post_status_c:"C",
  }

  return {
    
    checkCodeIsEntityExist: function (_entity,_dbFiled,_value) {
    
      let filters = 
      [
        new sap.ui.model.Filter({
          path: _dbFiled,
          value1: _value,
          operator: sap.ui.model.FilterOperator.EQ,
        }),
      ];
      this._readEntryByServiceAndEntity(_entity,filters,null).then((oData) => {
        if(oData.results.length>0) return true;
        
      });
      return false;
    },
    //删除状态的check 全体
    checkRecordDelFlag:function(delFlag,parmsMsg,view){
      if(_objectCommData._delete_y === delFlag){
        MessageTools._addMessage(MessageTools._getI18nMessage("MSG_ERR_DEL_MSG",parmsMsg, view), null, 1, view);
        return false;
      }
      return true;
    },
     //过账状态的check 全体
     checkRecordPostStatus:function(postStatus,parmsMsg,view){
      if(_objectCommData._post_status_w !== postStatus){
        MessageTools._addMessage(MessageTools._getI18nMessage("MSG_ERR_POST_STATUS_MSG",parmsMsg, view), null, 1, view);
        return false;
      }
      return true;
     },
    
     /**
      *  //检查普通input控件的必填
      * @param {*} _allInputs 
      * @returns 
      */
     _checkInputrequired:function(_allInputs,_that){
      var checkflag = false;
       
      _allInputs.forEach(function (oInputKey) {
        var oInput = _that.byId(oInputKey);
        if (null === oInput.getValue() || "" === oInput.getValue()) {
          oInput.setValueState("Error");
          checkflag = true;
        } else {
          oInput.setValueState("None");
        }
      });

      return checkflag;
     },
     //校验 decimal(13,3) 数字
     _validataNum13 : function(num_value) {
			var validNum = new RegExp("((^[1-9][0-9]{0,10})+(.?[0-9]{1,3})?$)|(^[0]+(.[0-9]{1,3})?$)").test(num_value);
			return validNum;
		  },
    
      _validataNumInt : function(value) {
        let reg = /^[1-9]\d*$/;
        return reg.test(value)
      },



  }
});
