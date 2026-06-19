package com.isyoudwn.market_service.infrastructure.kis;

public interface KisApprovalKeyService {
    String getOrIssue();
    void issueIfAbsent();
    String issueAndSave();
}
