package com.isyoudwn.account_service.account.infrastructure;

import com.isyoudwn.account_service.account.domain.PostingSourceType;
import com.isyoudwn.account_service.account.domain.PostingType;
import com.isyoudwn.account_service.account.domain.StockPositionChange;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockPositionChangeRepository extends JpaRepository<StockPositionChange, Long> {

    @Query("""
                select c
                from StockPositionChange c
                join c.accountPosting p
                where p.sourceId = :orderId
                  and p.sourceType = :sourceType
                  and p.postingType = :postingType
            """)
    List<StockPositionChange> findByPostingSource(
            @Param("orderId") Long orderId,
            @Param("sourceType") PostingSourceType sourceType,
            @Param("postingType") PostingType postingType
    );

    default List<StockPositionChange> findSellReservationChanges(Long orderId) {
        return findByPostingSource(
                orderId,
                PostingSourceType.STOCK_ORDER,
                PostingType.SELL_QUANTITY_RESERVED
        );
    }
}
