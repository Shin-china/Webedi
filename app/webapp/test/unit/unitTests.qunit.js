/* global QUnit */
QUnit.config.autostart = false;

sap.ui.getCore().attachInit(function () {
	"use strict";

	sap.ui.require([
		"umc/app/test/unit/AllTests"
	], function () {
		QUnit.start();
	});
});
