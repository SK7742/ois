package com.oxford.csms.config;

import com.oxford.csms.domain.academic.model.*;
import com.oxford.csms.domain.academic.repository.*;
import com.oxford.csms.domain.academic.service.TimetableService;
import com.oxford.csms.domain.admin.model.*;
import com.oxford.csms.domain.admin.repository.*;
import com.oxford.csms.domain.communication.model.IssueTicket;
import com.oxford.csms.domain.communication.model.WhatsAppMessage;
import com.oxford.csms.domain.communication.repository.IssueTicketRepository;
import com.oxford.csms.domain.communication.repository.WhatsAppMessageRepository;
import com.oxford.csms.domain.finance.model.*;
import com.oxford.csms.domain.finance.repository.*;
import com.oxford.csms.security.Role;
import com.oxford.csms.security.User;
import com.oxford.csms.security.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudentRepository studentRepository;
    private final TeachingStaffRepository teachingStaffRepository;
    private final NonTeachingStaffRepository nonTeachingStaffRepository;
    private final AdmissionInquiryRepository inquiryRepository;
    private final AdmissionApplicationRepository applicationRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRecordRepository paymentRecordRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final BusRouteRepository busRouteRepository;
    private final StudentTransitRepository studentTransitRepository;
    private final TimetableService timetableService;
    private final ActivityClubRepository clubRepository;
    private final ActivityEventRepository eventRepository;
    private final ClubEnrollmentRepository clubEnrollmentRepository;
    private final ExamTermRepository examTermRepository;
    private final ExamSubjectRepository examSubjectRepository;
    private final StudentGradeRepository studentGradeRepository;
    private final IssueTicketRepository issueTicketRepository;
    private final WhatsAppMessageRepository whatsAppMessageRepository;
    private final com.oxford.csms.domain.admin.repository.InquiryCommentRepository inquiryCommentRepository;
    private final com.oxford.csms.domain.admin.repository.WorkItemRepository workItemRepository;

    public DataInitializer(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           StudentRepository studentRepository,
                           TeachingStaffRepository teachingStaffRepository,
                           NonTeachingStaffRepository nonTeachingStaffRepository,
                           AdmissionInquiryRepository inquiryRepository,
                           AdmissionApplicationRepository applicationRepository,
                           FeeStructureRepository feeStructureRepository,
                           InvoiceRepository invoiceRepository,
                           PaymentRecordRepository paymentRecordRepository,
                           LeaveRequestRepository leaveRequestRepository,
                           VehicleRepository vehicleRepository,
                           DriverRepository driverRepository,
                           BusRouteRepository busRouteRepository,
                           StudentTransitRepository studentTransitRepository,
                           TimetableService timetableService,
                           ActivityClubRepository clubRepository,
                           ActivityEventRepository eventRepository,
                           ClubEnrollmentRepository clubEnrollmentRepository,
                           ExamTermRepository examTermRepository,
                           ExamSubjectRepository examSubjectRepository,
                           StudentGradeRepository studentGradeRepository,
                           IssueTicketRepository issueTicketRepository,
                           WhatsAppMessageRepository whatsAppMessageRepository,
                           com.oxford.csms.domain.admin.repository.InquiryCommentRepository inquiryCommentRepository,
                           com.oxford.csms.domain.admin.repository.WorkItemRepository workItemRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.studentRepository = studentRepository;
        this.teachingStaffRepository = teachingStaffRepository;
        this.nonTeachingStaffRepository = nonTeachingStaffRepository;
        this.inquiryRepository = inquiryRepository;
        this.applicationRepository = applicationRepository;
        this.feeStructureRepository = feeStructureRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRecordRepository = paymentRecordRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
        this.busRouteRepository = busRouteRepository;
        this.studentTransitRepository = studentTransitRepository;
        this.timetableService = timetableService;
        this.clubRepository = clubRepository;
        this.eventRepository = eventRepository;
        this.clubEnrollmentRepository = clubEnrollmentRepository;
        this.examTermRepository = examTermRepository;
        this.examSubjectRepository = examSubjectRepository;
        this.studentGradeRepository = studentGradeRepository;
        this.issueTicketRepository = issueTicketRepository;
        this.whatsAppMessageRepository = whatsAppMessageRepository;
        this.inquiryCommentRepository = inquiryCommentRepository;
        this.workItemRepository = workItemRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Seed inquiry comment history if not present
        if (inquiryCommentRepository.count() == 0 && inquiryRepository.count() > 0) {
            inquiryRepository.findAll().forEach(inq -> {
                inquiryCommentRepository.save(new com.oxford.csms.domain.admin.model.InquiryComment(
                        inq.getId(),
                        "Admissions Portal",
                        "System",
                        "INITIAL_INQUIRY",
                        "Prospect student inquiry registered for " + inq.getTargetGrade() + ". Parent contact: " + inq.getContactPhone(),
                        inq.getStatus().name()
                ));
                if ("Shivam Tiwari".equals(inq.getCandidateName())) {
                    inquiryCommentRepository.save(new com.oxford.csms.domain.admin.model.InquiryComment(
                            inq.getId(),
                            "Dr. Satish Sharma",
                            "Admissions Coordinator",
                            "PHONE_CALL",
                            "Spoke with father Mr. Alok Tiwari regarding bus Route #1 (Sugauli to Campus). Confirmed bus stop at Sugauli Chowk.",
                            "CONTACTED"
                    ));
                } else if ("Kavya Sharma".equals(inq.getCandidateName())) {
                    inquiryCommentRepository.save(new com.oxford.csms.domain.admin.model.InquiryComment(
                            inq.getId(),
                            "Pooja Verma",
                            "Senior Faculty",
                            "CAMPUS_VISIT",
                            "Parent visited campus physics and computer lab. Highly impressed with smart classrooms.",
                            "CONTACTED"
                    ));
                }
            });
        }

        // Seed initial work items for faculty & staff
        if (workItemRepository.count() == 0) {
            log.info("Seeding initial faculty work items, notes and reminders...");
            workItemRepository.save(new com.oxford.csms.domain.admin.model.WorkItem(
                    "Submit Class 10A CBSE Mid-Term Math Question Papers & Answer Keys",
                    "Finalize mathematics question paper blueprints, marking rubrics, and step-wise answer keys for Term 1 examinations. Submit physical copy to Principal's office.",
                    com.oxford.csms.domain.admin.model.WorkItem.ItemType.TASK,
                    com.oxford.csms.domain.admin.model.WorkItem.Priority.HIGH,
                    "teacher.sharma",
                    "Dr. Satish Sharma",
                    "anil.gupt",
                    "Anil Kumar Gupt (Management)",
                    "MANAGEMENT",
                    LocalDate.now().plusDays(3)
            ));

            workItemRepository.save(new com.oxford.csms.domain.admin.model.WorkItem(
                    "CBSE Science & Math Olympiad Mentor Verification",
                    "Review list of enrolled students from Class 10A for National Science Olympiad (NSO) and verify admit slips.",
                    com.oxford.csms.domain.admin.model.WorkItem.ItemType.REMINDER,
                    com.oxford.csms.domain.admin.model.WorkItem.Priority.URGENT,
                    "teacher.sharma",
                    "Dr. Satish Sharma",
                    "admin",
                    "Er. Sundram Kumar (Admin)",
                    "ADMIN",
                    LocalDate.now().plusDays(1)
            ));

            workItemRepository.save(new com.oxford.csms.domain.admin.model.WorkItem(
                    "Parent Consultation Follow-up: Rohan Gupta (Roll #3)",
                    "Remind parent Mr. Sunil Gupta regarding extra evening remedial classes for physics fundamentals and attendance recovery.",
                    com.oxford.csms.domain.admin.model.WorkItem.ItemType.NOTE,
                    com.oxford.csms.domain.admin.model.WorkItem.Priority.MEDIUM,
                    "teacher.sharma",
                    "Dr. Satish Sharma",
                    "teacher.sharma",
                    "Dr. Satish Sharma",
                    "SELF",
                    LocalDate.now().plusDays(4)
            ));

            com.oxford.csms.domain.admin.model.WorkItem doneItem = new com.oxford.csms.domain.admin.model.WorkItem(
                    "Safety & Equipment Inspection for Physics & Math Lab (Room 101)",
                    "Routine inspection of optical benches, electrical ammeters, and clean workbench compliance. All items certified in good working order.",
                    com.oxford.csms.domain.admin.model.WorkItem.ItemType.ACADEMIC_DUTY,
                    com.oxford.csms.domain.admin.model.WorkItem.Priority.LOW,
                    "teacher.sharma",
                    "Dr. Satish Sharma",
                    "anil.gupt",
                    "Anil Kumar Gupt (Management)",
                    "MANAGEMENT",
                    LocalDate.now().minusDays(1)
            );
            doneItem.setStatus(com.oxford.csms.domain.admin.model.WorkItem.Status.COMPLETED);
            doneItem.setCompletedAt(LocalDateTime.now().minusDays(1));
            workItemRepository.save(doneItem);
        }

        if (userRepository.count() > 0) {
            log.info("Oxford CSMS persistent database already contains data. Preserving all records.");
            return;
        }

        log.info("Infusing actual production data for Oxford International School (Harpur, Raxaul, Bihar)...");

        // 1. Users & Security Roles
        userRepository.save(new User("admin", passwordEncoder.encode("Admin@123"), "System Administrator", "admin@oxford.edu", "+91-9801234567", Set.of(Role.ROLE_ADMIN, Role.ROLE_MANAGEMENT)));
        userRepository.save(new User("anil.gupt", passwordEncoder.encode("Anil@123"), "Anil Kumar Gupt (Management)", "anil.gupt@oxford.edu", "+91-9801122334", Set.of(Role.ROLE_MANAGEMENT, Role.ROLE_ADMIN)));
        userRepository.save(new User("sundram.kumar", passwordEncoder.encode("Sundram@123"), "Er. Sundram Kumar (Lead Architect)", "sundram.kumar@oxford.edu", "+91-9801998877", Set.of(Role.ROLE_ADMIN, Role.ROLE_TEACHER)));
        userRepository.save(new User("teacher.sharma", passwordEncoder.encode("Teacher@123"), "Dr. Satish Sharma", "satish.sharma@oxford.edu", "+91-9801445566", Set.of(Role.ROLE_TEACHER)));
        userRepository.save(new User("teacher.verma", passwordEncoder.encode("Teacher@123"), "Mrs. Pooja Verma", "pooja.verma@oxford.edu", "+91-9801332211", Set.of(Role.ROLE_TEACHER)));
        userRepository.save(new User("student.aarav", passwordEncoder.encode("Student@123"), "Aarav Kumar", "aarav.kumar@student.oxford.edu", "+91-9801776655", Set.of(Role.ROLE_STUDENT)));
        userRepository.save(new User("student.ananya", passwordEncoder.encode("Student@123"), "Ananya Singh", "ananya.singh@student.oxford.edu", "+91-9801223344", Set.of(Role.ROLE_STUDENT)));

        // 2. Teaching & Non-Teaching Faculty
        TeachingStaff t1 = teachingStaffRepository.save(new TeachingStaff(
                "EMP-T-001", "Satish", "Sharma", "satish.sharma@oxford.edu", "+91-9801445566",
                "Mathematics", "Head of Department", "Ph.D. Mathematics, B.Ed", LocalDate.of(2018, 6, 1),
                new BigDecimal("55000"), new BigDecimal("12000"), "Mathematics", 24, true, "Class 10A"
        ));
        TeachingStaff t2 = teachingStaffRepository.save(new TeachingStaff(
                "EMP-T-002", "Pooja", "Verma", "pooja.verma@oxford.edu", "+91-9801332211",
                "Science", "Senior PGT Physics", "M.Sc. Physics, B.Ed", LocalDate.of(2019, 7, 15),
                new BigDecimal("52000"), new BigDecimal("10000"), "Science", 22, true, "Class 10B"
        ));
        TeachingStaff t3 = teachingStaffRepository.save(new TeachingStaff(
                "EMP-T-003", "Rakesh", "Jha", "rakesh.jha@oxford.edu", "+91-9801887766",
                "Humanities", "Senior PGT English", "M.A. English Literature", LocalDate.of(2020, 3, 10),
                new BigDecimal("48000"), new BigDecimal("9000"), "English", 24, false, null
        ));
        TeachingStaff t4 = teachingStaffRepository.save(new TeachingStaff(
                "EMP-T-004", "Sundram", "Kumar", "sundram.kumar@oxford.edu", "+91-9801998877",
                "Computer Science", "Lead Architect & IT Head", "B.Tech Computer Science", LocalDate.of(2021, 1, 5),
                new BigDecimal("65000"), new BigDecimal("15000"), "Computer Science", 18, false, null
        ));
        TeachingStaff t5 = teachingStaffRepository.save(new TeachingStaff(
                "EMP-T-005", "Manoj", "Tiwary", "manoj.tiwary@oxford.edu", "+91-9801448833",
                "Social Science", "TGT History & Civics", "M.A. History, B.Ed", LocalDate.of(2021, 8, 20),
                new BigDecimal("45000"), new BigDecimal("8000"), "Social Science", 22, false, null
        ));
        TeachingStaff t6 = teachingStaffRepository.save(new TeachingStaff(
                "EMP-T-006", "Sunita", "Devi", "sunita.devi@oxford.edu", "+91-9801339944",
                "Languages", "TGT Hindi & Sanskrit", "M.A. Hindi, B.Ed", LocalDate.of(2022, 4, 11),
                new BigDecimal("42000"), new BigDecimal("7500"), "Hindi", 20, false, null
        ));
        TeachingStaff t7 = teachingStaffRepository.save(new TeachingStaff(
                "EMP-T-007", "Amit", "Pandey", "amit.pandey@oxford.edu", "+91-9801662255",
                "Physical Education", "Sports Director", "M.P.Ed, NIS Coach", LocalDate.of(2019, 2, 1),
                new BigDecimal("46000"), new BigDecimal("8500"), "Physical Education", 16, false, null
        ));

        nonTeachingStaffRepository.save(new NonTeachingStaff(
                "EMP-NT-001", "Ramesh", "Yadav", "ramesh.yadav@oxford.edu", "+91-9801554433",
                "Operations", "Transport Supervisor", "Diploma in Automobile", LocalDate.of(2017, 4, 1),
                new BigDecimal("32000"), new BigDecimal("5000"), NonTeachingStaff.OperationalDomain.TRANSPORTATION, "06:30 AM - 04:30 PM"
        ));
        nonTeachingStaffRepository.save(new NonTeachingStaff(
                "EMP-NT-002", "Suresh", "Sah", "suresh.sah@oxford.edu", "+91-9801665544",
                "Laboratory", "Senior Lab Assistant", "B.Sc. Chemistry", LocalDate.of(2020, 8, 1),
                new BigDecimal("28000"), new BigDecimal("4000"), NonTeachingStaff.OperationalDomain.LAB_SUPPORT, "08:00 AM - 04:00 PM"
        ));
        nonTeachingStaffRepository.save(new NonTeachingStaff(
                "EMP-NT-003", "Bindu", "Devi", "bindu.devi@oxford.edu", "+91-9801773322",
                "Health Care", "Campus Nurse & First Aid Head", "Diploma in General Nursing", LocalDate.of(2021, 6, 15),
                new BigDecimal("26000"), new BigDecimal("3500"), NonTeachingStaff.OperationalDomain.ADMINISTRATION, "08:00 AM - 04:00 PM"
        ));
        nonTeachingStaffRepository.save(new NonTeachingStaff(
                "EMP-NT-004", "Ramvilas", "Paswan", "ramvilas.p@oxford.edu", "+91-9801882211",
                "Security", "Campus Security Supervisor", "Ex-Serviceman Army Guard", LocalDate.of(2018, 11, 1),
                new BigDecimal("30000"), new BigDecimal("4500"), NonTeachingStaff.OperationalDomain.SECURITY, "06:00 AM - 06:00 PM"
        ));

        // 3. Students Register (Oxford International School Roster)
        Student s1 = studentRepository.save(new Student(
                "OIS-2026-1001", 1, "Aarav", "Kumar", LocalDate.of(2011, 4, 15),
                "Male", "Class 10", "A", "Rajesh Kumar", "+91-9801776655", "rajesh.k@example.com",
                "O+", "Station Road, Harpur, Raxaul, East Champaran, Bihar"
        ));
        s1.setAttendanceRate(96.0);
        s1.setFeeStatus(Student.StudentFeeStatus.CLEARED);
        s1.setNotesSummary("Consistently excels in Mathematics and CBSE Science Olympiad preparation.");
        studentRepository.save(s1);

        Student s2 = studentRepository.save(new Student(
                "OIS-2026-1002", 2, "Ananya", "Singh", LocalDate.of(2011, 8, 22),
                "Female", "Class 10", "A", "Manoj Singh", "+91-9801223344", "manoj.singh@example.com",
                "B+", "Customs Colony, Raxaul, Bihar"
        ));
        s2.setAttendanceRate(95.2);
        s2.setFeeStatus(Student.StudentFeeStatus.CLEARED);
        s2.setNotesSummary("Active classroom participation and leadership in cultural committee.");
        studentRepository.save(s2);

        Student s3 = studentRepository.save(new Student(
                "OIS-2026-1003", 3, "Rohan", "Gupta", LocalDate.of(2011, 11, 5),
                "Male", "Class 10", "A", "Sunil Gupta", "+91-9801443322", "sunil.g@example.com",
                "A+", "Harpur Chowk, Raxaul, Bihar"
        ));
        s3.setAttendanceRate(68.5);
        s3.setFeeStatus(Student.StudentFeeStatus.PENDING);
        s3.setNotesSummary("Parent consultation held regarding low attendance (<70%) and Term 1 physics fundamentals.");
        studentRepository.save(s3);

        Student s4 = studentRepository.save(new Student(
                "OIS-2026-1004", 4, "Priya", "Kumari", LocalDate.of(2012, 2, 18),
                "Female", "Class 10", "A", "Vikash Kumar", "+91-9801994411", "vikash.k@example.com",
                "AB+", "Main Bazar, Ramgarhwa, East Champaran, Bihar"
        ));
        s4.setAttendanceRate(97.4);
        s4.setFeeStatus(Student.StudentFeeStatus.CLEARED);
        studentRepository.save(s4);

        Student s5 = studentRepository.save(new Student(
                "OIS-2026-1005", 5, "Aditya", "Sharma", LocalDate.of(2011, 6, 10),
                "Male", "Class 10", "A", "Santosh Sharma", "+91-9801662200", "santosh.sharma@example.com",
                "O+", "Harpur Main Road, Raxaul, Bihar"
        ));
        s5.setAttendanceRate(64.0);
        s5.setFeeStatus(Student.StudentFeeStatus.PENDING);
        s5.setNotesSummary("Medical leave submitted for 2 weeks. Needs remedial coaching in algebra.");
        studentRepository.save(s5);

        Student s6 = studentRepository.save(new Student(
                "OIS-2026-1006", 6, "Sneha", "Patel", LocalDate.of(2011, 9, 30),
                "Female", "Class 10", "A", "Dinesh Patel", "+91-9801551199", "dinesh.patel@example.com",
                "A+", "Pipra More, Raxaul, Bihar"
        ));
        s6.setAttendanceRate(93.8);
        s6.setFeeStatus(Student.StudentFeeStatus.CLEARED);
        studentRepository.save(s6);

        Student s7 = studentRepository.save(new Student(
                "OIS-2026-1007", 7, "Vikramaditya", "Verma", LocalDate.of(2011, 1, 14),
                "Male", "Class 10", "A", "Arvind Verma", "+91-9801338877", "arvind.verma@example.com",
                "B+", "Sugauli Road, East Champaran, Bihar"
        ));
        s7.setAttendanceRate(89.2);
        s7.setFeeStatus(Student.StudentFeeStatus.OVERDUE);
        s7.setNotesSummary("Quarter 2 tuition fee pending. Follow-up reminder dispatched to father Arvind Verma.");
        studentRepository.save(s7);

        Student s8 = studentRepository.save(new Student(
                "OIS-2026-1008", 8, "Muskan", "Khatun", LocalDate.of(2011, 3, 25),
                "Female", "Class 10", "A", "Md. Aslam", "+91-9801449922", "aslam.m@example.com",
                "O-", "Mithanpura, Raxaul, Bihar"
        ));
        s8.setAttendanceRate(98.1);
        s8.setFeeStatus(Student.StudentFeeStatus.CLEARED);
        studentRepository.save(s8);

        Student s9 = studentRepository.save(new Student(
                "OIS-2026-1009", 1, "Ayush", "Gupt", LocalDate.of(2009, 9, 12),
                "Male", "Class 12", "A", "Anil Kumar Gupt", "+91-9801122334", "anil.gupt@oxford.edu",
                "B+", "Oxford Executive Campus, Harpur, Raxaul, Bihar"
        ));
        s9.setAttendanceRate(96.5);
        s9.setFeeStatus(Student.StudentFeeStatus.CLEARED);
        studentRepository.save(s9);

        Student s10 = studentRepository.save(new Student(
                "OIS-2026-1010", 4, "Tanvi", "Jha", LocalDate.of(2011, 12, 19),
                "Female", "Class 10", "B", "Harish Jha", "+91-9801669955", "harish.jha@example.com",
                "A+", "Raxaul Bazar, Bihar"
        ));
        s10.setAttendanceRate(94.0);
        studentRepository.save(s10);

        Student s11 = studentRepository.save(new Student(
                "OIS-2026-1011", 12, "Shambhavi", "Mishra", LocalDate.of(2012, 5, 8),
                "Female", "Class 9", "A", "Prabhat Mishra", "+91-9801884433", "prabhat.m@example.com",
                "O+", "Dumariya, East Champaran, Bihar"
        ));
        s11.setAttendanceRate(91.5);
        studentRepository.save(s11);

        Student s12 = studentRepository.save(new Student(
                "OIS-2026-1012", 15, "Ritik", "Raj", LocalDate.of(2010, 7, 16),
                "Male", "Class 11", "A", "Ajay Raj", "+91-9801773311", "ajay.raj@example.com",
                "B+", "Ahiraulia, Raxaul, Bihar"
        ));
        s12.setAttendanceRate(90.0);
        studentRepository.save(s12);

        // 4. Inquiries & Applications
        inquiryRepository.save(new AdmissionInquiry("Kavya Sharma", "Deepak Sharma", "+91-9801667788", "deepak@example.com", "Class 11", "DAV Public School, Raxaul", "Interested in Science with Computer stream."));
        inquiryRepository.save(new AdmissionInquiry("Shivam Tiwari", "Alok Tiwari", "+91-9801556677", "alok@example.com", "Class 6", "St. Xavier High School", "Inquiry regarding bus route from Sugauli."));
        inquiryRepository.save(new AdmissionInquiry("Aryan Raj", "Manoj Raj", "+91-9801447722", "manoj.raj@example.com", "Class 10", "Kendriya Vidyalaya", "Mid-session transfer from Patna due to parent posting."));
        inquiryRepository.save(new AdmissionInquiry("Divya Pandey", "Gopal Pandey", "+91-9801331166", "gopal.p@example.com", "Class 9", "Bright Future Academy", "Fee concession inquiry under Sibling Category."));

        AdmissionApplication app1 = new AdmissionApplication();
        app1.setApplicationNumber("APP-2026-0042");
        app1.setStudentName("Meera Patel");
        app1.setDateOfBirth(LocalDate.of(2011, 5, 20));
        app1.setGender("Female");
        app1.setTargetGrade("Class 10");
        app1.setFatherName("Hasmukh Patel");
        app1.setFatherOccupation("Merchant");
        app1.setMotherName("Geeta Patel");
        app1.setContactPhone("+91-9801334455");
        app1.setContactEmail("hasmukh@example.com");
        app1.setAddress("Nepal Border Road, Raxaul, Bihar");
        app1.setBloodGroup("O+");
        app1.setDocumentsVerified(true);
        app1.setStatus(AdmissionApplication.ApplicationStatus.DOCUMENTS_VERIFIED);
        app1.setReviewRemarks("All academic transcripts verified. Ready for 1-click enrollment.");
        applicationRepository.save(app1);

        AdmissionApplication app2 = new AdmissionApplication();
        app2.setApplicationNumber("APP-2026-0043");
        app2.setStudentName("Harsh Kumar");
        app2.setDateOfBirth(LocalDate.of(2011, 10, 12));
        app2.setGender("Male");
        app2.setTargetGrade("Class 10");
        app2.setFatherName("Sanjay Kumar");
        app2.setFatherOccupation("Government Servant");
        app2.setMotherName("Anita Devi");
        app2.setContactPhone("+91-9801228899");
        app2.setContactEmail("sanjay.k@example.com");
        app2.setAddress("Railway Colony, Raxaul, Bihar");
        app2.setBloodGroup("B+");
        app2.setDocumentsVerified(false);
        app2.setStatus(AdmissionApplication.ApplicationStatus.UNDER_REVIEW);
        app2.setReviewRemarks("Transfer Certificate pending from previous school.");
        applicationRepository.save(app2);

        // 5. Fee Structures
        feeStructureRepository.save(new FeeStructure("Class 10", FeeCategory.TUITION, new BigDecimal("4500.00"), "MONTHLY", "Core academic instruction & smart classroom fee"));
        feeStructureRepository.save(new FeeStructure("Class 10", FeeCategory.COMPUTER_LAB, new BigDecimal("800.00"), "QUARTERLY", "High-speed lab, robotics & AI facility"));
        feeStructureRepository.save(new FeeStructure("Class 10", FeeCategory.EXAMINATION, new BigDecimal("1200.00"), "ANNUAL", "CBSE patterned term examinations & gradebook"));
        feeStructureRepository.save(new FeeStructure("Class 9", FeeCategory.TUITION, new BigDecimal("4000.00"), "MONTHLY", "Academic tuition fee for Grade 9"));
        feeStructureRepository.save(new FeeStructure("Class 11", FeeCategory.TUITION, new BigDecimal("5200.00"), "MONTHLY", "Senior secondary tuition fee for Grade 11"));
        feeStructureRepository.save(new FeeStructure("Class 12", FeeCategory.TUITION, new BigDecimal("5500.00"), "MONTHLY", "Senior secondary tuition fee for Grade 12"));

        // 6. Invoices & Payments (Term 1 - 2026)
        Invoice inv1 = new Invoice(
                "INV-2026-1001", s1.getId(), s1.getFullName(), s1.getAdmissionNo(), s1.getCurrentClass(),
                "Term 1 - 2026", new BigDecimal("5300.00"), new BigDecimal("0.00"), new BigDecimal("5300.00"),
                LocalDate.now().plusDays(10), "Standard Tuition & Lab Fee"
        );
        inv1.setPaidAmount(new BigDecimal("5300.00"));
        inv1.setStatus(Invoice.InvoiceStatus.PAID);
        invoiceRepository.save(inv1);

        paymentRecordRepository.save(new PaymentRecord(
                "REC-2026-1001", inv1.getId(), s1.getId(), new BigDecimal("5300.00"),
                PaymentRecord.PaymentMethod.UPI, "UPI-9847291048", "Accounts Desk", "Full Term 1 Fee Cleared via PhonePe"
        ));

        // Invoice with scholarship (Staff Ward - 75% waiver)
        Invoice inv2 = new Invoice(
                "INV-2026-1002", s9.getId(), s9.getFullName(), s9.getAdmissionNo(), s9.getCurrentClass(),
                "Term 1 - 2026", new BigDecimal("5500.00"), new BigDecimal("4125.00"), new BigDecimal("1375.00"),
                LocalDate.now().plusDays(15), "Oxford Staff Ward Benefit (75% Waiver)"
        );
        inv2.setPaidAmount(new BigDecimal("1375.00"));
        inv2.setStatus(Invoice.InvoiceStatus.PAID);
        invoiceRepository.save(inv2);

        paymentRecordRepository.save(new PaymentRecord(
                "REC-2026-1002", inv2.getId(), s9.getId(), new BigDecimal("1375.00"),
                PaymentRecord.PaymentMethod.NET_BANKING, "NEFT-SBI-491029", "Accounts Desk", "Staff concession fee cleared"
        ));

        // Invoice pending for Rohan Gupta
        Invoice inv3 = new Invoice(
                "INV-2026-1003", s3.getId(), s3.getFullName(), s3.getAdmissionNo(), s3.getCurrentClass(),
                "Term 1 - 2026", new BigDecimal("5300.00"), new BigDecimal("0.00"), new BigDecimal("5300.00"),
                LocalDate.now().plusDays(5), "Standard Rate"
        );
        invoiceRepository.save(inv3);

        // Invoice paid for Priya Kumari
        Invoice inv4 = new Invoice(
                "INV-2026-1004", s4.getId(), s4.getFullName(), s4.getAdmissionNo(), s4.getCurrentClass(),
                "Term 1 - 2026", new BigDecimal("5300.00"), new BigDecimal("795.00"), new BigDecimal("4505.00"),
                LocalDate.now().plusDays(10), "Sibling Discount (15% Waiver)"
        );
        inv4.setPaidAmount(new BigDecimal("4505.00"));
        inv4.setStatus(Invoice.InvoiceStatus.PAID);
        invoiceRepository.save(inv4);

        paymentRecordRepository.save(new PaymentRecord(
                "REC-2026-1004", inv4.getId(), s4.getId(), new BigDecimal("4505.00"),
                PaymentRecord.PaymentMethod.CASH, "CASH-REC-104", "Accounts Counter", "Cash received against Sibling Concession"
        ));

        // Invoice pending for Vikramaditya Verma
        Invoice inv5 = new Invoice(
                "INV-2026-1005", s7.getId(), s7.getFullName(), s7.getAdmissionNo(), s7.getCurrentClass(),
                "Term 1 - 2026", new BigDecimal("5300.00"), new BigDecimal("0.00"), new BigDecimal("5300.00"),
                LocalDate.now().plusDays(7), "Standard Rate"
        );
        invoiceRepository.save(inv5);

        // 7. Transportation Fleet & Routes
        Vehicle v1 = vehicleRepository.save(new Vehicle("BR-05-PA-1234", "Tata Marcopolo 40-Seater", 40));
        Vehicle v2 = vehicleRepository.save(new Vehicle("BR-05-TB-5678", "Force Traveller 26-Seater", 26));

        Driver d1 = driverRepository.save(new Driver("Mahendra Prasad", "+91-9801441122", "BR-05-DL-2015-889", 12));
        Driver d2 = driverRepository.save(new Driver("Suresh Sah", "+91-9801552233", "BR-05-DL-2018-442", 8));

        BusRoute route1 = new BusRoute("Route 1: Raxaul Station - Harpur Campus", "Raxaul Railway Station", "Harpur Oxford Campus", v1.getId(), d1.getId(), new BigDecimal("1200.00"));
        route1.getStops().add(new RouteStop("Raxaul Railway Station Gate 1", "07:15 AM", "03:45 PM", 1));
        route1.getStops().add(new RouteStop("Customs Chowk Raxaul", "07:30 AM", "03:30 PM", 2));
        route1.getStops().add(new RouteStop("Harpur High School More", "07:45 AM", "03:15 PM", 3));
        route1.getStops().add(new RouteStop("Oxford International School Main Gate", "08:00 AM", "03:00 PM", 4));
        BusRoute savedRoute1 = busRouteRepository.save(route1);

        BusRoute route2 = new BusRoute("Route 2: Ramgarhwa - Sugauli - Harpur Campus", "Ramgarhwa Bazar", "Harpur Oxford Campus", v2.getId(), d2.getId(), new BigDecimal("1500.00"));
        route2.getStops().add(new RouteStop("Ramgarhwa Bazar Chowk", "07:00 AM", "04:00 PM", 1));
        route2.getStops().add(new RouteStop("Sugauli By-pass More", "07:20 AM", "03:40 PM", 2));
        route2.getStops().add(new RouteStop("Pipra Kothi Junction", "07:40 AM", "03:20 PM", 3));
        route2.getStops().add(new RouteStop("Oxford International School Main Gate", "08:00 AM", "03:00 PM", 4));
        BusRoute savedRoute2 = busRouteRepository.save(route2);

        studentTransitRepository.save(new StudentTransit(s1.getId(), savedRoute1.getId(), savedRoute1.getStops().get(0).getId()));
        s1.setBusRouteId(savedRoute1.getId());
        studentRepository.save(s1);

        studentTransitRepository.save(new StudentTransit(s2.getId(), savedRoute1.getId(), savedRoute1.getStops().get(1).getId()));
        s2.setBusRouteId(savedRoute1.getId());
        studentRepository.save(s2);

        studentTransitRepository.save(new StudentTransit(s4.getId(), savedRoute2.getId(), savedRoute2.getStops().get(0).getId()));
        s4.setBusRouteId(savedRoute2.getId());
        studentRepository.save(s4);

        // 8. Auto Timetable Creation
        timetableService.generateTimetableForClass("Class 10", "A", "Room 101");
        timetableService.generateTimetableForClass("Class 10", "B", "Room 102");

        // 9. Clubs & Events
        ActivityClub c1 = clubRepository.save(new ActivityClub("Oxford Robotics & AI Society", "ROBOTICS", "Er. Sundram Kumar", "Wednesday & Friday 03:30 PM", 35, "Hands-on robotics, microcontrollers, IoT, and coding with Python."));
        ActivityClub c2 = clubRepository.save(new ActivityClub("Oxford Debate & Oratory Society", "DEBATE", "Mr. Rakesh Jha", "Tuesday & Thursday 03:30 PM", 40, "Inter-school debate preparation, public speaking, and Model UN training."));
        ActivityClub c3 = clubRepository.save(new ActivityClub("Eco & Nature Explorers Club", "SCIENCE", "Mrs. Pooja Verma", "Monday 03:30 PM", 50, "Botanical studies, rainwater harvesting, and environmental awareness in Champaran."));
        ActivityClub c4 = clubRepository.save(new ActivityClub("Oxford Sports & Athletics Guild", "SPORTS", "Mr. Amit Pandey", "Daily 06:30 AM & 03:30 PM", 60, "Athletics track training, football, basketball, and cricket teams."));

        clubEnrollmentRepository.save(new ClubEnrollment(c1.getId(), s1.getId(), s1.getFullName(), "President"));
        clubEnrollmentRepository.save(new ClubEnrollment(c2.getId(), s2.getId(), s2.getFullName(), "Secretary"));
        clubEnrollmentRepository.save(new ClubEnrollment(c3.getId(), s4.getId(), s4.getFullName(), "Executive Member"));
        clubEnrollmentRepository.save(new ClubEnrollment(c4.getId(), s5.getId(), s5.getFullName(), "Vice Captain"));

        eventRepository.save(new ActivityEvent("Annual Champaran Science & Tech Exhibition 2026", LocalDate.now().plusDays(20), "School Auditorium & Exhibition Hall", "All Grades", "Er. Sundram Kumar", "Showcasing student science innovations, smart farming models, and robotic rovers."));
        eventRepository.save(new ActivityEvent("Inter-School English Debate Championship", LocalDate.now().plusDays(32), "Auditorium Main Hall", "Grades 8 to 12", "Mr. Rakesh Jha", "Topic: 'Artificial Intelligence in Classroom Pedagogy: Catalyst or Compromise?'"));
        eventRepository.save(new ActivityEvent("Oxford Annual Winter Sports Meet 2026", LocalDate.now().plusDays(48), "Main Athletics Stadium", "All Grades", "Mr. Amit Pandey", "House competitions in sprint, relay, long jump, football, and cricket."));

        // 10. Exam Terms, Subjects & Report Cards
        ExamTerm term1 = examTermRepository.save(new ExamTerm("Term 1 Mid-Term Examination 2026", "2026-2027", LocalDate.now().minusDays(30), LocalDate.now().minusDays(10)));
        term1.setResultsPublished(true);
        examTermRepository.save(term1);

        examSubjectRepository.save(new ExamSubject(term1.getId(), "Class 10", "Mathematics", 100.0, 35.0, LocalDate.now().minusDays(28)));
        examSubjectRepository.save(new ExamSubject(term1.getId(), "Class 10", "Science", 100.0, 35.0, LocalDate.now().minusDays(26)));
        examSubjectRepository.save(new ExamSubject(term1.getId(), "Class 10", "English", 100.0, 35.0, LocalDate.now().minusDays(24)));
        examSubjectRepository.save(new ExamSubject(term1.getId(), "Class 10", "Social Science", 100.0, 35.0, LocalDate.now().minusDays(22)));
        examSubjectRepository.save(new ExamSubject(term1.getId(), "Class 10", "Computer Science", 100.0, 35.0, LocalDate.now().minusDays(20)));

        // Aarav Kumar Gradebook
        studentGradeRepository.save(new StudentGrade(term1.getId(), s1.getId(), s1.getFullName(), s1.getAdmissionNo(), "Class 10", "A", "Mathematics", 94.0, 100.0));
        studentGradeRepository.save(new StudentGrade(term1.getId(), s1.getId(), s1.getFullName(), s1.getAdmissionNo(), "Class 10", "A", "Science", 91.0, 100.0));
        studentGradeRepository.save(new StudentGrade(term1.getId(), s1.getId(), s1.getFullName(), s1.getAdmissionNo(), "Class 10", "A", "English", 88.0, 100.0));
        studentGradeRepository.save(new StudentGrade(term1.getId(), s1.getId(), s1.getFullName(), s1.getAdmissionNo(), "Class 10", "A", "Social Science", 86.0, 100.0));
        studentGradeRepository.save(new StudentGrade(term1.getId(), s1.getId(), s1.getFullName(), s1.getAdmissionNo(), "Class 10", "A", "Computer Science", 98.0, 100.0));

        // Ananya Singh Gradebook
        studentGradeRepository.save(new StudentGrade(term1.getId(), s2.getId(), s2.getFullName(), s2.getAdmissionNo(), "Class 10", "A", "Mathematics", 89.0, 100.0));
        studentGradeRepository.save(new StudentGrade(term1.getId(), s2.getId(), s2.getFullName(), s2.getAdmissionNo(), "Class 10", "A", "Science", 93.0, 100.0));
        studentGradeRepository.save(new StudentGrade(term1.getId(), s2.getId(), s2.getFullName(), s2.getAdmissionNo(), "Class 10", "A", "English", 96.0, 100.0));
        studentGradeRepository.save(new StudentGrade(term1.getId(), s2.getId(), s2.getFullName(), s2.getAdmissionNo(), "Class 10", "A", "Social Science", 91.0, 100.0));
        studentGradeRepository.save(new StudentGrade(term1.getId(), s2.getId(), s2.getFullName(), s2.getAdmissionNo(), "Class 10", "A", "Computer Science", 90.0, 100.0));

        // Rohan Gupta Gradebook
        studentGradeRepository.save(new StudentGrade(term1.getId(), s3.getId(), s3.getFullName(), s3.getAdmissionNo(), "Class 10", "A", "Mathematics", 72.0, 100.0));
        studentGradeRepository.save(new StudentGrade(term1.getId(), s3.getId(), s3.getFullName(), s3.getAdmissionNo(), "Class 10", "A", "Science", 76.0, 100.0));
        studentGradeRepository.save(new StudentGrade(term1.getId(), s3.getId(), s3.getFullName(), s3.getAdmissionNo(), "Class 10", "A", "English", 70.0, 100.0));
        studentGradeRepository.save(new StudentGrade(term1.getId(), s3.getId(), s3.getFullName(), s3.getAdmissionNo(), "Class 10", "A", "Social Science", 68.0, 100.0));
        studentGradeRepository.save(new StudentGrade(term1.getId(), s3.getId(), s3.getFullName(), s3.getAdmissionNo(), "Class 10", "A", "Computer Science", 78.0, 100.0));

        // Priya Kumari Gradebook
        studentGradeRepository.save(new StudentGrade(term1.getId(), s4.getId(), s4.getFullName(), s4.getAdmissionNo(), "Class 10", "A", "Mathematics", 95.0, 100.0));
        studentGradeRepository.save(new StudentGrade(term1.getId(), s4.getId(), s4.getFullName(), s4.getAdmissionNo(), "Class 10", "A", "Science", 94.0, 100.0));
        studentGradeRepository.save(new StudentGrade(term1.getId(), s4.getId(), s4.getFullName(), s4.getAdmissionNo(), "Class 10", "A", "English", 90.0, 100.0));
        studentGradeRepository.save(new StudentGrade(term1.getId(), s4.getId(), s4.getFullName(), s4.getAdmissionNo(), "Class 10", "A", "Social Science", 92.0, 100.0));
        studentGradeRepository.save(new StudentGrade(term1.getId(), s4.getId(), s4.getFullName(), s4.getAdmissionNo(), "Class 10", "A", "Computer Science", 96.0, 100.0));

        // 11. Leave Requests (Both Faculty and Student)
        LeaveRequest l1 = new LeaveRequest(t1.getId(), t1.getFullName(), "FACULTY", LeaveRequest.LeaveType.CASUAL,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(11), "Attending National Mathematics Symposium at Patna University");
        l1.setStatus(LeaveRequest.LeaveStatus.APPROVED);
        l1.setReviewedBy("Anil Kumar Gupt (Management)");
        l1.setRemarks("Academic duty approved with full allowance.");
        leaveRequestRepository.save(l1);

        LeaveRequest l2 = new LeaveRequest(s1.getId(), s1.getFullName(), "STUDENT", LeaveRequest.LeaveType.SICK,
                LocalDate.now().minusDays(5), LocalDate.now().minusDays(4), "Seasonal viral fever with doctor prescription on file");
        l2.setStatus(LeaveRequest.LeaveStatus.APPROVED);
        l2.setReviewedBy("Dr. Satish Sharma (Class Teacher)");
        l2.setRemarks("Medical leave verified and attendance excused.");
        leaveRequestRepository.save(l2);

        LeaveRequest l3 = new LeaveRequest(s3.getId(), s3.getFullName(), "STUDENT", LeaveRequest.LeaveType.CASUAL,
                LocalDate.now().plusDays(4), LocalDate.now().plusDays(5), "Attending elder brother's wedding ceremony in Motihari");
        l3.setStatus(LeaveRequest.LeaveStatus.PENDING);
        leaveRequestRepository.save(l3);

        // 12. WhatsApp Notification Logs
        whatsAppMessageRepository.save(new WhatsAppMessage("+91-9801776655", "Rajesh Kumar", "FEE_DUES_REMINDER", "Dear Parent, fee payment of Rs. 5300 for Aarav Kumar (OIS-2026-1001) was received via PhonePe. Receipt: REC-2026-1001. Oxford International School, Harpur."));
        whatsAppMessageRepository.save(new WhatsAppMessage("+91-9801443322", "Sunil Gupta", "FEE_DUES_REMINDER", "Dear Parent, reminder: Term 1 fee dues of Rs. 5300 for Rohan Gupta (OIS-2026-1003) is due on 29 Oct 2026. Oxford International School, Harpur."));
        whatsAppMessageRepository.save(new WhatsAppMessage("+91-9801338877", "Arvind Verma", "STUDENT_ABSENT_ALERT", "Dear Parent, your ward Vikramaditya Verma was marked ABSENT today. Kindly contact class teacher if unnotified. Oxford International School."));
        whatsAppMessageRepository.save(new WhatsAppMessage("+91-9801994411", "Vikash Kumar", "CAMPUS_CIRCULAR", "Dear Parent, Oxford Science & Tech Exhibition 2026 is scheduled for 15 Nov. You are cordially invited to campus. Oxford International School."));

        // 13. Service Tickets with Live SLA Escalation
        IssueTicket tck1 = new IssueTicket("TCK-2026-001", "Physics Lab Sensor Calibration", "The digital Vernier caliper and light bench in Physics Lab require recalibration before practical exams.", IssueTicket.IssueCategory.ACADEMIC_GRIEVANCE, IssueTicket.IssuePriority.MEDIUM, "Mrs. Pooja Verma", "+91-9801332211");
        tck1.setStatus(IssueTicket.TicketStatus.IN_PROGRESS);
        issueTicketRepository.save(tck1);

        IssueTicket tck2 = new IssueTicket("TCK-2026-002", "North Campus Security Boom Barrier Motor Stalled", "North gate boom barrier motor stopped responding during morning bus arrival rush at Harpur campus.", IssueTicket.IssueCategory.FACILITIES, IssueTicket.IssuePriority.CRITICAL, "Ramesh Yadav", "+91-9801554433");
        tck2.setEscalated(true);
        tck2.setEscalatedTo("Anil Kumar Gupt (Management)");
        tck2.setEscalatedAt(LocalDateTime.now().minusHours(3));
        tck2.setStatus(IssueTicket.TicketStatus.ESCALATED_TO_MANAGEMENT);
        tck2.setEscalationReason("CRITICAL SLA breached: Immediate management intervention required");
        issueTicketRepository.save(tck2);

        IssueTicket tck3 = new IssueTicket("TCK-2026-003", "Bus #2 AC Blower Low Pressure on Sugauli Route", "Force Traveller BR-05-TB-5678 passenger AC vents require cleaning and filter replacement.", IssueTicket.IssueCategory.FACILITIES, IssueTicket.IssuePriority.HIGH, "Suresh Sah", "+91-9801552233");
        issueTicketRepository.save(tck3);

        log.info("Oxford International School production seed data successfully infused and committed to disk!");
    }
}
