package altn72.aurora.controleur;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// the only field of an incident that can be modified
@Getter
@Setter
public class IncidentEditForm {

    @NotBlank(message = "La description est obligatoire.")
    @Size(min = 5, max = 1000, message = "La description doit contenir entre 5 et 1000 caractères.")
    private String description;
}