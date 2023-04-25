package ReminderFeatures;

import DatabaseInteractions.DbConnection;
import DatabaseInteractions.DbFeatures;
import Features.AppHome;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

/**
 * AppList
 * Class that manages the front and back of reminder list page
 *
 * @author @hassanpacary (Github)
 * @version 1.00
 */
public class AppListReminder extends JFrame {
    //Declaration of variables - do not modify: AUTOMATIC GENERATION
    private JButton btnHome;
    private JButton btnModify;
    private JButton btnDelete;
    private JList<String> listReminder;
    private JLabel labelTitle;
    private JLabel labelLastDate;
    private JLabel labelTask;
    private JLabel labelReminder;
    private JLabel labelNextDate;
    private JPanel panelList;
    private JPanel panelDescription;
    private JLabel labelTeam;
    private JLabel labelReccurence;
    private JLabel labelDescription;
    private JLabel labelPriority;
    private JList<String> listPriorityReminder;
    private JTabbedPane tabbedPane1;
    private JList<String> listTodayReminder;
    private JButton btnAdd;
    private JLabel labelEtat;
    private JPanel panelEtat;
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
     * AppList form constructor
     * This form is the task list page
     */
    public AppListReminder(int teamID) {
        this.teamID = teamID;
        this.isAdmin = dbFeatures.isAdmin(teamID);

        setContentPane(panelList);
        setTitle("Team Reminder - List Reminder");
        setSize(860, 540);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        dbFeatures.changeRecurrenceState();
        displayReminderList();

        //*******************//
        //     LISTENERS     //
        //*******************//

        //action performed on listReminder - Displays the details of the reminder the user clicked on
        listReminder.getSelectionModel().addListSelectionListener(e -> {
            selectedReminder = listReminder.getSelectedValue();
            displayElements();
        });

        //action performed on listPriorityReminder - Displays the details of the reminder the user clicked on
        listPriorityReminder.getSelectionModel().addListSelectionListener(e -> {
            selectedReminder = listPriorityReminder.getSelectedValue();
            displayElements();
        });

        //action performed on listTodayReminder - Displays the details of the reminder the user clicked on
        listTodayReminder.getSelectionModel().addListSelectionListener(e -> {
            selectedReminder = listTodayReminder.getSelectedValue();
            displayElements();
        });

        //action performed on btnDelete - Deletes the scheduled task in the database
        btnDelete.addActionListener(e -> deleteReminder());

        //action performed on btnAdd - Opens the page window for add reminder
        btnAdd.addActionListener(e -> {
            AppAddReminder AppAddReminder = new AppAddReminder(teamID);
            dispose();
        });

        //action performed on btnHome - Opens the home page window
        btnHome.addActionListener(e -> {
            AppHome appHome = new AppHome(teamID);
            dispose();
        });

        //action performed on btnModify - Modifies the scheduled task in the database
        btnModify.addActionListener(e -> {
            int ID = getIdReminder();

            if (ID != -1) {
                AppModifyReminder appModifyReminder = new AppModifyReminder(ID, teamID);
                dispose();
            }
        });
    }

    /**
     * Retrieve the ID of a selected task
     *
     * @return ID
     */
    private int getIdReminder() {
        int ID = -1;

        //*************************//
        //     GET ID REMINDER     //
        //*************************//

        try {
            Connection con = DbConnection.dbConnector();

            if (con != null) {
                Statement statement = con.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT id FROM reminder " +
                        "WHERE name = '" + selectedReminder + "'");
                while (resultSet.next()) {
                    ID = resultSet.getInt("id");
                }

                statement.close();
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ID;
    }

    /**
     * Displays the list of reminders
     */
    private void displayReminderList() {
        String rl;
        String pl;
        String tl;
        if (isAdmin) {
            rl = "SELECT name FROM reminder";
            pl = "SELECT name FROM reminder WHERE priority = true";
            tl = "SELECT name FROM reminder WHERE nextscheldule = '" + LocalDate.now() + "'";
        } else {
            rl = "SELECT name FROM reminder WHERE idTeam = " + teamID;
            pl = "SELECT name FROM reminder WHERE priority = true AND idteam = '" + teamID + "'";
            tl = "SELECT name FROM reminder WHERE nextscheldule = '" + LocalDate.now() + "' AND idteam = '" + teamID + "'";
        }

        //***************************//
        //     DISPLAY LIST DATA     //
        //***************************//

        //Initialise listReminder
        listReminder.setModel(dbFeatures.statementForList(rl));

        //Initialise listPriorityReminder
        listPriorityReminder.setModel(dbFeatures.statementForList(pl));

        //Initialise listTodayReminder
        listTodayReminder.setModel(dbFeatures.statementForList(tl));
    }

    /**
     * Displays the elements of the selected program on the side of the window
     */
    private void displayElements() {
        String sql;

        String name;
        String description;
        String lastScheldule;
        String nextScheldule;
        boolean priority;
        int idReccurence = -1;
        int idTaskType = -1;
        int idReminderType = -1;
        int idTeam = -1;
        int idEtatType = -1;

        //**********************//
        //     DISPLAY DATA     //
        //**********************//

        try {
            Connection con = DbConnection.dbConnector();

            if (con != null) {
                Statement stm = con.createStatement();

                //Initialise listReminder
                ResultSet rsReminder = stm.executeQuery("SELECT * FROM reminder " +
                        "WHERE name = '" + selectedReminder + "'");
                while (rsReminder.next()) {
                    name = rsReminder.getString("name");
                    description = rsReminder.getString("description");
                    lastScheldule = rsReminder.getString("lastScheldule");
                    nextScheldule = rsReminder.getString("nextScheldule");
                    priority = rsReminder.getBoolean("priority");
                    idReccurence = rsReminder.getInt("idReccurence");
                    idTaskType = rsReminder.getInt("idTaskType");
                    idReminderType = rsReminder.getInt("idReminderType");
                    idTeam = rsReminder.getInt("idTeam");
                    idEtatType = rsReminder.getInt("idEtat");

                    labelTitle.setText(name);
                    labelDescription.setText(description);
                    labelLastDate.setText(lastScheldule);
                    labelNextDate.setText(nextScheldule);

                    if (priority) {
                        labelPriority.setText("Prioritaire");
                    } else {
                        labelPriority.setText("Non prioritaire");
                    }
                }

                stm.close();
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        sql = "SELECT type FROM reccurencetype WHERE id = '" + idReccurence + "'";
        labelReccurence.setText(dbFeatures.statementForLabel(sql, "type"));

        sql = "SELECT type FROM tasktype WHERE id = '" + idTaskType + "'";
        labelTask.setText(dbFeatures.statementForLabel(sql, "type"));

        sql = "SELECT type FROM remindertype WHERE id = '" + idReminderType + "'";
        labelReminder.setText(dbFeatures.statementForLabel(sql, "type"));

        sql = "SELECT name FROM team WHERE id = '" + idTeam + "'";
        labelTeam.setText(dbFeatures.statementForLabel(sql, "name"));

        sql = "SELECT type FROM etattype WHERE id = '" + idEtatType + "'";
        labelEtat.setText(dbFeatures.statementForLabel(sql, "type"));

        //**********************//
        //     CHANGE COLOR     //
        //**********************//

        if (idEtatType == 1) {
            panelEtat.setBackground(new java.awt.Color(241, 107, 104));
        } else if (idEtatType == 2) {
            panelEtat.setBackground(new java.awt.Color(239, 207, 114));
        } else if (idEtatType == 3) {
            panelEtat.setBackground(new java.awt.Color(96, 160, 131));
        } else if (idEtatType == 4) {
            panelEtat.setBackground(new java.awt.Color(67, 116, 207));
        }
    }

    /**
     * Delete selected item
     */
    private void deleteReminder() {

        //*************************//
        //     DELETE REMINDER     //
        //*************************//

        try {
            Connection con = DbConnection.dbConnector();

            if (con != null) {
                PreparedStatement stm = con.prepareStatement("DELETE FROM reminder WHERE name = ?");
                stm.setString(1, selectedReminder);

                int confirm = JOptionPane.showConfirmDialog(this,
                        "voulez vous vraiment supprimé cette tâche ?",
                        "Suppression de tâche",
                        JOptionPane.YES_NO_CANCEL_OPTION);

                if (confirm == 0) {
                    stm.executeUpdate();

                    JOptionPane.showMessageDialog(this,
                            "Tâche supprimée avec succès !",
                            "Tâche supprimée",
                            JOptionPane.INFORMATION_MESSAGE);
                }

                displayReminderList();

                stm.close();
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        displayReminderList();
    }
}