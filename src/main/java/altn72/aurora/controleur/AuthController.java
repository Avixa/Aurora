package altn72.aurora.controleur;

import altn72.aurora.modele.auth.User;
import altn72.aurora.modele.auth.UserSession;
import altn72.aurora.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class AuthController {

    public static final String SESSION_KEY = "loggedUser";

    private final UserService userService;
    public AuthController(UserService userService) { this.userService = userService; }

    // GET /login: shows the login form (templates/login.html).
    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    // POST /login: checks the credentials.
    @PostMapping("/login")
    public String login(@RequestParam String login,
                        @RequestParam String password,
                        HttpServletRequest request,
                        Model model) {
        Optional<User> user = userService.authenticate(login, password);
        if (user.isEmpty()) {
            model.addAttribute("error", "Invalid login or password");
            model.addAttribute("login", login);
            return "login";
        }
        User u = user.get();
        request.getSession();
        request.changeSessionId();
        request.getSession().setAttribute(SESSION_KEY,
                new UserSession(u.getId(), u.getFirstName(), u.getProfile()));
        return "redirect:/stations";
    }

    // POST /logout: closes the session and goes back to the login page.
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}