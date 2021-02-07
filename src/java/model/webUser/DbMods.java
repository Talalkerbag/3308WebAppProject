package model.webUser;

import dbUtils.*;
import java.sql.PreparedStatement;

public class DbMods {

    /*
    Returns a "StringData" object that is full of field level validation
    error messages (or it is full of all empty strings if inputData
    totally passed validation.  
     */
    private static StringData validate(StringData inputData) {

        StringData errorMsgs = new StringData();

        /* Useful to copy field names from StringData as a reference
    public String webUserId = "";
    public String userEmail = "";
    public String userPassword = "";
    public String userPassword2 = "";
    public String birthday = "";
    public String membershipFee = "";
    public String userRoleId = "";   // Foreign Key
    public String userRoleType = ""; // getting it from joined user_role table.
         */
        // Validation
        errorMsgs.userEmail = ValidationUtils.stringValidationMsg(inputData.userEmail, 45, true);
        errorMsgs.userPassword = ValidationUtils.stringValidationMsg(inputData.userPassword, 45, true);

        if (inputData.userPassword.compareTo(inputData.userPassword2) != 0) { // case sensative comparison
            errorMsgs.userPassword2 = "Both passwords must match";
        }

        errorMsgs.birthday = ValidationUtils.dateValidationMsg(inputData.birthday, false);
        errorMsgs.membershipFee = ValidationUtils.decimalValidationMsg(inputData.membershipFee, false);
        errorMsgs.userRoleId = ValidationUtils.integerValidationMsg(inputData.userRoleId, true);

        return errorMsgs;
    } // validate 

    public static StringData insert(StringData inputData, DbConn dbc) {

        StringData errorMsgs = new StringData();
        errorMsgs = validate(inputData);
        if (errorMsgs.getCharacterCount() > 0) {  // at least one field has an error, don't go any further.
            errorMsgs.errorMsg = "Please try again";
            return errorMsgs;

        } else { // all fields passed validation

            /*
                       String sql = "SELECT web_user_id, user_email, user_password, membership_fee, birthday, "+
                    "web_user.user_role_id, user_role_type "+
                    "FROM web_user, user_role where web_user.user_role_id = user_role.user_role_id " + 
                    "ORDER BY web_user_id ";
             */
            // Start preparing SQL statement
            String sql = "INSERT INTO web_user (first_name, last_name, company, user_email, user_password, membership_fee, birthday, user_role_id) "
                    + "values (?,?,?,?,?,?,?,?)";

            // PrepStatement is Sally's wrapper class for java.sql.PreparedStatement
            // Only difference is that Sally's class takes care of encoding null 
            // when necessary. And it also System.out.prints exception error messages.
            PrepStatement pStatement = new PrepStatement(dbc, sql);

            // Encode string values into the prepared statement (wrapper class).
            pStatement.setString(1, inputData.firstName);
            pStatement.setString(2, inputData.lastName);
            pStatement.setString(3, inputData.company);
            pStatement.setString(4, inputData.userEmail); // string type is simple
            pStatement.setString(5, inputData.userPassword);
            pStatement.setBigDecimal(6, ValidationUtils.decimalConversion(inputData.membershipFee));
            pStatement.setDate(7, ValidationUtils.dateConversion(inputData.birthday));
            pStatement.setInt(8, ValidationUtils.integerConversion(inputData.userRoleId));

            // here the SQL statement is actually executed
            int numRows = pStatement.executeUpdate();

            // This will return empty string if all went well, else all error messages.
            errorMsgs.errorMsg = pStatement.getErrorMsg();
            if (errorMsgs.errorMsg.length() == 0) {
                if (numRows == 1) {
                    errorMsgs.errorMsg = ""; // This means SUCCESS. Let the user interface decide how to tell this to the user.
                } else {
                    // probably never get here unless you forgot your WHERE clause and did a bulk sql update.
                    errorMsgs.errorMsg = numRows + " records were inserted when exactly 1 was expected.";
                }
            } else if (errorMsgs.errorMsg.contains("foreign key")) {
                errorMsgs.errorMsg = "Invalid User Role Id";
            } else if (errorMsgs.errorMsg.contains("Duplicate entry")) {
                errorMsgs.errorMsg = "That email address is already taken";
            }

        } // customerId is not null and not empty string.
        return errorMsgs;
    } // insert

    public static String deleteUser(String userId, DbConn dbc) {

        if (userId == null) {
            return "Programmer error: cannot attempt to delete web_user record that matches null user id";
        }

        // This method assumes that the calling Web API (JSP page) has already confirmed 
        // that the database connection is OK. BUT if not, some reasonable exception should 
        // be thrown by the DB and passed back anyway... 
        String result = ""; // empty string result means the delete worked fine.
        try {

            String sql = "DELETE FROM web_user WHERE web_user_id = ?";

            // This line compiles the SQL statement (checking for syntax errors against your DB).
            PreparedStatement pStatement = dbc.getConn().prepareStatement(sql);

            // Encode user data into the prepared statement.
            pStatement.setString(1, userId);

            int numRowsDeleted = pStatement.executeUpdate();

            if (numRowsDeleted == 0) {
                result = "Programmer Error: did not delete the record with web_user_id " + userId;
            } else if (numRowsDeleted > 1) {
                result = "Programmer Error: > 1 record deleted. Did you forget the WHERE clause?";
            }

        } catch (Exception e) {
            result = "Exception thrown in model.webUser.DbMods.delete(): " + e.getMessage();
            if (result.contains("foreign key")) {
                result = "Cannot delete user " + userId + " because that user already posted reviews.";
            }
        }

        return result;
    }

    public static StringData updateUser(StringData inputData, DbConn dbc) {

        StringData errorMsgs = new StringData();
        errorMsgs = validate(inputData);
        if (errorMsgs.getCharacterCount() > 0) {  // at least one field has an error, don't go any further.
            errorMsgs.errorMsg = "Please try again";
            return errorMsgs;

        } else { // all fields passed validation

            /*
                String sql = "SELECT web_user_id, user_email, user_password, membership_fee, birthday, "+
                    "web_user.user_role_id, user_role_type "+
                    "FROM web_user, user_role where web_user.user_role_id = user_role.user_role_id " + 
                    "ORDER BY web_user_id ";
             */
            String sql = "UPDATE web_user SET user_email=?, user_password=?, membership_fee=?, birthday=?, "
                    + "user_role_id=? WHERE web_user_id = ?";

            // PrepStatement is Sally's wrapper class for java.sql.PreparedStatement
            // Only difference is that Sally's class takes care of encoding null 
            // when necessary. And it also System.out.prints exception error messages.
            PrepStatement pStatement = new PrepStatement(dbc, sql);

            // Encode string values into the prepared statement (wrapper class).
            pStatement.setString(1, inputData.userEmail); // string type is simple
            pStatement.setString(2, inputData.userPassword);
            pStatement.setBigDecimal(3, ValidationUtils.decimalConversion(inputData.membershipFee));
            pStatement.setDate(4, ValidationUtils.dateConversion(inputData.birthday));
            pStatement.setInt(5, ValidationUtils.integerConversion(inputData.userRoleId));
            pStatement.setInt(6, ValidationUtils.integerConversion(inputData.webUserId));

            // here the SQL statement is actually executed
            int numRows = pStatement.executeUpdate();

            // This will return empty string if all went well, else all error messages.
            errorMsgs.errorMsg = pStatement.getErrorMsg();
            if (errorMsgs.errorMsg.length() == 0) {
                if (numRows == 1) {
                    errorMsgs.errorMsg = ""; // This means SUCCESS. Let the user interface decide how to tell this to the user.
                } else {
                    // probably never get here unless you forgot your WHERE clause and did a bulk sql update.
                    errorMsgs.errorMsg = numRows + " records were updated (expected to update one record).";
                }
            } else if (errorMsgs.errorMsg.contains("foreign key")) {
                errorMsgs.errorMsg = "Invalid User Role Id";
            } else if (errorMsgs.errorMsg.contains("Duplicate entry")) {
                errorMsgs.errorMsg = "That email address is already taken";
            }

        } // customerId is not null and not empty string.
        return errorMsgs;
    } // updateUser

    private static StringDBSolution validateSolution(StringDBSolution inputData) {

        StringDBSolution errorMsgs = new StringDBSolution();

        // Validation
        errorMsgs.web_user_id = ValidationUtils.integerValidationMsg(inputData.web_user_id, true);
        errorMsgs.problem_id = ValidationUtils.integerValidationMsg(inputData.problem_id, true);
        errorMsgs.image_url = ValidationUtils.stringValidationMsg(inputData.image_url, 300, false);
        errorMsgs.viewer_rating = ValidationUtils.integerValidationMsg(inputData.viewer_rating, false);

        return errorMsgs;
    } // validate

    public static StringDBSolution insertSolution(StringDBSolution inputData, DbConn dbc) {

        StringDBSolution errorMsgs = new StringDBSolution();
        errorMsgs = validateSolution(inputData);
        if (errorMsgs.getCharacterCount() > 0) {  // at least one field has an error, don't go any further.
            errorMsgs.errorMsg = "Please try again";
            return errorMsgs;

        } else {
            // all fields passed validation
            // Start preparing SQL statement
            String sql = "INSERT INTO solution (web_user_id, problem_id, image_url, viewer_rating) "
                    + "values (?,?,?,?)";

            // PrepStatement is Sally's wrapper class for java.sql.PreparedStatement
            // Only difference is that Sally's class takes care of encoding null 
            // when necessary. And it also System.out.prints exception error messages.
            PrepStatement pStatement = new PrepStatement(dbc, sql);

            // Encode string values into the prepared statement (wrapper class).
            pStatement.setInt(1, ValidationUtils.integerConversion(inputData.web_user_id)); // string type is simple
            pStatement.setInt(2, ValidationUtils.integerConversion(inputData.problem_id));
            pStatement.setString(3, inputData.image_url);
            pStatement.setInt(4, ValidationUtils.integerConversion(inputData.viewer_rating));

            // here the SQL statement is actually executed
            int numRows = pStatement.executeUpdate();

            // This will return empty string if all went well, else all error messages.
            errorMsgs.errorMsg = pStatement.getErrorMsg();
            if (errorMsgs.errorMsg.length() == 0) {
                if (numRows == 1) {
                    errorMsgs.errorMsg = ""; // This means SUCCESS. Let the user interface decide how to tell this to the user.
                } else {
                    // probably never get here unless you forgot your WHERE clause and did a bulk sql update.
                    errorMsgs.errorMsg = numRows + " records were inserted when exactly 1 was expected.";
                }
            }
        }
        return errorMsgs;
    } // class

    public static String deleteSolution(String solutionId, DbConn dbc) {

        if (solutionId == null) {
            return "Programmer error: cannot attempt to delete web_user record that matches null user id";
        }

        // This method assumes that the calling Web API (JSP page) has already confirmed 
        // that the database connection is OK. BUT if not, some reasonable exception should 
        // be thrown by the DB and passed back anyway... 
        String result = ""; // empty string result means the delete worked fine.
        try {

            String sql = "DELETE FROM solution WHERE solution_id = ?";

            // This line compiles the SQL statement (checking for syntax errors against your DB).
            PreparedStatement pStatement = dbc.getConn().prepareStatement(sql);

            // Encode user data into the prepared statement.
            pStatement.setString(1, solutionId);

            int numRowsDeleted = pStatement.executeUpdate();

            if (numRowsDeleted == 0) {
                result = "Programmer Error: did not delete the record with solution_id " + solutionId;
            } else if (numRowsDeleted > 1) {
                result = "Programmer Error: > 1 record deleted. Did you forget the WHERE clause?";
            }

        } catch (Exception e) {
            result = "Exception thrown in model.webUser.DbMods.delete(): " + e.getMessage();
            if (result.contains("foreign key")) {
                result = "Cannot delete solution " + solutionId + " because that user already posted reviews.";
            }
        }

        return result;
    }

    public static StringDBSolution updateSolution(StringDBSolution inputData, DbConn dbc) {

        StringDBSolution errorMsgs = new StringDBSolution();
        errorMsgs = validateSolution(inputData);
        if (errorMsgs.getCharacterCount() > 0) {  // at least one field has an error, don't go any further.
            errorMsgs.errorMsg = "Please try again";
            return errorMsgs;

        } else { // all fields passed validation

            /*
            date_discovered, title, description, solution
             */
            String sql = "UPDATE solution SET web_user_id=?, problem_id=?, image_url=?, viewer_rating=? Where solution_id = ?";

            // PrepStatement is Sally's wrapper class for java.sql.PreparedStatement
            // Only difference is that Sally's class takes care of encoding null 
            // when necessary. And it also System.out.prints exception error messages.
            PrepStatement pStatement = new PrepStatement(dbc, sql);

            // Encode string values into the prepared statement (wrapper class).
            pStatement.setInt(1, ValidationUtils.integerConversion(inputData.web_user_id));
            pStatement.setInt(2, ValidationUtils.integerConversion(inputData.problem_id));
            pStatement.setString(3, inputData.image_url);
            pStatement.setInt(4, ValidationUtils.integerConversion(inputData.viewer_rating));
            pStatement.setInt(5, ValidationUtils.integerConversion(inputData.solution_id));

            // here the SQL statement is actually executed
            int numRows = pStatement.executeUpdate();

            // This will return empty string if all went well, else all error messages.
            errorMsgs.errorMsg = pStatement.getErrorMsg();
            if (errorMsgs.errorMsg.length() == 0) {
                if (numRows == 1) {
                    errorMsgs.errorMsg = ""; // This means SUCCESS. Let the user interface decide how to tell this to the user.
                } else {
                    // probably never get here unless you forgot your WHERE clause and did a bulk sql update.
                    errorMsgs.errorMsg = numRows + " records were updated (expected to update one record).";
                }
            } else if (errorMsgs.errorMsg.contains("Duplicate entry")) {
                errorMsgs.errorMsg = "That email address is already taken";
            }

        } // customerId is not null and not empty string.
        return errorMsgs;
    } // updateUser

    private static StringProblem validateProblem(StringProblem inputData) {

        StringProblem errorMsgs = new StringProblem();

        // Validation
        errorMsgs.date_discovered = ValidationUtils.dateValidationMsg(inputData.date_discovered, false);
        errorMsgs.title = ValidationUtils.stringValidationMsg(inputData.title, 45, true);
        errorMsgs.description = ValidationUtils.stringValidationMsg(inputData.description, 300, true);
        errorMsgs.solution = ValidationUtils.stringValidationMsg(inputData.solution, 300, false);

        return errorMsgs;
    } // validate

    public static StringProblem insertProblem(StringProblem inputData, DbConn dbc) {

        StringProblem errorMsgs = new StringProblem();
        errorMsgs = validateProblem(inputData);
        if (errorMsgs.getCharacterCount() > 0) {  // at least one field has an error, don't go any further.
            errorMsgs.errorMsg = "Please try again";
            return errorMsgs;

        } else {
            // all fields passed validation
            // Start preparing SQL statement
            String sql = "INSERT INTO problem (date_discovered, title, description, solution) "
                    + "values (?,?,?,?)";

            // PrepStatement is Sally's wrapper class for java.sql.PreparedStatement
            // Only difference is that Sally's class takes care of encoding null 
            // when necessary. And it also System.out.prints exception error messages.
            PrepStatement pStatement = new PrepStatement(dbc, sql);

            // Encode string values into the prepared statement (wrapper class).
            pStatement.setDate(1, ValidationUtils.dateConversion(inputData.date_discovered)); // string type is simple
            pStatement.setString(2, inputData.title);
            pStatement.setString(3, inputData.description);
            pStatement.setString(4, inputData.solution);

            // here the SQL statement is actually executed
            int numRows = pStatement.executeUpdate();

            // This will return empty string if all went well, else all error messages.
            errorMsgs.errorMsg = pStatement.getErrorMsg();
            if (errorMsgs.errorMsg.length() == 0) {
                if (numRows == 1) {
                    errorMsgs.errorMsg = ""; // This means SUCCESS. Let the user interface decide how to tell this to the user.
                } else {
                    // probably never get here unless you forgot your WHERE clause and did a bulk sql update.
                    errorMsgs.errorMsg = numRows + " records were inserted when exactly 1 was expected.";
                }
            } else if (errorMsgs.errorMsg.contains("Duplicate entry")) {
                errorMsgs.errorMsg = "This Web User is already connected to this Problem";
            }
        }
        return errorMsgs;
    }

    public static String deleteProblem(String problemId, DbConn dbc) {

        if (problemId == null) {
            return "Programmer error: cannot attempt to delete web_user record that matches null user id";
        }

        // This method assumes that the calling Web API (JSP page) has already confirmed 
        // that the database connection is OK. BUT if not, some reasonable exception should 
        // be thrown by the DB and passed back anyway... 
        String result = ""; // empty string result means the delete worked fine.
        try {

            String sql = "DELETE FROM problem WHERE problem_id = ?";

            // This line compiles the SQL statement (checking for syntax errors against your DB).
            PreparedStatement pStatement = dbc.getConn().prepareStatement(sql);

            // Encode user data into the prepared statement.
            pStatement.setString(1, problemId);

            int numRowsDeleted = pStatement.executeUpdate();

            if (numRowsDeleted == 0) {
                result = "Programmer Error: did not delete the record with solution_id " + problemId;
            } else if (numRowsDeleted > 1) {
                result = "Programmer Error: > 1 record deleted. Did you forget the WHERE clause?";
            }

        } catch (Exception e) {
            result = "Exception thrown in model.webUser.DbMods.delete(): " + e.getMessage();
            if (result.contains("foreign key")) {
                result = "Cannot delete problem " + problemId + " because that user already posted reviews.";
            }
        }

        return result;
    }

    public static StringProblem updateProblem(StringProblem inputData, DbConn dbc) {

        StringProblem errorMsgs = new StringProblem();
        errorMsgs = validateProblem(inputData);
        if (errorMsgs.getCharacterCount() > 0) {  // at least one field has an error, don't go any further.
            errorMsgs.errorMsg = "Please try again";
            return errorMsgs;

        } else { // all fields passed validation

            /*
            date_discovered, title, description, solution
             */
            String sql = "UPDATE problem SET date_discovered=?, title=?, description=?, solution=? Where problem_id = ?";

            // PrepStatement is Sally's wrapper class for java.sql.PreparedStatement
            // Only difference is that Sally's class takes care of encoding null 
            // when necessary. And it also System.out.prints exception error messages.
            PrepStatement pStatement = new PrepStatement(dbc, sql);

            // Encode string values into the prepared statement (wrapper class).
            pStatement.setDate(1, ValidationUtils.dateConversion(inputData.date_discovered)); // string type is simple
            pStatement.setString(2, inputData.title);
            pStatement.setString(3, inputData.description);
            pStatement.setString(4, inputData.solution);
            pStatement.setInt(5, ValidationUtils.integerConversion(inputData.problem_id));

            // here the SQL statement is actually executed
            int numRows = pStatement.executeUpdate();

            // This will return empty string if all went well, else all error messages.
            errorMsgs.errorMsg = pStatement.getErrorMsg();
            if (errorMsgs.errorMsg.length() == 0) {
                if (numRows == 1) {
                    errorMsgs.errorMsg = ""; // This means SUCCESS. Let the user interface decide how to tell this to the user.
                } else {
                    // probably never get here unless you forgot your WHERE clause and did a bulk sql update.
                    errorMsgs.errorMsg = numRows + " records were updated (expected to update one record).";
                }
            } else if (errorMsgs.errorMsg.contains("Duplicate entry")) {
                errorMsgs.errorMsg = "That email address is already taken";
            }

        } // customerId is not null and not empty string.
        return errorMsgs;
    } // updateUser

}
