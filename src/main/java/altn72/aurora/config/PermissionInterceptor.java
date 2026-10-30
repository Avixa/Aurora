package altn72.aurora.config;

import altn72.aurora.controleur.AuthController;
import altn72.aurora.modele.auth.UserSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

// runs after LoginInterceptor and blocks a method whose permission the user's profile lacks
@Slf4j
public class PermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // static resources are not controller methods
        if (!(handler instanceof HandlerMethod controllerMethod)) {
            return true;
        }

        RequiresPermission requirement = controllerMethod.getMethodAnnotation(RequiresPermission.class);

        if (requirement == null) {
            return true;
        }
        // LoginInterceptor runs first: a user is always in session here
        UserSession user = (UserSession) request.getSession().getAttribute(AuthController.SESSION_KEY);

        if (!user.can(requirement.value())) {
            log.warn("Access denied to {}: {} required", request.getRequestURI(), requirement.value());
            throw new ForbiddenActionException(requirement.value());
        }
        return true;
    }
}