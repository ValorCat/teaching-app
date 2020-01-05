package teaching.model;

import teaching.Application;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(ExerciseId.class)
@Table(name = "exercise", schema = Application.DB_SCHEMA)
public class Exercise {

    @Id private int chapter;
    @Id private int id;
    private int number;
    private String name;
    private String text;
    private String initial;
    private String tests;

    public Exercise() {}

    public Exercise(int chapter, int id, int number, String name, String text, String initial, String tests) {
        this.chapter = chapter;
        this.id = id;
        this.number = number;
        this.name = name;
        this.text = text;
        this.initial = initial;
        this.tests = tests;
    }

    public int getChapter() {
        return chapter;
    }

    public int getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getInitial() {
        return initial;
    }

    public String getTests() {
        return tests;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public void setTests(String tests) {
        this.tests = tests;
    }

    public String toString() {
        return String.format("Exercise(%d,%d)", chapter, id);
    }

}
