package com.fcv.citas.catalog.infrastructure.persistence;

import com.fcv.citas.auth.domain.port.out.InsurancePlanPort;
import com.fcv.citas.auth.domain.port.out.UserAffiliationPort;
import com.fcv.citas.catalog.domain.model.InsurancePlan;
import com.fcv.citas.catalog.domain.port.out.InsurancePlanRepositoryPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class InsurancePlanPersistenceAdapter implements InsurancePlanRepositoryPort, InsurancePlanPort, UserAffiliationPort {
    private final JdbcTemplate jdbc;
    public InsurancePlanPersistenceAdapter(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @Override public List<InsurancePlan> findActive() {
        return jdbc.query("SELECT id, code, name, active FROM eps_plans WHERE active = TRUE ORDER BY name",
                (rs, row) -> new InsurancePlan(rs.getLong("id"), rs.getString("code"), rs.getString("name"), rs.getBoolean("active")));
    }
    @Override public boolean isActive(Long id) {
        return Boolean.TRUE.equals(jdbc.query("SELECT active FROM eps_plans WHERE id = ?", rs -> rs.next() ? rs.getBoolean(1) : null, id));
    }
    @Override public void create(Long userId, Long planId) {
        jdbc.update("INSERT INTO user_insurance_affiliations (user_id, plan_id) VALUES (?, ?)", userId, planId);
    }
}
