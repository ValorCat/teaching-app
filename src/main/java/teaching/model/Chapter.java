package teaching.model;

import teaching.Application;

import javax.persistence.*;

@Entity
@Table(name = "chapter", schema = Application.DB_SCHEMA)
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int number;
    private String name;

    public int getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return String.format("Chapter(%d,%s)", id, name);
    }

}
