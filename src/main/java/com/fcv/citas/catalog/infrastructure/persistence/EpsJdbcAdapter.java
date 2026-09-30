package com.fcv.citas.catalog.infrastructure.persistence;

import com.fcv.citas.catalog.domain.model.Eps;
import com.fcv.citas.catalog.domain.port.out.EpsRepositoryPort;
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

/** Persistencia JDBC de EPS (HU-007). */
@Component
public class EpsJdbcAdapter implements EpsRepositoryPort {

    private static final RowMapper<Eps> EPS = (rs, row) ->
            new Eps(rs.getLong("id"), rs.getString("code"), rs.getString("name"), rs.getBoolean("active"));

    private final JdbcTemplate jdbc;

    public EpsJdbcAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Eps> findAll() {
        return jdbc.query("SELECT id, code, name, active FROM eps ORDER BY name", EPS);
    }

    @Override
    public Optional<Eps> findById(Long id) {
        return jdbc.query("SELECT id, code, name, active FROM eps WHERE id = ?", EPS, id).stream().findFirst();
    }

    @Override
    public boolean existsByCode(String code) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM eps WHERE code = ?", Integer.class, code);
        return count != null && count > 0;
    }

    @Override
    public boolean exists(Long id) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM eps WHERE id = ?", Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public Eps save(Eps eps) {
        if (eps.id() == null) {
            KeyHolder keys = new GeneratedKeyHolder();
            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO eps (code, name, active) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, eps.code());
                ps.setString(2, eps.name());
                ps.setBoolean(3, eps.active());
                return ps;
            }, keys);
            return new Eps(Objects.requireNonNull(keys.getKey()).longValue(), eps.code(), eps.name(), eps.active());
        }
        jdbc.update("UPDATE eps SET name = ?, active = ? WHERE id = ?", eps.name(), eps.active(), eps.id());
        return eps;
    }

    @Override
    public boolean isReferenced(Long id) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM eps_plans WHERE eps_id = ?", Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public void delete(Long id) {
        jdbc.update("DELETE FROM eps WHERE id = ?", id);
    }
}
