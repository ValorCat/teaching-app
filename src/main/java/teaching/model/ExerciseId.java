package teaching.model;

import java.io.Serializable;
import java.util.Objects;

public class ExerciseId implements Serializable {

    private int chapter, number;

    public ExerciseId() {}

    public ExerciseId(int chapter, int number) {
        this.chapter = chapter;
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExerciseId that = (ExerciseId) o;
        return chapter == that.chapter &&
                number == that.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(chapter, number);
    }

}
