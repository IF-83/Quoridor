package com.codecool.websocket.service;
import lombok.Data;

@Data
public class FieldNode {

    private FieldNode left = null;
    private FieldNode right = null;
    private FieldNode up = null;
    private FieldNode down = null;

}
