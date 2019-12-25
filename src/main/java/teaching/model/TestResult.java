package teaching.model;

public class TestResult {

    private final boolean pass;
    private final String test;

    public TestResult(boolean pass, String test) {
        this.pass = pass;
        this.test = test;
    }

    public boolean isPass() {
        return pass;
    }

    public String getTest() {
        return test;
    }

}
