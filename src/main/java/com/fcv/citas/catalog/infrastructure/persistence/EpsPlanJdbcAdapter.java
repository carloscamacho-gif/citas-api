package com.fcv.citas.catalog.infrastructure.persistence;

import com.fcv.citas.catalog.domain.model.EpsPlan;
import com.fcv.citas.catalog.domain.port.out.EpsPlanRepositoryPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Persistencia JDBC de planes de EPS (HU-008). */
@Component
public class EpsPlanJdbcAdapter implements EpsPlanRepositoryPort {

    private static final RowMapper<EpsPlan> PLAN = (rs, row) -> new EpsPlan(
            rs.getLong("id"), rs.getLong("eps_id"), rs.getString("eps_name"),
            rs.getLong("regime_id"), rs.getString("regime_name"),
            rs.getString("code"), rs.getString("name"), rs.getBoolean("active"));

    private static final String SELECT = """
            SELECT p.id, p.eps_id, e.name AS eps_name, p.regime_id, r.name AS regime_name,
                   p.code, p.name, p.active
            FROM eps_plans p
            JOIN eps e ON e.id = p.eps_id
            JOIN insurance_regimes r ON r.id = p.regime_id
            """;

    private final JdbcTemplate jdbc;

    public EpsPlanJdbcAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<EpsPlan> findAll() {
        return jdbc.query(SELECT + " ORDER BY e.name, p.name", PLAN);
    }

    @Override
    public Optional<EpsPlan> findById(Long id) {
        return jdbc.query(SELECT + " WHERE p.id = ?", PLAN, id).stream().findFirst();
    }

    @Override
    public boolean existsByCode(String code) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM eps_plans WHERE code = ?", Integer.class, code);
        return count != null && count > 0;
    }

    @Override
    public EpsPlan save(EpsPlan plan) {
        if (plan.id() == null) {
            KeyHolder keys = new GeneratedKeyHolder();
            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO eps_plans (eps_id, regime_id, code, name, active) VALUES (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, plan.epsId());
                ps.setLong(2, plan.regimeId());
                ps.setString(3, plan.code());
                ps.setString(4, plan.name());
                ps.setBoolean(5, plan.active());
                return ps;
            }, keys);
            return findById(Objects.requireNonNull(keys.getKey()).longValue()).orElseThrow();
        }
        jdbc.update("UPDATE eps_plans SET name = ?, active = ? WHERE id = ?", plan.name(), plan.active(), plan.id());
        return findById(plan.id()).orElseThrow();
    }

    @Override
    public boolean isReferenced(Long id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM user_insurance_affiliations WHERE plan_id = ?", Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public void delete(Long id) {
        jdbc.update("DELETE FROM eps_plans WHERE id = ?", id);
    }
}
