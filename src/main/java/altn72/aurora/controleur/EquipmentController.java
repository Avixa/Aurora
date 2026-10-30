package altn72.aurora.controleur;

import altn72.aurora.modele.auth.UserSession;
import altn72.aurora.modele.equipment.Equipment;
import altn72.aurora.modele.incident.Incident;
import altn72.aurora.service.EquipmentService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Slf4j
@Controller
@RequestMapping("/equipments")
public class EquipmentController {

    private final EquipmentService equipmentService;
    public EquipmentController(EquipmentService equipmentService) { this.equipmentService = equipmentService; }

    @GetMapping("/{equipmentId}")
    public String showEquipment(@PathVariable Long equipmentId, Model model) {
        Equipment equipment = equipmentService.getEquipment(equipmentId);
        model.addAttribute("equipment", equipment);
        model.addAttribute("ongoingIncidents", equipmentService.getOngoingIncidents(equipmentId));
        model.addAttribute("pastIncidents", equipmentService.getPastIncidents(equipmentId));
        log.info("Showing equipment {}", equipmentId);
        return "equipmentDetails";
    }


}