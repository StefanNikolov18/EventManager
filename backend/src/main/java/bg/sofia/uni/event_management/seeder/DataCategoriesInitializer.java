package bg.sofia.uni.event_management.seeder;

import bg.sofia.uni.event_management.model.Category;
import bg.sofia.uni.event_management.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataCategoriesInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataCategoriesInitializer.class);

    private final CategoryRepository categoryRepository;

    public DataCategoriesInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            log.info("Categories already exist in the database. Skipping seed.");
            return;
        }

        List<String> defaultCategories = List.of(
            "CONFERENCE",
            "WORKSHOP",
            "SEMINAR",
            "SPORTS",
            "MUSIC",
            "ART",
            "TECH",
            "BUSINESS",
            "SOCIAL",
            "OTHER"
        );

        List<Category> categories = defaultCategories.stream()
            .map(Category::new)
            .toList();

        categoryRepository.saveAll(categories);

        log.info("Successfully seeded {} categories.", categories.size());
    }
}