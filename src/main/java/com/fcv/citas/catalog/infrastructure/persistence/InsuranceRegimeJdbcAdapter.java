package com.fcv.citas.catalog.infrastructure.persistence;

import com.fcv.citas.catalog.domain.model.InsuranceRegime;
import com.fcv.citas.catalog.domain.port.out.InsuranceRegimeRepositoryPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/** Persistencia JDBC de regímenes de aseguramiento (catálogo de referencia para los planes). */
@Component
public class InsuranceRegimeJdbcAdapter implements InsuranceRegimeRepositoryPort {

    private static final RowMapper<InsuranceRegime> REGIME = (rs, row) ->
            new InsuranceRegime(rs.getLong("id"), rs.getString("code"), rs.getString("name"));

    private final JdbcTemplate jdbc;

    public InsuranceRegimeJdbcAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<InsuranceRegime> findAll() {
        return jdbc.query("SELECT id, code, name FROM insurance_regimes ORDER BY name", REGIME);
    }

    @Override
    public boolean exists(Long id) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM insurance_regimes WHERE id = ?", Integer.class, id);
        return count != null && count > 0;
    }
}
