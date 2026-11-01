package altn72.aurora.api;

import altn72.aurora.config.RequiresPermission;
import altn72.aurora.modele.auth.Permission;
import altn72.aurora.service.IncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
public class IncidentApiController {

    private final IncidentService incidentService;

    public IncidentApiController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @Operation(summary = "Liste tous les incidents")
    @GetMapping
    public List<IncidentResponse> getAll() {
        return incidentService.getIncidents().stream()
                .map(IncidentResponse::from)
                .toList();
    }

    @Operation(summary = "Détail d'un incident")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Incident trouvé"),
            @ApiResponse(responseCode = "404", description = "Incident inexistant")
    })
    @GetMapping("/{incidentId}")
    public IncidentResponse getOne(@PathVariable Long incidentId) {
        return IncidentResponse.from(incidentService.getIncident(incidentId));
    }

    @Operation(summary = "Déclare un incident résolu (agent uniquement)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Incident résolu"),
            @ApiResponse(responseCode = "403", description = "Profil non autorisé"),
            @ApiResponse(responseCode = "404", description = "Incident inexistant"),
            @ApiResponse(responseCode = "409", description = "Incident déjà résolu")
    })
    @RequiresPermission(Permission.RESOLVE_INCIDENT)
    @PostMapping("/{incidentId}/resolution")
    public IncidentResponse resolve(@PathVariable Long incidentId) {
        return IncidentResponse.from(incidentService.resolve(incidentId));
    }
}