package altn72.aurora.controleur;

import altn72.aurora.modele.incident.AttentionLevel;
import altn72.aurora.service.DashboardService;
import altn72.aurora.service.DashboardService.ActiveIncident;
import altn72.aurora.service.StationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;
    private final StationService stationService;

    public DashboardController(DashboardService dashboardService, StationService stationService) {
        this.dashboardService = dashboardService;
        this.stationService = stationService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam(required = false) Long stationId,
                                @RequestParam(required = false) AttentionLevel level,
                                Model model) {

        List<ActiveIncident> activeIncidents = dashboardService.getActiveIncidents();
        // counters describe the whole network, the table shows the filtered selection
        model.addAttribute("counts", dashboardService.countByLevel(activeIncidents));
        model.addAttribute("totalActive", activeIncidents.size());
        model.addAttribute("incidents", dashboardService.filter(activeIncidents, stationId, level));
        model.addAttribute("stations", stationService.getStations());
        model.addAttribute("levels", AttentionLevel.values());
        model.addAttribute("selectedStationId", stationId);
        model.addAttribute("selectedLevel", level);

        return "dashboard";
    }
}