package com.prototyne.domain;

import com.prototyne.domain.common.BaseEntity;
import com.prototyne.domain.enums.AddsetTitle;
import com.prototyne.domain.mapping.Additional;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ADD_set extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AddsetTitle title;

    @Column(nullable = false, length = 20)
    private String value;

    @OneToMany(mappedBy ="addSet",cascade=CascadeType.ALL)
    private List<Additional> additionalList=new ArrayList<>();




}
