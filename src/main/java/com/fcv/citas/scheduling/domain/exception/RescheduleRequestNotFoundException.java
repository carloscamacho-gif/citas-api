package com.fcv.citas.scheduling.domain.exception;

/** No existe una solicitud de reprogramación con el id indicado. Se mapea a 404. */
public class RescheduleRequestNotFoundException extends RuntimeException {
    public RescheduleRequestNotFoundException(Long id) {
        super("No existe la solicitud de reprogramación " + id);
    }
}
