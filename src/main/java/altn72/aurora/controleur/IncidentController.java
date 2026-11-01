package altn72.aurora.controleur;

import altn72.aurora.config.RequiresPermission;
import altn72.aurora.modele.auth.Permission;
import altn72.aurora.service.EvaluationService;

import altn72.aurora.modele.auth.UserSession;
import altn72.aurora.modele.incident.Incident;
import altn72.aurora.modele.provider.ProviderRepository;
import altn72.aurora.service.EquipmentService;
import altn72.aurora.service.IncidentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@Controller
public class IncidentController {

    private final IncidentService incidentService;
    private final EquipmentService equipmentService;
    private final ProviderRepository providerRepository;
    private final EvaluationService evaluationService;

    public IncidentController(IncidentService incidentService,
                              EquipmentService equipmentService,
                              ProviderRepository providerRepository,
                              EvaluationService evaluationService) {
        this.incidentService = incidentService;
        this.equipmentService = equipmentService;
        this.providerRepository = providerRepository;
        this.evaluationService = evaluationService;
    }

    // Only these two fields can be bound from the form
    @InitBinder("newIncident")
    public void restrictFields(WebDataBinder binder) {
        binder.setAllowedFields("description", "openingDate");
    }

    // form
    @RequiresPermission(Permission.DECLARE_INCIDENT)
    @GetMapping("/equipments/{equipmentId}/incidents/new")
    public String showForm(@PathVariable Long equipmentId, Model model) {
        Incident incident = new Incident();
        incident.setOpeningDate(LocalDateTime.now().withSecond(0).withNano(0));
        model.addAttribute("equipment", equipmentService.getEquipment(equipmentId));
        model.addAttribute("newIncident", incident);
        return "incidentForm";
    }

    // creation
    @RequiresPermission(Permission.DECLARE_INCIDENT)
    @PostMapping("/equipments/{equipmentId}/incidents")
    public String declare(@PathVariable Long equipmentId,
                          @Valid @ModelAttribute("newIncident") Incident incident,
                          BindingResult bindingResult,
                          @SessionAttribute(AuthController.SESSION_KEY) UserSession currentUser,
                          Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("equipment", equipmentService.getEquipment(equipmentId));
            return "incidentForm";
        }
        Incident created = incidentService.declareIncident(equipmentId, incident, currentUser.id());
        log.info("Incident {} declared on equipment {}", created.getId(), equipmentId);
        return "redirect:/equipments/" + equipmentId;
    }

    @GetMapping("/incidents/{incidentId}")
    public String show(@PathVariable Long incidentId, Model model) {
        Incident incident = incidentService.getIncident(incidentId);
        model.addAttribute("incident", incident);
        model.addAttribute("providers", providerRepository.findAll());
        model.addAttribute("evaluation", evaluationService.getEvaluation(incidentId).orElse(null));
        model.addAttribute("evaluable", evaluationService.isEvaluable(incident));
        model.addAttribute("hoursOpen", incidentService.getHoursOpen(incident));
        return "incidentDetails";
    }

    @GetMapping("/incidents")
    public String showAll(Model model){
        model.addAttribute("incidents", incidentService.getIncidents());
        return "fragments/incidentTable";
    }

    @PostMapping("/incidents/{incidentId}/provider")
    public String assignProvider(@PathVariable Long incidentId,
                                 @RequestParam Long providerId) {
        incidentService.assignProvider(incidentId, providerId);
        return "redirect:/incidents/" + incidentId;
    }

    @RequiresPermission(Permission.RESOLVE_INCIDENT)
    @PostMapping("/incidents/{incidentId}/resolution")
    public String resolve(@PathVariable Long incidentId) {
        incidentService.resolve(incidentId);
        return "redirect:/incidents/" + incidentId;
    }

    @GetMapping("/incidents/history")
    public String showHistory(Model model) {
        model.addAttribute("incidents", incidentService.getResolvedIncidents());
        return "incidentHistory";
    }

    // form
    @RequiresPermission(Permission.EDIT_INCIDENT)
    @GetMapping("/incidents/{incidentId}/edit")
    public String showEditForm(@PathVariable Long incidentId, Model model) {
        Incident incident = incidentService.getEditableIncident(incidentId);
        IncidentEditForm form = new IncidentEditForm();
        form.setDescription(incident.getDescription());
        model.addAttribute("incident", incident);
        model.addAttribute("editForm", form);
        return "incidentEditForm";
    }

    // update
    @RequiresPermission(Permission.EDIT_INCIDENT)
    @PostMapping("/incidents/{incidentId}/edit")
    public String update(@PathVariable Long incidentId,
                         @Valid @ModelAttribute("editForm") IncidentEditForm form,
                         BindingResult bindingResult,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("incident", incidentService.getEditableIncident(incidentId));
            return "incidentEditForm";
        }
        incidentService.updateDescription(incidentId, form.getDescription());
        return "redirect:/incidents/" + incidentId;
    }
}