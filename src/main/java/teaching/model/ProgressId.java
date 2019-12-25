package teaching.model;

import java.io.Serializable;
import java.util.Objects;

public class ProgressId implements Serializable {

    private String account;
    private int chapter;
    private int exercise;

    public ProgressId() {}

    public ProgressId(String account, int chapter, int exercise) {
        this.account = account;
        this.chapter = chapter;
        this.exercise = exercise;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProgressId that = (ProgressId) o;
        return chapter == that.chapter &&
                exercise == that.exercise &&
                account.equals(that.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, chapter, exercise);
    }

}
