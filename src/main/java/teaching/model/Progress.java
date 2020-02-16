package teaching.model;

import teaching.Application;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(ProgressId.class)
@Table(name = "progress", schema = Application.DB_SCHEMA)
public class Progress {

    @Id private String account;
    @Id private int category;
    @Id private int exercise;
    private String code;
    private boolean complete;

    public Progress() {}

    public Progress(String account, int category, int exercise, String code, boolean complete) {
        this.account = account;
        this.category = category;
        this.exercise = exercise;
        this.code = code;
        this.complete = complete;
    }

    public String getAccount() {
        return account;
    }

    public int getCategory() {
        return category;
    }

    public int getExercise() {
        return exercise;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

}
