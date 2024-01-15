package console;

import java.io.Serializable;
import java.sql.Timestamp;

public class Doc implements Serializable {
    private String ID;
    private String creator;
    private Timestamp Timestamp;
    private String description;
    private String filename;

    public Doc(String ID, String creator, Timestamp timestamp, String description, String filename) {
        super();
        this.ID = ID;
        this.creator = creator;
        Timestamp = timestamp;
        this.description = description;
        this.filename = filename;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public java.sql.Timestamp getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(java.sql.Timestamp timestamp) {
        Timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String toString() {
        return "filename:" + filename + '\t' +
                "ID:" + ID + "\t\t" +
                "creator:" + creator + '\t' +
                "Timestamp:" + Timestamp + '\t' +
                "description:" + description;
    }
}
