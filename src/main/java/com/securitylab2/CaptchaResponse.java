package com.securitylab2;

import lombok.Data;

@Data
public class CaptchaResponse {

    private boolean success;
    private String challengeTs;
    private String hostname;
}
