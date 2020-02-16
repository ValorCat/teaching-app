package teaching.model;

import teaching.Application;

import javax.persistence.*;

@Entity
@Table(name = "category", schema = Application.DB_SCHEMA)
public class Category {

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
        return String.format("Category(%d,%s)", id, name);
    }

}
