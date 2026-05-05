package com.nexusjobs.portal.config;

import com.nexusjobs.portal.model.*;
import com.nexusjobs.portal.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final MessageRepository messageRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded — skipping DataLoader.");
            return;
        }
        log.info("Seeding Nexus Jobs demo data...");

        // ── Users ──────────────────────────────────────────────────────────────
        User admin = userRepository.save(User.builder()
                .name("Alex Admin").email("admin@nexusjobs.com")
                .password(passwordEncoder.encode("admin123"))
                .role(User.Role.ADMIN)
                .avatarClass("avatar-gradient-3")
                .build());

        User employer1 = userRepository.save(User.builder()
                .name("Sarah Chen").email("employer@techcorp.com")
                .password(passwordEncoder.encode("employer123"))
                .role(User.Role.EMPLOYER)
                .company("TechCorp Inc.")
                .industry("Technology")
                .website("https://techcorp.com")
                .about("Leading tech company building next-gen solutions.")
                .avatarClass("avatar-gradient-1")
                .build());

        User employer2 = userRepository.save(User.builder()
                .name("Mark Rivera").email("mark@designco.io")
                .password(passwordEncoder.encode("employer123"))
                .role(User.Role.EMPLOYER)
                .company("DesignCo")
                .industry("Design")
                .website("https://designco.io")
                .about("Award-winning design agency.")
                .avatarClass("avatar-gradient-2")
                .build());

        User seeker1 = userRepository.save(User.builder()
                .name("Jordan Lee").email("YOUR_REAL_EMAIL@gmail.com")  // ← CHANGE THIS to your real email so OTP is delivered to you
                .password(passwordEncoder.encode("seeker123"))
                .role(User.Role.SEEKER)
                .title("Full Stack Developer")
                .location("San Francisco, CA")
                .skills("React,Node.js,Python,TypeScript,PostgreSQL")
                .experience(3)
                .bio("Passionate developer with 3 years of experience building scalable web applications.")
                .education("B.S. Computer Science, Stanford University")
                .salary(130000L)
                .avatarClass("avatar-gradient-1")
                .build());

        User seeker2 = userRepository.save(User.builder()
                .name("Priya Sharma").email("priya@email.com")
                .password(passwordEncoder.encode("seeker123"))
                .role(User.Role.SEEKER)
                .title("UX Designer")
                .location("New York, NY")
                .skills("Figma,User Research,Prototyping,CSS,Design Systems")
                .experience(4)
                .bio("Creative UX designer focused on building delightful user experiences.")
                .education("M.A. HCI, Carnegie Mellon")
                .salary(110000L)
                .avatarClass("avatar-gradient-2")
                .build());

        User seeker3 = userRepository.save(User.builder()
                .name("Carlos Mendez").email("carlos@email.com")
                .password(passwordEncoder.encode("seeker123"))
                .role(User.Role.SEEKER)
                .title("Data Scientist")
                .location("Austin, TX")
                .skills("Python,Machine Learning,TensorFlow,SQL,Tableau")
                .experience(5)
                .bio("Data scientist specializing in ML models and analytics pipelines.")
                .education("PhD, Data Science, UC Berkeley")
                .salary(155000L)
                .avatarClass("avatar-gradient-4")
                .build());

        log.info("✅ Created {} users", userRepository.count());

        // ── Jobs ───────────────────────────────────────────────────────────────
        Job j1 = jobRepository.save(Job.builder()
                .employer(employer1).title("Senior Full Stack Engineer")
                .company("TechCorp Inc.").companyLogo("🚀")
                .location("San Francisco, CA").type("Full-time").remote(true)
                .salaryMin(120000L).salaryMax(160000L)
                .skills("React,Node.js,TypeScript,PostgreSQL,AWS")
                .description("We are looking for a Senior Full Stack Engineer to join our growing team. You will own key product features end-to-end, work with a talented cross-functional team, and help shape the technical direction of our platform.")
                .experience("3-5 years").category("Engineering")
                .views(342).applicationCount(18)
                .createdAt(LocalDateTime.now().minusDays(5))
                .build());

        Job j2 = jobRepository.save(Job.builder()
                .employer(employer1).title("Product Designer")
                .company("TechCorp Inc.").companyLogo("🚀")
                .location("Remote").type("Full-time").remote(true)
                .salaryMin(100000L).salaryMax(130000L)
                .skills("Figma,User Research,Prototyping,Design Systems")
                .description("Join our design team to craft beautiful, user-centric interfaces. You will lead design sprints, own end-to-end product design, and collaborate closely with engineering.")
                .experience("2-4 years").category("Design")
                .views(215).applicationCount(11)
                .createdAt(LocalDateTime.now().minusDays(8))
                .build());

        Job j3 = jobRepository.save(Job.builder()
                .employer(employer2).title("UX/UI Designer")
                .company("DesignCo").companyLogo("🎨")
                .location("New York, NY").type("Full-time").remote(false)
                .salaryMin(90000L).salaryMax(115000L)
                .skills("Figma,Sketch,CSS,User Research,Wireframing")
                .description("We need a talented UX/UI Designer to lead design projects for our agency clients. You will work on a variety of exciting brands across industries.")
                .experience("2-5 years").category("Design")
                .views(178).applicationCount(9)
                .createdAt(LocalDateTime.now().minusDays(12))
                .build());

        Job j4 = jobRepository.save(Job.builder()
                .employer(employer1).title("Machine Learning Engineer")
                .company("TechCorp Inc.").companyLogo("🚀")
                .location("Austin, TX").type("Full-time").remote(true)
                .salaryMin(150000L).salaryMax(190000L)
                .skills("Python,TensorFlow,PyTorch,MLOps,SQL")
                .description("Help us build ML models powering millions of users. You will design, train, and deploy production ML systems at scale.")
                .experience("4-7 years").category("Data Science")
                .views(302).applicationCount(14)
                .createdAt(LocalDateTime.now().minusDays(3))
                .build());

        Job j5 = jobRepository.save(Job.builder()
                .employer(employer2).title("Frontend Developer")
                .company("DesignCo").companyLogo("🎨")
                .location("Remote").type("Contract").remote(true)
                .salaryMin(80000L).salaryMax(100000L)
                .skills("React,CSS,TypeScript,Jest,Storybook")
                .description("Build beautiful, performant UIs for our clients. You will work closely with designers to implement pixel-perfect interfaces.")
                .experience("1-3 years").category("Engineering")
                .views(141).applicationCount(7)
                .createdAt(LocalDateTime.now().minusDays(7))
                .build());

        Job j6 = jobRepository.save(Job.builder()
                .employer(employer1).title("DevOps Engineer")
                .company("TechCorp Inc.").companyLogo("🚀")
                .location("Seattle, WA").type("Full-time").remote(false)
                .salaryMin(130000L).salaryMax(165000L)
                .skills("Kubernetes,Docker,Terraform,AWS,CI/CD")
                .description("Manage our cloud infrastructure and deployment pipelines. You will ensure high availability and reliability of our platform infrastructure.")
                .experience("3-6 years").category("Engineering")
                .views(198).applicationCount(8)
                .createdAt(LocalDateTime.now().minusDays(10))
                .build());

        log.info("✅ Created {} jobs", jobRepository.count());

        // ── Applications ───────────────────────────────────────────────────────
        applicationRepository.save(Application.builder()
                .job(j1).seeker(seeker1).status(Application.Status.INTERVIEW)
                .coverLetter("I am excited about this role and believe my experience in React and Node.js makes me a strong candidate.")
                .steps("APPLIED,SCREENING,INTERVIEW")
                .appliedAt(LocalDateTime.now().minusDays(4))
                .build());

        applicationRepository.save(Application.builder()
                .job(j3).seeker(seeker2).status(Application.Status.SCREENING)
                .coverLetter("My design work aligns perfectly with DesignCo's creative vision.")
                .steps("APPLIED,SCREENING")
                .appliedAt(LocalDateTime.now().minusDays(6))
                .build());

        applicationRepository.save(Application.builder()
                .job(j4).seeker(seeker3).status(Application.Status.APPLIED)
                .coverLetter("My ML experience with TensorFlow and PyTorch matches this role perfectly.")
                .steps("APPLIED")
                .appliedAt(LocalDateTime.now().minusDays(2))
                .build());

        applicationRepository.save(Application.builder()
                .job(j2).seeker(seeker1).status(Application.Status.OFFERED)
                .coverLetter("I would love to join TechCorp's design team.")
                .steps("APPLIED,SCREENING,INTERVIEW,OFFERED")
                .appliedAt(LocalDateTime.now().minusDays(14))
                .build());

        log.info("✅ Created {} applications", applicationRepository.count());

        // ── Messages ───────────────────────────────────────────────────────────
        String t1 = "t_" + employer1.getId() + "_" + seeker1.getId();
        messageRepository.save(Message.builder().fromUser(employer1).toUser(seeker1).threadId(t1)
                .text("Hi Jordan! We reviewed your application for the Senior Full Stack Engineer role.")
                .timestamp(LocalDateTime.now().minusDays(3)).build());
        messageRepository.save(Message.builder().fromUser(seeker1).toUser(employer1).threadId(t1)
                .text("Thank you! I am very excited about the opportunity.")
                .timestamp(LocalDateTime.now().minusDays(3).plusHours(1)).build());
        messageRepository.save(Message.builder().fromUser(employer1).toUser(seeker1).threadId(t1)
                .text("We would like to schedule a technical interview. Are you available Thursday at 2PM PST?")
                .timestamp(LocalDateTime.now().minusDays(2)).build());

        String t2 = "t_" + employer2.getId() + "_" + seeker2.getId();
        messageRepository.save(Message.builder().fromUser(employer2).toUser(seeker2).threadId(t2)
                .text("Hi Priya, your portfolio is impressive. Would you like to discuss the UX role?")
                .timestamp(LocalDateTime.now().minusDays(5)).build());
        messageRepository.save(Message.builder().fromUser(seeker2).toUser(employer2).threadId(t2)
                .text("Absolutely! I have been following DesignCo for a while and love the work.")
                .timestamp(LocalDateTime.now().minusDays(5).plusHours(2)).build());

        log.info("✅ Created {} messages", messageRepository.count());

        // ── Notifications ──────────────────────────────────────────────────────
        notificationRepository.save(Notification.builder()
                .user(seeker1).type("success")
                .title("Interview Scheduled")
                .message("TechCorp wants to interview you for Sr. Full Stack Engineer")
                .isRead(false).timestamp(LocalDateTime.now().minusDays(2)).build());

        notificationRepository.save(Notification.builder()
                .user(seeker1).type("info")
                .title("Application Viewed")
                .message("DesignCo viewed your profile")
                .isRead(false).timestamp(LocalDateTime.now().minusDays(4)).build());

        notificationRepository.save(Notification.builder()
                .user(seeker1).type("success")
                .title("Offer Received!")
                .message("You have an offer for Product Designer at TechCorp")
                .isRead(false).timestamp(LocalDateTime.now().minusDays(1)).build());

        notificationRepository.save(Notification.builder()
                .user(employer1).type("info")
                .title("New Application")
                .message("Jordan Lee applied to Sr. Full Stack Engineer")
                .isRead(false).timestamp(LocalDateTime.now().minusDays(4)).build());

        notificationRepository.save(Notification.builder()
                .user(employer1).type("info")
                .title("New Application")
                .message("Jordan Lee applied to Product Designer")
                .isRead(true).timestamp(LocalDateTime.now().minusDays(14)).build());

        log.info("✅ Created {} notifications", notificationRepository.count());
        log.info("🚀 Nexus Jobs demo data seeded successfully!");
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("Demo Credentials:");
        log.info("  Admin   → admin@nexusjobs.com   / admin123");
        log.info("  Employer → employer@techcorp.com / employer123");
        log.info("  Seeker  → YOUR_REAL_EMAIL@gmail.com / seeker123  (OTP sent here)");
        log.info("  H2 DB Console → http://localhost:8080/h2-console");
        log.info("  JDBC URL      → jdbc:h2:mem:nexusjobs");
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}
