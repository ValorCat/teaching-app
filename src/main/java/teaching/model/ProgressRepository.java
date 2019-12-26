package teaching.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, ProgressId> {

    Optional<Progress> findByAccountAndChapterAndExercise(String account, int chapter, int exercise);
    List<Progress> findByAccountAndChapter(String account, int chapter);
    List<Progress> findByAccount(String account);

    default Map<Integer, Boolean> getCompletionIndex(String account, int chapter) {
        return findByAccountAndChapter(account, chapter)
                .stream()
                .collect(Collectors.toMap(Progress::getExercise, Progress::isComplete));
    }

    default Map<Integer, Integer> getChapterProgressIndex(String account) {
        List<Progress> progress = findByAccount(account);
        Map<Integer, Integer> index = new HashMap<>();
        for (Progress entry : progress) {
            if (entry.isComplete()) {
                index.merge(entry.getChapter(), 1, Integer::sum);
            }
        }
        return index;
    }

    default void updateProgress(String account, int chapter, int exercise, String code, boolean complete) {
        Optional<Progress> maybeEntry = findByAccountAndChapterAndExercise(account, chapter, exercise);
        if (maybeEntry.isPresent()) {
            Progress entry = maybeEntry.get();
            entry.setCode(code);
            entry.setComplete(complete);
            save(entry);
        } else {
            create(account, chapter, exercise, code, complete);
        }
    }

    default Progress create(String account, int chapter, int exercise, String code, boolean complete) {
        return save(new Progress(account, chapter, exercise, code, complete));
    }

}
