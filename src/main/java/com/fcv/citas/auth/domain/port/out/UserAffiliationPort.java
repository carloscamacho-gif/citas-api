package com.fcv.citas.auth.domain.port.out;

public interface UserAffiliationPort {
    void create(Long userId, Long planId);
}
