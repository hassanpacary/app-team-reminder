package DatabaseInteractions;

/**
 * Team
 * Class that initializes a Team that will be added to the database.
 * Allows in particular to check if the Team added to the database is empty or not
 *
 * @author @hassanpacary (Github)
 * @version 1.00
 */
public class Team {
    private String name;
    private String codeAccess;
    private String email;
    private boolean admin;

    //START GETTERS AND SETTERS

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCodeAccess() {
        return codeAccess;
    }

    public void setCodeAccess(String codeAccess) {
        this.codeAccess = codeAccess;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
