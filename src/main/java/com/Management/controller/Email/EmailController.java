package com.Management.controller.Email;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Management.Model.ApiResponse;
import com.Management.service.Email.EmailService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/auth")
public class EmailController {

    @Autowired
    private EmailService emailService;

    // @GetMapping("/send-email")
    // public String sendEmail() throws MessagingException {
    // String to = "mandiptamang159@gmail.com";
    // String subject = "Custom Email";
    // String body = "This is a custom email body.";
    // String recipientName = "Recipient Name";

    // emailService.sendEmail(to, subject, body, recipientName);

    // return "email-sent"; // Redirect to a success page
    // }

    @PostMapping("/send-email")
    public ResponseEntity<ApiResponse<?>> sendEmail(@RequestParam("to") String to,
            @RequestParam("subject") String subject,
            @RequestParam("personalizedMessage") String personalizedMessage,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("age") int age,
            @RequestParam("attachments") MultipartFile[] attachments) throws MessagingException, IOException {

        emailService.sendEmail(to, subject, personalizedMessage, name, email, age, attachments);

        ApiResponse<?> response = new ApiResponse<>(
                "Success",
                HttpStatus.OK.value(),
                "Email sent successfully",
                null,
                java.time.LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

}
