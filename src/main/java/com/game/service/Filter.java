package com.game.service;

public class Filter {
    private String field;
    private QueryOperator operator;
    private String value;

    public Filter(String field, QueryOperator operator, String value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public QueryOperator getOperator() {
        return operator;
    }

    public String getValue() {
        return value;
    }
}
