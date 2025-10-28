package com.swabhiman.shiftswap.domain.enums;

public enum Role {
    RN("Registered Nurse"),
    LPN("Licensed Practical Nurse"),
    CNA("Certified Nursing Assistant"),
    RT("Respiratory Therapist"),
    TECH("Medical Technician");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}