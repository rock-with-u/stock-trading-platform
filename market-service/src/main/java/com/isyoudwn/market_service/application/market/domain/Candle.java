package com.isyoudwn.market_service.application.market.domain;

import com.isyoudwn.common_service.domain.Stock;
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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "candle",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_candle_stock_timeframe_open_time",
                columnNames = {"stock_id", "timeframe", "open_time"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Candle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candle_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Enumerated(EnumType.STRING)
    @Column(name = "timeframe", nullable = false, length = 10)
    private CandleTimeframe timeframe;

    @Column(name = "open_time", nullable = false)
    private LocalDateTime openTime;

    @Column(name = "open_price", nullable = false)
    private Long openPrice;

    @Column(name = "high_price", nullable = false)
    private Long highPrice;

    @Column(name = "low_price", nullable = false)
    private Long lowPrice;

    @Column(name = "close_price", nullable = false)
    private Long closePrice;

    @Column(name = "volume", nullable = false)
    private Long volume;

    private Candle(
            Stock stock,
            CandleTimeframe timeframe,
            LocalDateTime openTime,
            long openPrice,
            long highPrice,
            long lowPrice,
            long closePrice,
            long volume
    ) {
        this.stock = stock;
        this.timeframe = timeframe;
        this.openTime = openTime;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
        this.volume = volume;
    }

    public static Candle create(
            Stock stock,
            CandleTimeframe timeframe,
            LocalDateTime openTime,
            long openPrice,
            long highPrice,
            long lowPrice,
            long closePrice,
            long volume
    ) {
        return new Candle(stock, timeframe, openTime, openPrice, highPrice, lowPrice, closePrice, volume);
    }
}

