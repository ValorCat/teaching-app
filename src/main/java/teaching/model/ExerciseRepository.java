package teaching.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, ExerciseId> {

    List<Exercise> findByCategoryOrderByNumber(int category);
    Exercise findOneByCategoryAndNumber(int category, int number);

    void deleteByCategoryAndId(int category, int id);

    default void update(int category, int number, String newName, String newText, String newInitial, String newTests) {
        Exercise exercise = findOneByCategoryAndNumber(category, number);
        exercise.setName(newName);
        exercise.setText(newText);
        exercise.setInitial(newInitial);
        exercise.setTests(newTests);
        save(exercise);
    }

    default void create(int category, int id, int number, String name, String text, String initial, String tests) {
        save(new Exercise(category, id, number, name, text, initial, tests));
    }

    default int findMaxByCategory(int category) {
        return findByCategoryOrderByNumber(category).stream()
                .mapToInt(Exercise::getId)
                .max()
                .orElse(0);
    }

}
