CREATE TABLE insurance_regimes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL
) ENGINE = InnoDB;

CREATE TABLE eps (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE = InnoDB;

CREATE TABLE eps_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eps_id BIGINT NOT NULL,
    regime_id BIGINT NOT NULL,
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_eps_plans_eps FOREIGN KEY (eps_id) REFERENCES eps(id),
    CONSTRAINT fk_eps_plans_regime FOREIGN KEY (regime_id) REFERENCES insurance_regimes(id)
) ENGINE = InnoDB;

CREATE TABLE user_insurance_affiliations (
    user_id BIGINT PRIMARY KEY,
    plan_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_insurance_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_insurance_plan FOREIGN KEY (plan_id) REFERENCES eps_plans(id)
) ENGINE = InnoDB;

INSERT INTO insurance_regimes (code, name) VALUES ('CONTRIBUTORY', 'Contributivo');
INSERT INTO eps (code, name) VALUES ('EPS-SINTETICA-01', 'EPS Sintética Uno');
INSERT INTO eps_plans (eps_id, regime_id, code, name)
SELECT e.id, r.id, 'PLAN-SINTETICO-01', 'Plan Integral Básico'
FROM eps e CROSS JOIN insurance_regimes r
WHERE e.code = 'EPS-SINTETICA-01' AND r.code = 'CONTRIBUTORY';
