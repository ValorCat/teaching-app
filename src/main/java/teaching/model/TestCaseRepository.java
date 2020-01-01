package teaching.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.StringJoiner;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, String> {

    List<TestCase> findByChapterAndExercise(int chapter, int exercise);

    default String getJson(List<TestCase> tests) {
        StringJoiner joiner = new StringJoiner(",", "{", "}");
        tests.forEach(test -> joiner.add(test.getJson()));
        return joiner.toString();
    }

    default TestCase create(int chapter, int exercise) {
        return save(new TestCase(chapter, exercise));
    }

}
