package altn72.aurora.controleur;

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

    public IncidentController(IncidentService incidentService,
                              EquipmentService equipmentService,
                              ProviderRepository providerRepository) {
        this.incidentService = incidentService;
        this.equipmentService = equipmentService;
        this.providerRepository = providerRepository;
    }

    // Only these two fields can be bound from the form
    @InitBinder("newIncident")
    public void restrictFields(WebDataBinder binder) {
        binder.setAllowedFields("description", "openingDate");
    }

    // F4: form
    @GetMapping("/equipments/{equipmentId}/incidents/new")
    public String showForm(@PathVariable Long equipmentId, Model model) {
        Incident incident = new Incident();
        incident.setOpeningDate(LocalDateTime.now().withSecond(0).withNano(0));
        model.addAttribute("equipment", equipmentService.getEquipment(equipmentId));
        model.addAttribute("newIncident", incident);
        return "incidentForm";
    }

    // F4: creation
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
        model.addAttribute("incident", incidentService.getIncident(incidentId));
        model.addAttribute("providers", providerRepository.findAll());
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
}