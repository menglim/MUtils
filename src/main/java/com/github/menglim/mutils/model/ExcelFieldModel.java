package com.github.menglim.mutils.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExcelFieldModel {

    private String fieldName;
    private Object fieldValue;
    private int fieldOrder;
}
