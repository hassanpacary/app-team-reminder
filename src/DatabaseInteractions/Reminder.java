package DatabaseInteractions;

import java.sql.Date;

/**
 * Reminder
 * Class that initializes a DatabaseInteractions.reminder that will be added to the database.
 * Allows in particular to check if the DatabaseInteractions.reminder added to the database is empty or not
 *
 * @author @hassanpacary (Github)
 * @version 1.00
 */
public class Reminder {
    private String name;
    private String description;
    private java.sql.Date lastSchedule;
    private java.sql.Date nextSchedule;
    private boolean priority;
    private int idRecurrence;
    private int idTaskType;
    private int idReminderType;
    private int idTeam;
    private int idStateType;


    //GETTERS AND SETTERS

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getLastSchedule() {
        return lastSchedule;
    }

    public void setLastSchedule(Date lastSchedule) {
        this.lastSchedule = lastSchedule;
    }

    public Date getNextSchedule() {
        return nextSchedule;
    }

    public void setNextSchedule(Date nextSchedule) {
        this.nextSchedule = nextSchedule;
    }

    public boolean isPriority() {
        return priority;
    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }

    public int getIdRecurrence() {
        return idRecurrence;
    }

    public void setIdRecurrence(int idRecurrence) {
        this.idRecurrence = idRecurrence;
    }

    public int getIdTaskType() {
        return idTaskType;
    }

    public void setIdTaskType(int idTaskType) {
        this.idTaskType = idTaskType;
    }

    public int getIdReminderType() {
        return idReminderType;
    }

    public void setIdReminderType(int idReminderType) {
        this.idReminderType = idReminderType;
    }

    public int getIdTeam() {
        return idTeam;
    }

    public void setIdTeam(int idTeam) {
        this.idTeam = idTeam;
    }

    public int getIdStateType() {
        return idStateType;
    }

    public void setIdStateType(int idStateType) {
        this.idStateType = idStateType;
    }
}
