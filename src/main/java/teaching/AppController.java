package teaching;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
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
            return "redirect:/content";
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
    public ModelAndView processLogin(HttpSession session, ModelMap model, String username, String password) {
        Account account = accountDb.findFirstByUsernameAndPassword(username, password);
        if (account != null) {
            session.setAttribute("user", account);
            return new ModelAndView("redirect:/content");
        } else {
            model.addAttribute("error", true);
            return new ModelAndView("redirect:/login", model);
        }
    }

    @GetMapping("/content")
    public String content(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", session.getAttribute("user"));
        model.addAttribute("chapters", chapterDb.getChapterData(exerciseDb));
        return "content";
    }

    @GetMapping("/content/{chapter}/{exercise}/practice")
    public String practice(HttpSession session, @PathVariable int chapter, @PathVariable int exercise, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", session.getAttribute("user"));
        model.addAttribute("chapter", chapter);
        model.addAttribute("exercises", exerciseDb.findByChapter(chapter));
        model.addAttribute("exercise", exerciseDb.findOneByChapterAndNumber(chapter, exercise));
        return "practice";
    }

    @GetMapping("/content/{chapter}/{exercise}/edit")
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

    @PostMapping("/content/{chapter}/{exercise}/edit")
    public String processEdit(HttpSession session, @PathVariable int chapter, @PathVariable int exercise,
                              String name, String text, String initial) {
        Account user = (Account) session.getAttribute("user");
        if (user == null || !user.getRole().equals("admin")) {
            return "redirect:/login";
        }
        exerciseDb.update(chapter, exercise, name, text, initial);
        return "redirect:practice";
    }

}