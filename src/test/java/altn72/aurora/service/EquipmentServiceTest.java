package altn72.aurora.service;

import altn72.aurora.modele.equipment.Equipment;
import altn72.aurora.modele.equipment.EquipmentRepository;
import altn72.aurora.modele.incident.Incident;
import altn72.aurora.modele.incident.IncidentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private IncidentRepository incidentRepository;

    @InjectMocks
    private EquipmentService equipmentService;

    private Equipment equipment;
    private Incident incident;

    @BeforeEach
    void setUp() {
        equipment = new Equipment();
        equipment.setId(1L);
        equipment.setReference("EQ-100");
        equipment.setLabel("Borne de recharge");

        incident = new Incident();
        incident.setId(99L);
        incident.setDescription("Ecran tactile HS");
    }

    @Test
    void getEquipment_shouldReturnEquipment_whenExists() {
        // Arrange
        when(equipmentRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(equipment));

        // Act
        Equipment result = equipmentService.getEquipment(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("EQ-100", result.getReference());
        verify(equipmentRepository, times(1)).findByIdWithDetails(1L);
    }

    @Test
    void getEquipment_shouldThrowException_whenNotFound() {
        // Arrange
        when(equipmentRepository.findByIdWithDetails(999L)).thenReturn(Optional.empty());

        // Act & Assert
        EquipmentService.EquipmentNotFoundException exception = assertThrows(
                EquipmentService.EquipmentNotFoundException.class,
                () -> equipmentService.getEquipment(999L)
        );

        assertEquals("Equipment 999 does not exist", exception.getMessage());
        verify(equipmentRepository, times(1)).findByIdWithDetails(999L);
    }

    @Test
    void getOngoingIncidents_shouldReturnListOfOngoingIncidents() {
        // Arrange
        List<Incident> ongoingList = List.of(incident);
        when(incidentRepository.findOngoingByEquipmentId(1L)).thenReturn(ongoingList);

        // Act
        List<Incident> result = equipmentService.getOngoingIncidents(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(incidentRepository, times(1)).findOngoingByEquipmentId(1L);
    }

    @Test
    void getPastIncidents_shouldReturnListOfPastIncidents() {
        // Arrange
        List<Incident> pastList = List.of(incident);
        when(incidentRepository.findPastByEquipmentId(1L)).thenReturn(pastList);

        // Act
        List<Incident> result = equipmentService.getPastIncidents(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(incidentRepository, times(1)).findPastByEquipmentId(1L);
    }
}