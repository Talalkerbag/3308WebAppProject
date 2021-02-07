function logonFn(emailId, pwId, msgId) {
    
    var emailUserInput = escape(document.getElementById(emailId).value);
    var pwUserInput = escape(document.getElementById(pwId).value);
// escape cleans user input
    var URL = "webAPIs/logOnAPI.jsp?email=" + emailUserInput + "&password=" + pwUserInput;
    ajaxCall(URL, processLogon, processHttpError);
    function processLogon(httpRequest) {
// this function defined inside of logonFn, local to logonFn
        var msg = "Successfully called the logon API (check console log for object contents). ";
        var obj = JSON.parse(httpRequest.responseText);
        console.log("Successfully called the logon API. Next line shows the returned object.");
        console.log(obj);
        if (obj.webUserList[0].errorMsg.length > 0) {
            msg += "However, the Logon API supplied this error message: " + obj.errorMsg;
        } else {
            msg += "<br/>Welcome Web User number " + obj.webUserList[0].webUserId;
            msg += "<br/> &nbsp; with Birthday: " + obj.webUserList[0].birthday;
            msg += "<br/> &nbsp; and MembershipFee: " + obj.webUserList[0].membershipFee;
            msg += "<br/> &nbsp; and User Role Id: " + obj.webUserList[0].userRoleId;
            msg += "<br/> &nbsp; and User Role: " + obj.webUserList[0].userRoleType;
        }
        /* 
         I like to copy/paste the JSON code (below) so that I do not misspell any property name
         s (above):
         "webUserId": "110",
         "userEmail": "bri",
         "userPassword": "no",
         "birthday": "",
         "membershipFee": "",
         "userRoleId": "1",
         "userRoleType": "Admin",
         "errorMsg": ""     */
        document.getElementById(msgId).innerHTML = msg;
    }
    function processHttpError(httpRequest) {
// this fn is also private/local to logonFn, good coding style
        document.getElementById(msgId).innerHTML = "LogonAPIcall failed: " + httpRequest.errorMsg;
    }
}
