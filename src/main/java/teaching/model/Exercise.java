package teaching.model;

import javax.persistence.*;

@Entity
@IdClass(ExerciseId.class)
public class Exercise {

    @Id private int chapter;
    @Id private int id;
    private int number;
    private String name;
    private String text;
    private String initial;

    public Exercise() {}

    public Exercise(int chapter, int id, int number, String name, String text, String initial) {
        this.chapter = chapter;
        this.id = id;
        this.number = number;
        this.name = name;
        this.text = text;
        this.initial = initial;
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

    public String toString() {
        return String.format("Exercise(%d,%d)", chapter, id);
    }

}
