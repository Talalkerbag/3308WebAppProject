
var problemCRUD = {}; // globally available object


(function () {  // This is an IIFE, an Immediately Invoked Function Expression
    //alert("I am an IIFE!");

    problemCRUD.startInsert = function () {

        ajax('htmlPartials/insertUpdateProblem.html', setInsertUI, 'content');

        function setInsertUI(httpRequest) {
            // Place the inserttUser html snippet into the content area.
            console.log("Ajax call was successful.");
            document.getElementById("content").innerHTML = httpRequest.responseText;
            document.getElementById("updateSaveProblemButton").style.display = "none";
            document.getElementById("problemIdRow").style.display = "none";
        }
    };

    function getProblemDataFromUI() {
        // create a user object from the values that the user has typed into the page.
        var problemInputObj = {
            "problem_id": document.getElementById("problemId").value,
            "date_discovered": document.getElementById("dateDiscovered").value,
            "title": document.getElementById("title").value,
            "description": document.getElementById("description").value,
            "solution": document.getElementById("solution").value,

            "errorMsg": ""
        };

        console.log(problemInputObj);
        // build the url for the ajax call. Remember to escape the user input object or else 
        // you'll get a security error from the server. JSON.stringify converts the javaScript
        // object into JSON format (the reverse operation of what gson does on the server side).
        return escape(JSON.stringify(problemInputObj));
    }

    function writeErrorObjToUI(jsonObj) {
        console.log("here is JSON object (holds error messages.");
        console.log(jsonObj);
        document.getElementById("titleError").innerHTML = jsonObj.title;
        document.getElementById("descriptionError").innerHTML = jsonObj.description;
        document.getElementById("solutionError").innerHTML = jsonObj.solution;
        document.getElementById("dateDiscoveredError").innerHTML = jsonObj.date_discovered;
        document.getElementById("recordError").innerHTML = jsonObj.errorMsg;
    }

    problemCRUD.insertSave = function () {
        console.log("problemCRUD.insertSave was called");
        // create a user object from the values that the user has typed into the page.
        var problemInputObj = {
            "problem_id": "",
            "date_discovered": document.getElementById("dateDiscovered").value,
            "title": document.getElementById("title").value,
            "description": document.getElementById("description").value,
            "solution": document.getElementById("solution").value,
            "errorMsg": ""
        };
        console.log(problemInputObj);
        // build the url for the ajax call. Remember to escape the user input object or else 
        // you'll get a security error from the server. JSON.stringify converts the javaScript
        // object into JSON format (the reverse operation of what gson does on the server side).
        var myData = escape(JSON.stringify(problemInputObj));
        var url = "webAPIs/insertProblemAPI.jsp?jsonData=" + myData;
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

            document.getElementById("titleError").innerHTML = jsonObj.title;
            document.getElementById("descriptionError").innerHTML = jsonObj.description;
            document.getElementById("solutionError").innerHTML = jsonObj.solution;
            document.getElementById("dateDiscoveredError").innerHTML = jsonObj.date_discovered;

            if (jsonObj.errorMsg.length === 0) { // success
                jsonObj.errorMsg = "Record successfully inserted !!!";
            }
            document.getElementById("recordError").innerHTML = jsonObj.errorMsg;
        }
    };

    problemCRUD.delete = function (problemId, icon) {
        // clear out any old error msg (non-breaking space)
        document.getElementById("listMsg").innerHTML = "&nbsp;";

        if (confirm("Do you really want to delete problem " + problemId + "? ")) {
            // Calling the DELETE API. 
            var url = "webAPIs/deleteProblemAPI.jsp?deleteId=" + problemId;
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

                    document.getElementById("listMsg").innerHTML = "problem " + problemId + " deleted.";
                }
            }
        }
    };


    problemCRUD.list = function () {

        document.getElementById("content").innerHTML = "";
        var dataList = document.createElement("div");
        dataList.id = "dataList"; // set the id so it matches CSS styling rule.
        dataList.innerHTML = "<h2>Problem <img src='icons/insert.png' onclick='problemCRUD.startInsert();'/></h2>" +
                "<div id='listMsg'>&nbsp;</div>";
        document.getElementById("content").appendChild(dataList);

        ajax('webAPIs/listProblemsAPI.jsp', setListUI, 'dataList');

        function setListUI(httpRequest) {

            console.log("starting userCRUD.list (setListUI) with this httpRequest object (next line)");
            console.log(httpRequest);

            var obj = JSON.parse(httpRequest.responseText);

            if (obj === null) {
                dataList.innerHTML = "listUsersResponse Error: JSON string evaluated to null.";
                return;
            }

            for (var i = 0; i < obj.problemList.length; i++) {

                // add a property to each object in webUserList - a span tag that when clicked 
                // invokes a JS function call that passes in the web user id that should be deleted
                // from the database and a reference to itself (the span tag that was clicked)
                var id = obj.problemList[i].problem_id;
                console.log("Problem ID = " + id);
                obj.problemList[i].delete = "<img src='icons/delete.png'  onclick='problemCRUD.delete(" + id + ",this)'  />";
                obj.problemList[i].update = "<img onclick='problemCRUD.startUpdate(" + id + ")' src='icons/update.png' />";

            }

            // buildTable Parameters: 
            // First:  array of objects that are to be built into an HTML table.
            // Second: string that is database error (if any) or empty string if all OK.
            // Third:  reference to DOM object where built table is to be stored. 
            buildTable(obj.problemList, obj.dbError, dataList);
        }
    };

    // user has clicked on an update icon from the web_user list UI. 
    // inject the insert/update web_user UI into the content area and pre-fill 
    // that with the web_user data exracted from the database. 
    problemCRUD.startUpdate = function (userId) {

        console.log("startUpdate");

        // make ajax call to get the insert/update user UI
        ajax('htmlPartials/insertUpdateProblem.html', setUpdateUI, "content");

        // place the insert/update user UI into the content area
        function setUpdateUI(httpRequest) {
            console.log("Ajax call was successful.");
            document.getElementById("content").innerHTML = httpRequest.responseText;

            document.getElementById("insertSaveProblemButton").style.display = "none";
            //document.getElementById("updateSaveUserButton").style.display = "inline";

            // Call the Get User by id API and (if success), fill the UI with the User data
            ajax("webAPIs/getProblemByIdAPI.jsp?id=" + userId, displayUser, "recordError");

            function displayUser(httpRequest) {
                var obj = JSON.parse(httpRequest.responseText);
                if (obj.errorMsg.length > 0) {
                    document.getElementById("recordError").innerHTML = "Database error: " +
                            obj.errorMsg;
                } else if (obj.problem_id.length < 1) {
                    document.getElementById("recordError").innerHTML = "There is no user with id '" +
                            userId + "' in the database";
                } else {

                    document.getElementById("problemId").value = obj.problem_id;
                    document.getElementById("dateDiscovered").value = obj.date_discovered;
                    document.getElementById("title").value = obj.title;
                    document.getElementById("description").value = obj.description;
                    document.getElementById("solution").value = obj.solution;

                }
            }
        } // setUpdateUI
    };

    problemCRUD.updateSave = function () {

        console.log("userCRUD.updateSave was called");
        var myData = getProblemDataFromUI();
        var url = "webAPIs/updateProblemAPI.jsp?jsonData=" + myData;
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