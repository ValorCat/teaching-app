package teaching;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import teaching.model.*;

@Controller
public class AppController {

    @Autowired
    AccountRepository accountDb;

    @Autowired
    ChapterRepository chapterDb;

    @Autowired
    ExerciseRepository exerciseDb;

    @GetMapping("/")
    public String main() {
        return "login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Account account) {
        boolean exists = accountDb.findAll().stream()
                .anyMatch(a -> a.getUsername().equals(account.getUsername())
                        && a.getPassword().equals(account.getPassword()));
        if (exists) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("chapters", chapterDb.getChapterData(exerciseDb));
        return "home";
    }

    @GetMapping("/practice/{chapter}/{exercise}")
    public String practice(@PathVariable int chapter, @PathVariable int exercise, Model model) {
        model.addAttribute("chapter", chapter);
        model.addAttribute("exercises", exerciseDb.findByChapter(chapter));
        model.addAttribute("exercise", exerciseDb.findFirstByChapterAndNumber(chapter, exercise));
        return "practice";
    }

    // inject via application.properties
    // @Value("${welcome.message}")
    // private String message;
    // /hello?name=kotlin
    // @RequestParam(name = "name", required = false, defaultValue = "")
    // model.addAttribute("message", name);

}