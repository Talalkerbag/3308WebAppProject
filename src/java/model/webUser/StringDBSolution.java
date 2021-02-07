package model.webUser;

import dbUtils.FormatUtils;
import java.sql.ResultSet;


/* The purpose of this class is just to "bundle together" all the 
 * character data that the user might type in when they want to 
 * add a new Customer or edit an existing customer.  This String
 * data is "pre-validated" data, meaning they might have typed 
 * in a character string where a number was expected.
 * 
 * There are no getter or setter methods since we are not trying to
 * protect this data in any way.  We want to let the JSP page have
 * free access to put data in or take it out. */
public class StringDBSolution {

    public String solution_id = "";
    public String web_user_id = "";
    public String problem_id = "";
    public String image_url = "";
    public String viewer_rating = "";
    
    

    public String errorMsg = "";


    // default constructor leaves all data members with empty string (Nothing null).
    public StringDBSolution() {
    }

    // overloaded constructor sets all data members by extracting from resultSet.
    public StringDBSolution(ResultSet results) {
        try {
            this.solution_id = FormatUtils.formatInteger(results.getObject("solution_id"));
            this.web_user_id = FormatUtils.formatInteger(results.getObject("web_user_id"));
            this.problem_id = FormatUtils.formatInteger(results.getObject("problem_id"));
            this.image_url = FormatUtils.formatString(results.getObject("image_url"));
            this.viewer_rating = FormatUtils.formatInteger(results.getObject("viewer_rating"));
           
        } catch (Exception e) {
            this.errorMsg = "Exception thrown in model.webUser.SolutionProblem (the constructor that takes a ResultSet): " + e.getMessage();
        }
    }

    public int getCharacterCount() {
        String s = this.solution_id + this.web_user_id + this.problem_id + this.image_url;
        return s.length();
    }

    public String toString() {
        return "Solution Id:" + this.solution_id
                + ", Web User Id: " + this.web_user_id
                + ", Problem Id: " + this.problem_id
                + ", Image url: " + this.image_url
                + ", Viewer Rating: " + this.viewer_rating;
    }
}