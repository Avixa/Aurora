package altn72.aurora.controleur;

import altn72.aurora.modele.equipment.Equipment;
import altn72.aurora.service.EquipmentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/equipments")
public class EquipmentController {

    private final EquipmentService equipmentService;
    public EquipmentController(EquipmentService equipmentService) { this.equipmentService = equipmentService; }

    // GET /equipments/{id}: F3, equipment sheet with ongoing and past incidents
    @GetMapping("/{equipmentId}")
    public String showEquipment(@PathVariable Long equipmentId, Model model) {
        Equipment equipment = equipmentService.getEquipment(equipmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Equipment " + equipmentId + " does not exist"));
        model.addAttribute("equipment", equipment);
        //model.addAttribute("ongoingIncidents", equipmentService.getOngoingIncidents(equipmentId));
        //model.addAttribute("pastIncidents", equipmentService.getPastIncidents(equipmentId));
        return "equipmentDetails";
    }
}