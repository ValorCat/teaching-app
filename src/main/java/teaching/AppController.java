package teaching;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import teaching.model.Account;
import teaching.model.AccountRepository;
import teaching.model.ChapterRepository;
import teaching.model.ExerciseRepository;

import javax.servlet.http.HttpSession;

@Controller
public class AppController {

    @Autowired AccountRepository accountDb;
    @Autowired ChapterRepository chapterDb;
    @Autowired ExerciseRepository exerciseDb;

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
        Account account = accountDb.findFirstByUsernameAndPassword(username, password);
        if (account != null) {
            session.setAttribute("user", account);
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
        Account account = accountDb.findFirstByUsername(username);
        if (account != null) {
            return "redirect:/register?taken";
        } else if (username.isEmpty() || password.isEmpty()) {
            return "redirect:/register?empty";
        } else {
            Account newAccount = accountDb.create(username, password, "user");
            session.setAttribute("user", newAccount);
            return "redirect:/content";
        }
    }

    @GetMapping("/chapters")
    public String content(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", session.getAttribute("user"));
        model.addAttribute("chapters", chapterDb.getChapterData(exerciseDb));
        return "chapters";
    }

    @GetMapping("/chapter/{chapter}/exercise/{exercise}")
    public String practice(HttpSession session, @PathVariable int chapter, @PathVariable int exercise, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", session.getAttribute("user"));
        model.addAttribute("chapter", chapter);
        model.addAttribute("exercises", exerciseDb.findByChapter(chapter));
        model.addAttribute("exercise", exerciseDb.findOneByChapterAndNumber(chapter, exercise));
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
        model.addAttribute("exercises", exerciseDb.findByChapter(chapter));
        return "new";
    }

    @PostMapping("/chapter/{chapter}/new")
    public String processCreate(HttpSession session, Model model, @PathVariable int chapter,
                                String name, String text, String initial) {
        Account user = (Account) session.getAttribute("user");
        if (user == null || !user.getRole().equals("admin")) {
            return "redirect:/login";
        }
        int number = 1 + exerciseDb.findMaxByChapter(chapter);
        exerciseDb.create(chapter, number, name, text, initial);
        return "redirect:exercise/" + number;
    }

    @GetMapping("/chapter/{chapter}/exercise/{exercise}/edit")
    public String edit(HttpSession session, Model model, @PathVariable int chapter, @PathVariable int exercise) {
        Account user = (Account) session.getAttribute("user");
        if (user == null || !user.getRole().equals("admin")) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("chapter", chapter);
        model.addAttribute("exercises", exerciseDb.findByChapter(chapter));
        model.addAttribute("exercise", exerciseDb.findOneByChapterAndNumber(chapter, exercise));
        return "edit";
    }

    @PostMapping("/chapters/{chapter}/exercises/{exercise}/edit")
    public String processEdit(HttpSession session, @PathVariable int chapter, @PathVariable int exercise,
                              String name, String text, String initial) {
        Account user = (Account) session.getAttribute("user");
        if (user == null || !user.getRole().equals("admin")) {
            return "redirect:/login";
        }
        exerciseDb.update(chapter, exercise, name, text, initial);
        return "redirect:..";
    }

    @GetMapping("/chapters/{chapter}/exercises/{exercise}/remove")
    @Transactional
    public String remove(HttpSession session, @PathVariable int chapter, @PathVariable int exercise) {
        Account user = (Account) session.getAttribute("user");
        if (user == null || !user.getRole().equals("admin")) {
            return "redirect:/login";
        }
        exerciseDb.deleteByChapterAndNumber(chapter, exercise);
        return "redirect:../1";
    }

}