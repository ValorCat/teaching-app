package teaching.model;

import javax.persistence.*;

@Entity
@Table(name = "chapter", schema = "teaching-app")
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return String.format("Chapter(%d,%s)", id, name);
    }

}
