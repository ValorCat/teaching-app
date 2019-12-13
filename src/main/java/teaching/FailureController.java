package teaching;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class FailureController implements ErrorController {

    @RequestMapping("/error")
    public String handle(HttpServletRequest request, Model model) {
        Integer status = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            model.addAttribute("status", status);
            model.addAttribute("description", HttpStatus.resolve(status).getReasonPhrase());
        }
        return "error";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

}
