package com.isyoudwn.common_service.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "stock")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long id;

    @Column(name = "stock_code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "stock_name", nullable = false, length = 100)
    private String name;

    private Stock(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Stock create(String code, String name) {
        return new Stock(code, name);
    }
}
