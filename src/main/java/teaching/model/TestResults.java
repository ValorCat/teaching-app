package teaching.model;

import java.util.List;

public class TestResults {

    private boolean allPass;
    private List<TestResult> results;

    private boolean hasError;
    private int errorLine, errorCol;
    private String errorMessage;

    public TestResults(List<TestResult> results, boolean allPass) {
        this.results = results;
        this.allPass = allPass;
    }

    public TestResults(int errorLine, int errorCol, String errorMessage) {
        this.hasError = true;
        this.errorLine = errorLine;
        this.errorCol = errorCol;
        this.errorMessage = errorMessage;
    }

    public TestResults(String errorMessage) {
        this.hasError = true;
        this.errorLine = 1;
        this.errorCol = 1;
        this.errorMessage = errorMessage;
    }

    public boolean doAllPass() {
        return allPass;
    }

    public List<TestResult> getResults() {
        return results;
    }

    public boolean hasError() {
        return hasError;
    }

    public int getErrorLine() {
        return errorLine;
    }

    public int getErrorCol() {
        return errorCol;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
