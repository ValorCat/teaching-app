package teaching.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {

    List<Exercise> findByChapterOrderByNumber(int chapter);
    Exercise findOneByChapterAndNumber(int chapter, int number);

    void deleteByChapterAndId(int chapter, int id);

    default void update(int chapter, int number, String newName, String newText, String newInitial) {
        Exercise exercise = findOneByChapterAndNumber(chapter, number);
        exercise.setName(newName);
        exercise.setText(newText);
        exercise.setInitial(newInitial);
        save(exercise);
    }

    default void create(int chapter, int id, int number, String name, String text, String initial) {
        save(new Exercise(chapter, id, number, name, text, initial));
    }

    default int findMaxByChapter(int chapter) {
        return findByChapterOrderByNumber(chapter).stream()
                .mapToInt(Exercise::getId)
                .max()
                .orElse(0);
    }

}
