package DatabaseInteractions;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * DbFeatures
 * Class which allows the optimization of certain connections to the database by avoiding redundancies in particular
 *
 * @author @hassanpacary (Github)
 * @version 1.00
 */
public class DbFeatures extends JFrame {

    /**
     * Retrieves data from the database to initialize a label
     *
     * @param sql         - SQL request
     * @param columnFocus - column in db
     * @return label
     */
    public String statementForLabel(String sql, String columnFocus) {
        String text = "";

        try {
            Connection con = DbConnection.dbConnector();

            if (con != null) {
                Statement statement = con.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);
                while (resultSet.next()) {
                    text = resultSet.getString(columnFocus);
                }

                resultSet.close();
                statement.close();
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return text;
    }

    /**
     * Retrieves data from the database to initialize a comboBox
     *
     * @param sql         - SQL request
     * @param columnFocus - column in db
     * @return label
     */
    public List<String> statementForComboBox(String sql, String columnFocus) {
        List<String> list = new ArrayList<>();

        try {
            Connection con = DbConnection.dbConnector();

            if (con != null) {
                Statement statement = con.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);
                while (resultSet.next()) {
                    list.add(resultSet.getString(columnFocus));
                }

                resultSet.close();
                statement.close();
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Get id from a combo box
     *
     * @param comboBox - ComboBox
     * @param sql      - SQL request
     * @return label
     */
    public int cbGetID(JComboBox<String> comboBox, String sql) {
        int ID = -1;
        String cbData = "";

        if (comboBox.getSelectedItem() != null) {
            cbData = comboBox.getSelectedItem().toString();
        }

        try {
            Connection con = DbConnection.dbConnector();

            if (con != null) {
                Statement statement = con.createStatement();
                ResultSet resultSet = statement.executeQuery(sql + cbData + "'");
                while (resultSet.next()) {
                    ID = resultSet.getInt("id");
                }

                resultSet.close();
                statement.close();
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ID;
    }

    /**
     * Get id from a text field
     *
     * @param textField - TextField
     * @param sql       - SQL request
     * @return label
     */
    public int tfGetID(JTextField textField, String sql) {
        int ID = -1;

        try {
            Connection con = DbConnection.dbConnector();

            if (con != null) {
                Statement statement = con.createStatement();
                ResultSet resultSet = statement.executeQuery(sql + textField.getText() + "'");
                while (resultSet.next()) {
                    ID = resultSet.getInt("id");
                }

                resultSet.close();
                statement.close();
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ID;
    }

    /**
     * Update the recurrence date of a task according to its recurrence ID
     */
    public void updateRecurrence(int ID, String date) {
        try {
            Connection con = DbConnection.dbConnector();

            if (con != null) {
                //Quotidien
                PreparedStatement stm = con.prepareStatement("UPDATE reminder " +
                        "SET lastScheldule = ?, " +
                        "nextScheldule = ?, " +
                        "idEtat = ? " +
                        "WHERE idEtat = 3 AND nextscheldule = '" + LocalDate.now() + "' AND idreccurence = " + ID);
                stm.setString(1, LocalDate.now().toString());
                stm.setString(2, date);
                stm.setInt(3, 1);
                stm.executeUpdate();

                stm.close();
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves data from the database to initialize a list
     *
     * @param sql - SQL request
     * @return DefaultListModel<String>
     */
    public DefaultListModel<String> statementForList(String sql) {
        DefaultListModel<String> listModel = new DefaultListModel<>();

        try {
            Connection con = DbConnection.dbConnector();

            if (con != null) {
                Statement statement = con.createStatement();

                //Initialise listTodayReminder
                ResultSet rsTodayReminderList = statement.executeQuery(sql);
                while (rsTodayReminderList.next()) {
                    String name = rsTodayReminderList.getString("name");
                    listModel.addElement(name);
                }

                statement.close();
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listModel;
    }

    /**
     * Changes the status and date of a task if it has a recurrence and the deadline is reached and it is done
     */
    public void changeRecurrenceState() {
        //Daily recurrence
        Calendar calQuotidien = Calendar.getInstance();
        calQuotidien.add(Calendar.DAY_OF_WEEK, 1);
        String daily = java.sql.Date.valueOf(calQuotidien.getTime().toInstant().atZone(ZoneId.of("Europe/Paris")).toLocalDate().toString()).toString();
        updateRecurrence(2, daily);

        //Weekly recurrence
        Calendar calHebdomadaire = Calendar.getInstance();
        calHebdomadaire.add(Calendar.WEEK_OF_YEAR, 1);
        String weekly = java.sql.Date.valueOf(calHebdomadaire.getTime().toInstant().atZone(ZoneId.of("Europe/Paris")).toLocalDate().toString()).toString();
        updateRecurrence(3, weekly);

        //Monthly recurrence
        Calendar calMensuel = Calendar.getInstance();
        calMensuel.add(Calendar.MONTH, 1);
        String monthly = java.sql.Date.valueOf(calMensuel.getTime().toInstant().atZone(ZoneId.of("Europe/Paris")).toLocalDate().toString()).toString();
        updateRecurrence(4, monthly);

        //Annual recurrence
        Calendar calAnnuel = Calendar.getInstance();
        calAnnuel.add(Calendar.YEAR, 1);
        String annual = java.sql.Date.valueOf(calAnnuel.getTime().toInstant().atZone(ZoneId.of("Europe/Paris")).toLocalDate().toString()).toString();
        updateRecurrence(5, annual);
    }

    /**
     * Show admin team features
     * Only if the administration team (direction) is connected
     */
    public boolean isAdmin(int teamID) {
        boolean isAdmin = false;

        try {
            Connection con = DbConnection.dbConnector();

            if (con != null) {
                PreparedStatement preparedStatement = con.prepareStatement("SELECT super_admin " +
                        "FROM team " +
                        "WHERE id = ?");
                preparedStatement.setInt(1, teamID);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    if (resultSet.getBoolean("super_admin")) {
                        isAdmin = true;
                    }
                }

                preparedStatement.close();
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isAdmin;
    }
}
