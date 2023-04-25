package ReminderFeatures;

import DatabaseInteractions.DbFeatures;
import DatabaseInteractions.DbConnection;
import DatabaseInteractions.Reminder;
import Features.AppHome;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * AppAddReminder
 * Class that manages the front and back of adding a task schedule
 *
 * @author @hassanpacary (Github)
 * @version 1.00
 */
public class AppAddReminder extends JFrame {
    //Declaration of variables - do not modify: AUTOMATIC GENERATION
    private JPanel panelAdd;
    private JButton btnHome;
    private JButton btnAdd;
    private JTextPane panelDescription;
    private JTextField tfTitle;
    private JRadioButton rbtnPriority;
    private JComboBox<String> cbTaskType;
    private JComboBox<String> cbReminderType;
    private JPanel panelLastSchedule;
    private JPanel panelNextSchedule;
    private JComboBox<String> cbRecurrence;
    private JButton btnList;
    private JComboBox<String> cbStateType;
    private JLabel labelTeam;
    private JComboBox<String> cbSelectTeam;
    private JLabel labelSelectTeam;
    //End of variable declaration

    //Connected Team ID
    private final int teamID;

    //Admin team
    private final boolean isAdmin;

    //Gives access to the functions available in the class DbFeatures
    DbFeatures dbFeatures = new DbFeatures();

    //java calendar which allows user to choose a date graphically - Last schedule & Next schedule
    private final JDateChooser LastScheduleChooser = new JDateChooser();
    private final JDateChooser NextScheduleChooser = new JDateChooser();

    /**
     * AppAddReminder form constructor
     * This function is called as soon as the appAdd program starts
     *
     * @param teamID - Connected Team ID
     */
    public AppAddReminder(int teamID) {
        this.teamID = teamID;
        this.isAdmin = dbFeatures.isAdmin(teamID);

        setTitle("Team Reminder - Add reminder");
        setContentPane(panelAdd);
        setSize(860, 540);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        adminFeatures();
        formInitialization();

        //*******************//
        //     LISTENERS     //
        //*******************//

        //action performed on btnHome - Opens the home page window
        btnHome.addActionListener(e -> {
            AppHome appHome = new AppHome(teamID);
            dispose();
        });

        //action performed on btnList - Opens the List page window
        btnList.addActionListener(e -> {
            AppListReminder appListReminder = new AppListReminder(teamID);
            dispose();
        });

        //action performed on btnAdd - Add the task to be scheduled to the database
        btnAdd.addActionListener(e -> collectForm());
    }

    /**
     * Show admin team features
     * Only if the administration team is connected
     */
    private void adminFeatures() {
        if (isAdmin) {
            labelSelectTeam.setVisible(true);
            cbSelectTeam.setVisible(true);
        }
    }

    /**
     * Function that initializes task schedule form data
     * Java calendars and comboBox which fetches data from application database
     */
    private void formInitialization() {
        String sql;

        //************************//
        //     GET & SET DATA     //
        //************************//

        //Add java calendars to panel - Last schedule & Next schedule selection graphically
        panelLastSchedule.add(LastScheduleChooser);
        panelNextSchedule.add(NextScheduleChooser);

        //Initialize labelTeam
        sql = "SELECT name FROM team WHERE id = '" + teamID + "'";
        labelTeam.setText(dbFeatures.statementForLabel(sql, "name"));

        //Initialize cbRecurrence
        sql = "SELECT type FROM reccurencetype";
        List<String> listRecurrence = dbFeatures.statementForComboBox(sql, "type");
        for (String reccurence : listRecurrence) {
            cbRecurrence.addItem(reccurence);
        }

        //Initialize cbTaskType
        sql = "SELECT type FROM tasktype";
        List<String> listTask = dbFeatures.statementForComboBox(sql, "type");
        for (String task : listTask) {
            cbTaskType.addItem(task);
        }

        //Initialize cbReminderType
        sql = "SELECT type FROM remindertype";
        List<String> listReminder = dbFeatures.statementForComboBox(sql, "type");
        for (String reminder : listReminder) {
            cbReminderType.addItem(reminder);
        }

        //Initialize cbStateType
        sql = "SELECT type FROM etattype";
        List<String> listState = dbFeatures.statementForComboBox(sql, "type");
        for (String state : listState) {
            cbStateType.addItem(state);
        }

        //Initialize cbSelectTeam
        if (isAdmin) {
            sql = "SELECT name FROM team";
            List<String> listTeam = dbFeatures.statementForComboBox(sql, "name");
            for (String name : listTeam) {
                cbSelectTeam.addItem(name);
            }
        }
    }

    /**
     * saves the schedule of a task
     * retrieves the data entered by a user in the appAdd form, then saves them in the DatabaseInteractions.Reminder class
     */
    private void collectForm() {
        String sql;
        int selectedTeamID = -1;
        Reminder reminder;

        //******************//
        //     GET DATA     //
        //******************//

        //Boolean indicating whether the task has priority or not
        boolean priority = rbtnPriority.isSelected();

        //Initialization of the IDs of the form according to what the user has chosen in the comboBoxes
        sql = "SELECT id FROM reccurencetype WHERE type = '";
        int recurrenceID = dbFeatures.cbGetID(cbRecurrence, sql);

        sql = "SELECT id FROM tasktype WHERE type = '";
        int taskID = dbFeatures.cbGetID(cbTaskType, sql);

        sql = "SELECT id FROM remindertype WHERE type = '";
        int reminderID = dbFeatures.cbGetID(cbReminderType, sql);

        sql = "SELECT id FROM etattype WHERE type = '";
        int stateID = dbFeatures.cbGetID(cbStateType, sql);

        if (isAdmin) {
            sql = "SELECT id FROM team WHERE name = '";
            selectedTeamID = dbFeatures.cbGetID(cbSelectTeam, sql);
        }

        //boolean indicating whether the title is already in use or not
        boolean existingTitle = false;
        sql = "SELECT id FROM Reminder WHERE name = '";
        int titleID = dbFeatures.tfGetID(tfTitle, sql);

        if (titleID != -1) {
            existingTitle = true;
        }

        //****************//
        //     CHECKS     //
        //****************//

        //Check if required fields are filled
        if (tfTitle.getText().isEmpty() || panelDescription.getText().isEmpty() || LastScheduleChooser.getDate() == null
                || NextScheduleChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this,
                    "Merci de remplir tous les champs obligatoires !",
                    "Formulaire obsolète",
                    JOptionPane.ERROR_MESSAGE);

            return;
        }

        //Check if the nextScheduled date is later than today's date
        if (NextScheduleChooser.getDate().toInstant().atZone(ZoneId.of("Europe/Paris")).toLocalDate()
                .isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this,
                    "La date de programmation est incorrecte !",
                    "Formulaire obsolète",
                    JOptionPane.ERROR_MESSAGE);

            return;
        }

        //Check if a DatabaseInteractions.reminder with the same title already exists
        if (existingTitle) {
            JOptionPane.showMessageDialog(this,
                    "Une tâche programmée avec le même titre existe déjà !",
                    "titre obsolète",
                    JOptionPane.ERROR_MESSAGE);

            return;
        }

        if (isAdmin) {
            if (selectedTeamID == -1) {
                JOptionPane.showMessageDialog(this,
                        "Veuillez selectionner une équipe !",
                        "Équipe non selectionnée",
                        JOptionPane.ERROR_MESSAGE);

                return;
            }
        }

        //******************//
        //     GET DATA     //
        //******************//

        //initialize the rest of the form information after verification
        String title = tfTitle.getText();
        String description = panelDescription.getText();
        java.sql.Date lastSchedule = java.sql.Date.valueOf(LastScheduleChooser.getDate().toInstant()
                .atZone(ZoneId.of("Europe/Paris")).toLocalDate().toString());
        java.sql.Date nextSchedule = java.sql.Date.valueOf(NextScheduleChooser.getDate().toInstant()
                .atZone(ZoneId.of("Europe/Paris")).toLocalDate().toString());

        //Add all form information to reminder class
        if (isAdmin) {
            reminder = addReminderToDb(title, description, lastSchedule, nextSchedule, priority, recurrenceID, taskID,
                    reminderID, selectedTeamID, stateID);
        } else {
            reminder = addReminderToDb(title, description, lastSchedule, nextSchedule, priority, recurrenceID, taskID,
                    reminderID, this.teamID, stateID);
        }

        if (reminder != null) {
            AppHome appHome = new AppHome(teamID);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Échec dans la programmation de la tâche. Veuillez recommencer !",
                    "Formulaire obsolète",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Adds a DatabaseInteractions.Reminder to the application database and into DatabaseInteractions.Reminder class
     *
     * @param title        - Title of DatabaseInteractions.Reminder
     * @param description  - description of DatabaseInteractions.reminder
     * @param lastSchedule - Last Schedule
     * @param nextSchedule - Next Schedule
     * @param priority     - If it is priority
     * @param recurrenceID - Recurrence type
     * @param taskID       - Task type
     * @param reminderID   - Reminder type
     * @param teamID       - DatabaseInteractions.team
     * @param stateID      - State type
     * @return a DatabaseInteractions.reminder
     */
    private Reminder addReminderToDb(String title, String description, java.sql.Date lastSchedule, java.sql.Date nextSchedule,
                                     boolean priority, int recurrenceID, int taskID, int reminderID, int teamID,
                                     int stateID) {
        Reminder reminder = null;

        //******************//
        //     ADD DATA     //
        //******************//

        try {
            //class call that connects us to the database
            Connection con = DbConnection.dbConnector();

            if (con != null) {
                PreparedStatement preparedStatement;

                //inserting data into the database
                preparedStatement = con.prepareStatement("INSERT INTO reminder " +
                        "(name, description, lastScheldule, NextScheldule, priority, idReccurence, idTaskType, " +
                        "idReminderType, idTeam, idEtat) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, description);
                preparedStatement.setObject(3, lastSchedule);
                preparedStatement.setObject(4, nextSchedule);
                preparedStatement.setBoolean(5, priority);
                preparedStatement.setInt(6, recurrenceID);
                preparedStatement.setInt(7, taskID);
                preparedStatement.setInt(8, reminderID);
                preparedStatement.setInt(9, teamID);
                preparedStatement.setInt(10, stateID);

                //At the same time add the data in the DatabaseInteractions.reminder class
                int addedRows = preparedStatement.executeUpdate();
                if (addedRows > 0) {
                    reminder = new Reminder();
                    reminder.setName(title);
                    reminder.setDescription(description);
                    reminder.setLastSchedule(lastSchedule);
                    reminder.setNextSchedule(nextSchedule);
                    reminder.setPriority(priority);
                    reminder.setIdRecurrence(recurrenceID);
                    reminder.setIdTaskType(taskID);
                    reminder.setIdReminderType(reminderID);
                    reminder.setIdTeam(teamID);
                    reminder.setIdStateType(stateID);
                }

                if (reminder != null) {
                    JOptionPane.showMessageDialog(this,
                            "Tâche planifiée avec succès !",
                            "Tâche planifiée",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                preparedStatement.close();

                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reminder;
    }
}
