package com.fcv.citas.auth.infrastructure.security;

import com.fcv.citas.auth.domain.model.DocumentType;
import com.fcv.citas.auth.domain.model.RoleName;
import com.fcv.citas.auth.domain.model.User;
import com.fcv.citas.catalog.domain.port.in.CreateSpecialtyUseCase;
import com.fcv.citas.catalog.domain.port.in.ListActiveInsurancePlansUseCase;
import com.fcv.citas.catalog.domain.port.in.ListLocationsUseCase;
import com.fcv.citas.catalog.domain.port.in.ListSpecialtiesUseCase;
import com.fcv.citas.catalog.domain.port.in.UpdateSpecialtyUseCase;
import com.fcv.citas.catalog.infrastructure.web.AdminSpecialtyController;
import com.fcv.citas.catalog.infrastructure.web.CatalogController;
import com.fcv.citas.professional.domain.port.in.ProfessionalManagementUseCase;
import com.fcv.citas.professional.infrastructure.web.AdminProfessionalController;
import com.fcv.citas.scheduling.domain.port.in.AppointmentDecisionUseCase;
import com.fcv.citas.scheduling.domain.port.in.AppointmentHistoryUseCase;
import com.fcv.citas.scheduling.domain.port.in.AvailabilityBlockUseCase;
import com.fcv.citas.scheduling.domain.port.in.AvailabilityQueryUseCase;
import com.fcv.citas.scheduling.domain.port.in.BookAppointmentUseCase;
import com.fcv.citas.scheduling.infrastructure.web.AdminAppointmentController;
import com.fcv.citas.scheduling.infrastructure.web.AppointmentController;
import com.fcv.citas.scheduling.infrastructure.web.AvailabilityController;
import com.fcv.citas.scheduling.infrastructure.web.ProfessionalAvailabilityController;
import com.fcv.citas.shared.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.Instant;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Autorización por rol y ownership (PRD sección 8). Usa la cadena real de Spring Security y JWT reales
 * emitidos por JwtTokenIssuerAdapter, sin base de datos: los casos de uso se sustituyen por mocks.
 *
 * <ul>
 *   <li>Sin sesión o con token inválido/vencido → 401 (el cliente debe reautenticarse).</li>
 *   <li>Con sesión pero rol insuficiente → 403.</li>
 * </ul>
 */
@WebMvcTest(controllers = {
        CatalogController.class, AdminSpecialtyController.class, AdminProfessionalController.class,
        AdminAppointmentController.class, ProfessionalAvailabilityController.class,
        AvailabilityController.class, AppointmentController.class
})
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtTokenIssuerAdapter.class, GlobalExceptionHandler.class})
@TestPropertySource(properties = {
        "app.jwt.access-secret=test-access-secret-must-be-32-chars-min",
        "app.jwt.refresh-secret=test-refresh-secret-must-be-32-chars-min",
        "app.jwt.access-minutes=15",
        "app.jwt.refresh-days=7",
        "app.cors.allowed-origins=http://localhost:5173"
})
class SecurityAuthorizationTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private JwtTokenIssuerAdapter tokens;

    @MockitoBean
    private ListLocationsUseCase listLocations;
    @MockitoBean
    private ListSpecialtiesUseCase listSpecialties;
    @MockitoBean
    private ListActiveInsurancePlansUseCase listPlans;
    @MockitoBean
    private CreateSpecialtyUseCase createSpecialty;
    @MockitoBean
    private UpdateSpecialtyUseCase updateSpecialty;
    @MockitoBean
    private ProfessionalManagementUseCase professionalManagement;
    @MockitoBean
    private AppointmentDecisionUseCase appointmentDecision;
    @MockitoBean
    private AvailabilityBlockUseCase availabilityBlocks;
    @MockitoBean
    private AvailabilityQueryUseCase availabilityQuery;
    @MockitoBean
    private BookAppointmentUseCase bookAppointment;
    @MockitoBean
    private AppointmentHistoryUseCase appointmentHistory;

    private String bearer(RoleName role) {
        User user = new User(9L, "Test", "User", DocumentType.CC, "1", "t@example.test", "3", "hash",
                Set.of(role), true, Instant.now());
        return "Bearer " + tokens.issueAccessToken(user);
    }

    private static MockHttpServletRequestBuilder withBearer(MockHttpServletRequestBuilder builder, String bearer) {
        return builder.header("Authorization", bearer);
    }

    // ---- autenticación --------------------------------------------------------------------------------

    @Test
    void protectedEndpointWithoutTokenIsUnauthorized() throws Exception {
        mvc.perform(get("/api/v1/availability").param("locationId", "1").param("specialtyId", "1")
                        .param("date", "2026-10-06"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void malformedTokenIsUnauthorized() throws Exception {
        mvc.perform(get("/api/v1/admin/specialties").header("Authorization", "Bearer no-es-un-jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void publicCatalogsNeedNoToken() throws Exception {
        mvc.perform(get("/api/v1/catalogs/locations")).andExpect(status().isOk());
        mvc.perform(get("/api/v1/specialties")).andExpect(status().isOk());
        mvc.perform(get("/api/v1/catalogs/plans")).andExpect(status().isOk());
    }

    // ---- ADMIN ----------------------------------------------------------------------------------------

    @Test
    void adminEndpointsRejectUserAndProfessional() throws Exception {
        for (RoleName role : new RoleName[]{RoleName.USER, RoleName.PROFESSIONAL}) {
            mvc.perform(withBearer(get("/api/v1/admin/specialties"), bearer(role))).andExpect(status().isForbidden());
            mvc.perform(withBearer(get("/api/v1/admin/professionals"), bearer(role))).andExpect(status().isForbidden());
            mvc.perform(withBearer(get("/api/v1/admin/appointments/pending-specialized"), bearer(role)))
                    .andExpect(status().isForbidden());
        }
    }

    @Test
    void adminEndpointsAcceptAdmin() throws Exception {
        String admin = bearer(RoleName.ADMIN);
        mvc.perform(withBearer(get("/api/v1/admin/specialties"), admin)).andExpect(status().isOk());
        mvc.perform(withBearer(get("/api/v1/admin/professionals"), admin)).andExpect(status().isOk());
        mvc.perform(withBearer(get("/api/v1/admin/appointments/pending-specialized"), admin)).andExpect(status().isOk());
    }

    // ---- PROFESSIONAL ---------------------------------------------------------------------------------

    @Test
    void availabilityBlocksAreOnlyForProfessionals() throws Exception {
        mvc.perform(withBearer(get("/api/v1/professional/availability-blocks"), bearer(RoleName.USER)))
                .andExpect(status().isForbidden());
        mvc.perform(withBearer(get("/api/v1/professional/availability-blocks"), bearer(RoleName.ADMIN)))
                .andExpect(status().isForbidden());
        mvc.perform(withBearer(get("/api/v1/professional/availability-blocks"), bearer(RoleName.PROFESSIONAL)))
                .andExpect(status().isOk());
    }

    // ---- USER -----------------------------------------------------------------------------------------

    @Test
    void onlyUsersCanBookAppointments() throws Exception {
        String emptyBody = "{}";
        for (RoleName role : new RoleName[]{RoleName.PROFESSIONAL, RoleName.ADMIN}) {
            mvc.perform(withBearer(post("/api/v1/appointments"), bearer(role))
                            .contentType(MediaType.APPLICATION_JSON).content(emptyBody))
                    .andExpect(status().isForbidden());
        }
        // Un USER pasa la autorización: el cuerpo vacío falla después, en la validación (400, no 403).
        mvc.perform(withBearer(post("/api/v1/appointments"), bearer(RoleName.USER))
                        .contentType(MediaType.APPLICATION_JSON).content(emptyBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void anyAuthenticatedRoleCanSearchAvailability() throws Exception {
        for (RoleName role : RoleName.values()) {
            mvc.perform(withBearer(get("/api/v1/availability"), bearer(role))
                            .param("locationId", "1").param("specialtyId", "1").param("date", "2026-10-06"))
                    .andExpect(status().isOk());
        }
    }
}
