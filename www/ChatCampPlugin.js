var exec = require('cordova/exec');

exports.init = function (arg0, success, error) {
    exec(success, error, 'ChatCampPlugin', 'init', [arg0]);
};
exports.createGroup = function (arg0, success, error) {
   exec(success, error, 'ChatCampPlugin', 'createGroup', [arg0]);
};
