package com.fcv.citas.catalog.domain.port.in;

import com.fcv.citas.catalog.domain.model.Location;

import java.util.List;

public interface ListLocationsUseCase {
    List<Location> list();
}
