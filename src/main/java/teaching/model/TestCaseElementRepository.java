package teaching.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestCaseElementRepository extends JpaRepository<TestCaseElement, String> {

    List<TestCaseElement> findByTestCaseId(int testCaseId);

    default void addElements(TestCase testCase) {
        testCase.setElements(findByTestCaseId(testCase.getId()));
    }

    default TestCaseElement create(int testCaseId, String location, boolean output, String content) {
        return save(new TestCaseElement(testCaseId, location, output, content));
    }

}
