package teaching.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {

    List<Exercise> findByChapter(int chapter);
    Exercise findFirstByChapterAndNumber(int chapter, int number);

}
