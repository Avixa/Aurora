package altn72.aurora.service;

import altn72.aurora.modele.equipment.Equipment;
import altn72.aurora.modele.incident.AttentionLevel;
import altn72.aurora.modele.incident.Incident;
import altn72.aurora.modele.incident.IncidentRepository;
import altn72.aurora.modele.provider.Provider;
import altn72.aurora.modele.station.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    private Clock fixedClock;
    private DashboardService dashboardService;

    // Fixed test timestamp: 2026-11-02 10:00:00
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 2, 10, 0, 0);

    @BeforeEach
    void setUp() {
        Instant fixedInstant = NOW.atZone(ZoneId.systemDefault()).toInstant();
        fixedClock = Clock.fixed(fixedInstant, ZoneId.systemDefault());

        dashboardService = new DashboardService(incidentRepository, fixedClock);
    }

    @Nested
    @DisplayName("getActiveIncidents Tests")
    class GetActiveIncidentsTests {

        @Test
        @DisplayName("Should return GREEN when open for less than 48 hours and not assigned")
        void getActiveIncidents_WhenOpenUnder48Hours_ShouldBeGreen() {
            // Given: Opened 10 hours ago
            Incident incident = createIncident(NOW.minusHours(10), null, 1L);
            when(incidentRepository.findOngoingWithDetails()).thenReturn(List.of(incident));

            // When
            List<DashboardService.ActiveIncident> activeIncidents = dashboardService.getActiveIncidents();

            // Then
            assertThat(activeIncidents).hasSize(1);
            DashboardService.ActiveIncident active = activeIncidents.get(0);
            assertThat(active.level()).isEqualTo(AttentionLevel.GREEN);
            assertThat(active.hoursOpen()).isEqualTo(10L);
        }

        @Test
        @DisplayName("Should return ORANGE when open for 48 to 71 hours and not assigned")
        void getActiveIncidents_WhenOpenBetween48And72Hours_ShouldBeOrange() {
            // Given: Opened 50 hours ago
            Incident incident = createIncident(NOW.minusHours(50), null, 1L);
            when(incidentRepository.findOngoingWithDetails()).thenReturn(List.of(incident));

            // When
            List<DashboardService.ActiveIncident> activeIncidents = dashboardService.getActiveIncidents();

            // Then
            assertThat(activeIncidents.get(0).level()).isEqualTo(AttentionLevel.ORANGE);
            assertThat(activeIncidents.get(0).hoursOpen()).isEqualTo(50L);
        }

        @Test
        @DisplayName("Should return RED when open for 72+ hours and not assigned")
        void getActiveIncidents_WhenOpenOver72Hours_ShouldBeRed() {
            // Given: Opened 80 hours ago
            Incident incident = createIncident(NOW.minusHours(80), null, 1L);
            when(incidentRepository.findOngoingWithDetails()).thenReturn(List.of(incident));

            // When
            List<DashboardService.ActiveIncident> activeIncidents = dashboardService.getActiveIncidents();

            // Then
            assertThat(activeIncidents.get(0).level()).isEqualTo(AttentionLevel.RED);
            assertThat(activeIncidents.get(0).hoursOpen()).isEqualTo(80L);
        }

        @Test
        @DisplayName("Should return ASSIGNED regardless of age when provider is present")
        void getActiveIncidents_WhenProviderAssigned_ShouldBeAssigned() {
            // Given: Provider assigned on an old incident (opened 100h ago)
            Provider provider = mock(Provider.class);
            Incident incident = createIncident(NOW.minusHours(100), provider, 1L);
            when(incidentRepository.findOngoingWithDetails()).thenReturn(List.of(incident));

            // When
            List<DashboardService.ActiveIncident> activeIncidents = dashboardService.getActiveIncidents();

            // Then
            assertThat(activeIncidents.get(0).level()).isEqualTo(AttentionLevel.ASSIGNED);
        }
    }

    @Nested
    @DisplayName("Filter Tests")
    class FilterTests {

        @Test
        @DisplayName("filter with null stationId and level should return all items")
        void filter_WithNullFilters_ShouldReturnAll() {
            DashboardService.ActiveIncident item1 = createActiveIncident(100L, AttentionLevel.GREEN);
            DashboardService.ActiveIncident item2 = createActiveIncident(200L, AttentionLevel.RED);

            List<DashboardService.ActiveIncident> filtered = dashboardService.filter(
                    List.of(item1, item2), null, null
            );

            assertThat(filtered).containsExactly(item1, item2);
        }

        @Test
        @DisplayName("filter by stationId should filter out other stations")
        void filter_ByStationId_ShouldReturnMatchingStation() {
            DashboardService.ActiveIncident itemStation1 = createActiveIncident(100L, AttentionLevel.GREEN);
            DashboardService.ActiveIncident itemStation2 = createActiveIncident(200L, AttentionLevel.GREEN);

            List<DashboardService.ActiveIncident> filtered = dashboardService.filter(
                    List.of(itemStation1, itemStation2), 100L, null
            );

            assertThat(filtered).containsExactly(itemStation1);
        }

        @Test
        @DisplayName("filter by level should filter out other levels")
        void filter_ByLevel_ShouldReturnMatchingLevel() {
            DashboardService.ActiveIncident itemGreen = createActiveIncident(100L, AttentionLevel.GREEN);
            DashboardService.ActiveIncident itemRed = createActiveIncident(100L, AttentionLevel.RED);

            List<DashboardService.ActiveIncident> filtered = dashboardService.filter(
                    List.of(itemGreen, itemRed), null, AttentionLevel.RED
            );

            assertThat(filtered).containsExactly(itemRed);
        }

        @Test
        @DisplayName("filter by stationId AND level combined should apply both")
        void filter_ByStationAndLevel_ShouldApplyBothFilters() {
            DashboardService.ActiveIncident match = createActiveIncident(100L, AttentionLevel.RED);
            DashboardService.ActiveIncident wrongStation = createActiveIncident(200L, AttentionLevel.RED);
            DashboardService.ActiveIncident wrongLevel = createActiveIncident(100L, AttentionLevel.GREEN);

            List<DashboardService.ActiveIncident> filtered = dashboardService.filter(
                    List.of(match, wrongStation, wrongLevel), 100L, AttentionLevel.RED
            );

            assertThat(filtered).containsExactly(match);
        }
    }

    @Nested
    @DisplayName("countByLevel Tests")
    class CountByLevelTests {

        @Test
        @DisplayName("countByLevel on empty list should return 0 for all AttentionLevel values")
        void countByLevel_WhenEmpty_ShouldContainZeroForEveryLevel() {
            Map<AttentionLevel, Long> counts = dashboardService.countByLevel(List.of());

            assertThat(counts).hasSize(AttentionLevel.values().length);
            for (AttentionLevel level : AttentionLevel.values()) {
                assertThat(counts.get(level)).isEqualTo(0L);
            }
        }

        @Test
        @DisplayName("countByLevel should sum incident occurrences correctly per level")
        void countByLevel_ShouldSumByLevel() {
            DashboardService.ActiveIncident item1 = createActiveIncident(1L, AttentionLevel.RED);
            DashboardService.ActiveIncident item2 = createActiveIncident(1L, AttentionLevel.RED);
            DashboardService.ActiveIncident item3 = createActiveIncident(2L, AttentionLevel.ASSIGNED);

            Map<AttentionLevel, Long> counts = dashboardService.countByLevel(List.of(item1, item2, item3));

            assertThat(counts.get(AttentionLevel.RED)).isEqualTo(2L);
            assertThat(counts.get(AttentionLevel.ASSIGNED)).isEqualTo(1L);
            assertThat(counts.get(AttentionLevel.GREEN)).isEqualTo(0L);
            assertThat(counts.get(AttentionLevel.ORANGE)).isEqualTo(0L);
        }
    }

    // Helper method to create Incident with nested Station ID
    private Incident createIncident(LocalDateTime openingDate, Provider provider, Long stationId) {
        Station station = mock(Station.class);
        Mockito.lenient().when(station.getId()).thenReturn(stationId);

        Equipment equipment = mock(Equipment.class);
        Mockito.lenient().when(equipment.getStation()).thenReturn(station);

        Incident incident = new Incident();
        incident.setOpeningDate(openingDate);
        incident.setProvider(provider);
        incident.setEquipment(equipment);
        return incident;
    }

    // Helper method to create ActiveIncident stub for filtering & counting tests
    private DashboardService.ActiveIncident createActiveIncident(Long stationId, AttentionLevel level) {
        Incident incident = createIncident(NOW.minusHours(1), null, stationId);
        return new DashboardService.ActiveIncident(incident, level, 1L);
    }
}