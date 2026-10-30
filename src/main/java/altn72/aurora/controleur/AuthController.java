package altn72.aurora.controleur;

import altn72.aurora.modele.auth.User;
import altn72.aurora.modele.auth.UserSession;
import altn72.aurora.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@Controller
public class AuthController {

    public static final String SESSION_KEY = "loggedUser";

    private final UserService userService;

    public AuthController(UserService userService) { this.userService = userService; }

    @GetMapping("/login")
    public String showLogin(@SessionAttribute(name = SESSION_KEY, required = false) UserSession user) {
        if (user != null) {
            return "redirect:/dashboard";
        }
        log.debug("Opening login page");
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String login, @RequestParam String password,
                        HttpServletRequest request, Model model) {

        Optional<User> user = userService.authenticate(login, password);

        if (user.isEmpty()) {
            model.addAttribute("error", "Invalid login or password");
            model.addAttribute("login", login);
            log.info("Unable to find a corresponding user, redirect to /login with error");
            return "login";
        }

        User u = user.get();
        request.getSession();
        request.changeSessionId();
        request.getSession().setAttribute(SESSION_KEY, new UserSession(u.getId(), u.getFirstName(), u.getProfile()));
        log.debug("User authenticated, redirect to /dashboard");

        return "redirect:/dashboard";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        log.info("User logout, redirect to /login");
        return "redirect:/login";
    }
}