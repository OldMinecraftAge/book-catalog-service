package com.vishnu.bookcatalog.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record TotpRequest(@Min(0) @Max(999999) int totpCode) {
}