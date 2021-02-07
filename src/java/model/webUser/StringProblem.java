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
public class StringProblem {

    public String problem_id = "";
    public String date_discovered = "";
    public String title = "";
    public String description = "";
    public String solution = "";
    

    public String errorMsg = "";

    // default constructor leaves all data members with empty string (Nothing null).
    public StringProblem() {
    }

    // overloaded constructor sets all data members by extracting from resultSet.
    public StringProblem(ResultSet results) {
        try {
            this.problem_id = FormatUtils.formatInteger(results.getObject("problem_id"));
            this.date_discovered = FormatUtils.formatDate(results.getObject("date_discovered"));
            this.title = FormatUtils.formatString(results.getObject("title"));
            this.description = FormatUtils.formatString(results.getObject("description"));
            this.solution = FormatUtils.formatString(results.getObject("solution"));
            
        } catch (Exception e) {
            this.errorMsg = "Exception thrown in model.webUser.StringProblem (the constructor that takes a ResultSet): " + e.getMessage();
        }
    }

    public int getCharacterCount() {
        String s = this.problem_id + this.date_discovered + this.title + this.description
                + this.solution;
        return s.length();
    }

    public String toString() {
        return "problem_id:" + this.problem_id
                + ", date_discovered: " + this.date_discovered
                + ", title: " + this.title
                + ", description: " + this.description
                + ", solution: " + this.solution;
    }
}