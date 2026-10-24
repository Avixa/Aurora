package altn72.aurora.controleur;

import altn72.aurora.modele.equipment.Equipment;
import altn72.aurora.modele.incident.Incident;
import altn72.aurora.service.EquipmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Controller
@RequestMapping("/equipments")
public class EquipmentController {

    private final EquipmentService equipmentService;
    public EquipmentController(EquipmentService equipmentService) { this.equipmentService = equipmentService; }

    @GetMapping("/{equipmentId}")
    public String showEquipment(@PathVariable Long equipmentId, Model model) {
        Equipment equipment = equipmentService.getEquipment(equipmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Equipment " + equipmentId + " does not exist"));
        model.addAttribute("equipment", equipment);
        model.addAttribute("ongoingIncidents", equipmentService.getOngoingIncidents(equipmentId));
        model.addAttribute("pastIncidents", equipmentService.getPastIncidents(equipmentId));
        log.info("New equipment created");
        return "equipmentDetails";
    }

//    @GetMapping("/{equipmentId}/incidents/new")
//    public String declareIncidentForm(@PathVariable Long equipmentId, Model model){
//        Equipment equipment = equipmentService.getEquipment(equipmentId)
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.NOT_FOUND, "Equipment " + equipmentId + " does not exist"));
//        model.addAttribute("equipment", equipment);
//        return "incidentForm";
//    }

    @PostMapping("/{equipmentId}/incidents")
    public String declareIncident(@PathVariable Long equipmentId,
                                  @ModelAttribute("newIncident") Incident incident) {
        equipmentService.declareIncident(equipmentId, incident);
        log.info("New incident declared, redirect to associated equipment page");
        return "redirect:/equipments/" + equipmentId;
    }


}