package teaching.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {

    List<Exercise> findByChapter(int chapter);
    Exercise findOneByChapterAndNumber(int chapter, int number);

    default void update(int chapter, int number, String newName, String newText, String newInitial) {
        Exercise exercise = findOneByChapterAndNumber(chapter, number);
        exercise.setName(newName);
        exercise.setText(newText);
        exercise.setInitial(newInitial);
        save(exercise);
    }

}
