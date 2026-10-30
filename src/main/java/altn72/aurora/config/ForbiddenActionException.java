package altn72.aurora.config;

import altn72.aurora.modele.auth.Permission;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// an action refused to the user's profile is answered with HTTP 403
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenActionException extends RuntimeException {

    public ForbiddenActionException(Permission permission) {
        super("Vous ne pouvez pas " + permission.getAction() + ".");
    }
}