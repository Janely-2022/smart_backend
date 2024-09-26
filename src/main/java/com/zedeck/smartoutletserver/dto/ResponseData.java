package com.zedeck.smartoutletserver.dto;

import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ResponseData {

    private  Integer code;
    private  String message;
}
