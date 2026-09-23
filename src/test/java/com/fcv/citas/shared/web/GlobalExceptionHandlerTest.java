package com.fcv.citas.shared.web;

import com.fcv.citas.auth.domain.exception.InvalidInsurancePlanException;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.http.HttpStatus;

class GlobalExceptionHandlerTest {
    @Test
    void mapsInvalidInsurancePlanToControlledBadRequest() {
        var response = new GlobalExceptionHandler().handleInvalidInsurancePlan(new InvalidInsurancePlanException(8L));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).contains("no existe o está inactivo");
    }
}
