/**
 * @return Object literal singleton instance of ExtTest
 */
var ExtTest = function() {
};

/**
 * @param directory The directory for which we want the listing
 * @param successCallback The callback which will be called when directory listing is successful
 * @param failureCallback The callback which will be called when directory listing encouters an error
 */
ExtTest.prototype.test = function(param, successCallback, failureCallback) {
	return PhoneGap.exec(
			successCallback, //Success callback from the plugin
			failureCallback, //Error callback from the plugin
			'ExtTestPlugin', //Tell PhoneGap to run "DirectoryListingPlugin" Plugin
			'test', //Tell plugin, which action we want to perform
			[ param ]); //Passing list of args to the plugin
};

PhoneGap.addConstructor(function() {
    PhoneGap.addPlugin("extTest", new ExtTest());
});