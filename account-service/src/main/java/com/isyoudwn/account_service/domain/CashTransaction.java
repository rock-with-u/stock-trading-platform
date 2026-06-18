package com.isyoudwn.account_service.domain;

import com.isyoudwn.common_service.domain.BaseEntity;
import com.isyoudwn.common_service.exception.AccountException;
import com.isyoudwn.common_service.response.ResponseMessage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "cash_transaction")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CashTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cash_transaction_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "cash_transaction_amount", nullable = false)
    private Long transactionAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "cash_transaction_type", nullable = false, length = 20)
    private CashTransactionType transactionType;

    @Column(name = "request_key", nullable = false, unique = true, length = 100)
    private String transactionRequestKey;

    private CashTransaction(Account account, long transactionAmount, CashTransactionType transactionType,
                            String transactionRequestKey) {
        this.account = account;
        this.transactionAmount = transactionAmount;
        this.transactionType = transactionType;
        this.transactionRequestKey = transactionRequestKey;
    }

    public static CashTransaction create(
            Account account,
            long transactionAmount,
            CashTransactionType transactionType,
            String transactionRequestKey
    ) {
        if (transactionAmount <= 0) {
            throw new AccountException(ResponseMessage.INVALID_CASH_TRANSACTION_AMOUNT);
        }
        return new CashTransaction(account, transactionAmount, transactionType, transactionRequestKey);
    }
}

