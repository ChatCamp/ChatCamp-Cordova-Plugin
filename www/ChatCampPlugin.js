var exec = require('cordova/exec');

exports.initChatcamp = function (arg0, success, error) {
    exec(success, error, 'ChatCampPlugin', 'initChatcamp', [arg0]);
};
exports.openConversation = function (arg0, success, error) {
   exec(success, error, 'ChatCampPlugin', 'openConversation', [arg0]);
};
