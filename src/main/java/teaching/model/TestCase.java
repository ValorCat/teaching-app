package teaching.model;

import teaching.Application;
import teaching.ClientCodeExecutor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Entity
@Table(name = "test_case", schema = Application.DB_SCHEMA)
public class TestCase {

    @Id private int id;
    private int chapter;
    private int exercise;
    private String testExpression;
    @Transient private List<TestCaseElement> inputs;
    @Transient private List<TestCaseElement> outputs;

    public TestCase() {}

    public TestCase(int chapter, int exercise) {
        this.chapter = chapter;
        this.exercise = exercise;
    }

    public String getJson() {
        // method probably has a bug with inserting newlines (should they be escaped?)

        StringBuilder result = new StringBuilder("\"" + id + "\": {");
        if (testExpression != null) {
            result.append(String.format("\"test\": \"%s\", ", ClientCodeExecutor.encode(testExpression)));
        }

        // add the inputs
        result.append("\"inputs\": {");
        StringJoiner joiner = new StringJoiner(",");
        for (TestCaseElement input : inputs) {
            joiner.add(input.getJson());
        }
        result.append(joiner.toString());

        // add the outputs
        result.append("}, \"outputs\": {");
        joiner = new StringJoiner(",");
        for (TestCaseElement output : outputs) {
            joiner.add(output.getJson());
        }
        result.append(joiner.toString());
        result.append("}}");
        return result.toString();
    }

    public int getId() {
        return id;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public int getExercise() {
        return exercise;
    }

    public void setExercise(int exercise) {
        this.exercise = exercise;
    }

    public String getTestExpression() {
        return testExpression;
    }

    public void setTestExpression(String testExpression) {
        this.testExpression = testExpression;
    }

    public void setElements(List<TestCaseElement> elements) {
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        elements.forEach(elem -> (elem.isOutput() ? outputs : inputs).add(elem));
    }

    public List<TestCaseElement> getInputs() {
        return inputs;
    }

    public List<TestCaseElement> getOutputs() {
        return outputs;
    }

}
