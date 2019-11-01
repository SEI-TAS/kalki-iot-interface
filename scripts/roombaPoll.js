var dorita980 = require('dorita980');

var myRobotViaLocal = new dorita980.Local('3174410072111800',':1:1572535592:xZGONbFHFcKGAc7n',"10.27.151.122");


myRobotViaLocal.on('connect', init);

function init () {
    myRobotViaLocal.getWeek()
        .then((weekConfig) => {
        console.log("{schedule: ")
        console.log(weekConfig)
        console.log(",")
    myRobotViaLocal.getMission()
        .then((state) => {
        console.log("mission: ")
        console.log(state)
        console.log("}") })
    myRobotViaLocal.end()
    //myRobotViaLocal.clean()
})
.catch(console.log);
}