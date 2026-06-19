package com.isyoudwn.market_service.infrastructure.kis.repository;

import java.util.Optional;

public interface KisApprovalKeyRepository {
    void save(String approvalKey);

    Optional<String> find();
}
