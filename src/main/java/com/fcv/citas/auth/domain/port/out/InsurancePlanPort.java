package com.fcv.citas.auth.domain.port.out;

public interface InsurancePlanPort {
    boolean isActive(Long planId);
}
