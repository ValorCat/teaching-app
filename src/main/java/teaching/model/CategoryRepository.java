package teaching.model;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    default List<CategoryData> getCategoryData(ExerciseRepository exercises) {
        return findAll(Sort.by("id").ascending())
                .stream()
                .sorted(Comparator.comparingInt(Category::getNumber))
                .map(c -> new CategoryData(c, exercises.findByCategoryOrderByNumber(c.getId()).size()))
                .collect(Collectors.toList());
    }

}
