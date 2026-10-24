package altn72.aurora.config;

import altn72.aurora.controleur.AuthController;
import jakarta.servlet.http.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(AuthController.SESSION_KEY) == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            log.debug("Session auth not initialized, request redirect to /login");
            return false;
        }
        log.debug("Session auth initialized, request may pass");
        return true;
    }
}