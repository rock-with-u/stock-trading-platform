package com.isyoudwn.market_service.auth.infrastructure;

import com.isyoudwn.common_service.exception.KisException;
import com.isyoudwn.common_service.response.ResponseMessage;
import com.isyoudwn.market_service.common.config.KisProperties;
import com.isyoudwn.market_service.auth.infrastructure.dto.KisApprovalKeyDto.KisApprovalKeyRequest;
import com.isyoudwn.market_service.auth.infrastructure.dto.KisApprovalKeyDto.KisApprovalKeyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class KisApprovalKeyClient {

    private final RestClient kisRestClient;
    private final KisProperties kisProperties;

    public String issueApprovalKey() {
        KisApprovalKeyResponse kisApprovalKeyResponse = kisRestClient.post()
                .uri("/oauth2/Approval")
                .contentType(MediaType.APPLICATION_JSON)
                .body(KisApprovalKeyRequest.of(
                        kisProperties.appKey(),
                        kisProperties.appSecret()
                ))
                .retrieve()
                .body(KisApprovalKeyResponse.class);

        return validate(kisApprovalKeyResponse);
    }

    private String validate(KisApprovalKeyResponse kisApprovalKeyResponse) {
        if (kisApprovalKeyResponse == null || kisApprovalKeyResponse.approvalKey() == null
                || kisApprovalKeyResponse.approvalKey().isBlank()) {
            throw new KisException(ResponseMessage.APPROVAL_KEY_ISSUE_FAIL);
        }

        return kisApprovalKeyResponse.approvalKey();
    }
}
