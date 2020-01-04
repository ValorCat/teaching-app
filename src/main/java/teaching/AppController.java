package teaching;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;
import teaching.model.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class AppController {

    @Autowired AccountRepository accountDb;
    @Autowired ChapterRepository chapterDb;
    @Autowired ExerciseRepository exerciseDb;
    @Autowired ProgressRepository progressDb;
    @Autowired TestCaseRepository testCaseDb;
    @Autowired TestCaseElementRepository testCaseElementDb;

    @GetMapping("/")
    public String root(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        } else {
            return "redirect:/chapters";
        }
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping(value = "/login", params = {"logout"})
    public String logout(HttpSession session) {
        session.invalidate();
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(HttpSession session, String username, String password) {
        Optional<Account> account = accountDb.findByUsernameAndPassword(username, Account.hashPassword(password));
        if (account.isPresent()) {
            accountDb.updateLastLoginTime(account.get());
            session.setAttribute("user", account.get());
            session.setMaxInactiveInterval(3600);
            return "redirect:/chapters";
        } else {
            return "redirect:/login?error";
        }
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(HttpSession session, String username, String password) {
        Optional<Account> account = accountDb.findByUsername(username);
        if (account.isPresent()) {
            return "redirect:/register?taken";
        } else if (username.isEmpty() || password.isEmpty()) {
            return "redirect:/register?empty";
        } else {
            Account newAccount = accountDb.create(username, password, "user");
            session.setAttribute("user", newAccount);
            session.setMaxInactiveInterval(3600);
            return "redirect:/chapters";
        }
    }

    @GetMapping("/chapters")
    public String chapters(HttpSession session, Model model) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("chapters", chapterDb.getChapterData(exerciseDb));
        model.addAttribute("progress", progressDb.getChapterProgressIndex(user.getUsername()));
        return "chapters";
    }

    @GetMapping("/chapter/{chapter}")
    public String chapter(HttpSession session, @PathVariable int chapter, Model model) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("chapter", chapter);
        model.addAttribute("exercises", exerciseDb.findByChapterOrderByNumber(chapter));
        model.addAttribute("completion", progressDb.getCompletionIndex(user.getUsername(), chapter));
        model.addAttribute("exercise", null);
        return "exercise";
    }

    @GetMapping("/chapter/{chapter}/exercise/{exercise}")
    public String practice(HttpSession session, HttpServletRequest request, @PathVariable int chapter,
                           @PathVariable int exercise, Model model) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        List<Exercise> exercises = exerciseDb.findByChapterOrderByNumber(chapter);
        if (exercises.isEmpty()) {
            return "redirect:..";
        }
        Exercise exerciseData = exerciseDb.findOneByChapterAndNumber(chapter, exercise);
        Optional<Progress> progress = progressDb.findByAccountAndChapterAndExercise(user.getUsername(), chapter, exercise);
        String code = progress.map(Progress::getCode).orElse(exerciseData.getInitial());
        model.addAttribute("user", user);
        model.addAttribute("chapter", chapter);
        model.addAttribute("exercises", exercises);
        model.addAttribute("completion", progressDb.getCompletionIndex(user.getUsername(), chapter));
        model.addAttribute("exercise", exerciseData);
        model.addAttribute("code", code);
        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            model.addAttribute("results", inputFlashMap.get("results"));
        }
        return "exercise";
    }

    @GetMapping("/chapter/{chapter}/new")
    public String create(HttpSession session, Model model, @PathVariable int chapter) {
        Account user = (Account) session.getAttribute("user");
        if (user == null || !user.getRole().equals("admin")) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("chapter", chapter);
        model.addAttribute("exercises", exerciseDb.findByChapterOrderByNumber(chapter));
        model.addAttribute("completion", progressDb.getCompletionIndex(user.getUsername(), chapter));
        return "new";
    }

    @PostMapping("/chapter/{chapter}/new")
    public String processCreate(HttpSession session, Model model, @PathVariable int chapter,
                                String name, String text, String initial) {
        Account user = (Account) session.getAttribute("user");
        if (user == null || !user.getRole().equals("admin")) {
            return "redirect:/login";
        }
        int id = 1 + exerciseDb.findMaxByChapter(chapter);
        exerciseDb.create(chapter, id, id, name, text, initial);
        return "redirect:exercise/" + id;
    }

    @PostMapping("/chapter/{chapter}/exercise/{exercise}/run")
    public RedirectView run(HttpSession session, RedirectAttributes redirectAttributes, @PathVariable int chapter,
                            @PathVariable int exercise, String attempt) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) {
            return new RedirectView("/login");
        }
        List<TestCase> tests = testCaseDb.findByChapterAndExercise(chapter, exercise);
        tests.forEach(test -> testCaseElementDb.addElements(test));
        String testJson = testCaseDb.getJson(tests);
        TestResults results = ClientCodeExecutor.INSTANCE.execute(attempt, tests, testJson);
        progressDb.updateProgress(user.getUsername(), chapter, exercise, attempt, results.doAllPass());
        if (results.hasError()) {
            redirectAttributes.addFlashAttribute("error", results);
        } else {
            redirectAttributes.addFlashAttribute("results", results.getResults());
        }
        return new RedirectView("../" + exercise);
    }

    @GetMapping("/chapter/{chapter}/exercise/{exercise}/edit")
    public String edit(HttpSession session, Model model, @PathVariable int chapter, @PathVariable int exercise) {
        Account user = (Account) session.getAttribute("user");
        if (user == null || !user.getRole().equals("admin")) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("chapter", chapter);
        model.addAttribute("exercises", exerciseDb.findByChapterOrderByNumber(chapter));
        model.addAttribute("completion", progressDb.getCompletionIndex(user.getUsername(), chapter));
        model.addAttribute("exercise", exerciseDb.findOneByChapterAndNumber(chapter, exercise));
        return "edit";
    }

    @PostMapping("/chapter/{chapter}/exercise/{exercise}/edit")
    public String processEdit(HttpSession session, @PathVariable int chapter, @PathVariable int exercise,
                              String name, String text, String initial) {
        Account user = (Account) session.getAttribute("user");
        if (user == null || !user.getRole().equals("admin")) {
            return "redirect:/login";
        }
        exerciseDb.update(chapter, exercise, name, text, initial);
        return "redirect:../" + exercise;
    }

    @GetMapping("/chapter/{chapter}/exercise/{exercise}/remove")
    @Transactional
    public String remove(HttpSession session, @PathVariable int chapter, @PathVariable int exercise) {
        Account user = (Account) session.getAttribute("user");
        if (user == null || !user.getRole().equals("admin")) {
            return "redirect:/login";
        }
        exerciseDb.deleteByChapterAndId(chapter, exercise);
        return "redirect:../1";
    }

}