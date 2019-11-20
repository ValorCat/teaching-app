package teaching.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {

    List<Exercise> findByChapter(int chapter);
    Exercise findOneByChapterAndNumber(int chapter, int number);

    void deleteByChapterAndNumber(int chapter, int number);

    default void update(int chapter, int number, String newName, String newText, String newInitial) {
        Exercise exercise = findOneByChapterAndNumber(chapter, number);
        exercise.setName(newName);
        exercise.setText(newText);
        exercise.setInitial(newInitial);
        save(exercise);
    }

    default void create(int chapter, int number, String name, String text, String initial) {
        save(new Exercise(chapter, number, name, text, initial));
    }

    default int findMaxByChapter(int chapter) {
        return findByChapter(chapter).stream()
                .mapToInt(Exercise::getNumber)
                .max()
                .orElse(-1);
    }

}
