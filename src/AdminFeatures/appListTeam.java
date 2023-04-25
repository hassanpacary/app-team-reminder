package AdminFeatures;

import DatabaseInteractions.DbConnection;
import DatabaseInteractions.Team;
import Features.AppHome;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * AppAddTeam
 * page for adding a team to the database
 *
 * @author @hassanpacary (Github)
 * @version 1.00
 */
public class appListTeam extends JFrame {
    //Declaration of variables - do not modify: AUTOMATIC GENERATION
    private JButton btnHome;
    private JTextField tfName;
    private JButton btnAddTeam;
    private JList<String> listTeam;
    private JPasswordField pwfTeam;
    private JPasswordField pwfTeamVerification;
    private JPanel panelAddTeam;
    private JTextField tfEmail;
    private JButton btnDeleteTeam;
    private JLabel labelName;
    private JLabel labelCodeAccess;
    private JLabel labelEmail;
    private JRadioButton rbtnAdmin;
    private JLabel labelAdmin;
    //End of variable declaration

    /**
     * AppAddTeam form constructor
     * This function is called as soon as the appAddTeam program starts
     *
     * @param teamID - Connected Team ID
     */
    public appListTeam(int teamID) {
        setContentPane(panelAddTeam);
        setTitle("Team Reminder - Team List");
        setSize(860, 540);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        displayTeamList();

        //*******************//
        //     LISTENERS     //
        //*******************//

        //action performed on btnHome - Opens the home page window
        btnHome.addActionListener(e -> {
            AppHome appHome = new AppHome(teamID);
            dispose();
        });

        //action performed on btnAddTeam - add a team to the database
        btnAddTeam.addActionListener(e -> addTeam());

        //action performed on btnDeleteTeam - Delete a team
        btnDeleteTeam.addActionListener(e -> deleteTeam());

        //action performed on listReminder - Displays the details of the team the user clicked on
        listTeam.getSelectionModel().addListSelectionListener(e -> displayElements());
    }

    /**
     * Saves a team in the database
     */
    private void addTeam() {

        //******************//
        //     GET DATA     //
        //******************//

        String name = tfName.getText();
        String mail = tfEmail.getText();
        String codeAcces = String.valueOf(pwfTeam.getPassword());
        String codeAccesVerif = String.valueOf(pwfTeamVerification.getPassword());
        Boolean admin = rbtnAdmin.isSelected();

        boolean teamExist = false;

        try {
            Connection con = DbConnection.dbConnector();

            if (con != null) {
                Statement stm = con.createStatement();

                //****************//
                //     CHECKS     //
                //****************//

                //Initializes the boolean that indicates if the name is already in use
                String formReminderTitle = tfName.getText();
                String nameTmp = "";
                ResultSet rsTeamName = stm.executeQuery("SELECT name FROM team WHERE name = '" + formReminderTitle + "'");
                while (rsTeamName.next()) {
                    nameTmp = rsTeamName.getString("name");
                }
                if (nameTmp.equals(formReminderTitle)) {
                    teamExist = true;
                }

                //Initializes the boolean indicating if the access code is already in use
                String formcodeAcces = String.valueOf(pwfTeam.getPassword());
                ResultSet rsTeamPw = stm.executeQuery("SELECT codeacces FROM team WHERE codeacces = '" + formcodeAcces + "'");
                while (rsTeamPw.next()) {
                    rsTeamPw.getString("codeacces");
                }

                stm.close();
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Check if required fields are filled
        if (tfName.getText().isEmpty() || tfEmail.getText().isEmpty() || String.valueOf(pwfTeam.getPassword()).isEmpty()
                || String.valueOf(pwfTeamVerification.getPassword()).isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Merci de remplir tout les champs obligatoire !",
                    "Formulaire obselète",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!codeAcces.equals(codeAccesVerif)) {
            JOptionPane.showMessageDialog(this,
                    "Les champs du code d'accès ne sont pas identique !",
                    "Code d'accès erroné",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Checks if a team with the same name already exists
        if (teamExist) {
            JOptionPane.showMessageDialog(this,
                    "Cette équipe existe déjà !",
                    "Équipe existante",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        //*********************//
        //     INSERT DATA     //
        //*********************//

        //Call of the team class in which we reset the team to add to the database
        Team team = addTeamToDb(name, mail, codeAcces, admin);
        if (team != null) {
            displayTeamList();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Echec dans l'ajout de l'équipe à la base de données.\n" +
                            "Veuillez recommencer !",
                    "Formulaire obselète",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Adds a team to the application database
     *
     * @param name      - team name
     * @param mail      - team email
     * @param codeAcces - team access code
     * @param admin     - is Admin
     * @return team from team class
     */
    private Team addTeamToDb(String name, String mail, String codeAcces, boolean admin) {
        Team team = null;

        try {
            Connection con = DbConnection.dbConnector();

            //*********************//
            //     INSERT DATA     //
            //*********************//

            if (con != null) {
                Statement stm = con.createStatement();
                String sql = "INSERT INTO team (name, codeacces, email, super_admin) VALUES (?, ?, ?, ?)";
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, codeAcces);
                preparedStatement.setObject(3, mail);
                preparedStatement.setObject(4, admin);

                int addedRows = preparedStatement.executeUpdate();
                if (addedRows > 0) {
                    team = new Team();
                    team.setName(name);
                    team.setCodeAccess(codeAcces);
                    team.setEmail(mail);
                    team.setAdmin(admin);
                }

                if (team != null) {
                    JOptionPane.showMessageDialog(this,
                            "Équipe ajoutée avec succès à la base de données !",
                            "Équipe ajoutée",
                            JOptionPane.INFORMATION_MESSAGE);
                }

                stm.close();
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return team;
    }

    /**
     * Displays the list of teams
     */
    private void displayTeamList() {

        //**********************//
        //     DISPLAY DATA     //
        //**********************//

        try {
            Connection con = DbConnection.dbConnector();

            if (con != null) {
                Statement stm = con.createStatement();

                ResultSet rsTeamList = stm.executeQuery("SELECT name FROM team");
                DefaultListModel<String> listModel = new DefaultListModel<>();
                while (rsTeamList.next()) {
                    String name = rsTeamList.getString("name");
                    listModel.addElement(name);
                }
                listTeam.setModel(listModel);

                stm.close();
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays the elements of the selected program on the side of the window
     */
    private void displayElements() {
        String teamName = listTeam.getSelectedValue();

        String name;
        String mail;
        String codeAcces;
        boolean admin;

        //**********************//
        //     DISPLAY DATA     //
        //**********************//

        try {
            Connection con = DbConnection.dbConnector();

            if (con != null) {
                Statement stm = con.createStatement();

                //Initialise listReminder
                ResultSet rs = stm.executeQuery("SELECT * FROM team WHERE name = '" + teamName + "'");
                while (rs.next()) {
                    name = rs.getString("name");
                    mail = rs.getString("email");
                    codeAcces = rs.getString("codeacces");
                    admin = rs.getBoolean("super_admin");

                    labelName.setText(name);
                    labelEmail.setText(mail);
                    labelCodeAccess.setText(codeAcces);

                    if (admin) {
                        labelAdmin.setText("Oui");
                    } else {
                        labelAdmin.setText("Non");
                    }
                }

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
    private void deleteTeam() {
        String team = listTeam.getSelectedValue();

        //*********************//
        //     DELETE DATA     //
        //*********************//

        if (!team.equals("Direction")) {
            try {
                Connection con = DbConnection.dbConnector();

                if (con != null) {
                    PreparedStatement stm = con.prepareStatement("DELETE FROM team WHERE name = ?");
                    stm.setString(1, team);


                    int confirm = JOptionPane.showConfirmDialog(this,
                            "voulez vous vraiment supprimé cette équipe ?",
                            "Suppression d'une équipe'",
                            JOptionPane.YES_NO_CANCEL_OPTION);

                    if (confirm == 0) {
                        stm.executeUpdate();

                        JOptionPane.showMessageDialog(this,
                                "Équipe supprimée avec succès !",
                                "Équipe supprimée",
                                JOptionPane.INFORMATION_MESSAGE);
                    }

                    displayTeamList();

                    stm.close();
                    con.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Vous ne pouvez pas supprimer cette équipe !",
                    "Suppression annulée",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
