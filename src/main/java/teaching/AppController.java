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

    @Autowired private AccountRepository accountTable;
    @Autowired private CategoryRepository categoryTable;
    @Autowired private ExerciseRepository exerciseTable;
    @Autowired private ProgressRepository progressTable;

    @GetMapping("/")
    public String root(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        } else {
            return "redirect:/categories";
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
        Optional<Account> account = accountTable.findByUsernameAndPassword(username, Account.hashPassword(password));
        if (account.isPresent()) {
            accountTable.updateLastLoginTime(account.get());
            session.setAttribute("user", account.get());
            session.setMaxInactiveInterval(3600);
            return "redirect:/categories";
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
        Optional<Account> account = accountTable.findByUsername(username);
        if (account.isPresent()) {
            return "redirect:/register?taken";
        } else if (username.isEmpty() || password.isEmpty()) {
            return "redirect:/register?empty";
        } else {
            Account newAccount = accountTable.create(username, password, "user");
            session.setAttribute("user", newAccount);
            session.setMaxInactiveInterval(3600);
            return "redirect:/categories";
        }
    }

    @GetMapping("/categories")
    public String categories(HttpSession session, Model model) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("categories", categoryTable.getCategoryData(exerciseTable));
        model.addAttribute("progress", progressTable.getCategoryProgressIndex(user.getUsername()));
        return "category-list";
    }

    @GetMapping("/category/{category}")
    public String category(HttpSession session, @PathVariable int category, Model model) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("category", category);
        model.addAttribute("exercises", exerciseTable.findByCategoryOrderByNumber(category));
        model.addAttribute("completion", progressTable.getCompletionIndex(user.getUsername(), category));
        model.addAttribute("exercise", null);
        return "attempt-exercise";
    }

    @GetMapping("/category/{category}/exercise/{exercise}")
    public String practice(HttpSession session, HttpServletRequest request, @PathVariable int category,
                           @PathVariable int exercise, Model model) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        List<Exercise> exercises = exerciseTable.findByCategoryOrderByNumber(category);
        if (exercises.isEmpty()) {
            return "redirect:..";
        }
        Exercise exerciseData = exerciseTable.findOneByCategoryAndNumber(category, exercise);
        Optional<Progress> progress = progressTable.findByAccountAndCategoryAndExercise(user.getUsername(), category, exercise);
        String code = progress.map(Progress::getCode).orElse(exerciseData.getInitial());
        model.addAttribute("user", user);
        model.addAttribute("category", category);
        model.addAttribute("exercises", exercises);
        model.addAttribute("completion", progressTable.getCompletionIndex(user.getUsername(), category));
        model.addAttribute("exercise", exerciseData);
        model.addAttribute("code", code);
        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            model.addAttribute("results", inputFlashMap.get("results"));
        }
        return "attempt-exercise";
    }

    @GetMapping("/category/{category}/new")
    public String create(HttpSession session, Model model, @PathVariable int category) {
        Account user = (Account) session.getAttribute("user");
        if (user == null || !user.getRole().equals("admin")) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("category", category);
        model.addAttribute("exercises", exerciseTable.findByCategoryOrderByNumber(category));
        model.addAttribute("completion", progressTable.getCompletionIndex(user.getUsername(), category));
        return "create-exercise";
    }

    @PostMapping("/category/{category}/new")
    public String processCreate(HttpSession session, Model model, @PathVariable int category,
                                String name, String text, String initial, String tests) {
        Account user = (Account) session.getAttribute("user");
        if (user == null || !user.getRole().equals("admin")) {
            return "redirect:/login";
        }
        int id = 1 + exerciseTable.findMaxByCategory(category);
        exerciseTable.create(category, id, id, name, text, initial, tests);
        return "redirect:exercise/" + id;
    }

    @PostMapping("/category/{category}/exercise/{exercise}/run")
    public RedirectView run(HttpSession session, RedirectAttributes redirectAttributes, @PathVariable int category,
                            @PathVariable int exercise, String attempt) {
        Account user = (Account) session.getAttribute("user");
        if (user == null) {
            return new RedirectView("/login");
        }
        String testJson = exerciseTable.findOneByCategoryAndNumber(category, exercise).getTests();
        TestResults results = ClientCodeExecutor.INSTANCE.execute(attempt, testJson);
        progressTable.updateProgress(user.getUsername(), category, exercise, attempt, results.doAllPass());
        if (results.hasError()) {
            redirectAttributes.addFlashAttribute("error", results);
        } else {
            redirectAttributes.addFlashAttribute("results", results.getResults());
        }
        return new RedirectView("../" + exercise);
    }

    @GetMapping("/category/{category}/exercise/{exercise}/edit")
    public String edit(HttpSession session, Model model, @PathVariable int category, @PathVariable int exercise) {
        Account user = (Account) session.getAttribute("user");
        if (user == null || !user.getRole().equals("admin")) {
            return "redirect:/login";
        }
        //Account user = accountTable.getOne("a");
        model.addAttribute("user", user);
        model.addAttribute("category", category);
        model.addAttribute("exercises", exerciseTable.findByCategoryOrderByNumber(category));
        model.addAttribute("completion", progressTable.getCompletionIndex(user.getUsername(), category));
        model.addAttribute("exercise", exerciseTable.findOneByCategoryAndNumber(category, exercise));
        return "edit-exercise";
    }

    @PostMapping("/category/{category}/exercise/{exercise}/edit")
    public String processEdit(HttpSession session, @PathVariable int category, @PathVariable int exercise,
                              String name, String text, String initial, String tests) {
        Account user = (Account) session.getAttribute("user");
        if (user == null || !user.getRole().equals("admin")) {
            return "redirect:/login";
        }
        exerciseTable.update(category, exercise, name, text, initial, tests);
        return "redirect:../" + exercise;
    }

    @GetMapping("/category/{category}/exercise/{exercise}/remove")
    @Transactional
    public String remove(HttpSession session, @PathVariable int category, @PathVariable int exercise) {
        Account user = (Account) session.getAttribute("user");
        if (user == null || !user.getRole().equals("admin")) {
            return "redirect:/login";
        }
        exerciseTable.deleteByCategoryAndId(category, exercise);
        return "redirect:../1";
    }

}