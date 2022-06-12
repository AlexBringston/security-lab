package com.securitylab2.services;

import com.securitylab2.CaptchaResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CaptchaService {

    private RestTemplate restTemplate;

    public CaptchaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean isCaptchaValid(String googleCaptcha, String captchaText, String trueCaptcha) {
        String url = "https://www.google.com/recaptcha/api/siteverify";
        String params = "?secret=6LfnYPkfAAAAAMdVv0TZs2QCmTa6p923lW-h7voT&response=" + googleCaptcha;
        String completeURL = url+params;
        CaptchaResponse response = restTemplate.postForObject(completeURL,null, CaptchaResponse.class);
        return response.isSuccess() && (captchaText.equals(trueCaptcha));
    }
}
