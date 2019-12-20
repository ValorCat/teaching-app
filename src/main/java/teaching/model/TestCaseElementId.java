package teaching.model;

import java.io.Serializable;
import java.util.Objects;

public class TestCaseElementId implements Serializable {

    private int testCaseId;
    private String location;
    private boolean output;

    public TestCaseElementId() {}

    public TestCaseElementId(int testCaseId, String location, boolean output) {
        this.testCaseId = testCaseId;
        this.location = location;
        this.output = output;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestCaseElementId that = (TestCaseElementId) o;
        return testCaseId == that.testCaseId &&
                output == that.output &&
                location.equals(that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(testCaseId, location, output);
    }

}
