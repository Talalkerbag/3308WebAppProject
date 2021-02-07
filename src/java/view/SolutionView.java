package view;

// classes imported from java.sql.*
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.webUser.*;

// classes in my project
import dbUtils.*;

public class SolutionView {

    public static StringSolutionList allUsersAPI(DbConn dbc) {

        //PreparedStatement stmt = null;
        //ResultSet results = null;
        StringSolutionList sdl = new StringSolutionList();
        try {
            String sql = "SELECT * FROM web_user, problem, solution "
                    + "WHERE web_user.web_user_id = solution.web_user_id and solution.problem_id = problem.problem_id ORDER by solution_id";  // you always want to order by something, not just random order.
            PreparedStatement stmt = dbc.getConn().prepareStatement(sql);
            ResultSet results = stmt.executeQuery();
            while (results.next()) {
                sdl.add(results);
            }
            results.close();
            stmt.close();
        } catch (Exception e) {
            StringSolution sd = new StringSolution();
            sd.errorMsg = "Exception thrown in problemView.allUsersAPI(): " + e.getMessage();
            sdl.add(sd);
        }
        return sdl;
    }

}