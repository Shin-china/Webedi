jQuery.sap.require("umc/app/util/jszip");
sap.ui.define(
  [
    "sap/base/i18n/ResourceBundle",
    "sap/ui/model/resource/ResourceModel",
    "umc/app/controller/com/MessageTools",
    "umc/app/lib/xml-js",
    "sap/ui/model/json/JSONModel",
    "sap/ui/model/Filter",
    "sap/ui/model/FilterOperator",
  ],
  function (ResourceBundle, ResourceModel, MessageTools, xml, JSONModel, Filter, FilterOperator) {
    "use strict";

    return {
      /**
       * 账票打印，正常打印不下载
       * @param {*} _that 打印画面
       * @param {* 打印数据} _sResponse 【json2xml 格式数据】
       * @param {* 模板} _xdpTemplateID
       * @param {* 打印参数} _data
       * @param {* 是否调用打印回写处理方法} _printBackFuncation
       */
      _detailSelectPrint: function (_that, _sResponse, _xdpTemplateID, _data, _printBackFuncation, _smartTableId, entityInModelID) {
        var that = this;

        return new Promise(function (resolve, reject) {
          // check if running in localhost
          if (that._getIsHost()) {
            // 本地开发打印
            that._getOAuthToken(_that).then(
              function (token) {
                //生成pdf
                that
                  ._createPDF(_that, _sResponse, _xdpTemplateID, token)
                  .then(
                    function () {
                      //打印回写处理
                      if (_printBackFuncation) {
                        that.printBackAction(_that, _data, _printBackFuncation, _smartTableId, entityInModelID);
                      }
                    },
                    function (error) {
                      //异常MSG处理
                      reject(error);
                      sap.m.MessageBox.alert(error.responseText);
                      _that._setBusy(false);
                    }
                  )
                  .finally(function () {
                    resolve(true);
                  });
              },
              function (error) {
                _that._setBusy(false);
                reject(error);
                sap.m.MessageBox.alert(error.responseText);
                _that._setBusy(false);
              }
            );
          } else {
            //BTP 打印
            that
              ._createPDF(_that, _sResponse, _xdpTemplateID)
              .then(
                function () {
                  if (_printBackFuncation) {
                    that.printBackAction(_that, _data, _printBackFuncation, _smartTableId, entityInModelID);
                  }
                },
                function (error) {
                  reject(error);
                  sap.m.MessageBox.alert(error.responseText);
                  _that._setBusy(false);
                }
              )
              .finally(function () {
                resolve(true);
              });
          }
        });
      },
      /**
       * 账票下载不打印，正常下载不打印
       * @param {*} _that 打印画面
       * @param {* 打印数据} _sResponse 【json2xml 格式数据】
       * @param {* 模板} _xdpTemplateID
       * @param {* 打印参数} _data
       * @param {* 打印回写处理方法} _printBackFuncation
       * @param {* pdf下载名字} _name
       */
      _detailSelectPrintDow: function (_that, _sResponse, _xdpTemplateID, _data, _printBackFuncation,_name, _smartTableId, entityInModelID) {
            var that = this;
            var zip = new JSZipSync();
    				_that.printTaskPdf = {
              completedCount: 0,
              count: 1,
              progress : 0,
              pdfUrl: [],
              zip : zip,
              zipFolder : zip.folder(),
              zipFile : [],
              
            };
            return new Promise(function (resolve, reject) {
              // check if running in localhost
              if (that._getIsHost()) {
                // 本地开发打印
                that._getOAuthToken(_that).then(
                  function (token) {
                    //生成pdf
                    that
                      ._createPDFDow(_that, _sResponse, _xdpTemplateID, token,_name)
                      .then(
                        function (blob) {
                          //打印回写处理
                          if (_printBackFuncation) {
                            that.printBackAction(_that, _data, _printBackFuncation, _smartTableId, entityInModelID);
                          }
                          resolve(blob); 
                        },
                        function (error) {
                          //异常MSG处理
                          reject(error);
                          sap.m.MessageBox.alert(error.responseText);
                          _that._setBusy(false);
                        }
                      )
                      .finally(function () {
                        that._checkPrintTaskPdf(_that);
                        resolve(true);
                      });
                  },
                  function (error) {
                    _that._setBusy(false);
                    reject(error);
                    sap.m.MessageBox.alert(error.responseText);
                    _that._setBusy(false);
                  }
                );
              } else {
                //BTP 打印
                that
                  ._createPDF(_that, _sResponse, _xdpTemplateID)
                  .then(
                    function () {
                      if (_printBackFuncation) {
                        that.printBackAction(_that, _data, _printBackFuncation, _smartTableId, entityInModelID);
                      }
                    },
                    function (error) {
                      reject(error);
                      sap.m.MessageBox.alert(error.responseText);
                      _that._setBusy(false);
                    }
                  )
                  .finally(function () {
                    that._checkPrintTaskPdf(_that);
                    resolve(true);
                  });
              }
            });
          },
          /**
       * 账票EMAIL不打印
       * @param {*} _that 打印画面
       * @param {* 打印数据} _sResponse 【json2xml 格式数据】
       * @param {* 模板} _xdpTemplateID
       * @param {* 打印参数} _data
       * @param {* 打印回写处理方法} _printBackFuncation
       * @param {* pdf下载名字} _name
       */
      _detailSelectPrintEmil: function (_that, _sResponse, _xdpTemplateID, _data, _printBackFuncation,_name, _smartTableId, entityInModelID,count) {
        var that = this;

        return new Promise(function (resolve, reject) {
          // check if running in localhost
          if (that._getIsHost()) {
            // 本地开发打印
            that._getOAuthToken(_that).then(
              function (token) {
                //生成pdf
                that
                  ._createPDFEml(_that, _sResponse, _xdpTemplateID, token)
                  .then(
                    function (blob) {
                      //打印回写处理
                      if (_printBackFuncation) {
                        that.printBackAction(_that, _data, _printBackFuncation, _smartTableId, entityInModelID);
                      }
                      resolve(blob); ;
                    },
                    function (error) {
                      //异常MSG处理
                      reject(error);
                      sap.m.MessageBox.alert(error.responseText);
                      _that._setBusy(false);
                    }
                  )
                  .finally(function () {
                    resolve(true);
                  });
              },
              function (error) {
                _that._setBusy(false);
                reject(error);
                sap.m.MessageBox.alert(error.responseText);
                _that._setBusy(false);
              }
            );
          } else {
            //BTP 打印
            that
              ._createPDFEml(_that, _sResponse, _xdpTemplateID)
              .then(
                function () {
                  if (_printBackFuncation) {
                    that.printBackAction(_that, _data, _printBackFuncation, _smartTableId, entityInModelID);
                  }
                },
                function (error) {
                  reject(error);
                  sap.m.MessageBox.alert(error.responseText);
                  _that._setBusy(false);
                }
              )
              .finally(function () {
                resolve(true);
              });
          }
        });
      },
        /**
               * 账票下载不打印2,多种格式一并下载调用
               * @param {*} _that 打印画面
               * @param {* 打印数据} _sResponse 【json2xml 格式数据】
               * @param {* 模板} _xdpTemplateID
               * @param {* 打印参数} _data
               * @param {* 打印回写处理方法} _printBackFuncation
               * @param {* pdf下载名字} _name
               */
        _detailSelectPrintDowS: function (_that, _sResponse, _xdpTemplateID, _data, _printBackFuncation,_name, _smartTableId, entityInModelID) {
          var that = this;

          return new Promise(function (resolve, reject) {
            // check if running in localhost
            if (that._getIsHost()) {
              // 本地开发打印
              that._getOAuthToken(_that).then(
                function (token) {
                  //生成pdf
                  that
                    ._createPDFDow(_that, _sResponse, _xdpTemplateID, token,_name)
                    .then(
                      function (blob) {
                        //打印回写处理
                        if (_printBackFuncation) {
                          that.printBackAction(_that, _data, _printBackFuncation, _smartTableId, entityInModelID);
                        }
                        resolve(blob); ;
                      },
                      function (error) {
                        //异常MSG处理
                        reject(error);
                        sap.m.MessageBox.alert(error.responseText);
                        _that._setBusy(false);
                      }
                    )
                    .finally(function () {
                      that._checkPrintTaskPdf(_that);
                      resolve(true);
                    });
                },
                function (error) {
                  _that._setBusy(false);
                  reject(error);
                  sap.m.MessageBox.alert(error.responseText);
                  _that._setBusy(false);
                }
              );
            } else {
              //BTP 打印
              that
                ._createPDF(_that, _sResponse, _xdpTemplateID)
                .then(
                  function () {
                    if (_printBackFuncation) {
                      that.printBackAction(_that, _data, _printBackFuncation, _smartTableId, entityInModelID);
                    }
                  },
                  function (error) {
                    reject(error);
                    sap.m.MessageBox.alert(error.responseText);
                    _that._setBusy(false);
                  }
                )
                .finally(function () {
                  that._checkPrintTaskPdf();
                  resolve(true);
                });
            }
          });
        },

      /**
       * 本地打印时，现获取token
       * @returns token
       */
      _getOAuthToken: function (_that) {
         
        return new Promise(function (resolve, reject) {
          $.ajax({
            url:  _that.getGlobProperty("ADS_tokenurl"),
            type: "post",
            headers: {
              Authorization: "Basic " + btoa(_that.getGlobProperty("ADS_clientid") + ":" + _that.getGlobProperty("ADS_clientsecret")),
            },
            success: function (data, status, xhr) {
              resolve(data.access_token);
            },
            error: function (xhr, status, error) {
              _that._setBusy(false);
              reject(error);
            },
          });
        });
      },
      
		/* 全部pdf取得后打包 */
		_checkPrintTaskPdf: function (_that) {
			if (_that.printTaskPdf) {
				_that.printTaskPdf.completedCount++;
				if (_that.printTaskPdf.completedCount == _that.printTaskPdf.count) {
					this._saveFileZip(_that);
				}
			} else {
				// this._setBusy(false);
			}
		},

		_saveFileZip: function (_that) {
			_that.printTaskPdf.pdfUrl.forEach(pdf => {
				var xhr = new XMLHttpRequest();
				xhr.open("GET", pdf.url, true)
				xhr.responseType = "arraybuffer";
				xhr.onload = () => {
					_that.printTaskPdf.zipFile.push({ name: pdf.name, content: xhr.response });
					if (_that.printTaskPdf.zipFile.length == _that.printTaskPdf.pdfUrl.length) {
						_that.printTaskPdf.zipFile.forEach(file => {
							_that.printTaskPdf.zipFolder.file(file.name, file.content, { binary: true });
						});
						var blob = _that.printTaskPdf.zip.generate({ type: 'blob' });
						var aLabel = document.createElement('a');
						aLabel.href = URL.createObjectURL(blob);
						aLabel.download = "PDF.rar";
						aLabel.click();
					
						_that.printTaskPdf = undefined;
					}
				}
        _that._setBusy(false);
				xhr.send();
			});
		},

      /**
       *
       * @param {* 打印数据源表格 }
       * @param {* 选中的数据} selectedIndices
       * @param {* 打印调用方法} entityPath4Print
       * @param {* 过滤条件} propertyInEntity4Filter
       * @param {* 指定model}entityInModelID
       * @returns
       */
      _getPrintDataInfo: function (_that, selectedIndices, entityPath4Print, propertyInEntity4Filter, entityInModelID) {
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
    /*==============================
     * call ADS Service to generate PDF
     * @param {*} dataXML
     * @param {*} accessToken
    ==============================*/
    _createPDF: function (_that, dataXML, xdpTemplateID, accessToken) {
      return new Promise(
        function (resolve, reject) {
          dataXML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><form>" +
            dataXML +
            "</form>";
          var encdata = btoa(unescape(encodeURIComponent(dataXML)));
          var jsonData =
            "{	" +
            "\"xdpTemplate\": \"" +
            xdpTemplateID +
            "\", " +
            "\"xmlData\": \"" +
            encdata +
            "\"}";
          if (accessToken) {
            var headers = { Authorization: "Bearer " + accessToken };
          } else {
            var headers = {};
          }

          $.ajax({
            url:
              _that.getGlobProperty("ADS_url"),
            type: "post",
            headers: headers,
            contentType: "application/json",
            data: jsonData,
            success: function (data, textStatus, jqXHR) {
              //once the API call is successfull, Display PDF on screen
              var decodedPdfContent = atob(data.fileContent);
              var byteArray = new Uint8Array(decodedPdfContent.length);
              for (var i = 0; i < decodedPdfContent.length; i++) {
                byteArray[i] = decodedPdfContent.charCodeAt(i);
              }
              var blob = new Blob([byteArray.buffer], {
                type: "application/pdf",
              });
              _that._blob =blob;
              var _pdfurl = URL.createObjectURL(blob);

              if (!this._PDFViewer) {
                this._PDFViewer = new sap.m.PDFViewer({
                  width: "auto",
                  source: _pdfurl,
                });
                jQuery.sap.addUrlWhitelist("blob"); // register blob url as whitelist
              }
              resolve(1);
              this._PDFViewer.open();
            },
            error: function (xhr, status, error) {
              reject(error);
            },
          });
        }.bind(this)
      );
    },
      /*==============================
     * call ADS Service to generate PDF
      email专用
     * @param {*} dataXML
     * @param {*} accessToken
    ==============================*/
      _createPDFEml: function (_that, dataXML, xdpTemplateID, accessToken) {
        return new Promise(
          function (resolve, reject) {
            dataXML =
              "<?xml version=\"1.0\" encoding=\"UTF-8\"?><form>" +
              dataXML +
              "</form>";
            var encdata = btoa(unescape(encodeURIComponent(dataXML)));
            var jsonData =
              "{	" +
              "\"xdpTemplate\": \"" +
              xdpTemplateID +
              "\", " +
              "\"xmlData\": \"" +
              encdata +
              "\"}";
            if (accessToken) {
              var headers = { Authorization: "Bearer " + accessToken };
            } else {
              var headers = {};
            }
  
            $.ajax({
              url:
                _that.getGlobProperty("ADS_url"),
              type: "post",
              headers: headers,
              contentType: "application/json",
              data: jsonData,
              success: function (data, textStatus, jqXHR) {
                //once the API call is successfull, Display PDF on screen
                var decodedPdfContent = atob(data.fileContent);
                var byteArray = new Uint8Array(decodedPdfContent.length);
                for (var i = 0; i < decodedPdfContent.length; i++) {
                  byteArray[i] = decodedPdfContent.charCodeAt(i);
                }
                var blob = new Blob([byteArray.buffer], {
                  type: "application/pdf",
                });
                _that._blob =blob;
                resolve(blob);

              },
              error: function (xhr, status, error) {
                reject(error);
              },
            });
          }.bind(this)
        );
      },
      /*==============================
     * call ADS Service to generate PDF
      只下载不打印
     * @param {*} dataXML
     * @param {*} accessToken
    ==============================*/
    _createPDFDow: function (_that, dataXML, xdpTemplateID, accessToken,_name) {
      var that = this;
      return new Promise(
        function (resolve, reject) {
          dataXML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><form>" +
            dataXML +
            "</form>";
          var encdata = btoa(unescape(encodeURIComponent(dataXML)));
          var jsonData =
            "{	" +
            "\"xdpTemplate\": \"" +
            xdpTemplateID +
            "\", " +
            "\"xmlData\": \"" +
            encdata +
            "\"}";
          if (accessToken) {
            var headers = { Authorization: "Bearer " + accessToken };
          } else {
            var headers = {};
          }

          $.ajax({
            url:
              _that.getGlobProperty("ADS_url"),
            type: "post",
            headers: headers,
            contentType: "application/json",
            data: jsonData,
            success: function (data, textStatus, jqXHR) {
              //once the API call is successfull, Display PDF on screen
              var decodedPdfContent = atob(data.fileContent);
              var byteArray = new Uint8Array(decodedPdfContent.length);
              for (var i = 0; i < decodedPdfContent.length; i++) {
                byteArray[i] = decodedPdfContent.charCodeAt(i);
              }
              var blob = new Blob([byteArray.buffer], {
                type: "application/pdf",
              });
              _that._blob =blob;
              var _pdfurl = URL.createObjectURL(blob);
              if (_that.printTaskPdf) {
                if(_name){
                  _that.printTaskPdf.pdfUrl.push({ url: _pdfurl, name: _name });
                }else{
                  _that.printTaskPdf.pdfUrl.push({ url: _pdfurl, name: that._getDownloadName(xdpTemplateID) });
                }

								resolve(blob);
								return;
							}
              resolve(blob);
          
            },
            error: function (xhr, status, error) {
              reject(error);
            },
          });
        }.bind(this)
      );
    },
    // test1--
		_getDownloadName: function (templateId) {
			var downloadName = "";
			switch (templateId) {
				case "uwms_umce_dev_zws/T":
					downloadName = "注文書";
					break;
				case "uwms_umce_dev_nps/T":
					downloadName = "納品書";
          break;
			}
			var date = new Date();
			return downloadName + date.toJSON().replaceAll("-", "").replaceAll("T", "").replaceAll(":", "").replaceAll(".", "").replaceAll("Z", "") + ".pdf";
		},
      /**
       * 打印结束后 回写方法
       * @param {*打印的数据集合 key } _data
       * @param {*回写方法} _cdsAction
       * @param {*回写完成后 刷新页面数据}
       * @param {*打印页面非V2时 指定model}entityInModelID
       */
      printBackAction: function (_that, _data, _cdsAction, _smartTableId, entityInModelID) {
        var _parameters = [];
        for (var i = 0; i < _data.length; i++) {
          let keyId = _data[i];
          var params = { keyId: keyId };
          _parameters.push(params);
        }
        var _parametersString = { parms: JSON.stringify(_parameters) };
        _that._callCdsAction(_cdsAction, _parametersString, _that, entityInModelID).then((oData) => {
          //刷新数据
          if (_smartTableId) {
            _that.byId(_smartTableId).rebindTable();
            _that.getModel().refresh(true); //刷新数据
          }
        });
      },
  /**
       * 打印结束后 po-SAP回写方法
       * @param {*打印的数据集合 key } _data

       */
  printBackActionPo: function (_that,_blob, _data) {

    _that.PrintTool.getImageBase64(_blob).then((odata)=>{
      var mailObj = { attachmentJson:{
        object: _data.po,
        object_type:_data.type,
        value:odata,
        file_type:btoa(_blob.type),
        file_name:_data.fileName
      }}

			$.ajax({
				url: "srv/odata/v4/Common/s3uploadAttachment",
				type: "POST",
				contentType: "application/json; charset=utf-8",
				dataType: "json",
				async: false,
				crossDomain: true,
				responseType: 'blob',
				data: JSON.stringify(mailObj),
				success: function (base64) {
          console.log("上传成功");
				}
			})

    });
  },
      /**
       * 系统 UPN 共通打印
       * @param {*} _that
       * @param {*} _upnDatas
       * @param {*} _smartTableId
       * @param {* 非V2时指定V2 model}entityInModelID
       */
      printUpnCommonByUpnDatas: function (_that, _upnDatas, _smartTableId) {
        var that = this;
        return new Promise(function (resolve, reject) {
          var selectedIndices = []; //传入key 参数 用与 获取打印数据、回写打印方法
          for(var u=0;u<_upnDatas.length;u++){
            selectedIndices.push(_upnDatas[u].UPN_NO);
          }
          var entityPath4Print = "/INV01_LIST"; //打印数据方法
          var propertyInEntity4Filter = "UPN_NO"; //过滤条件
          var entityInModelID = "TableService";
          
          var xdpTemplateID = _that.getGlobProperty("ADS_formname")+"/REP01_UPN"; //"USAP_REP01/USAP_REP01"; //模板
          var printBackFuncation = "/UPN_PRINT_BACK_WRITE";

          //数据处理。
          that._getPrintDataInfo(_that, selectedIndices, entityPath4Print, propertyInEntity4Filter, entityInModelID).then(
            function (oData) {
              var options = { compact: true, ignoreComment: true, spaces: 4 };
              //打印数据格式转换
              //check 打印数据是否为空
              if(oData.results.length===0){
                
                resolve(false);
              }else{
                //打印数据处理，打印的数据，没条记录打印张数及打印的回写处理
               
                var resultsNew = new Array();

                for(var i=0;i<oData.results.length;i++){
                    for(var p=0;p<_upnDatas.length;p++){
                      if(oData.results[i].UPN_NO === _upnDatas[p].UPN_NO){
                        for(var pn=0;pn<_upnDatas[i].P_NUM;pn++){
                          resultsNew.push(oData.results[i]);
                        }
                      }
                    }
                }
                oData.results = resultsNew;
                var sResponse = json2xml(oData, options);
                //开始打印
                that._detailSelectPrint(_that, sResponse, xdpTemplateID, selectedIndices, printBackFuncation, _smartTableId, entityInModelID).then((sta) => {
                  resolve(sta);
                });
              }
              
            },
            function (error) {
              sap.m.MessageBox.alert(error.message);
              //_that.MessageTools._addMessage(_that.MessageTools._getI18nText("MSG_ERR_SERVER_EXCEPTION", _that.getView()), null, 1, _that.getView());
              _that._setBusy(false);
              reject(error);
            }
          );
        });
      },
      //将pdf转换为base64传到后台打印
      getImageBase64(blob) {
        return new Promise((resolve,reject) => {
          const reader = new FileReader();
          reader.readAsDataURL(blob);
          reader.onload = () => {
            const base64 = reader.result;
            
            resolve(base64.replace("data:application/pdf;base64,",""));
          }
          reader.onerror = error => reject(error);
        });
      },
      //将pdf转换为base64传到后台打印
      getCsvBase64(blob) {
        return new Promise((resolve,reject) => {
          const reader = new FileReader();
          reader.readAsDataURL(blob);
          reader.onload = () => {
            const base64 = reader.result;
            
            resolve(base64.replace("data:text/csv;charset=utf-8;base64,",""));
          }
          reader.onerror = error => reject(error);
        });
      },
      //判断是否能使用打印的功能,改为都可以使用//ldd追加
      _getIsHost() {
        if(window.location.hostname === "localhost" ||true || window.location.hostname === "umc-electronics-co---ltd--s01-test-dev-uweb-umce-dev.cfapps.jp10.hana.ondemand.com"){
          return true;
        }
        else {
          return false;
        }
      }
    };
  }
);
