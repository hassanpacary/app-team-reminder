package AdminFeatures;

import DatabaseInteractions.DbConnection;
import Features.AppHome;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * AppModifyDB
 * Class that manages the front and back of Modify the DB structure
 *
 * @author @hassanpacary (Github)
 * @version 1.00
 */
public class appModifyDB extends JFrame {
    //Declaration of variables - do not modify: AUTOMATIC GENERATION
    private JButton btnHome;
    private JTextField tfName;
    private JButton btnAdd;
    private JTabbedPane tabbedPane1;
    private JButton btnDeleteType;
    private JList<String> listTaskType;
    private JPanel panelModifyBdd;
    //End of variable declaration

    /**
     * AppModifyDB form constructor
     * This function is called as soon as the appModifyDB program starts
     *
     * @param teamID - Connected Team ID
     */
    public appModifyDB(int teamID) {

        setContentPane(panelModifyBdd);
        setTitle("Team Reminder - DB Modifier");
        setSize(860, 540);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        displayTypesList();

        //*******************//
        //     LISTENERS     //
        //*******************//

        //action performed on btnHome - Opens the home page window
        btnHome.addActionListener(e -> {
            AppHome appHome = new AppHome(teamID);
            dispose();
        });

        //action performed on btnAdd - add the new data to the database
        btnAdd.addActionListener(e -> {
            addType();
            displayTypesList();
        });

        //action performed on btnDelete - delete the data from the database
        btnDeleteType.addActionListener(e -> deleteType());
    }

    /**
     * displays all the different types that are stored in the database
     */
    private void displayTypesList() {

        //**********************//
        //     DISPLAY DATA     //
        //**********************//

        try {
            Connection con = DbConnection.dbConnector();

            if (con != null) {
                Statement stm = con.createStatement();

                ResultSet rsTask = stm.executeQuery("SELECT type FROM tasktype");
                DefaultListModel<String> listModel2 = new DefaultListModel<>();
                while (rsTask.next()) {
                    String name = rsTask.getString("type");
                    listModel2.addElement(name);
                }
                listTaskType.setModel(listModel2);

                stm.close();
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete selected item
     */
    private void deleteType() {
        String type = listTaskType.getSelectedValue();

        //*********************//
        //     DELETE DATA     //
        //*********************//

        try {
            Connection con = DbConnection.dbConnector();

            if (con != null) {
                PreparedStatement stm = con.prepareStatement("DELETE FROM tasktype WHERE type = ?");
                stm.setString(1, type);

                int confirm = JOptionPane.showConfirmDialog(this,
                        "voulez vous vraiment supprimé ce type ?",
                        "Suppression du type'",
                        JOptionPane.YES_NO_CANCEL_OPTION);

                if (confirm == 0) {
                    stm.executeUpdate();

                    JOptionPane.showMessageDialog(this,
                            "Type supprimé avec succès !",
                            "Type supprimé",
                            JOptionPane.INFORMATION_MESSAGE);
                }

                displayTypesList();

                stm.close();
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Register a new type in the database
     */
    private void addType() {
        String typeSel = "tasktype";
        String name = tfName.getText();
        boolean typeExist = false;

        //*********************//
        //     INSERT DATA     //
        //*********************//

        try {
            Connection con = DbConnection.dbConnector();

            if (con != null) {
                Statement stm = con.createStatement();

                String formTypeName = tfName.getText();
                String nameTmp = "";
                ResultSet rsTypeName = stm.executeQuery("SELECT type FROM " + typeSel + " WHERE type = '" +
                        formTypeName + "'");
                while (rsTypeName.next()) {
                    nameTmp = rsTypeName.getString("type");
                }
                if (nameTmp.equals(formTypeName)) {
                    typeExist = true;
                }

                stm.close();
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //****************//
        //     CHECKS     //
        //****************//

        //Check if required fields are filled
        if (tfName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Merci de remplir tout les champs obligatoire !",
                    "Formulaire obselète",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Checks if a data with the same name already exists
        if (typeExist) {
            JOptionPane.showMessageDialog(this,
                    "Cette donnée existe déjà dans la base de données!",
                    "Équipe existante",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        addTypeToDb(typeSel, name);
    }

    /**
     * Adds data to the application database
     *
     * @param typeSel - name of the table to modify
     * @param name    - data name
     */
    private void addTypeToDb(String typeSel, String name) {

        //*****************************//
        //     INSERT DATA INTO DB     //
        //*****************************//

        try {
            Connection con = DbConnection.dbConnector();

            if (con != null) {
                PreparedStatement stm = con.prepareStatement("INSERT INTO " + typeSel + " (type) VALUES (?)");
                stm.setString(1, name);
                stm.executeUpdate();

                JOptionPane.showMessageDialog(this,
                        "Donnée ajoutée avec succès à la base de données !",
                        "Donnée ajoutée",
                        JOptionPane.INFORMATION_MESSAGE);

                stm.close();
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
