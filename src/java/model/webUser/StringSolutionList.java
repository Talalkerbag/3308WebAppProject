package model.webUser;

import java.util.ArrayList;
import java.sql.ResultSet;


// The purpose of this class is to have a nice java object that can be converted to JSON 
// to communicate everything necessary to the web page (the array of users plus a possible 
// list level database error message). 
public class StringSolutionList {

    public String dbError = "";
    public ArrayList<StringSolution> solutionList = new ArrayList();

    // Default constructor leaves StringDataList objects nicely set with properties 
    // indicating no database error and 0 elements in the list.
    public StringSolutionList() {
    }

    // Adds one StringData element to the array list of StringData elements
    public void add(StringSolution stringSolution) {
        this.solutionList.add(stringSolution);
    }

    // Adds creates a StringData element from a ResultSet (from SQL select statement), 
    // then adds that new element to the array list of StringData elements.
    public void add(ResultSet results) {
        StringSolution sd = new StringSolution(results);
        this.solutionList.add(sd);
    }
}