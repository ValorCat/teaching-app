package teaching.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

@Entity
@Table(name = "test_case", schema = "teaching-app")
public class TestCase {

    @Id private int id;
    private int chapter;
    private int exercise;
    private String testExpression;
    @Transient private List<TestCaseElement> elements;

    public TestCase() {}

    public TestCase(int chapter, int exercise) {
        this.chapter = chapter;
        this.exercise = exercise;
    }

    public String getJson() {
        // method probably has a bug with inserting newlines (should they be escaped?)

        StringBuilder result = new StringBuilder("\"" + id + "\": {");
        if (testExpression != null) {
            result.append(String.format("\"test\": \"%s\", ", testExpression));
        }

        // sort elements into inputs and outputs
        List<TestCaseElement> inputs = new LinkedList<>();
        List<TestCaseElement> outputs = new LinkedList<>();
        elements.forEach(elem -> (elem.isOutput() ? outputs : inputs).add(elem));

        // add the inputs
        result.append("\"inputs\": {");
        for (TestCaseElement input : inputs) {
            result.append(String.format("\"%s\": \"%s\",", input.getLocation(), input.getContent()));
        }

        // add the outputs
        result.append("}, \"outputs\": {");
        for (TestCaseElement output : outputs) {
            result.append(String.format("\"%s\": \"%s\",", output.getLocation(), output.getContent()));
        }
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

    public List<TestCaseElement> getElements() {
        return elements;
    }

    public void setElements(List<TestCaseElement> elements) {
        this.elements = elements;
    }

}
