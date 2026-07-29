package com.hackathon.HackSync.utils.service;

import com.hackathon.HackSync.auth.entity.ROLE;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendVerificationEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);

        emailSender.send(message);
    }

    public void sendInvitationEmail(String email, ROLE role, String hackathonTitle) {
        String subject = "Invitation to be a " + role.name() + " at " + hackathonTitle;
        String htmlTemplate = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>You're Invited</title>
                </head>
                <body style="margin:0;padding:0;background-color:#F7F7F5;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Helvetica,Arial,sans-serif;">

                  <table width="100%" cellpadding="0" cellspacing="0" role="presentation" style="background-color:#F7F7F5;min-height:100vh;">
                    <tr>
                      <td align="center" style="padding:48px 16px;">
                        <table width="100%" cellpadding="0" cellspacing="0" role="presentation" style="max-width:540px;">
                          <tr>
                            <td style="background-color:#D97706;height:3px;border-radius:2px 2px 0 0;"></td>
                          </tr>
                          <tr>
                            <td style="background-color:#FFFFFF;border-radius:0 0 6px 6px;padding:52px 52px 48px;">

                              <p style="margin:0 0 40px;font-size:11px;letter-spacing:0.12em;text-transform:uppercase;color:#A3A3A3;font-weight:500;">
                                Hackathon Platform
                              </p>

                              <h1 style="margin:0 0 12px;font-family:Georgia,'Times New Roman',serif;font-size:28px;font-weight:400;color:#0A0A0A;line-height:1.25;letter-spacing:-0.01em;">
                                You've been invited.
                              </h1>

                              <p style="margin:0 0 36px;font-size:15px;color:#6B6B6B;line-height:1.7;">
                                You've been selected as a <span style="color:#0A0A0A;font-weight:500;">{{role}}</span> for
                                <span style="color:#0A0A0A;font-weight:500;">{{hackathonTitle}}</span>.
                                We'd love to have you on board.
                              </p>

                              <table width="100%" cellpadding="0" cellspacing="0" role="presentation">
                                <tr>
                                  <td style="border-top:1px solid #F0F0EE;padding-bottom:36px;"></td>
                                </tr>
                              </table>
                              <table cellpadding="0" cellspacing="0" role="presentation">
                                <tr>
                                  <td style="border-radius:4px;background-color:#0A0A0A;">
                                    <a href="http://localhost:5173/login?email={{email}}&role={{role}}"
                                       style="display:inline-block;padding:13px 28px;font-size:14px;font-weight:500;color:#FFFFFF;text-decoration:none;letter-spacing:0.02em;border-radius:4px;">
                                      Accept Invitation &rarr;
                                    </a>
                                  </td>
                                </tr>
                              </table>
                              <p style="margin:28px 0 0;font-size:12px;color:#A3A3A3;line-height:1.6;">
                                Or copy this link into your browser:<br>
                                <span style="color:#6B6B6B;word-break:break-all;">
                                  http://localhost:5173/login?email={{email}}&role={{role}}
                                </span>
                              </p>

                            </td>
                          </tr>
                          <tr>
                            <td style="padding:24px 52px 0;">
                              <p style="margin:0;font-size:11px;color:#BCBCBA;line-height:1.6;">
                                You received this because you were nominated by an organiser.
                                If this was a mistake, you can safely ignore this email.
                              </p>
                            </td>
                          </tr>

                        </table>
                      </td>
                    </tr>
                  </table>

                </body>
                </html>
                """;

        String htmlMessage = htmlTemplate
                .replace("{{hackathonTitle}}", hackathonTitle)
                .replace("{{email}}", email)
                .replace("{{role}}", role.name());
        try {
            sendVerificationEmail(email, subject, htmlMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send invitation email to " + e + " " + email);
        }
    }
}
