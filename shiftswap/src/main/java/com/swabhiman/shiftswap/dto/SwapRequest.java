package com.swabhiman.shiftswap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SwapRequest {
    @NotNull
    private Long shiftId;

    @NotBlank
    private String reason;
}


