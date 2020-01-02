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
    @Id private int chapter;
    @Id private int exercise;
    private String code;
    private boolean complete;

    public Progress() {}

    public Progress(String account, int chapter, int exercise, String code, boolean complete) {
        this.account = account;
        this.chapter = chapter;
        this.exercise = exercise;
        this.code = code;
        this.complete = complete;
    }

    public String getAccount() {
        return account;
    }

    public int getChapter() {
        return chapter;
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
