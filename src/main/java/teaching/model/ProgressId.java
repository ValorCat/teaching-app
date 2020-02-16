package teaching.model;

import java.io.Serializable;
import java.util.Objects;

public class ProgressId implements Serializable {

    private String account;
    private int category;
    private int exercise;

    public ProgressId() {}

    public ProgressId(String account, int category, int exercise) {
        this.account = account;
        this.category = category;
        this.exercise = exercise;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProgressId that = (ProgressId) o;
        return category == that.category &&
                exercise == that.exercise &&
                account.equals(that.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, category, exercise);
    }

}
