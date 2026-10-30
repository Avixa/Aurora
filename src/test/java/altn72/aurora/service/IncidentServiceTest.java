package altn72.aurora.service;

import altn72.aurora.modele.auth.User;
import altn72.aurora.modele.auth.UserRepository;
import altn72.aurora.modele.equipment.Equipment;
import altn72.aurora.modele.equipment.EquipmentRepository;
import altn72.aurora.modele.incident.Incident;
import altn72.aurora.modele.incident.IncidentRepository;
import altn72.aurora.modele.provider.Provider;
import altn72.aurora.modele.provider.ProviderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private IncidentService incidentService;

    private Equipment equipment;
    private User declarant;
    private Incident incident;
    private Provider provider;

    @BeforeEach
    void setUp() {
        equipment = new Equipment();
        equipment.setId(1L);
        equipment.setReference("EQ-001");
        equipment.setLabel("Coffee Machine");

        declarant = new User();
        declarant.setId(10L);
        declarant.setFirstName("John");

        incident = new Incident();
        incident.setId(100L);
        incident.setDescription("  Screen broken   ");

        provider = new Provider();
        provider.setId(5L);
        provider.setName("TechFix Corp");
    }

    @Test
    void declareIncident_shouldSuccess() {
        // Arrange
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        when(userRepository.getReferenceById(10L)).thenReturn(declarant);

        // Simuler que la base de données génère un ID lors de l'enregistrement
        when(incidentRepository.save(any(Incident.class))).thenAnswer(invocation -> {
            Incident saved = invocation.getArgument(0);
            saved.setId(200L);
            return saved;
        });

        // Act
        Incident result = incidentService.declareIncident(1L, incident, 10L);

        // Assert
        assertNotNull(result);
        assertEquals(200L, result.getId());
        assertEquals(equipment, result.getEquipment());
        assertEquals(declarant, result.getDeclarant());
        assertEquals(Incident.StatusEnum.NON_AFFECTE, result.getStatus());
        assertNull(result.getProvider());
        assertNull(result.getAffectationDate());
        assertNull(result.getResolutionDate());
        assertEquals("Screen broken", result.getDescription()); // Vérifie le trim()

        verify(incidentRepository, times(1)).save(any(Incident.class));
    }

    @Test
    void declareIncident_shouldThrowException_whenEquipmentNotFound() {
        // Arrange
        when(equipmentRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EquipmentService.EquipmentNotFoundException.class, () -> {
            incidentService.declareIncident(99L, incident, 10L);
        });

        verify(incidentRepository, never()).save(any(Incident.class));
    }

    @Test
    void getIncident_shouldReturnIncident_whenExists() {
        // Arrange
        when(incidentRepository.findById(100L)).thenReturn(Optional.of(incident));

        // Act
        Incident found = incidentService.getIncident(100L);

        // Assert
        assertNotNull(found);
        assertEquals(100L, found.getId());
    }

    @Test
    void getIncident_shouldThrowException_whenNotFound() {
        // Arrange
        when(incidentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> incidentService.getIncident(999L));
    }

    @Test
    void getIncidents_shouldReturnAllIncidents() {
        // Arrange
        when(incidentRepository.findAll()).thenReturn(List.of(incident));

        // Act
        List<Incident> list = incidentService.getIncidents();

        // Assert
        assertNotNull(list);
        assertEquals(1, list.size());
        verify(incidentRepository, times(1)).findAll();
    }

    @Test
    void assignProvider_shouldSuccess() {
        // Arrange
        incident.setStatus(Incident.StatusEnum.NON_AFFECTE);
        when(incidentRepository.findById(100L)).thenReturn(Optional.of(incident));
        when(providerRepository.findById(5L)).thenReturn(Optional.of(provider));
        when(incidentRepository.save(any(Incident.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Incident updated = incidentService.assignProvider(100L, 5L);

        // Assert
        assertEquals(provider, updated.getProvider());
        assertEquals(Incident.StatusEnum.AFFECTE, updated.getStatus());
        assertNotNull(updated.getAffectationDate());
        verify(incidentRepository, times(1)).save(incident);
    }

    @Test
    void assignProvider_shouldThrowException_whenIncidentAlreadyResolved() {
        // Arrange
        incident.setStatus(Incident.StatusEnum.RESOLU);
        when(incidentRepository.findById(100L)).thenReturn(Optional.of(incident));

        // Act & Assert
        assertThrows(IncidentService.ResolvedIncidentException.class, () -> {
            incidentService.assignProvider(100L, 5L);
        });

        verify(providerRepository, never()).findById(any());
        verify(incidentRepository, never()).save(any());
    }

    @Test
    void assignProvider_shouldThrowException_whenProviderNotFound() {
        // Arrange
        incident.setStatus(Incident.StatusEnum.NON_AFFECTE);
        when(incidentRepository.findById(100L)).thenReturn(Optional.of(incident));
        when(providerRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IncidentService.ProviderNotFoundException.class, () -> {
            incidentService.assignProvider(100L, 99L);
        });

        verify(incidentRepository, never()).save(any());
    }
}