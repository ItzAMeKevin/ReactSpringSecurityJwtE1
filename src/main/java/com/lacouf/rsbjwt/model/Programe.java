package com.lacouf.rsbjwt.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor

public enum Programe {
    TECHNIQUES_INFORMATIQUE("420.B0"),
    TECHNIQUES_GESTION_HOTELIERE("430.A0"),
    TECHNIQUE_GENIE_CIVIL("221B0"),
    SOINS_INFIRMIERS("180.A0"),
    TECHNIQUE_GENIE_PHYSIQUE("244.A0"),
    TECHNIQUES_LOGISTIQUE_TRANSPORT("410.A0");

    private final String code;

}
