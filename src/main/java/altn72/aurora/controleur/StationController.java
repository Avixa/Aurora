package altn72.aurora.controleur;

import altn72.aurora.modele.station.Station;
import altn72.aurora.service.StationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/stations")
public class StationController {

    private final StationService stationService;
    public StationController(StationService stationService) { this.stationService = stationService; }

    @GetMapping
    public String listStations(Model model) {
        model.addAttribute("stations", stationService.getStations());
        return "stationList";
    }

    @GetMapping("/{stationId}/equipments")
    public String listEquipments(@PathVariable Long stationId, Model model) {
        Station station = stationService.getStation(stationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Station " + stationId + " does not exist"));
        model.addAttribute("station", station);
        model.addAttribute("equipments", stationService.getEquipmentsByStation(stationId));
        return "stationEquipments";
    }
}