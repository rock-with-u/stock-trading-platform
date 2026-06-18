package com.isyoudwn.account_service.domain;


import com.isyoudwn.common_service.domain.BaseEntity;
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
@Table(name = "account")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "account_number", nullable = false, unique = true, length = 20)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "activate_status", nullable = false, length = 20)
    private AccountStatus activateStatus;

    @Column(name = "available_settled_cash_amount", nullable = false)
    private long availableSettledCashAmount;

    @Column(name = "reserved_buy_amount", nullable = false)
    private long reservedBuyAmount;

    @Column(name = "unsettled_sell_receivable_amount", nullable = false)
    private long unsettledSellReceivableAmount;

    private Account(Member member, String accountNumber) {
        this.member = member;
        this.accountNumber = accountNumber;
        this.activateStatus = AccountStatus.ENABLE;
        this.availableSettledCashAmount = 0L;
        this.reservedBuyAmount = 0L;
        this.unsettledSellReceivableAmount = 0L;
    }
}
