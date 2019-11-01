var dorita980 = require('dorita980');

var myRobotViaLocal = new dorita980.Local('3174410072111800',':1:1572535592:xZGONbFHFcKGAc7n',"10.27.151.122");


myRobotViaLocal.on('connect', init);

function init () {
    myRobotViaLocal.start()
        .then(() => myRobotViaLocal.end()) // disconnect to leave free the channel for the mobile app.
.catch(console.log);
}