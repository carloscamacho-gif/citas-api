package com.fcv.citas.scheduling.infrastructure.web.dto;

import com.fcv.citas.scheduling.domain.model.StatusHistoryEntry;
import com.fcv.citas.shared.web.ApiTime;

import java.time.OffsetDateTime;

public record StatusHistoryResponse(String status, String source, Long changedByUserId, String reason,
                                    OffsetDateTime changedAt) {

    public static StatusHistoryResponse from(StatusHistoryEntry e) {
        return new StatusHistoryResponse(e.status().name(), e.source().name(), e.changedByUserId(), e.reason(),
                ApiTime.toOffset(e.changedAt()));
    }
}
