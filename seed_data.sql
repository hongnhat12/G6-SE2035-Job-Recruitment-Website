USE JobRecruitmentDB;
GO

-- =========================
-- USERS
-- 1 admin, 4 recruiters, 5 candidates = 10 rows
-- PasswordHash is bcrypt of "Password@123" for all
-- =========================
SET IDENTITY_INSERT Users ON;
INSERT INTO Users (UserID, Email, PasswordHash, Role, Status, CreatedAt) VALUES
(1,  'admin@joboard.io',          '$2a$10$Xl0yhvzLIaJCDdKBS0Mu9eAlSLtPRZ.U5jxFbgLV9bAcBkVVqxW5e', 'ADMIN',     'ACTIVE', '2025-01-01 08:00:00'),
(2,  'recruiter.minh@fpt.com.vn', '$2a$10$Xl0yhvzLIaJCDdKBS0Mu9eAlSLtPRZ.U5jxFbgLV9bAcBkVVqxW5e', 'RECRUITER', 'ACTIVE', '2025-01-05 09:00:00'),
(3,  'recruiter.lan@vng.com.vn',  '$2a$10$Xl0yhvzLIaJCDdKBS0Mu9eAlSLtPRZ.U5jxFbgLV9bAcBkVVqxW5e', 'RECRUITER', 'ACTIVE', '2025-01-06 09:30:00'),
(4,  'recruiter.duc@momo.vn',     '$2a$10$Xl0yhvzLIaJCDdKBS0Mu9eAlSLtPRZ.U5jxFbgLV9bAcBkVVqxW5e', 'RECRUITER', 'ACTIVE', '2025-01-07 10:00:00'),
(5,  'recruiter.hoa@tiki.vn',     '$2a$10$Xl0yhvzLIaJCDdKBS0Mu9eAlSLtPRZ.U5jxFbgLV9bAcBkVVqxW5e', 'RECRUITER', 'ACTIVE', '2025-01-08 10:30:00'),
(6,  'nguyen.tuan@gmail.com',     '$2a$10$Xl0yhvzLIaJCDdKBS0Mu9eAlSLtPRZ.U5jxFbgLV9bAcBkVVqxW5e', 'CANDIDATE', 'ACTIVE', '2025-02-01 07:00:00'),
(7,  'tran.mai@gmail.com',        '$2a$10$Xl0yhvzLIaJCDdKBS0Mu9eAlSLtPRZ.U5jxFbgLV9bAcBkVVqxW5e', 'CANDIDATE', 'ACTIVE', '2025-02-03 08:00:00'),
(8,  'pham.khoa@gmail.com',       '$2a$10$Xl0yhvzLIaJCDdKBS0Mu9eAlSLtPRZ.U5jxFbgLV9bAcBkVVqxW5e', 'CANDIDATE', 'ACTIVE', '2025-02-05 09:00:00'),
(9,  'le.bich@gmail.com',         '$2a$10$Xl0yhvzLIaJCDdKBS0Mu9eAlSLtPRZ.U5jxFbgLV9bAcBkVVqxW5e', 'CANDIDATE', 'ACTIVE', '2025-02-07 10:00:00'),
(10, 'hoang.son@gmail.com',       '$2a$10$Xl0yhvzLIaJCDdKBS0Mu9eAlSLtPRZ.U5jxFbgLV9bAcBkVVqxW5e', 'CANDIDATE', 'ACTIVE', '2025-02-10 11:00:00');
SET IDENTITY_INSERT Users OFF;
GO

-- =========================
-- COMPANY (4 rows — each recruiter maps to one)
-- =========================
SET IDENTITY_INSERT Company ON;
INSERT INTO Company (CompanyID, CompanyName, Description, Website, Email, Phone, Address, Logo, Status) VALUES
(1, N'FPT Software',   N'Largest IT outsourcing company in Vietnam, providing software services to global clients.', 'https://fptsoftware.com',  'hr@fpt.com.vn',      '024-7300-7300', N'FPT Complex, Hoa Lac Hi-Tech Park, Hanoi',       'fpt_logo.png',   'ACTIVE'),
(2, N'VNG Corporation',N'Leading technology corporation in Vietnam, operating Zalo, ZaloPay and cloud services.',   'https://vng.com.vn',       'careers@vng.com.vn', '028-3562-6789', N'Z06, Street 13, Tan Thuan Dong, HCMC',           'vng_logo.png',   'ACTIVE'),
(3, N'MoMo',           N'Vietnam''s leading e-wallet platform with over 30 million users nationwide.',               'https://momo.vn',          'hr@momo.vn',         '028-3636-0068', N'Flemington Tower, 182 Le Dai Hanh, Ward 15, HCMC','momo_logo.png',  'ACTIVE'),
(4, N'Tiki',           N'One of Vietnam''s top e-commerce platforms, offering fast 2-hour delivery nationwide.',     'https://tiki.vn',          'hr@tiki.vn',         '028-3620-3636', N'52 Ut Tich, Ward 4, Tan Binh District, HCMC',    'tiki_logo.png',  'ACTIVE');
SET IDENTITY_INSERT Company OFF;
GO

-- =========================
-- RECRUITER (4 rows, one per company)
-- =========================
SET IDENTITY_INSERT Recruiter ON;
INSERT INTO Recruiter (RecruiterID, UserID, CompanyID, FullName, Phone, Position) VALUES
(1, 2, 1, N'Nguyen Van Minh', '0912-345-678', N'Senior HR Manager'),
(2, 3, 2, N'Tran Thi Lan',    '0923-456-789', N'Talent Acquisition Lead'),
(3, 4, 3, N'Le Van Duc',      '0934-567-890', N'HR Specialist'),
(4, 5, 4, N'Pham Thi Hoa',    '0945-678-901', N'Recruitment Manager');
SET IDENTITY_INSERT Recruiter OFF;
GO

-- =========================
-- CANDIDATE (5 rows)
-- =========================
SET IDENTITY_INSERT Candidate ON;
INSERT INTO Candidate (CandidateID, UserID, FullName, Phone, Birthday, Gender, Address, Headline, Summary, ProfileImage, Skills) VALUES
(1, 6,  N'Nguyen Minh Tuan', '0901-111-222', '1998-05-14', N'Male',   N'Ha Dong, Hanoi',         N'Java Backend Developer | 3 years exp',           N'Experienced backend developer specializing in Spring Boot microservices and cloud-native applications. Passionate about clean architecture and performance optimization.',                        'tuan.jpg',   'Java,Spring Boot,PostgreSQL,Docker,Redis'),
(2, 7,  N'Tran Ngoc Mai',    '0902-222-333', '1999-11-22', N'Female', N'Binh Thanh, HCMC',       N'Frontend Developer | React & Vue specialist',    N'Creative frontend developer with 2 years of experience building responsive web applications. Strong eye for UI/UX and passionate about accessibility.',                                        'mai.jpg',    'React,Vue.js,TypeScript,Tailwind CSS,Figma'),
(3, 8,  N'Pham Duc Khoa',    '0903-333-444', '1997-08-30', N'Male',   N'Cau Giay, Hanoi',        N'Full Stack Developer | Node.js & React',         N'Full stack developer with 4 years of experience in both frontend and backend systems. Comfortable working across the entire stack from database design to deployment.',                        'khoa.jpg',   'Node.js,React,MongoDB,Express,AWS,Docker'),
(4, 9,  N'Le Thi Bich Ngoc', '0904-444-555', '2000-03-18', N'Female', N'Go Vap, HCMC',           N'Mobile Developer | Flutter & React Native',      N'Mobile developer passionate about cross-platform applications. Built 3 published apps on both App Store and Google Play with combined 50k+ downloads.',                                         'bich.jpg',   'Flutter,Dart,React Native,Firebase,REST APIs'),
(5, 10, N'Hoang Quoc Son',   '0905-555-666', '1996-12-05', N'Male',   N'Dong Da, Hanoi',         N'DevOps Engineer | Kubernetes & AWS certified',   N'DevOps engineer with 5 years of experience automating infrastructure and CI/CD pipelines. AWS Certified Solutions Architect. Experienced with large-scale distributed systems.',                 'son.jpg',    'Kubernetes,AWS,Terraform,Jenkins,Docker,Linux');
SET IDENTITY_INSERT Candidate OFF;
GO

-- =========================
-- CV (2 CVs per candidate = 10 rows)
-- =========================
SET IDENTITY_INSERT CV ON;
INSERT INTO CV (CVID, CandidateID, CVName, FilePath, IsDefault, UploadedAt) VALUES
(1,  1, N'Tuan_CV_Backend_2025.pdf',      'uploads/cv/tuan_backend_2025.pdf',   1, '2025-02-01 10:00:00'),
(2,  1, N'Tuan_CV_Fullstack_2025.pdf',    'uploads/cv/tuan_fullstack_2025.pdf', 0, '2025-02-15 11:00:00'),
(3,  2, N'Mai_CV_Frontend_2025.pdf',      'uploads/cv/mai_frontend_2025.pdf',   1, '2025-02-03 09:00:00'),
(4,  2, N'Mai_CV_UX_2025.pdf',            'uploads/cv/mai_ux_2025.pdf',         0, '2025-02-20 10:00:00'),
(5,  3, N'Khoa_CV_Fullstack_2025.pdf',    'uploads/cv/khoa_fullstack_2025.pdf', 1, '2025-02-05 08:30:00'),
(6,  3, N'Khoa_CV_Backend_2025.pdf',      'uploads/cv/khoa_backend_2025.pdf',   0, '2025-03-01 09:00:00'),
(7,  4, N'Bich_CV_Mobile_2025.pdf',       'uploads/cv/bich_mobile_2025.pdf',    1, '2025-02-07 14:00:00'),
(8,  4, N'Bich_CV_Flutter_2025.pdf',      'uploads/cv/bich_flutter_2025.pdf',   0, '2025-03-05 10:00:00'),
(9,  5, N'Son_CV_DevOps_2025.pdf',        'uploads/cv/son_devops_2025.pdf',     1, '2025-02-10 13:00:00'),
(10, 5, N'Son_CV_Cloud_Architect_2025.pdf','uploads/cv/son_cloud_2025.pdf',     0, '2025-03-10 11:00:00');
SET IDENTITY_INSERT CV OFF;
GO

-- =========================
-- JOB (10 rows — mix of APPROVED, PENDING, CLOSED)
-- ApprovedBy = UserID 1 (admin)
-- =========================
SET IDENTITY_INSERT Job ON;
INSERT INTO Job (JobID, CompanyID, RecruiterID, Title, Industry, Description, Requirement, Benefit, Location, SalaryMin, SalaryMax, EmploymentType, ExperienceRequired, RequiredSkills, Deadline, Status, ApprovedBy, ApprovedAt, CreatedAt, UpdatedAt) VALUES
(1,  1, 1, N'Senior Java Backend Developer',
    N'Information Technology',
    N'Join FPT Software to build scalable microservices for enterprise clients across the US and Japan markets. You will work on high-traffic systems processing millions of requests daily.',
    N'3+ years Java experience. Proficiency with Spring Boot and microservices architecture. Experience with PostgreSQL or MySQL. Knowledge of Docker and Kubernetes is a plus.',
    N'Competitive salary up to $2000/month. 13th month salary. Premium health insurance. Annual team-building trip. Flexible remote work 2 days/week.',
    N'Hanoi (Hoa Lac)', 25000000, 45000000, N'Full-time', 3,
    'Java,Spring Boot,PostgreSQL,Docker,Microservices',
    '2025-07-31', 'APPROVED', 1, '2025-03-10 09:00:00', '2025-03-08 08:00:00', '2025-03-10 09:00:00'),

(2,  1, 1, N'Junior Backend Developer (.NET)',
    N'Information Technology',
    N'Great opportunity for fresh graduates or junior developers to join FPT Software''s growing .NET team. Mentorship provided by senior engineers.',
    N'0-1 year experience. Knowledge of C# and .NET fundamentals. Understanding of RESTful APIs. Eagerness to learn and grow.',
    N'Salary 10-15 million VND. Full social insurance. Training and certification support. Clear promotion roadmap.',
    N'Hanoi (Hoa Lac)', 10000000, 15000000, N'Full-time', 0,
    'C#,.NET,SQL Server,REST APIs',
    '2025-08-15', 'APPROVED', 1, '2025-03-11 10:00:00', '2025-03-09 09:00:00', '2025-03-11 10:00:00'),

(3,  2, 2, N'Frontend Developer (React)',
    N'Information Technology',
    N'VNG is looking for a talented React developer to build next-generation features for Zalo Web — Vietnam''s most used messaging platform with 74 million users.',
    N'2+ years React experience. Strong TypeScript skills. Experience with state management (Redux or Zustand). Eye for UI/UX detail.',
    N'Salary up to 30 million VND. Stock options available. World-class tech stack. Free lunch at office. Flexible hours.',
    N'Ho Chi Minh City (Thu Duc)', 20000000, 30000000, N'Full-time', 2,
    'React,TypeScript,Redux,HTML,CSS',
    '2025-07-20', 'APPROVED', 1, '2025-03-12 11:00:00', '2025-03-10 10:00:00', '2025-03-12 11:00:00'),

(4,  2, 2, N'Mobile Developer (iOS)',
    N'Information Technology',
    N'Build native iOS features for ZaloPay, VNG''s fast-growing digital payments app. Work with a cross-functional team of designers and product managers.',
    N'2+ years Swift/Objective-C experience. Deep understanding of iOS SDK and UIKit. Experience with payment or fintech apps preferred.',
    N'Salary 25-40 million VND. Annual performance bonus. MacBook Pro provided. Overseas tech conferences. Premium health insurance.',
    N'Ho Chi Minh City (Thu Duc)', 25000000, 40000000, N'Full-time', 2,
    'Swift,iOS,UIKit,Xcode,REST APIs',
    '2025-07-25', 'PENDING', NULL, NULL, '2025-03-15 08:00:00', '2025-03-15 08:00:00'),

(5,  3, 3, N'Backend Developer (Node.js)',
    N'Financial Technology',
    N'MoMo is scaling its payment infrastructure to support 50M users. We need a Node.js developer to build and maintain high-availability financial services.',
    N'2+ years Node.js experience. Understanding of financial systems or payment processing. Experience with event-driven architecture. Redis and MongoDB knowledge preferred.',
    N'Salary 20-35 million VND. Quarterly bonus. Full remote option. Health care for family. 15 days annual leave.',
    N'Ho Chi Minh City (Le Dai Hanh)', 20000000, 35000000, N'Full-time', 2,
    'Node.js,MongoDB,Redis,Docker,AWS',
    '2025-08-01', 'APPROVED', 1, '2025-03-14 14:00:00', '2025-03-12 11:00:00', '2025-03-14 14:00:00'),

(6,  3, 3, N'DevOps Engineer',
    N'Financial Technology',
    N'Join MoMo''s platform team to build and maintain the infrastructure powering Vietnam''s #1 e-wallet. Manage Kubernetes clusters handling millions of transactions per day.',
    N'3+ years DevOps experience. Strong Kubernetes and Docker expertise. Experience with AWS or GCP. CI/CD pipeline management. Terraform or Ansible experience required.',
    N'Salary 30-50 million VND. AWS certification sponsorship. Extra PTO for on-call shifts. Modern office in central HCMC.',
    N'Ho Chi Minh City (Le Dai Hanh)', 30000000, 50000000, N'Full-time', 3,
    'Kubernetes,AWS,Terraform,Docker,Jenkins,Linux',
    '2025-07-15', 'APPROVED', 1, '2025-03-13 10:00:00', '2025-03-11 09:00:00', '2025-03-13 10:00:00'),

(7,  4, 4, N'Full Stack Developer (React + Node.js)',
    N'E-commerce',
    N'Tiki is building the future of Vietnamese e-commerce. As a full stack developer you will own features end-to-end — from database schema to pixel-perfect UI.',
    N'3+ years full stack experience with React and Node.js. PostgreSQL proficiency. Experience with microservices. Comfortable with agile and fast-paced environments.',
    N'Salary 25-40 million VND. Tiki vouchers monthly. Hybrid work model. Annual team trip abroad. MacBook Air provided.',
    N'Ho Chi Minh City (Tan Binh)', 25000000, 40000000, N'Full-time', 3,
    'React,Node.js,PostgreSQL,Docker,AWS',
    '2025-08-10', 'APPROVED', 1, '2025-03-16 09:00:00', '2025-03-14 08:00:00', '2025-03-16 09:00:00'),

(8,  4, 4, N'Flutter Mobile Developer',
    N'E-commerce',
    N'Tiki''s mobile app has 10M+ downloads. We are looking for a Flutter expert to lead mobile feature development for our iOS and Android apps.',
    N'2+ years Flutter/Dart experience. Published apps on App Store or Google Play. Experience with Firebase and REST API integration. Strong knowledge of state management (BLoC or Riverpod).',
    N'Salary 22-35 million VND. Flexible hours. Career growth path to Tech Lead. Premium health insurance.',
    N'Ho Chi Minh City (Tan Binh)', 22000000, 35000000, N'Full-time', 2,
    'Flutter,Dart,Firebase,REST APIs,BLoC',
    '2025-07-30', 'APPROVED', 1, '2025-03-17 11:00:00', '2025-03-15 10:00:00', '2025-03-17 11:00:00'),

(9,  1, 1, N'Data Engineer',
    N'Information Technology',
    N'FPT Software is expanding its data engineering practice. Help clients build modern data lakehouse architectures on AWS and Azure.',
    N'2+ years data engineering experience. Proficiency with Apache Spark and Airflow. SQL expertise. Experience with dbt or similar transformation tools. Python required.',
    N'Salary 20-38 million VND. Remote-friendly. International project exposure. Annual salary review.',
    N'Hanoi (Hoa Lac)', 20000000, 38000000, N'Full-time', 2,
    'Python,Apache Spark,Airflow,SQL,AWS,dbt',
    '2025-06-30', 'CLOSED', 1, '2025-02-01 08:00:00', '2025-01-28 09:00:00', '2025-03-01 08:00:00'),

(10, 2, 2, N'QA Engineer (Automation)',
    N'Information Technology',
    N'VNG needs a QA Automation engineer to build and maintain test frameworks for Zalo backend services. Own quality for features used by 74M Vietnamese users.',
    N'2+ years automation testing experience. Selenium or Playwright proficiency. API testing with Postman or RestAssured. Basic CI/CD knowledge.',
    N'Salary 18-28 million VND. Flexible working hours. Quarterly performance bonus. Free parking.',
    N'Ho Chi Minh City (Thu Duc)', 18000000, 28000000, N'Full-time', 2,
    'Selenium,Playwright,Java,Postman,CI/CD',
    '2025-08-20', 'PENDING', NULL, NULL, '2025-03-18 08:00:00', '2025-03-18 08:00:00');
SET IDENTITY_INSERT Job OFF;
GO

-- =========================
-- APPLICATION (10 rows — candidates apply to approved jobs)
-- =========================
SET IDENTITY_INSERT Application ON;
INSERT INTO Application (ApplicationID, JobID, CandidateID, CVID, Status, AppliedAt, Note) VALUES
(1,  1, 1, 1, 'SHORTLISTED', '2025-03-12 08:30:00', N'Strong Java background, good portfolio. Shortlisted for technical interview.'),
(2,  5, 1, 2, 'PENDING',     '2025-03-14 09:00:00', N'Applied for Node.js role with fullstack CV.'),
(3,  3, 2, 3, 'HIRED',       '2025-03-13 10:00:00', N'Excellent React skills demonstrated in take-home test. Offer accepted.'),
(4,  8, 2, 3, 'REJECTED',    '2025-03-16 11:00:00', N'Good candidate but lacks Flutter-specific experience.'),
(5,  7, 3, 5, 'SHORTLISTED', '2025-03-17 08:00:00', N'Strong full stack profile matching requirements well.'),
(6,  5, 3, 5, 'PENDING',     '2025-03-18 09:30:00', N'Also applied to MoMo Node.js role with same CV.'),
(7,  8, 4, 7, 'SHORTLISTED', '2025-03-18 10:00:00', N'Published 3 Flutter apps. Strong technical screen result.'),
(8,  3, 4, 7, 'REJECTED',    '2025-03-19 11:00:00', N'Mobile background doesn''t align with frontend React role.'),
(9,  6, 5, 9, 'HIRED',       '2025-03-15 07:30:00', N'Exceptional DevOps experience, AWS certified. Offer extended and accepted.'),
(10, 1, 5, 9, 'REJECTED',    '2025-03-16 08:00:00', N'DevOps profile not aligned with Java backend position.');
SET IDENTITY_INSERT Application OFF;
GO

-- =========================
-- SAVEDJOB (10 rows — candidates bookmark interesting jobs)
-- =========================
INSERT INTO SavedJob (CandidateID, JobID, SavedAt) VALUES
(1, 1,  '2025-03-10 20:00:00'),
(1, 5,  '2025-03-11 21:00:00'),
(2, 3,  '2025-03-10 19:30:00'),
(2, 7,  '2025-03-12 20:00:00'),
(3, 7,  '2025-03-14 18:00:00'),
(3, 1,  '2025-03-14 18:30:00'),
(4, 8,  '2025-03-15 19:00:00'),
(4, 6,  '2025-03-16 20:00:00'),
(5, 6,  '2025-03-13 17:00:00'),
(5, 7,  '2025-03-14 18:00:00');
GO
