package teaching.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, ProgressId> {

    Optional<Progress> findByAccountAndChapterAndExercise(String account, int chapter, int exercise);

    default void updateProgress(String account, int chapter, int exercise, String code) {
        Optional<Progress> entry = findByAccountAndChapterAndExercise(account, chapter, exercise);
        if (entry.isPresent()) {
            entry.get().setCode(code);
            save(entry.get());
        } else {
            create(account, chapter, exercise, code);
        }
    }

    default void setComplete(String account, int chapter, int exercise) {
        Optional<Progress> entry = findByAccountAndChapterAndExercise(account, chapter, exercise);
        entry.ifPresent(progress -> progress.setComplete(true));
    }

    default Progress create(String account, int chapter, int exercise, String code) {
        return save(new Progress(account, chapter, exercise, code));
    }

}
