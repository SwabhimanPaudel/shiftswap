package com.swabhiman.shiftswap.domain.enums;

public enum Unit {
    ICU("Intensive Care Unit"),
    ER("Emergency Room"),
    MED_SURG("Medical-Surgical"),
    PEDIATRICS("Pediatrics"),
    ONCOLOGY("Oncology"),
    TELEMETRY("Telemetry");

    private final String displayName;

    Unit(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}