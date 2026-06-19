package com.isyoudwn.market_service.infrastructure.kis;

import com.isyoudwn.market_service.infrastructure.kis.repository.KisApprovalKeyRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KisApprovalKeyServiceImpl implements KisApprovalKeyService {

    private final KisApprovalKeyClient approvalKeyClient;
    private final KisApprovalKeyRepository approvalKeyRepository;

    @Override
    public String getOrIssue() {
        return approvalKeyRepository.find()
                .orElseGet(this::issueAndSave);
    }

    @Override
    public void issueIfAbsent() {
        Optional<String> savedApprovalKey = approvalKeyRepository.find();
        if (savedApprovalKey.isPresent()) {
            log.info("KIS WebSocket 접속키가 이미 존재합니다.");
            return;
        }

        issueAndSave();
    }

    @Override
    public String issueAndSave() {
        String approvalKey = approvalKeyClient.issueApprovalKey();

        approvalKeyRepository.save(approvalKey);

        log.info("KIS WebSocket 접속키 발급 및 저장 완료");

        return approvalKey;
    }
}
