package com.isyoudwn.account_service.domain;

import com.isyoudwn.common_service.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "member_email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "member_name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_status", nullable = false, length = 20)
    private MemberStatus status;

    private Member(String email, String name) {
        this.email = email;
        this.name = name;
        this.status = MemberStatus.ENABLE;
    }

    public static Member create(String email, String name) {
        return new Member(email, name);
    }

    public void disable() {
        this.status = MemberStatus.DISABLE;
    }

    public void enable() {
        this.status = MemberStatus.ENABLE;
    }
}
