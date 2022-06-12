package com.securitylab2.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "captchas")
public class Captcha {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "captcha_text")
    private String captchaText;

    @Lob
    @Column(name = "image")
    private byte[] image;

    public Captcha(String captchaText, byte[] image) {
        this.captchaText = captchaText;
        this.image = image;
    }
}
