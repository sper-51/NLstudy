package com.nl.nlstudy.common;

import lombok.Data;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Data
public class PageRequest {
    @Min(value = 1, message = "页码必须大于0")
    private int page = 1;

    @Min(value = 1, message = "每页条数必须大于0")
    @Max(value = 100, message = "每页条数不能超过100")
    private int pageSize = 10;
}
