package ReminderFeatures;

import DatabaseInteractions.DbConnection;
import DatabaseInteractions.DbFeatures;
import DatabaseInteractions.Reminder;
import Features.AppHome;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * AppModify
 * Class that manages the front and back of modify a task schedule
 *
 * @author @hassanpacary (Github)
 * @version 1.00
 */
public class AppModifyReminder extends JFrame {
    //Declaration of variables - do not modify: AUTOMATIC GENERATION
    private JButton btnHome;
    private JTextPane panelDescription;
    private JTextField tfTitle;
    private JRadioButton rbtnPriority;
    private JComboBox<String> cbTaskType;
    private JComboBox<String> cbReminderType;
    private JPanel panelLastSchedule;
    private JPanel panelNextSchedule;
    private JComboBox<String> cbReccurence;
    private JButton btnList;
    private JComboBox<String> cbEtatType;
    private JPanel panelModify;
    private JButton btnModify;
    private JLabel labelTeam;
    //End of variable declaration

    //Connected Team ID
    private final int teamID;

    //Reminder ID
    private final int reminderID;

    //Reminder to edit
    public Reminder reminder;

    //Gives access to the functions available in the class DbFeatures
    DbFeatures dbFeatures = new DbFeatures();

    //java calendar which allows user to choose a date graphically - Last schedule & Next schedule
    private final JDateChooser LastScheduleChooser = new JDateChooser();
    private final JDateChooser NextScheduleChooser = new JDateChooser();

    /**
     * AppModifyReminder form constructor
     * This function is called as soon as the appModifyReminder program starts
     *
     * @param reminderID - a reminder id to edit
     * @param teamID     - team connected
     */
    public AppModifyReminder(int reminderID, int teamID) {
        this.teamID = teamID;
        this.reminderID = reminderID;

        setContentPane(panelModify);
        setTitle("Team Reminder - Reminder Modifier");
        setSize(860, 540);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        formInitialization();

        //*******************//
        //     LISTENERS     //
        //*******************//

        //action performed on btnHome - Opens the home page window
        btnHome.addActionListener(e -> {
            AppHome appHome = new AppHome(teamID);
            dispose();
        });

        //action performed on btnLsit - Opens the task list page window
        btnList.addActionListener(e -> {
            AppListReminder appListReminder = new AppListReminder(teamID);
            dispose();
        });

        //action performed on btnModify - Modify the scheduled task in the database
        btnModify.addActionListener(e -> ModifyReminder());
    }

    /**
     * Function that initializes task schedule form data
     * Java calendars and comboBox which fetches data from application database
     */
    private void formInitialization() {
        String sql;

        //******************//
        //     GET DATA     //
        //******************//

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
            cbReccurence.addItem(reccurence);
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
            cbEtatType.addItem(state);
        }

        //***************************//
        //     GET REMINDER DATA     //
        //***************************//

        try {
            Connection con = DbConnection.dbConnector();

            if (con != null) {
                Statement statement = con.createStatement();

                //Initialise les valeurs de base de la tache
                ResultSet resultSet = statement.executeQuery("SELECT * FROM reminder " +
                        "WHERE id = '" + reminderID + "'");
                while (resultSet.next()) {
                    tfTitle.setText(resultSet.getString("name"));
                    panelDescription.setText(resultSet.getString("description"));
                    LastScheduleChooser.setDate(new SimpleDateFormat("yyyy-MM-dd")
                            .parse(resultSet.getString("lastScheldule")));
                    NextScheduleChooser.setDate(new SimpleDateFormat("yyyy-MM-dd")
                            .parse(resultSet.getString("nextScheldule")));
                    rbtnPriority.setSelected(resultSet.getBoolean("priority"));
                    cbReccurence.setSelectedIndex(resultSet.getInt("idReccurence") - 1);
                    cbTaskType.setSelectedIndex(resultSet.getInt("idTaskType") - 1);
                    cbReminderType.setSelectedIndex(resultSet.getInt("idReminderType") - 1);
                    cbEtatType.setSelectedIndex(resultSet.getInt("idEtat") - 1);


                }

                statement.close();
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves a reminder schedule
     */
    private void ModifyReminder() {
        String sql;

        //******************//
        //     GET DATA     //
        //******************//

        //Boolean indicating whether the task has priority or not
        boolean priority = rbtnPriority.isSelected();

        //Initialization of the IDs of the form according to what the user has chosen in the comboBoxes
        sql = "SELECT id FROM reccurencetype WHERE type = '";
        int recurrenceID = dbFeatures.cbGetID(cbReccurence, sql);

        sql = "SELECT id FROM tasktype WHERE type = '";
        int taskID = dbFeatures.cbGetID(cbTaskType, sql);

        sql = "SELECT id FROM remindertype WHERE type = '";
        int reminderID = dbFeatures.cbGetID(cbReminderType, sql);

        sql = "SELECT id FROM etattype WHERE type = '";
        int stateID = dbFeatures.cbGetID(cbEtatType, sql);

        //****************//
        //     CHECKS     //
        //****************//

        //Check if required fields are filled
        if (tfTitle.getText().isEmpty() || panelDescription.getText().isEmpty()
                || LastScheduleChooser.getDate() == null || NextScheduleChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this,
                    "Merci de remplir tout les champs obligatoire !", "Formulaire obselète",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Checks if the next scheduled date is greater than today's date
        if (NextScheduleChooser.getDate().toInstant().atZone(ZoneId.of("Europe/Paris")).toLocalDate()
                .isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "La date de programmation est incorrect !",
                    "Formulaire obselète", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //*********************//
        //     UPDATE DATA     //
        //*********************//

        String name = tfTitle.getText();
        String description = panelDescription.getText();
        java.sql.Date lastSchedule = java.sql.Date.valueOf(LastScheduleChooser.getDate().toInstant()
                .atZone(ZoneId.of("Europe/Paris")).toLocalDate().toString());
        java.sql.Date nextSchedule = java.sql.Date.valueOf(NextScheduleChooser.getDate().toInstant()
                .atZone(ZoneId.of("Europe/Paris")).toLocalDate().toString());

        reminder = ModifyReminderToDb(name, description, lastSchedule, nextSchedule, priority, recurrenceID, taskID, reminderID, stateID);

        if (reminder != null) {
            AppListReminder appListReminder = new AppListReminder(teamID);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Echec dans la modification de la tâche.\n" + "Veuillez recommencer !",
                    "Formulaire obselète", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Adds a DatabaseInteractions.Reminder to the application database and into DatabaseInteractions.Reminder class
     *
     * @param name           - Title of DatabaseInteractions.Reminder
     * @param description    - description of DatabaseInteractions.reminder
     * @param lastScheldule  - Last Schedule
     * @param nextScheldule  - Next Schedule
     * @param priority       - If it is priority
     * @param idReccurence   - Recurrence type
     * @param idTaskType     - Task type
     * @param idReminderType - Reminder type
     * @param idEtatType     - State type
     * @return a DatabaseInteractions.reminder
     */
    private Reminder ModifyReminderToDb(String name, String description, java.sql.Date lastScheldule,
                                        java.sql.Date nextScheldule, boolean priority, int idReccurence, int idTaskType,
                                        int idReminderType, int idEtatType) {
        Reminder reminder = null;

        try {
            Connection con = DbConnection.dbConnector();

            if (con != null) {
                Statement stm = con.createStatement();
                String sql = "UPDATE reminder " +
                        "SET name = ?, " +
                        "description = ?, " +
                        "lastScheldule = ?, " +
                        "nextScheldule = ?, " +
                        "priority = ?, " +
                        "idReccurence = ?, " +
                        "idTaskType = ?, " +
                        "idReminderType = ?, " +
                        "idEtat = ? " +
                        "WHERE id = '" + reminderID + "'";

                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, description);
                preparedStatement.setObject(3, lastScheldule);
                preparedStatement.setObject(4, nextScheldule);
                preparedStatement.setBoolean(5, priority);
                preparedStatement.setInt(6, idReccurence);
                preparedStatement.setInt(7, idTaskType);
                preparedStatement.setInt(8, idReminderType);
                preparedStatement.setInt(9, idEtatType);

                int addedRows = preparedStatement.executeUpdate();
                if (addedRows > 0) {
                    reminder = new Reminder();
                    reminder.setName(name);
                    reminder.setDescription(description);
                    reminder.setLastSchedule(lastScheldule);
                    reminder.setNextSchedule(nextScheldule);
                    reminder.setPriority(priority);
                    reminder.setIdRecurrence(idReccurence);
                    reminder.setIdTaskType(idTaskType);
                    reminder.setIdReminderType(idReminderType);
                    reminder.setIdStateType(idEtatType);
                }

                if (reminder != null) {
                    JOptionPane.showMessageDialog(this, "Tâche modifiée avec succès !",
                            "Tâche plannifiée", JOptionPane.INFORMATION_MESSAGE);
                }

                stm.close();
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reminder;
    }
}
