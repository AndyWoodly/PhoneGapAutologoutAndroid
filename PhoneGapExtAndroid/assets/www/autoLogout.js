var AutoLogout = function() {
};

AutoLogout.prototype.test = function(param, successCallback, failureCallback) {
	return PhoneGap.exec(
			successCallback,
			failureCallback,
			'AutoLogoutPlugin',
			'startAutoLogout',
			[ param ]);
};

PhoneGap.addConstructor(function() {
    PhoneGap.addPlugin("autoLogout", new AutoLogout());
});