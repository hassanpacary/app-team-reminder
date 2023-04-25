package Features;

import AdminFeatures.appListTeam;
import AdminFeatures.appModifyDB;
import DatabaseInteractions.DbConnection;
import DatabaseInteractions.DbFeatures;
import ReminderFeatures.AppAddReminder;
import ReminderFeatures.AppListReminder;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;

/**
 * AppHome
 * Class that manages the front and back of application home page
 *
 * @author @hassanpacary (Github)
 * @version 1.00
 */
public class AppHome extends JFrame {
    //Declaration of variables - do not modify: AUTOMATIC GENERATION
    private JPanel panelHome;
    private JButton btnAdd;
    private JButton btnList;
    private JButton btnDeconnect;
    private JList<String> listToday;
    private JTabbedPane tabbedPane1;
    private JButton btnAddTeam;
    private JButton btnModifyBdd;
    private JButton tacheAccomplieButton;
    private JList<String> listReport;
    private JPanel panelTaches;
    //End of variable declaration

    //Connected Team ID
    private final int teamID;

    //Admin team
    private final boolean isAdmin;

    // Name of the selected Reminder
    private String selectedReminder;

    //Gives access to the functions available in the class DbFeatures
    DbFeatures dbFeatures = new DbFeatures();

    /**
     * AppHome form constructor
     * This form is the home page of the application
     *
     * @param teamID - Connected Team ID
     */
    public AppHome(int teamID) {
        this.teamID = teamID;
        this.isAdmin = dbFeatures.isAdmin(teamID);

        setContentPane(panelHome);
        setTitle("Team Reminder - Home");
        setSize(860, 540);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        adminFeatures();
        changeState();
        displayReminderList();

        //*******************//
        //     LISTENERS     //
        //*******************//

        //action performed on btnAdd - Open the window to schedule a task
        btnAdd.addActionListener(e -> {
            AppAddReminder AppAddReminder = new AppAddReminder(teamID);
            dispose();
        });

        //action performed on btnList - Open the scheduled tasks window
        btnList.addActionListener(e -> {
            AppListReminder appListReminder = new AppListReminder(teamID);
            dispose();
        });

        //action performed on btnDeconnect - disconnect the connected team
        btnDeconnect.addActionListener(e -> {
            AppLogin appLogin = new AppLogin();
            dispose();
        });

        //action performed on btnAddTeam - Open the team list window
        btnAddTeam.addActionListener(e -> {
            appListTeam appListTeam = new appListTeam(teamID);
            dispose();
        });

        //action performed on btnModifyBdd - opens the window to add data to the database
        btnModifyBdd.addActionListener(e -> {
            appModifyDB appModifyDB = new appModifyDB(teamID);
            dispose();
        });

        //action performed on btnAccomplieButton - indicate that a selected task is completed
        tacheAccomplieButton.addActionListener(e -> completedTasks());

        //action performed on listToday - Displays the details of the reminder the user clicked on
        listToday.getSelectionModel().addListSelectionListener(e -> selectedReminder = listToday.getSelectedValue());

        //action performed on listReport - Displays the details of the reminder the user clicked on
        listReport.getSelectionModel().addListSelectionListener(e -> selectedReminder = listReport.getSelectedValue());
    }

    /**
     * Show admin team features
     * Only if the administration team is connected
     */
    private void adminFeatures() {
        if (isAdmin) {
            btnAddTeam.setVisible(true);
            btnModifyBdd.setVisible(true);
        }
    }

    /**
     * Modifies the status of a task if it has been indicated as completed
     */
    private void completedTasks() {

        //*********************//
        //     UPDATE DATA     //
        //*********************//

        try {
            Connection con = DbConnection.dbConnector();

            if (con != null) {
                PreparedStatement preparedStatement = con.prepareStatement("UPDATE reminder " +
                        "SET idetat = 3 " +
                        "WHERE name = ?");
                preparedStatement.setString(1, selectedReminder);
                preparedStatement.executeUpdate();

                preparedStatement.close();
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dbFeatures.changeRecurrenceState();
        displayReminderList();
    }

    /**
     * Displays the list of reminders
     */
    private void displayReminderList() {
        String sql;

        //******************//
        //     GET DATA     //
        //******************//

        if (isAdmin) {
            //Initialise listTodayReminder
            sql = "SELECT name FROM reminder WHERE nextscheldule = '" + LocalDate.now() + "' AND idetat != 3";
            listToday.setModel(dbFeatures.statementForList(sql));

            //Initialise listReport
            sql = "SELECT name FROM reminder WHERE idetat = 4";
            listReport.setModel(dbFeatures.statementForList(sql));

        } else {
            //Initialise listTodayReminder
            sql = "SELECT name FROM reminder WHERE nextscheldule = '" + LocalDate.now() + "' AND idteam = '" + teamID + "'";
            listToday.setModel(dbFeatures.statementForList(sql));

            //Initialise listReport
            sql = "SELECT name FROM reminder WHERE idetat = 4 AND idteam = '" + teamID + "'";
            listReport.setModel(dbFeatures.statementForList(sql));
        }

        if (listToday != null || listReport != null) {
            panelTaches.setVisible(true);
        }
    }

    /**
     * Changes the status of a task if it has not been done during the day
     */
    public void changeState() {

        //*********************//
        //     UPDATE DATA     //
        //*********************//

        try {
            Connection con = DbConnection.dbConnector();

            if (con != null) {
                PreparedStatement preparedStatement = con.prepareStatement("UPDATE reminder " +
                        "SET idEtat = ? " +
                        "WHERE idEtat != 3 AND idEtat != 4 AND nextscheldule < '" + LocalDate.now() + "'");
                preparedStatement.setInt(1, 4);
                preparedStatement.executeUpdate();

                preparedStatement.close();
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
