package com.ot.popIce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseStructure<T> {
    private int status;
    private String message;
    private T data;
}