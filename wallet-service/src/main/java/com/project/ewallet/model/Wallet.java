package com.project.ewallet.model;

import com.project.ewallet.UserIdentifier;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private Long userId;

    private String phoneNumber;
    private Double balance;

    @Enumerated(value = EnumType.STRING)
    private UserIdentifier userIdentifier;

    private String identifierValue;
}
