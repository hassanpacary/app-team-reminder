package Features;

import DatabaseInteractions.DbConnection;

import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * AppLogin
 * Class that manages the front and back of the login page and retrieves the information of the connected team
 *
 * @author @hassanpacary (Github)
 * @version 1.00
 */
public class AppLogin extends JFrame {
    //Declaration of variables - do not modify: AUTOMATIC GENERATION
    private JPasswordField pwfPassword;
    private JButton btnConnect;
    private JPanel panelLogin;
    //End of variable declaration

    //Connected Team ID
    private int teamID = -1;

    /**
     * appLogin form constructor
     * This form is the login page
     */
    public AppLogin() {
        setContentPane(panelLogin);
        setTitle("Team Reminder - Login");
        setSize(860, 540);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        //*******************//
        //     LISTENERS     //
        //*******************//

        //action performed on btnConnect - log in the user if the conditions are met
        btnConnect.addActionListener(e -> getAccess());
    }

    /**
     * Retrieves the id of the user logging in
     */
    private void getAccess() {
        String pw = String.valueOf(pwfPassword.getPassword());

        try {
            Connection con = DbConnection.dbConnector();

            if (con != null) {
                Statement statement = con.createStatement();

                ResultSet rsPw = statement.executeQuery("SELECT id FROM team WHERE codeAcces = '" + pw + "'");
                while (rsPw.next()) {
                    teamID = rsPw.getInt("id");
                }

                statement.close();
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (teamID == -1) {
            JOptionPane.showMessageDialog(this,
                    "Code d'accès erroné !",
                    "Mauvais code d'accès",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            AppHome appHome = new AppHome(teamID);
            dispose();
        }
    }
}
