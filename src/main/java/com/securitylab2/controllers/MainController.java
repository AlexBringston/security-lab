package com.securitylab2.controllers;

import com.securitylab2.FileManager;
import com.securitylab2.models.Captcha;
import com.securitylab2.services.CaptchaService;
import com.securitylab2.services.FileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    private final FileService fileService;
    private final CaptchaService captchaService;

    public MainController(FileService fileService, CaptchaService captchaService) {
        this.fileService = fileService;
        this.captchaService = captchaService;

        fileService.setupCaptchaDB();
    }

    @GetMapping
    public String indexPage(Model model) {
        Captcha captchaImage = fileService.getRandomCaptchaFromDB();
        model.addAttribute("noisyImage", Base64Utils.encodeToString(captchaImage.getImage()));
        model.addAttribute("hiddenCaptcha", captchaImage.getCaptchaText());
        return "captchaview";
    }

    @GetMapping("/success")
    public String getSuccessPage() {
        return "successpage";
    }

    @PostMapping("/save")
    public String activateProgram(Model model,
                                  @RequestParam("g-recaptcha-response") String googleCaptcha,
                                  @RequestParam("captchaText") String captchaText,
                                  @RequestParam("hiddenCaptcha") String hiddenCaptcha) {

        if(!captchaService.isCaptchaValid(googleCaptcha, captchaText, hiddenCaptcha)) {
            Captcha captchaImage = fileService.getRandomCaptchaFromDB();
            model.addAttribute("noisyImage", Base64Utils.encodeToString(captchaImage.getImage()));
            model.addAttribute("hiddenCaptcha", captchaImage.getCaptchaText());
            model.addAttribute("errorMessage", "Please pass the captcha to proceed");
            return "captchaview";
        }
        FileManager.setIsCaptchaEntered(true);
        return  "redirect:/success";
    }
}
