package com.isyoudwn.account_service.account.infrastructure;

import com.isyoudwn.account_service.account.domain.CashBalanceChange;
import com.isyoudwn.account_service.account.domain.PostingSourceType;
import com.isyoudwn.account_service.account.domain.PostingType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CashBalanceChangeRepository extends JpaRepository<CashBalanceChange, Long> {

    @Query("""
                select c
                from CashBalanceChange c
                join c.accountPosting p
                where p.sourceType = :sourceType
                  and p.sourceId = :sourceId
                  and p.postingType = :postingType
            """)
    List<CashBalanceChange> findByPostingSource(
            @Param("sourceType") PostingSourceType sourceType,
            @Param("sourceId") Long sourceId,
            @Param("postingType") PostingType postingType
    );

    default List<CashBalanceChange> findBuyReservationChanges(Long orderId) {
        return findByPostingSource(
                PostingSourceType.STOCK_ORDER,
                orderId,
                PostingType.BUY_ORDER_RESERVED
        );
    }
}
