package com.isyoudwn.market_service.infrastructure.kis.auth;

public interface KisApprovalKeyService {
    String getOrIssue();
    void issueIfAbsent();
    String issueAndSave();
}
