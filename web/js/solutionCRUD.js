
var solutionCRUD = {}; // globally available object

(function () {  // This is an IIFE, an Immediately Invoked Function Expression
    //alert("I am an IIFE!");

    solutionCRUD.startInsert = function () {

        ajax('htmlPartials/insertUpdateSolution.html', setInsertUI, 'content');

        function setInsertUI(httpRequest) {

            // Place the inserttUser html snippet into the content area.
            console.log("Ajax call was successful.");
            document.getElementById("content").innerHTML = httpRequest.responseText;

            document.getElementById("updateSaveSolutionButton").style.display = "none";
            document.getElementById("solutionIdRow").style.display = "none";

            ajax("webAPIs/getWebUserIdAPI.jsp", setRolePickList1, "userRoleIdError");
            ajax("webAPIs/getProblemIdAPI.jsp", setRolePickList2, "userRoleIdError");

            function setRolePickList1(httpRequest) {

                console.log("setRolePickList was called, see next line for object holding list of roles");
                var jsonObj = JSON.parse(httpRequest.responseText); // convert from JSON to JS Object.
                console.log(jsonObj);

                if (jsonObj.dbError.length > 0) {
                    document.getElementById("userRoleIdError").innerHTML = jsonObj.dbError;
                    return;
                }

                makePickList(jsonObj.webUserList, "webUserId","userEmail", "rolePickList1");


            }
            function setRolePickList2(httpRequest) {

                console.log("setRolePickList was called, see next line for object holding list of roles");
                var jsonObj = JSON.parse(httpRequest.responseText); // convert from JSON to JS Object.
                console.log(jsonObj);

                if (jsonObj.dbError.length > 0) {
                    document.getElementById("userRoleIdError").innerHTML = jsonObj.dbError;
                    return;
                }

               makePickList(jsonObj.problemList, "problem_id", "title", "rolePickList2");
            }
        }
    };

    function getSolutionDataFromUI() {

        var userId = document.getElementById("rolePickList1");
        var problemId = document.getElementById("rolePickList2");


        // create a solution object from the values that the user has typed into the page.
        var solutionInputObj = {
            "solution_id": document.getElementById("solutionId").value,
            "web_user_id": userId.options[userId.selectedIndex].value,
            "problem_id": problemId.options[problemId.selectedIndex].value,
            "viewer_rating": document.getElementById("viewerrating").value,
            "image_url": document.getElementById("imageurl").value,


            "errorMsg": ""
        };

        console.log(solutionInputObj);

        // build the url for the ajax call. Remember to escape the user input object or else 
        // you'll get a security error from the server. JSON.stringify converts the javaScript
        // object into JSON format (the reverse operation of what gson does on the server side).
        return escape(JSON.stringify(solutionInputObj));
    }

    function writeErrorObjToUI(jsonObj) {
        console.log("here is JSON object (holds error messages.");
        console.log(jsonObj);

        document.getElementById("imageUrlError").innerHTML = jsonObj.image_url;
        document.getElementById("viewerRatingError").innerHTML = jsonObj.viewer_rating;


        document.getElementById("recordError").innerHTML = jsonObj.errorMsg;
    }


    solutionCRUD.insertSave = function () {

        console.log("solutionCRUD.insertSave was called");

        var userId = document.getElementById("rolePickList1");
        var problemId = document.getElementById("rolePickList2");

        // create a user object from the values that the user has typed into the page.
        var solutionInputObj = {
            "solution_id": "",
            "web_user_id": userId.options[userId.selectedIndex].value,
            "problem_id": problemId.options[problemId.selectedIndex].value,
            "viewer_rating": document.getElementById("viewerrating").value,
            "image_url": document.getElementById("imageurl").value,

            "errorMsg": ""
        };
        console.log(solutionInputObj);

        // build the url for the ajax call. Remember to escape the user input object or else 
        // you'll get a security error from the server. JSON.stringify converts the javaScript
        // object into JSON format (the reverse operation of what gson does on the server side).
        var myData = escape(JSON.stringify(solutionInputObj));
        var url = "webAPIs/insertSolutionAPI.jsp?jsonData=" + myData;
        ajax(url, processInsert, "recordError");

        function processInsert(httpRequest) {
            console.log("processInsert was called here is httpRequest.");
            console.log(httpRequest);

            // the server prints out a JSON string of an object that holds field level error 
            // messages. The error message object (conveniently) has its fiels named exactly 
            // the same as the input data was named. 
            var jsonObj = JSON.parse(httpRequest.responseText); // convert from JSON to JS Object.
            console.log("here is JSON object (holds error messages.");
            console.log(jsonObj);

            document.getElementById("imageUrlError").innerHTML = jsonObj.image_url;
            document.getElementById("viewerRatingError").innerHTML = jsonObj.viewer_rating;


            if (jsonObj.errorMsg.length === 0) { // success
                jsonObj.errorMsg = "Record successfully inserted !!!";
            }
            document.getElementById("recordError").innerHTML = jsonObj.errorMsg;
        }
    };

    solutionCRUD.delete = function (solutionId, icon) {
        // clear out any old error msg (non-breaking space)
        document.getElementById("listMsg").innerHTML = "&nbsp;";

        if (confirm("Do you really want to delete user " + solutionId + "? ")) {

            // Calling the DELETE API. 
            var url = "webAPIs/deleteSolutionAPI.jsp?deleteId=" + solutionId;
            ajax(url, success, "listMsg");

            function success(http) { // API was successfully called (doesnt mean delete worked)
                var obj = JSON.parse(http.responseText);
                console.log("delete API called with success. next line has the object returned.");
                console.log(obj);
                if (obj.errorMsg.length > 0) {
                    document.getElementById("listMsg").innerHTML = obj.errorMsg;
                } else { // everything good, no error means record was deleted

                    // delete the <tr> (row) of the clicked icon from the HTML table
                    console.log("icon that was passed into JS function is printed on next line");
                    console.log(icon);

                    // icon's parent is cell whose parent is row 
                    var dataRow = icon.parentNode.parentNode;
                    var rowIndex = dataRow.rowIndex - 1; // adjust for column header row?
                    var dataTable = dataRow.parentNode;
                    dataTable.deleteRow(rowIndex);

                    document.getElementById("listMsg").innerHTML = "Solution " + solutionId + " deleted.";
                }
            }
        }
    };


    solutionCRUD.list = function () {

        document.getElementById("content").innerHTML = "";
        var dataList = document.createElement("div");
        dataList.id = "dataList"; // set the id so it matches CSS styling rule.
//        ajax('webAPIs/logOffAPI.jsp', checkLogOff, 'dataList');
//        function checkLogOff(httpRequest) {
//            var obj = JSON.parse(httpRequest.responseText);
//            if (obj !== null) {
//            dataList.innerHTML = "<h2>Solution <img src='icons/insert.png' onclick='solutionCRUD.startInsert();'/></h2>";
//            }
//        }
        dataList.innerHTML = "<h2>Solution <img src='icons/insert.png' onclick='solutionCRUD.startInsert();'/></h2>" +
                "<div id='listMsg'>&nbsp;</div>";
        document.getElementById("content").appendChild(dataList);

        ajax('webAPIs/listSolutionsAPI.jsp', setListUI, 'dataList');

        function setListUI(httpRequest) {

            console.log("starting SolutionCRUD.list (setListUI) with this httpRequest object (next line)");
            console.log(httpRequest);

            var obj = JSON.parse(httpRequest.responseText);

            if (obj === null) {
                dataList.innerHTML = "listSolutionResponse Error: JSON string evaluated to null.";
                return;
            }


            for (var i = 0; i < obj.solutionList.length; i++) {

                // add a property to each object in webUserList - a span tag that when clicked 
                // invokes a JS function call that passes in the web user id that should be deleted
                // from the database and a reference to itself (the span tag that was clicked)
                var id = obj.solutionList[i].solution_id;
                console.log("Solution ID = " + id);
                obj.solutionList[i].delete = "<img src='icons/delete.png'  onclick='solutionCRUD.delete(" + id + ",this)'  />";
                obj.solutionList[i].update = "<img onclick='solutionCRUD.startUpdate(" + id + ")' src='icons/update.png' />";
            }


            // buildTable Parameters: 
            // First:  array of objects that are to be built into an HTML table.
            // Second: string that is database error (if any) or empty string if all OK.
            // Third:  reference to DOM object where built table is to be stored. 
            buildTable(obj.solutionList, obj.dbError, dataList);
        }
    };
    
    // user has clicked on an update icon from the web_user list UI. 
    // inject the insert/update web_user UI into the content area and pre-fill 
    // that with the web_user data exracted from the database. 
    solutionCRUD.startUpdate = function (userId) {

        console.log("startUpdate");

        // make ajax call to get the insert/update user UI
        ajax('htmlPartials/insertUpdateSolution.html', setUpdateUI, "content");

        // place the insert/update user UI into the content area
        function setUpdateUI(httpRequest) {
            function setRolePickList1(httpRequest) {

                console.log("setRolePickList was called, see next line for object holding list of roles");
                var jsonObj = JSON.parse(httpRequest.responseText); // convert from JSON to JS Object.
                console.log(jsonObj);

                if (jsonObj.dbError.length > 0) {
                    document.getElementById("userRoleIdError").innerHTML = jsonObj.dbError;
                    return;
                }

               makePickList(jsonObj.webUserList, "webUserId","userEmail", "rolePickList1");


            }
            function setRolePickList2(httpRequest) {

                console.log("setRolePickList was called, see next line for object holding list of roles");
                var jsonObj = JSON.parse(httpRequest.responseText); // convert from JSON to JS Object.
                console.log(jsonObj);

                if (jsonObj.dbError.length > 0) {
                    document.getElementById("userRoleIdError").innerHTML = jsonObj.dbError;
                    return;
                }

                makePickList(jsonObj.problemList, "problem_id", "title", "rolePickList2");
            }
            console.log("Ajax call was successful.");
            document.getElementById("content").innerHTML = httpRequest.responseText;

            document.getElementById("insertSaveSolutionButton").style.display = "none";
            //document.getElementById("updateSaveUserButton").style.display = "inline";

            // Call the Get solution by id API and (if success), fill the UI with the User data
            ajax("webAPIs/getSolutionByIdAPI.jsp?id=" + userId, displaySolution, "recordError");

            function displaySolution(httpRequest) {
                ajax("webAPIs/getWebUserIdAPI.jsp", setRolePickList1, "userRoleIdError");
                ajax("webAPIs/getProblemIdAPI.jsp", setRolePickList2, "userRoleIdError");
                var obj = JSON.parse(httpRequest.responseText);

                if (obj.errorMsg.length > 0) {
                    document.getElementById("recordError").innerHTML = "Database error: " +
                            obj.errorMsg;
                } else if (obj.solution_id.length < 1) {
                    document.getElementById("recordError").innerHTML = "There is no user with id '" +
                            userId + "' in the database";
                } else {
                    makePickList2(obj.webUserList, "webUserId", "rolePickList1");
                    makePickList2(obj.problemList, "problem_id", "rolePickList2");
                    document.getElementById("solutionId").value = obj.solution_id;
                    document.getElementById("viewerrating").value = obj.viewer_rating;
                    document.getElementById("imageurl").value = obj.image_url;


                }
            }
        } // setUpdateUI
    };

    solutionCRUD.updateSave = function () {

        console.log("userCRUD.updateSave was called");
        var myData = getSolutionDataFromUI();
        var url = "webAPIs/updateSolutionAPI.jsp?jsonData=" + myData;
        ajax(url, processUpdate, "recordError");

        function processUpdate(httpRequest) {
            console.log("processUpdate was called here is httpRequest.");
            console.log(httpRequest);

            // the server prints out a JSON string of an object that holds field level error 
            // messages. The error message object (conveniently) has its fields named exactly 
            // the same as the input data was named. 
            var jsonObj = JSON.parse(httpRequest.responseText); // convert from JSON to JS Object.
            console.log("here is JSON object (holds error messages.");
            console.log(jsonObj);

            if (jsonObj.errorMsg.length === 0) { // success
                jsonObj.errorMsg = "Record successfully updated !!!";
            }

            writeErrorObjToUI(jsonObj);

        }
    };

}());  // the end of the IIFE