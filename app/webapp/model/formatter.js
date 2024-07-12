sap.ui.define([
], function() {
    'use strict';
    return {
        getEditableSaveCreateCommon : function(isHEditing,isJurisdiction,isCreate){
			return isHEditing && isJurisdiction && isCreate;
        },

        isExternalVendor:function(){
            return true;
        }
    }
});