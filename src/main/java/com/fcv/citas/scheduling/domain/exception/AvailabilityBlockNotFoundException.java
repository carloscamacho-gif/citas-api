package com.fcv.citas.scheduling.domain.exception;
public class AvailabilityBlockNotFoundException extends RuntimeException { public AvailabilityBlockNotFoundException(Long id){super("No existe el bloque de disponibilidad con id "+id);} }
