package teaching.model;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Integer> {

    default List<ChapterData> getChapterData(ExerciseRepository exercises) {
        return findAll(Sort.by("id").ascending())
                .stream()
                .map(c -> new ChapterData(c, exercises.findByChapter(c.getId()).size()))
                .collect(Collectors.toList());
    }

}
