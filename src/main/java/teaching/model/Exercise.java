package teaching.model;

import javax.persistence.*;

@Entity
@Table(name = "exercise", schema = "teaching-app")
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int chapter;
    private int number;
    private String name;
    private String text;

    public int getId() {
        return id;
    }

    public int getChapter() {
        return chapter;
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

    public String toString() {
        return String.format("Exercise(%d,%d,%d)", id, chapter, number);
    }

}
