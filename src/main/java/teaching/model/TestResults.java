package teaching.model;

import java.util.Collections;
import java.util.List;

public class TestResults {

    private boolean allPass;
    private List<TestResult> results;

    public TestResults(List<TestResult> results, boolean allPass) {
        this.results = results;
        this.allPass = allPass;
    }

    public TestResults() {
        this.results = Collections.emptyList();
        this.allPass = false;
    }

    public boolean doAllPass() {
        return allPass;
    }

    public List<TestResult> getResults() {
        return results;
    }

}
