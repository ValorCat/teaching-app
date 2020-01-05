package teaching.model;

import teaching.Application;
import teaching.ClientCodeExecutor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(TestCaseElementId.class)
@Table(name = "test_case_element", schema = Application.DB_SCHEMA)
public class TestCaseElement {

    @Id private int testCaseId;
    @Id private String location;
    @Id private boolean output;
    private String content;

    public TestCaseElement() {}

    public TestCaseElement(int testCaseId, String location, boolean output, String content) {
        this.testCaseId = testCaseId;
        this.location = location;
        this.output = output;
        this.content = content;
    }

    public String getJson() {
        return "\"" + location + "\": \"" + ClientCodeExecutor.encode(content) + "\"";
    }

    public int getTestCaseId() {
        return testCaseId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isOutput() {
        return output;
    }

    public void setOutput(boolean output) {
        this.output = output;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
