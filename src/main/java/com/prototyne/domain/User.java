package com.prototyne.domain;

import com.prototyne.domain.common.BaseEntity;
import com.prototyne.domain.enums.Gender;
import com.prototyne.domain.mapping.Additional;
import com.prototyne.domain.mapping.Heart;
import com.prototyne.web.dto.DeliveryDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String username;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = true, length = 512)
    private String profileUrl;

    @Column(nullable = false)
    private LocalDateTime birth;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer tickets;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer familyMember;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean signupComplete;

    @Column(length = 20)
    private String deliveryName;

    @Column(length = 20)
    private String deliveryPhone;

    @Column(length = 200)
    private String deliveryAddress;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Investment> investmentList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Heart> heartList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Ticket> ticketList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Additional> additionalList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Alarm> alarmList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Feedback> feedbackList = new ArrayList<>();

    public void setDelivery(DeliveryDto deliveryDto) {
        this.deliveryName = deliveryDto.getDeliveryName();
        System.out.println("deliveryDto.getDeliveryPhone() = " + deliveryDto.getDeliveryPhone());
        this.deliveryPhone = deliveryDto.getDeliveryPhone();
        this.deliveryAddress = deliveryDto.getDeliveryAddress();
    }
}
