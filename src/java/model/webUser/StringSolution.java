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
public class StringSolution {

    public String solution_id = "";
    public String web_user_id = "";
    public String problem_id = "";
    public String image_url = "";
    public String viewer_rating = "";
    public String date_discovered = "";
    public String title = "";
    public String description = "";
    public String solution = "";
    public String userEmail = "";
    public String userPassword = "";
    public String membershipFee = "";
    public String firstName = "";
    public String lastName = "";
    public String dateCreated = "";
    public String company = "";
    public String birthday = "";
    

    public String errorMsg = "";


    // default constructor leaves all data members with empty string (Nothing null).
    public StringSolution() {
    }

    // overloaded constructor sets all data members by extracting from resultSet.
    public StringSolution(ResultSet results) {
        try {
            this.solution_id = FormatUtils.formatInteger(results.getObject("solution_id"));

            this.image_url = FormatUtils.formatString(results.getObject("image_url"));
            this.viewer_rating = FormatUtils.formatInteger(results.getObject("viewer_rating"));
            this.date_discovered = FormatUtils.formatDate(results.getObject("date_discovered"));
            this.title = FormatUtils.formatString(results.getObject("title"));
            this.description = FormatUtils.formatString(results.getObject("description"));
            this.solution = FormatUtils.formatString(results.getObject("solution"));
            this.userEmail = FormatUtils.formatString(results.getObject("user_email"));
            this.userPassword = FormatUtils.formatString(results.getObject("user_password"));
            this.membershipFee = FormatUtils.formatDollar(results.getObject("membership_fee"));
            this.birthday = FormatUtils.formatDate(results.getObject("birthday"));
            this.firstName = FormatUtils.formatString(results.getObject("first_name"));
            this.lastName = FormatUtils.formatString(results.getObject("last_name"));
            this.dateCreated = FormatUtils.formatDate(results.getObject("date_created"));
            this.company = FormatUtils.formatString(results.getObject("company"));
            
        } catch (Exception e) {
            this.errorMsg = "Exception thrown in model.webUser.SolutionProblem (the constructor that takes a ResultSet): " + e.getMessage();
        }
    }

    public int getCharacterCount() {
        String s = this.solution_id + this.userEmail + this.userPassword
                + this.membershipFee + this.image_url +this.solution + this.description + this.title + this.date_discovered
                + this.viewer_rating + this.birthday
                + this.firstName + this.lastName + this.dateCreated + this.company;
        return s.length();
    }

    public String toString() {
        return "Solution Id:" + this.solution_id
                + ", Image url: " + this.image_url
                + ", Viewer Rating: " + this.viewer_rating
                + ", User Email: " + this.userEmail
                + ", User Password: " + this.userPassword
                + ", Membership Fee: " + this.membershipFee
                + ", User Email: " + this.userEmail
                + ", User Password: " + this.userPassword
                + ", Birthday: " + this.birthday
                + ", Membership Fee: " + this.membershipFee
                + ", firstName: " + this.firstName
                + ", lastName: " + this.lastName
                + ", dateCreated: " + this.dateCreated
                + ", company: " + this.company;
    }
}