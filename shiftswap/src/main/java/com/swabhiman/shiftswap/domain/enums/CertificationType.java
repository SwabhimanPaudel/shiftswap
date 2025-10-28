package com.swabhiman.shiftswap.domain.enums;

public enum CertificationType {
    BLS("Basic Life Support"),
    ACLS("Advanced Cardiovascular Life Support"),
    PALS("Pediatric Advanced Life Support"),
    NRP("Neonatal Resuscitation Program"),
    TNCC("Trauma Nursing Core Course"),
    CCRN("Critical Care Registered Nurse");

    private final String displayName;

    CertificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}