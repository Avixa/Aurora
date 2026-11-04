package altn72.aurora.service;

import altn72.aurora.modele.equipment.Equipment;
import altn72.aurora.modele.equipment.EquipmentRepository;
import altn72.aurora.modele.station.Station;
import altn72.aurora.modele.station.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @Mock
    private StationRepository stationRepository;

    @Mock
    private EquipmentRepository equipmentRepository;

    @InjectMocks
    private StationService stationService;

    private static final Long STATION_ID = 1L;

    // ------------------------------------------------------------------------
    // Tests for getStations
    // ------------------------------------------------------------------------
    @Nested
    @DisplayName("getStations Tests")
    class GetStationsTests {

        @Test
        @DisplayName("Should return all stations from repository")
        void getStations_ShouldReturnAllStations() {
            // Given
            Station station1 = mock(Station.class);
            Station station2 = mock(Station.class);
            when(stationRepository.findAll()).thenReturn(List.of(station1, station2));

            // When
            List<Station> result = stationService.getStations();

            // Then
            assertThat(result).containsExactly(station1, station2);
            verify(stationRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no stations exist")
        void getStations_WhenEmpty_ShouldReturnEmptyList() {
            // Given
            when(stationRepository.findAll()).thenReturn(List.of());

            // When
            List<Station> result = stationService.getStations();

            // Then
            assertThat(result).isEmpty();
            verify(stationRepository).findAll();
        }
    }

    // ------------------------------------------------------------------------
    // Tests for getStation
    // ------------------------------------------------------------------------
    @Nested
    @DisplayName("getStation Tests")
    class GetStationTests {

        @Test
        @DisplayName("Should return station when found by ID")
        void getStation_WhenFound_ShouldReturnStationOptional() {
            // Given
            Station station = mock(Station.class);
            when(stationRepository.findById(STATION_ID)).thenReturn(Optional.of(station));

            // When
            Optional<Station> result = stationService.getStation(STATION_ID);

            // Then
            assertThat(result).contains(station);
            verify(stationRepository).findById(STATION_ID);
        }

        @Test
        @DisplayName("Should return empty optional when station not found by ID")
        void getStation_WhenNotFound_ShouldReturnEmptyOptional() {
            // Given
            when(stationRepository.findById(STATION_ID)).thenReturn(Optional.empty());

            // When
            Optional<Station> result = stationService.getStation(STATION_ID);

            // Then
            assertThat(result).isEmpty();
            verify(stationRepository).findById(STATION_ID);
        }
    }

    // ------------------------------------------------------------------------
    // Tests for getEquipmentsByStation
    // ------------------------------------------------------------------------
    @Nested
    @DisplayName("getEquipmentsByStation Tests")
    class GetEquipmentsByStationTests {

        @Test
        @DisplayName("Should return equipment list with types for given station ID")
        void getEquipmentsByStation_ShouldReturnEquipmentsWithType() {
            // Given
            Equipment equipment1 = mock(Equipment.class);
            Equipment equipment2 = mock(Equipment.class);
            when(equipmentRepository.findByStationIdWithType(STATION_ID))
                    .thenReturn(List.of(equipment1, equipment2));

            // When
            List<Equipment> result = stationService.getEquipmentsByStation(STATION_ID);

            // Then
            assertThat(result).containsExactly(equipment1, equipment2);
            verify(equipmentRepository).findByStationIdWithType(STATION_ID);
        }

        @Test
        @DisplayName("Should return empty list when station has no equipments")
        void getEquipmentsByStation_WhenNoEquipments_ShouldReturnEmptyList() {
            // Given
            when(equipmentRepository.findByStationIdWithType(STATION_ID)).thenReturn(List.of());

            // When
            List<Equipment> result = stationService.getEquipmentsByStation(STATION_ID);

            // Then
            assertThat(result).isEmpty();
            verify(equipmentRepository).findByStationIdWithType(STATION_ID);
        }
    }
}