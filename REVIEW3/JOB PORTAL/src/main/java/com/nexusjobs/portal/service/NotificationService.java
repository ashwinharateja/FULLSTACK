package com.nexusjobs.portal.service;

import com.nexusjobs.portal.model.Notification;
import com.nexusjobs.portal.model.User;
import com.nexusjobs.portal.repository.NotificationRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@nexusjobs.com}")
    private String fromEmail;

    // ── Read / list ───────────────────────────────────────────────────────────

    public List<Notification> getForUser(Long userId) {
        return notificationRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    // ── Create notification + fire email ──────────────────────────────────────

    /**
     * Saves the in-app notification AND sends an HTML email to the user's inbox.
     */
    @Transactional
    public Notification add(User user, String type, String title, String message) {
        Notification n = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .isRead(false)
                .build();
        Notification saved = notificationRepository.save(n);

        // Send real-time email notification (async — won't block the request)
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            sendNotificationEmailAsync(user.getEmail(), user.getName(), type, title, message);
        }

        return saved;
    }

    // ── Mark read ─────────────────────────────────────────────────────────────

    @Transactional
    public void markAllRead(Long userId) {
        List<Notification> list = notificationRepository.findByUserIdOrderByTimestampDesc(userId);
        list.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(list);
    }

    // ── Async email sender ────────────────────────────────────────────────────

    @Async
    public void sendNotificationEmailAsync(String toEmail, String userName,
                                           String type, String title, String message) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(fromEmail, "Nexus Jobs");
            helper.setTo(toEmail);
            helper.setSubject("🔔 " + title + " — Nexus Jobs");
            helper.setText(buildHtmlEmail(userName, type, title, message), true);
            mailSender.send(msg);
            log.info("✅ Notification email sent to {} — [{}]", toEmail, title);
        } catch (Exception e) {
            log.error("❌ Could not send notification email to {} — {}", toEmail, e.getMessage());
        }
    }

    // ── HTML email builder ────────────────────────────────────────────────────

    private String buildHtmlEmail(String userName, String type, String title, String message) {
        // Pick accent color based on notification type
        String accentColor = switch (type) {
            case "success" -> "#00BFA5";
            case "danger"  -> "#FF7E67";
            case "warning" -> "#FFD166";
            default        -> "#7C6FFF";
        };

        String accentLight = switch (type) {
            case "success" -> "rgba(0,191,165,0.10)";
            case "danger"  -> "rgba(255,126,103,0.10)";
            case "warning" -> "rgba(255,209,102,0.10)";
            default        -> "rgba(124,111,255,0.10)";
        };

        String icon = switch (type) {
            case "success" -> "🎉";
            case "danger"  -> "📋";
            case "warning" -> "⚠️";
            default        -> "🔔";
        };

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
                                box-shadow:0 8px 40px rgba(124,111,255,0.14);
                                border:1px solid rgba(124,111,255,0.12);max-width:520px;width:100%%;">

                    <!-- Header -->
                    <tr>
                      <td style="background:linear-gradient(135deg,#7C6FFF,#00BFA5);
                                 padding:30px 40px;text-align:center;">
                        <p style="margin:0;color:#fff;font-size:1.45rem;font-weight:800;">⚡ NexusJobs</p>
                        <p style="margin:7px 0 0;color:rgba(255,255,255,0.88);font-size:0.85rem;">
                          Activity Notification
                        </p>
                      </td>
                    </tr>

                    <!-- Body -->
                    <tr>
                      <td style="padding:36px 44px 32px;">

                        <!-- Greeting -->
                        <p style="margin:0 0 22px;color:#4A4A72;font-size:0.92rem;line-height:1.6;">
                          Hi <strong style="color:#1A1A2E;">%s</strong>, you have a new update on your Nexus Jobs account.
                        </p>

                        <!-- Notification card -->
                        <div style="background:%s;border-left:4px solid %s;
                                    border-radius:12px;padding:20px 22px;margin-bottom:28px;">
                          <p style="margin:0 0 6px;font-size:1.05rem;font-weight:800;color:#1A1A2E;">
                            %s &nbsp;%s
                          </p>
                          <p style="margin:0;color:#4A4A72;font-size:0.88rem;line-height:1.65;">
                            %s
                          </p>
                        </div>

                        <!-- CTA button -->
                        <div style="text-align:center;margin-bottom:28px;">
                          <a href="http://localhost:8080/dashboard/seeker"
                             style="display:inline-block;background:linear-gradient(135deg,#7C6FFF,#00BFA5);
                                    color:#fff;text-decoration:none;font-weight:700;font-size:0.9rem;
                                    padding:12px 32px;border-radius:10px;
                                    box-shadow:0 4px 16px rgba(124,111,255,0.28);">
                            View My Dashboard →
                          </a>
                        </div>

                        <!-- Tip -->
                        <div style="background:#F5F6FF;border-radius:10px;padding:14px 18px;
                                    border-left:3px solid #7C6FFF;">
                          <p style="margin:0;color:#4A4A72;font-size:0.8rem;line-height:1.6;">
                            💡 Log in to your dashboard to take action on this update.
                          </p>
                        </div>

                      </td>
                    </tr>

                    <!-- Footer -->
                    <tr>
                      <td style="background:#F5F6FF;padding:16px 44px;text-align:center;
                                 border-top:1px solid rgba(124,111,255,0.1);">
                        <p style="margin:0;color:#8A8AAC;font-size:0.73rem;line-height:1.8;">
                          © 2026 Nexus Jobs &nbsp;·&nbsp; All rights reserved<br/>
                          You're receiving this because you have an account on Nexus Jobs.<br/>
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
            """.formatted(userName, accentLight, accentColor, icon, title, message);
    }
}
