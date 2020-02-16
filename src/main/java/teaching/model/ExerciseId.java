package teaching.model;

import java.io.Serializable;
import java.util.Objects;

public class ExerciseId implements Serializable {

    private int category, id;

    public ExerciseId() {}

    public ExerciseId(int category, int id) {
        this.category = category;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExerciseId that = (ExerciseId) o;
        return category == that.category &&
                id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, id);
    }

}
