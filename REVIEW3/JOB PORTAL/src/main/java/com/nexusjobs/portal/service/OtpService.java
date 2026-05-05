package com.nexusjobs.portal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private final JavaMailSender mailSender;

    @Value("${otp.expiry-minutes:5}")
    private int expiryMinutes;

    @Value("${otp.dev-mode:false}")
    private boolean devMode;

    @Value("${spring.mail.username:noreply@nexusjobs.com}")
    private String fromEmail;

    // In-memory store: email → OTP record
    private final Map<String, OtpRecord> otpStore = new ConcurrentHashMap<>();

    private static final SecureRandom RANDOM = new SecureRandom();

    // ── Internal record ────────────────────────────────────────────
    private record OtpRecord(String code, LocalDateTime expiresAt) {
        boolean isExpired() {
            return LocalDateTime.now().isAfter(expiresAt);
        }
    }

    // ── Public API ─────────────────────────────────────────────────

    /**
     * Generate a fresh 6-digit OTP, store it, and send it to the given email.
     */
    public void generateAndSend(String email) {
        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        otpStore.put(email.toLowerCase(), new OtpRecord(code, LocalDateTime.now().plusMinutes(expiryMinutes)));

        if (devMode) {
            log.info("╔══════════════════════════════════════╗");
            log.info("║  DEV-MODE OTP for {}  ║", email);
            log.info("║  Code: {}  ·  Expires in {} min     ║", code, expiryMinutes);
            log.info("╚══════════════════════════════════════╝");
        }

        sendOtpEmailAsync(email, code);
    }

    /**
     * Validate the supplied OTP.
     * Returns true if valid; removes the entry on success (one-time use).
     */
    public boolean validate(String email, String inputCode) {
        OtpRecord record = otpStore.get(email.toLowerCase());
        if (record == null || record.isExpired()) {
            otpStore.remove(email.toLowerCase());
            return false;
        }
        if (record.code().equals(inputCode.trim())) {
            otpStore.remove(email.toLowerCase());
            return true;
        }
        return false;
    }

    /**
     * Returns the current OTP code for dev-mode display on the verify page.
     * Returns null when devMode=false or no OTP exists.
     */
    public String getDevOtp(String email) {
        if (!devMode) return null;
        OtpRecord record = otpStore.get(email.toLowerCase());
        if (record == null || record.isExpired()) return null;
        return record.code();
    }

    /**
     * Invalidate any existing OTP for this email (called on resend).
     */
    public void invalidate(String email) {
        otpStore.remove(email.toLowerCase());
    }

    // ── Private helpers ────────────────────────────────────────────

    /**
     * Sends the OTP email asynchronously so the HTTP login request is not blocked by SMTP.
     * Requires @EnableAsync on the application class.
     */
    @Async
    public void sendOtpEmailAsync(String toEmail, String code) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(fromEmail, "Nexus Jobs");
            helper.setTo(toEmail);
            helper.setSubject("🔐 Your Nexus Jobs Login Code: " + code);
            helper.setText(buildHtmlEmail(code), true);
            mailSender.send(msg);
            log.info("✅ OTP email sent successfully to {}", toEmail);
        } catch (Exception e) {
            log.error("❌ Failed to send OTP email to {} — {}", toEmail, e.getMessage());
            log.error("   → Check spring.mail.username and spring.mail.password in application.properties");
            log.error("   → Gmail users: use a 16-char App Password from https://myaccount.google.com/apppasswords");
        }
    }

    private String buildHtmlEmail(String code) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8"/>
              <meta name="viewport" content="width=device-width,initial-scale=1"/>
            </head>
            <body style="margin:0;padding:0;background:#F0F2FF;font-family:'Segoe UI',Arial,sans-serif;">
              <table width="100%%" cellpadding="0" cellspacing="0" style="padding:40px 20px;background:#F0F2FF;">
                <tr><td align="center">
                  <table width="520" cellpadding="0" cellspacing="0"
                         style="background:#FFFFFF;border-radius:20px;overflow:hidden;
                                box-shadow:0 8px 40px rgba(124,111,255,0.15);
                                border:1px solid rgba(124,111,255,0.12);max-width:520px;width:100%%;">

                    <!-- Header -->
                    <tr>
                      <td style="background:linear-gradient(135deg,#7C6FFF,#00BFA5);padding:36px 40px;text-align:center;">
                        <p style="margin:0;color:#fff;font-size:1.55rem;font-weight:800;">⚡ NexusJobs</p>
                        <p style="margin:8px 0 0;color:rgba(255,255,255,0.88);font-size:0.88rem;">Secure Login Verification</p>
                      </td>
                    </tr>

                    <!-- Body -->
                    <tr>
                      <td style="padding:38px 44px 32px;">
                        <div style="text-align:center;margin-bottom:22px;">
                          <span style="display:inline-block;width:64px;height:64px;line-height:64px;
                                       font-size:1.9rem;border-radius:18px;text-align:center;
                                       background:linear-gradient(135deg,rgba(124,111,255,.1),rgba(0,191,165,.08));
                                       border:2px solid rgba(124,111,255,.22);">🔐</span>
                        </div>

                        <h2 style="margin:0 0 10px;text-align:center;color:#1A1A2E;font-size:1.4rem;font-weight:800;">
                          Your one-time login code
                        </h2>
                        <p style="margin:0 0 28px;text-align:center;color:#4A4A72;font-size:0.9rem;line-height:1.6;">
                          Enter this code to complete sign-in. It expires in
                          <strong style="color:#7C6FFF;">%d minutes</strong> and can only be used once.
                        </p>

                        <!-- OTP box -->
                        <div style="background:linear-gradient(135deg,#EEF0FF,#F5F6FF);
                                    border:2px solid rgba(124,111,255,0.28);border-radius:16px;
                                    text-align:center;padding:28px 0;margin-bottom:32px;">
                          <span style="font-size:2.8rem;font-weight:900;letter-spacing:14px;
                                       color:#5A4FD6;font-family:'Courier New',monospace;">%s</span>
                          <p style="margin:10px 0 0;font-size:0.72rem;color:#8A8AAC;
                                    text-transform:uppercase;letter-spacing:0.08em;">One-Time Password</p>
                        </div>

                        <!-- Security note -->
                        <table width="100%%" cellpadding="0" cellspacing="0">
                          <tr>
                            <td style="background:#F5F6FF;border-radius:12px;padding:15px 18px;
                                       border-left:4px solid #7C6FFF;">
                              <p style="margin:0;color:#4A4A72;font-size:0.82rem;line-height:1.6;">
                                🛡️ <strong>Security:</strong> Nexus Jobs will <strong>never</strong>
                                ask for this code by phone or chat. If you did not request this, ignore this email.
                              </p>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>

                    <!-- Footer -->
                    <tr>
                      <td style="background:#F5F6FF;padding:16px 44px;text-align:center;
                                 border-top:1px solid rgba(124,111,255,0.1);">
                        <p style="margin:0;color:#8A8AAC;font-size:0.74rem;line-height:1.8;">
                          © 2026 Nexus Jobs · All rights reserved<br/>
                          <a href="mailto:support@nexusjobs.com"
                             style="color:#7C6FFF;text-decoration:none;">support@nexusjobs.com</a>
                        </p>
                      </td>
                    </tr>

                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(expiryMinutes, code);
    }
}
