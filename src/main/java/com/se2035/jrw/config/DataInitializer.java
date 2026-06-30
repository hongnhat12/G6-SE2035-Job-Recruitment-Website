package com.se2035.jrw.config;

import com.se2035.jrw.entity.*;
import com.se2035.jrw.enums.*;
import com.se2035.jrw.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepo userRepo;
    private final CompanyRepo companyRepo;
    private final RecruiterRepo recruiterRepo;
    private final CandidateRepo candidateRepo;
    private final CVRepo cvRepo;
    private final IndustryRepo industryRepo;
    private final JobRepo jobRepo;
    private final ApplicationRepo applicationRepo;
    private final SavedJobRepo savedJobRepo;

    @Override
    public void run(String... args) throws Exception {
        if (userRepo.count() > 0) {
            log.info("Database already contains data. Skipping data initialization.");
            return;
        }

        log.info("Initializing 100% exact seed data in database to match seed_data.sql...");

        // 1. Create Users (1 admin, 4 recruiters, 5 candidates)
        User admin = User.builder()
                .email("admin@joboard.io")
                .passwordHash("$2a$10$U946dAuOxDLmELSPu3SIBebHgfmJH9pbc9XXbH34RMQ82u1rhgcSm") // Password@123
                .role(UserRole.ADMIN)
                .status(UserStatus.ACTIVE)
                .build();
        userRepo.save(admin);

        User rUser1 = User.builder().email("recruiter.minh@fpt.com.vn").passwordHash("$2a$10$U946dAuOxDLmELSPu3SIBebHgfmJH9pbc9XXbH34RMQ82u1rhgcSm").role(UserRole.RECRUITER).status(UserStatus.ACTIVE).build();
        User rUser2 = User.builder().email("recruiter.lan@vng.com.vn").passwordHash("$2a$10$U946dAuOxDLmELSPu3SIBebHgfmJH9pbc9XXbH34RMQ82u1rhgcSm").role(UserRole.RECRUITER).status(UserStatus.ACTIVE).build();
        User rUser3 = User.builder().email("recruiter.duc@momo.vn").passwordHash("$2a$10$U946dAuOxDLmELSPu3SIBebHgfmJH9pbc9XXbH34RMQ82u1rhgcSm").role(UserRole.RECRUITER).status(UserStatus.ACTIVE).build();
        User rUser4 = User.builder().email("recruiter.hoa@tiki.vn").passwordHash("$2a$10$U946dAuOxDLmELSPu3SIBebHgfmJH9pbc9XXbH34RMQ82u1rhgcSm").role(UserRole.RECRUITER).status(UserStatus.ACTIVE).build();
        userRepo.saveAll(Arrays.asList(rUser1, rUser2, rUser3, rUser4));

        User cUser1 = User.builder().email("nguyen.tuan@gmail.com").passwordHash("$2a$10$U946dAuOxDLmELSPu3SIBebHgfmJH9pbc9XXbH34RMQ82u1rhgcSm").role(UserRole.CANDIDATE).status(UserStatus.ACTIVE).build();
        User cUser2 = User.builder().email("tran.mai@gmail.com").passwordHash("$2a$10$U946dAuOxDLmELSPu3SIBebHgfmJH9pbc9XXbH34RMQ82u1rhgcSm").role(UserRole.CANDIDATE).status(UserStatus.ACTIVE).build();
        User cUser3 = User.builder().email("pham.khoa@gmail.com").passwordHash("$2a$10$U946dAuOxDLmELSPu3SIBebHgfmJH9pbc9XXbH34RMQ82u1rhgcSm").role(UserRole.CANDIDATE).status(UserStatus.ACTIVE).build();
        User cUser4 = User.builder().email("le.bich@gmail.com").passwordHash("$2a$10$U946dAuOxDLmELSPu3SIBebHgfmJH9pbc9XXbH34RMQ82u1rhgcSm").role(UserRole.CANDIDATE).status(UserStatus.ACTIVE).build();
        User cUser5 = User.builder().email("hoang.son@gmail.com").passwordHash("$2a$10$U946dAuOxDLmELSPu3SIBebHgfmJH9pbc9XXbH34RMQ82u1rhgcSm").role(UserRole.CANDIDATE).status(UserStatus.ACTIVE).build();
        userRepo.saveAll(Arrays.asList(cUser1, cUser2, cUser3, cUser4, cUser5));

        // 2. Create Companies (4 companies)
        Company c1 = Company.builder()
                .companyName("FPT Software")
                .description("Largest IT outsourcing company in Vietnam, providing software services to global clients.")
                .website("https://fptsoftware.com")
                .email("hr@fpt.com.vn")
                .phone("024-7300-7300")
                .address("FPT Complex, Hoa Lac Hi-Tech Park, Hanoi")
                .logo("https://logo.clearbit.com/fpt.com")
                .status("ACTIVE")
                .build();

        Company c2 = Company.builder()
                .companyName("VNG Corporation")
                .description("Leading technology corporation in Vietnam, operating Zalo, ZaloPay and cloud services.")
                .website("https://vng.com.vn")
                .email("careers@vng.com.vn")
                .phone("028-3562-6789")
                .address("Z06, Street 13, Tan Thuan Dong, HCMC")
                .logo("https://logo.clearbit.com/vng.com.vn")
                .status("ACTIVE")
                .build();

        Company c3 = Company.builder()
                .companyName("MoMo")
                .description("Vietnam's leading e-wallet platform with over 30 million users nationwide.")
                .website("https://momo.vn")
                .email("hr@momo.vn")
                .phone("028-3636-0068")
                .address("Flemington Tower, 182 Le Dai Hanh, Ward 15, HCMC")
                .logo("https://logo.clearbit.com/momo.vn")
                .status("ACTIVE")
                .build();

        Company c4 = Company.builder()
                .companyName("Tiki")
                .description("One of Vietnam's top e-commerce platforms, offering fast 2-hour delivery nationwide.")
                .website("https://tiki.vn")
                .email("hr@tiki.vn")
                .phone("028-3620-3636")
                .address("52 Ut Tich, Ward 4, Tan Binh District, HCMC")
                .logo("https://logo.clearbit.com/tiki.vn")
                .status("ACTIVE")
                .build();

        companyRepo.saveAll(Arrays.asList(c1, c2, c3, c4));

        // 3. Create Recruiters (4 recruiters)
        Recruiter rec1 = Recruiter.builder().user(rUser1).company(c1).fullName("Nguyen Van Minh").phone("0912-345-678").position("Senior HR Manager").build();
        Recruiter rec2 = Recruiter.builder().user(rUser2).company(c2).fullName("Tran Thi Lan").phone("0923-456-789").position("Talent Acquisition Lead").build();
        Recruiter rec3 = Recruiter.builder().user(rUser3).company(c3).fullName("Le Van Duc").phone("0934-567-890").position("HR Specialist").build();
        Recruiter rec4 = Recruiter.builder().user(rUser4).company(c4).fullName("Pham Thi Hoa").phone("0945-678-901").position("Recruitment Manager").build();
        recruiterRepo.saveAll(Arrays.asList(rec1, rec2, rec3, rec4));

        // 4. Create Candidates (5 candidates)
        Candidate cand1 = Candidate.builder()
                .user(cUser1)
                .fullName("Nguyen Minh Tuan")
                .phone("0901-111-222")
                .birthday(LocalDate.of(1998, 5, 14))
                .gender("Male")
                .address("Ha Dong, Hanoi")
                .headline("Java Backend Developer | 3 years exp")
                .summary("Experienced backend developer specializing in Spring Boot microservices and cloud-native applications. Passionate about clean architecture and performance optimization.")
                .profileImage("tuan.jpg")
                .skills("Java,Spring Boot,PostgreSQL,Docker,Redis")
                .build();

        Candidate cand2 = Candidate.builder()
                .user(cUser2)
                .fullName("Tran Ngoc Mai")
                .phone("0902-222-333")
                .birthday(LocalDate.of(1999, 11, 22))
                .gender("Female")
                .address("Binh Thanh, HCMC")
                .headline("Frontend Developer | React & Vue specialist")
                .summary("Creative frontend developer with 2 years of experience building responsive web applications. Strong eye for UI/UX and passionate about accessibility.")
                .profileImage("mai.jpg")
                .skills("React,Vue.js,TypeScript,Tailwind CSS,Figma")
                .build();

        Candidate cand3 = Candidate.builder()
                .user(cUser3)
                .fullName("Pham Duc Khoa")
                .phone("0903-333-444")
                .birthday(LocalDate.of(1997, 8, 30))
                .gender("Male")
                .address("Cau Giay, Hanoi")
                .headline("Full Stack Developer | Node.js & React")
                .summary("Full stack developer with 4 years of experience in both frontend and backend systems. Comfortable working across the entire stack from database design to deployment.")
                .profileImage("khoa.jpg")
                .skills("Node.js,React,MongoDB,Express,AWS,Docker")
                .build();

        Candidate cand4 = Candidate.builder()
                .user(cUser4)
                .fullName("Le Thi Bich Ngoc")
                .phone("0904-444-555")
                .birthday(LocalDate.of(2000, 3, 18))
                .gender("Female")
                .address("Go Vap, HCMC")
                .headline("Mobile Developer | Flutter & React Native")
                .summary("Mobile developer passionate about cross-platform applications. Built 3 published apps on both App Store and Google Play with combined 50k+ downloads.")
                .profileImage("bich.jpg")
                .skills("Flutter,Dart,React Native,Firebase,REST APIs")
                .build();

        Candidate cand5 = Candidate.builder()
                .user(cUser5)
                .fullName("Hoang Quoc Son")
                .phone("0905-555-666")
                .birthday(LocalDate.of(1996, 12, 5))
                .gender("Male")
                .address("Dong Da, Hanoi")
                .headline("DevOps Engineer | Kubernetes & AWS certified")
                .summary("DevOps engineer with 5 years of experience automating infrastructure and CI/CD pipelines. AWS Certified Solutions Architect. Experienced with large-scale distributed systems.")
                .profileImage("son.jpg")
                .skills("Kubernetes,AWS,Terraform,Jenkins,Docker,Linux")
                .build();

        candidateRepo.saveAll(Arrays.asList(cand1, cand2, cand3, cand4, cand5));

        // 5. Create CVs (10 CVs - 2 per candidate)
        CV cv1 = CV.builder().candidate(cand1).cvName("Tuan_CV_Backend_2025.pdf").filePath("uploads/cv/tuan_backend_2025.pdf").isDefault(true).uploadedAt(LocalDateTime.of(2025, 2, 1, 10, 0)).build();
        CV cv2 = CV.builder().candidate(cand1).cvName("Tuan_CV_Fullstack_2025.pdf").filePath("uploads/cv/tuan_fullstack_2025.pdf").isDefault(false).uploadedAt(LocalDateTime.of(2025, 2, 15, 11, 0)).build();
        CV cv3 = CV.builder().candidate(cand2).cvName("Mai_CV_Frontend_2025.pdf").filePath("uploads/cv/mai_frontend_2025.pdf").isDefault(true).uploadedAt(LocalDateTime.of(2025, 2, 3, 9, 0)).build();
        CV cv4 = CV.builder().candidate(cand2).cvName("Mai_CV_UX_2025.pdf").filePath("uploads/cv/mai_ux_2025.pdf").isDefault(false).uploadedAt(LocalDateTime.of(2025, 2, 20, 10, 0)).build();
        CV cv5 = CV.builder().candidate(cand3).cvName("Khoa_CV_Fullstack_2025.pdf").filePath("uploads/cv/khoa_fullstack_2025.pdf").isDefault(true).uploadedAt(LocalDateTime.of(2025, 2, 5, 8, 30)).build();
        CV cv6 = CV.builder().candidate(cand3).cvName("Khoa_CV_Backend_2025.pdf").filePath("uploads/cv/khoa_backend_2025.pdf").isDefault(false).uploadedAt(LocalDateTime.of(2025, 3, 1, 9, 0)).build();
        CV cv7 = CV.builder().candidate(cand4).cvName("Bich_CV_Mobile_2025.pdf").filePath("uploads/cv/bich_mobile_2025.pdf").isDefault(true).uploadedAt(LocalDateTime.of(2025, 2, 7, 14, 0)).build();
        CV cv8 = CV.builder().candidate(cand4).cvName("Bich_CV_Flutter_2025.pdf").filePath("uploads/cv/bich_flutter_2025.pdf").isDefault(false).uploadedAt(LocalDateTime.of(2025, 3, 5, 10, 0)).build();
        CV cv9 = CV.builder().candidate(cand5).cvName("Son_CV_DevOps_2025.pdf").filePath("uploads/cv/son_devops_2025.pdf").isDefault(true).uploadedAt(LocalDateTime.of(2025, 2, 10, 13, 0)).build();
        CV cv10 = CV.builder().candidate(cand5).cvName("Son_CV_Cloud_Architect_2025.pdf").filePath("uploads/cv/son_cloud_2025.pdf").isDefault(false).uploadedAt(LocalDateTime.of(2025, 3, 10, 11, 0)).build();
        cvRepo.saveAll(Arrays.asList(cv1, cv2, cv3, cv4, cv5, cv6, cv7, cv8, cv9, cv10));

        // 6. Create Industries (3 industries)
        Industry ind1 = Industry.builder().industryName("Information Technology").description("Software development, system integration, QA, cloud and tech services.").build();
        Industry ind2 = Industry.builder().industryName("Financial Technology").description("E-wallet, digital banking, payment gateway, and financial platforms.").build();
        Industry ind3 = Industry.builder().industryName("E-commerce").description("Online marketplace, retail networks, and logistics platforms.").build();
        industryRepo.saveAll(Arrays.asList(ind1, ind2, ind3));

        // 7. Create Jobs (10 jobs)
        Job j1 = Job.builder().company(c1).recruiter(rec1).industry(ind1).title("Senior Java Backend Developer").description("Join FPT Software to build scalable microservices for enterprise clients across the US and Japan markets. You will work on high-traffic systems processing millions of requests daily.").requirement("3+ years Java experience. Proficiency with Spring Boot and microservices architecture. Experience with PostgreSQL or MySQL. Knowledge of Docker and Kubernetes is a plus.").benefit("Competitive salary up to $2000/month. 13th month salary. Premium health insurance. Annual team-building trip. Flexible remote work 2 days/week.").location("Hanoi (Hoa Lac)").salaryMin(new BigDecimal("25000")).salaryMax(new BigDecimal("45000")).employmentType("Full-time").experienceRequired(3).requiredSkills("Java,Spring Boot,PostgreSQL,Docker,Microservices").deadline(LocalDate.of(2025, 7, 31)).status(JobStatus.APPROVED).approvedBy(admin).approvedAt(LocalDateTime.of(2025, 3, 10, 9, 0)).build();
        Job j2 = Job.builder().company(c1).recruiter(rec1).industry(ind1).title("Junior Backend Developer (.NET)").description("Great opportunity for fresh graduates or junior developers to join FPT Software's growing .NET team. Mentorship provided by senior engineers.").requirement("0-1 year experience. Knowledge of C# and .NET fundamentals. Understanding of RESTful APIs. Eagerness to learn and grow.").benefit("Salary 10-15 million VND. Full social insurance. Training and certification support. Clear promotion roadmap.").location("Hanoi (Hoa Lac)").salaryMin(new BigDecimal("10000")).salaryMax(new BigDecimal("15000")).employmentType("Full-time").experienceRequired(0).requiredSkills("C#,.NET,SQL Server,REST APIs").deadline(LocalDate.of(2025, 8, 15)).status(JobStatus.APPROVED).approvedBy(admin).approvedAt(LocalDateTime.of(2025, 3, 11, 10, 0)).build();
        Job j3 = Job.builder().company(c2).recruiter(rec2).industry(ind1).title("Frontend Developer (React)").description("VNG is looking for a talented React developer to build next-generation features for Zalo Web — Vietnam's most used messaging platform with 74 million users.").requirement("2+ years React experience. Strong TypeScript skills. Experience with state management (Redux or Zustand). Eye for UI/UX detail.").benefit("Salary up to 30 million VND. Stock options available. World-class tech stack. Free lunch at office. Flexible hours.").location("Ho Chi Minh City (Thu Duc)").salaryMin(new BigDecimal("20000")).salaryMax(new BigDecimal("30000")).employmentType("Full-time").experienceRequired(2).requiredSkills("React,TypeScript,Redux,HTML,CSS").deadline(LocalDate.of(2025, 7, 20)).status(JobStatus.APPROVED).approvedBy(admin).approvedAt(LocalDateTime.of(2025, 3, 12, 11, 0)).build();
        Job j4 = Job.builder().company(c2).recruiter(rec2).industry(ind1).title("Mobile Developer (iOS)").description("Build native iOS features for ZaloPay, VNG's fast-growing digital payments app. Work with a cross-functional team of designers and product managers.").requirement("2+ years Swift/Objective-C experience. Deep understanding of iOS SDK and UIKit. Experience with payment or fintech apps preferred.").benefit("Salary 25-40 million VND. Annual performance bonus. MacBook Pro provided. Overseas tech conferences. Premium health insurance.").location("Ho Chi Minh City (Thu Duc)").salaryMin(new BigDecimal("25000")).salaryMax(new BigDecimal("40000")).employmentType("Full-time").experienceRequired(2).requiredSkills("Swift,iOS,UIKit,Xcode,REST APIs").deadline(LocalDate.of(2025, 7, 25)).status(JobStatus.PENDING).build();
        Job j5 = Job.builder().company(c3).recruiter(rec3).industry(ind2).title("Backend Developer (Node.js)").description("MoMo is scaling its payment infrastructure to support 50M users. We need a Node.js developer to build and maintain high-availability financial services.").requirement("2+ years Node.js experience. Understanding of financial systems or payment processing. Experience with event-driven architecture. Redis and MongoDB knowledge preferred.").benefit("Salary 20-35 million VND. Quarterly bonus. Full remote option. Health care for family. 15 days annual leave.").location("Ho Chi Minh City (Le Dai Hanh)").salaryMin(new BigDecimal("20000")).salaryMax(new BigDecimal("35000")).employmentType("Full-time").experienceRequired(2).requiredSkills("Node.js,MongoDB,Redis,Docker,AWS").deadline(LocalDate.of(2025, 8, 1)).status(JobStatus.APPROVED).approvedBy(admin).approvedAt(LocalDateTime.of(2025, 3, 14, 14, 0)).build();
        Job j6 = Job.builder().company(c3).recruiter(rec3).industry(ind2).title("DevOps Engineer").description("Join MoMo's platform team to build and maintain the infrastructure powering Vietnam's #1 e-wallet. Manage Kubernetes clusters handling millions of transactions per day.").requirement("3+ years DevOps experience. Strong Kubernetes and Docker expertise. Experience with AWS or GCP. CI/CD pipeline management. Terraform or Ansible experience required.").benefit("Salary 30-50 million VND. AWS certification sponsorship. Extra PTO for on-call shifts. Modern office in central HCMC.").location("Ho Chi Minh City (Le Dai Hanh)").salaryMin(new BigDecimal("30000")).salaryMax(new BigDecimal("50000")).employmentType("Full-time").experienceRequired(3).requiredSkills("Kubernetes,AWS,Terraform,Docker,Jenkins,Linux").deadline(LocalDate.of(2025, 7, 15)).status(JobStatus.APPROVED).approvedBy(admin).approvedAt(LocalDateTime.of(2025, 3, 13, 10, 0)).build();
        Job j7 = Job.builder().company(c4).recruiter(rec4).industry(ind3).title("Full Stack Developer (React + Node.js)").description("Tiki is building the future of Vietnamese e-commerce. As a full stack developer you will own features end-to-end — from database schema to pixel-perfect UI.").requirement("3+ years full stack experience with React and Node.js. PostgreSQL proficiency. Experience with microservices. Comfortable with agile and fast-paced environments.").benefit("Salary 25-40 million VND. Tiki vouchers monthly. Hybrid work model. Annual team trip abroad. MacBook Air provided.").location("Ho Chi Minh City (Tan Binh)").salaryMin(new BigDecimal("25000")).salaryMax(new BigDecimal("40000")).employmentType("Full-time").experienceRequired(3).requiredSkills("React,Node.js,PostgreSQL,Docker,AWS").deadline(LocalDate.of(2025, 8, 10)).status(JobStatus.APPROVED).approvedBy(admin).approvedAt(LocalDateTime.of(2025, 3, 16, 9, 0)).build();
        Job j8 = Job.builder().company(c4).recruiter(rec4).industry(ind3).title("Flutter Mobile Developer").description("Tiki's mobile app has 10M+ downloads. We are looking for a Flutter expert to lead mobile feature development for our iOS and Android apps.").requirement("2+ years Flutter/Dart experience. Published apps on App Store or Google Play. Experience with Firebase and REST API integration. Strong knowledge of state management (BLoC or Riverpod).").benefit("Salary 22-35 million VND. Flexible hours. Career growth path to Tech Lead. Premium health insurance.").location("Ho Chi Minh City (Tan Binh)").salaryMin(new BigDecimal("22000")).salaryMax(new BigDecimal("35000")).employmentType("Full-time").experienceRequired(2).requiredSkills("Flutter,Dart,Firebase,REST APIs,BLoC").deadline(LocalDate.of(2025, 7, 30)).status(JobStatus.APPROVED).approvedBy(admin).approvedAt(LocalDateTime.of(2025, 3, 17, 11, 0)).build();
        Job j9 = Job.builder().company(c1).recruiter(rec1).industry(ind1).title("Data Engineer").description("FPT Software is expanding its data engineering practice. Help clients build modern data lakehouse architectures on AWS and Azure.").requirement("2+ years data engineering experience. Proficiency with Apache Spark and Airflow. SQL expertise. Experience with dbt or similar transformation tools. Python required.").benefit("Salary 20-38 million VND. Remote-friendly. International project exposure. Annual salary review.").location("Hanoi (Hoa Lac)").salaryMin(new BigDecimal("20000")).salaryMax(new BigDecimal("38000")).employmentType("Full-time").experienceRequired(2).requiredSkills("Python,Apache Spark,Airflow,SQL,AWS,dbt").deadline(LocalDate.of(2025, 6, 30)).status(JobStatus.CLOSED).approvedBy(admin).approvedAt(LocalDateTime.of(2025, 2, 1, 8, 0)).build();
        Job j10 = Job.builder().company(c2).recruiter(rec2).industry(ind1).title("QA Engineer (Automation)").description("VNG needs a QA Automation engineer to build and maintain test frameworks for Zalo backend services. Own quality for features used by 74M Vietnamese users.").requirement("2+ years automation testing experience. Selenium or Playwright proficiency. API testing with Postman or RestAssured. Basic CI/CD knowledge.").benefit("Salary 18-28 million VND. Flexible working hours. Quarterly performance bonus. Free parking.").location("Ho Chi Minh City (Thu Duc)").salaryMin(new BigDecimal("18000")).salaryMax(new BigDecimal("28000")).employmentType("Full-time").experienceRequired(2).requiredSkills("Selenium,Playwright,Java,Postman,CI/CD").deadline(LocalDate.of(2025, 8, 20)).status(JobStatus.PENDING).build();

        jobRepo.saveAll(Arrays.asList(j1, j2, j3, j4, j5, j6, j7, j8, j9, j10));

        // 8. Create Applications (10 applications)
        Application app1 = Application.builder().job(j1).candidate(cand1).cv(cv1).status(ApplicationStatus.SHORTLISTED).appliedAt(LocalDateTime.of(2025, 3, 12, 8, 30)).note("Strong Java background, good portfolio. Shortlisted for technical interview.").build();
        Application app2 = Application.builder().job(j5).candidate(cand1).cv(cv2).status(ApplicationStatus.PENDING).appliedAt(LocalDateTime.of(2025, 3, 14, 9, 0)).note("Applied for Node.js role with fullstack CV.").build();
        Application app3 = Application.builder().job(j3).candidate(cand2).cv(cv3).status(ApplicationStatus.HIRED).appliedAt(LocalDateTime.of(2025, 3, 13, 10, 0)).note("Excellent React skills demonstrated in take-home test. Offer accepted.").build();
        Application app4 = Application.builder().job(j8).candidate(cand2).cv(cv3).status(ApplicationStatus.REJECTED).appliedAt(LocalDateTime.of(2025, 3, 16, 11, 0)).note("Good candidate but lacks Flutter-specific experience.").build();
        Application app5 = Application.builder().job(j7).candidate(cand3).cv(cv5).status(ApplicationStatus.SHORTLISTED).appliedAt(LocalDateTime.of(2025, 3, 17, 8, 0)).note("Strong full stack profile matching requirements well.").build();
        Application app6 = Application.builder().job(j5).candidate(cand3).cv(cv5).status(ApplicationStatus.PENDING).appliedAt(LocalDateTime.of(2025, 3, 18, 9, 30)).note("Also applied to MoMo Node.js role with same CV.").build();
        Application app7 = Application.builder().job(j8).candidate(cand4).cv(cv7).status(ApplicationStatus.SHORTLISTED).appliedAt(LocalDateTime.of(2025, 3, 18, 10, 0)).note("Published 3 Flutter apps. Strong technical screen result.").build();
        Application app8 = Application.builder().job(j3).candidate(cand4).cv(cv7).status(ApplicationStatus.REJECTED).appliedAt(LocalDateTime.of(2025, 3, 19, 11, 0)).note("Mobile background doesn't align with frontend React role.").build();
        Application app9 = Application.builder().job(j6).candidate(cand5).cv(cv9).status(ApplicationStatus.HIRED).appliedAt(LocalDateTime.of(2025, 3, 15, 7, 30)).note("Exceptional DevOps experience, AWS certified. Offer extended and accepted.").build();
        Application app10 = Application.builder().job(j1).candidate(cand5).cv(cv9).status(ApplicationStatus.REJECTED).appliedAt(LocalDateTime.of(2025, 3, 16, 8, 0)).note("DevOps profile not aligned with Java backend position.").build();
        applicationRepo.saveAll(Arrays.asList(app1, app2, app3, app4, app5, app6, app7, app8, app9, app10));

        // 9. Create SavedJobs (10 saved jobs)
        SavedJob sj1 = SavedJob.builder().id(new SavedJobId(cand1.getCandidateId(), j1.getJobId())).candidate(cand1).job(j1).savedAt(LocalDateTime.of(2025, 3, 10, 20, 0)).build();
        SavedJob sj2 = SavedJob.builder().id(new SavedJobId(cand1.getCandidateId(), j5.getJobId())).candidate(cand1).job(j5).savedAt(LocalDateTime.of(2025, 3, 11, 21, 0)).build();
        SavedJob sj3 = SavedJob.builder().id(new SavedJobId(cand2.getCandidateId(), j3.getJobId())).candidate(cand2).job(j3).savedAt(LocalDateTime.of(2025, 3, 10, 19, 30)).build();
        SavedJob sj4 = SavedJob.builder().id(new SavedJobId(cand2.getCandidateId(), j7.getJobId())).candidate(cand2).job(j7).savedAt(LocalDateTime.of(2025, 3, 12, 20, 0)).build();
        SavedJob sj5 = SavedJob.builder().id(new SavedJobId(cand3.getCandidateId(), j7.getJobId())).candidate(cand3).job(j7).savedAt(LocalDateTime.of(2025, 3, 14, 18, 0)).build();
        SavedJob sj6 = SavedJob.builder().id(new SavedJobId(cand3.getCandidateId(), j1.getJobId())).candidate(cand3).job(j1).savedAt(LocalDateTime.of(2025, 3, 14, 18, 30)).build();
        SavedJob sj7 = SavedJob.builder().id(new SavedJobId(cand4.getCandidateId(), j8.getJobId())).candidate(cand4).job(j8).savedAt(LocalDateTime.of(2025, 3, 15, 19, 0)).build();
        SavedJob sj8 = SavedJob.builder().id(new SavedJobId(cand4.getCandidateId(), j6.getJobId())).candidate(cand4).job(j6).savedAt(LocalDateTime.of(2025, 3, 16, 20, 0)).build();
        SavedJob sj9 = SavedJob.builder().id(new SavedJobId(cand5.getCandidateId(), j6.getJobId())).candidate(cand5).job(j6).savedAt(LocalDateTime.of(2025, 3, 13, 17, 0)).build();
        SavedJob sj10 = SavedJob.builder().id(new SavedJobId(cand5.getCandidateId(), j7.getJobId())).candidate(cand5).job(j7).savedAt(LocalDateTime.of(2025, 3, 14, 18, 0)).build();
        savedJobRepo.saveAll(Arrays.asList(sj1, sj2, sj3, sj4, sj5, sj6, sj7, sj8, sj9, sj10));

        log.info("Database successfully seeded with 100% exact SQL matching values!");
    }
}
