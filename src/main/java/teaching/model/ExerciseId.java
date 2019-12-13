package teaching.model;

import java.io.Serializable;
import java.util.Objects;

public class ExerciseId implements Serializable {

    private int chapter, id;

    public ExerciseId() {}

    public ExerciseId(int chapter, int id) {
        this.chapter = chapter;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExerciseId that = (ExerciseId) o;
        return chapter == that.chapter &&
                id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(chapter, id);
    }

}
