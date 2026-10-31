package altn72.aurora.controleur;

import altn72.aurora.config.RequiresPermission;
import altn72.aurora.modele.auth.Permission;
import altn72.aurora.modele.evaluation.Evaluation;
import altn72.aurora.service.EvaluationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    // only these two fields can be bound from the form
    @InitBinder("newEvaluation")
    public void restrictFields(WebDataBinder binder) {
        binder.setAllowedFields("rating", "comment");
    }

    // form
    @RequiresPermission(Permission.EVALUATE_PROVIDER)
    @GetMapping("/incidents/{incidentId}/evaluation/new")
    public String showForm(@PathVariable Long incidentId, Model model) {
        model.addAttribute("incident", evaluationService.getEvaluableIncident(incidentId));
        model.addAttribute("newEvaluation", new Evaluation());
        return "evaluationForm";
    }

    // creation
    @RequiresPermission(Permission.EVALUATE_PROVIDER)
    @PostMapping("/incidents/{incidentId}/evaluation")
    public String evaluate(@PathVariable Long incidentId,
                           @Valid @ModelAttribute("newEvaluation") Evaluation evaluation,
                           BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("incident", evaluationService.getEvaluableIncident(incidentId));
            return "evaluationForm";
        }
        evaluationService.evaluate(incidentId, evaluation);
        return "redirect:/incidents/" + incidentId;
    }
}