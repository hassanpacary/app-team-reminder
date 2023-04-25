package DatabaseInteractions;

import java.sql.DriverManager;

/**
 * DbConnection
 * Connection with the application database
 *
 * @author @hassanpacary (Github)
 * @version 1.00
 */
public class DbConnection {
    private static final String url = "" /* YOUR LOCALHOST URL*/;
    private static final String user = "" /* YOUR LOCALHOST USERNAME*/;
    private static final String password = "" /* YOUR LOCALHOST PASSWORD*/;

    public static java.sql.Connection dbConnector() {
        try {

            //Driver used
            Class.forName("com.mysql.cj.jdbc.Driver");

            //Return connect to the database at the following address
            return DriverManager.getConnection(url, user, password);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
