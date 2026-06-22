package com.isyoudwn.market_service.auth.application;

public interface KisApprovalKeyService {
    String getOrIssue();
    void issueIfAbsent();
    String issueAndSave();
}
