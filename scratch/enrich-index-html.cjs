const fs = require('fs');
let html = fs.readFileSync('C:/Users/DELL/.gemini/antigravity/scratch/oxford-csms/backend/src/main/resources/static/index.html', 'utf8');

// Function to replace thead for a specific table pattern
function updateTableHead(targetTbodyId, newTheadContent, tableId) {
    const tbodyIdx = html.indexOf(`id="${targetTbodyId}"`);
    if (tbodyIdx === -1) {
        console.warn(`Tbody ${targetTbodyId} not found!`);
        return;
    }
    const tableStart = html.lastIndexOf('<table', tbodyIdx);
    const tableEnd = html.indexOf('>', tableStart);
    let originalTableTag = html.substring(tableStart, tableEnd + 1);
    
    // Add id to table if not present
    if (tableId && !originalTableTag.includes('id=')) {
        const newTableTag = originalTableTag.replace('<table', `<table id="${tableId}"`);
        html = html.substring(0, tableStart) + newTableTag + html.substring(tableEnd + 1);
    }
    
    // Now find thead
    const currentTableStart = html.lastIndexOf('<table', html.indexOf(`id="${targetTbodyId}"`));
    const theadStart = html.indexOf('<thead>', currentTableStart);
    const theadEnd = html.indexOf('</thead>', theadStart) + '</thead>'.length;
    
    if (theadStart !== -1 && theadStart < html.indexOf(`id="${targetTbodyId}"`)) {
        html = html.substring(0, theadStart) + newTheadContent + html.substring(theadEnd);
        console.log(`Updated thead for ${targetTbodyId}`);
    } else {
        console.warn(`Thead not found before ${targetTbodyId}`);
    }
}

// 1. Applications table
updateTableHead('applications-table-body', `<thead>
                                            <tr>
                                                <th data-sort="applicationNumber">Application #</th>
                                                <th data-sort="studentName">Student Name</th>
                                                <th data-sort="appliedGrade">Applied Grade</th>
                                                <th data-sort="targetGrade">Assigned Placement</th>
                                                <th data-sort="fatherName">Parent / Guardian</th>
                                                <th data-sort="documentsVerified">Documents</th>
                                                <th data-sort="status">Status</th>
                                                <th data-sort="active">Active State</th>
                                                <th style="width: 140px; text-align: center;">Actions</th>
                                            </tr>
                                        </thead>`, 'applications-table');

// 2. Inquiries table
updateTableHead('inquiries-table-body', `<thead>
                                        <tr>
                                            <th data-sort="studentName">Candidate</th>
                                            <th data-sort="grade">Grade</th>
                                            <th data-sort="parentName">Parent / Phone</th>
                                            <th data-sort="previousSchool">Previous School</th>
                                            <th data-sort="status">Status</th>
                                            <th data-sort="notes">Notes</th>
                                            <th style="width: 100px; text-align: center;">Action</th>
                                        </tr>
                                    </thead>`, 'inquiries-table');

// 3. Students table
updateTableHead('students-table-body', `<thead>
                                        <tr>
                                            <th data-sort="admissionNo">Admission #</th>
                                            <th data-sort="fullName">Student Full Name</th>
                                            <th data-sort="currentClass">Class & Section</th>
                                            <th data-sort="rollNo" data-sort-type="number">Roll No</th>
                                            <th data-sort="guardianPhone">Guardian Phone</th>
                                            <th data-sort="attendanceRate" data-sort-type="number">Attendance</th>
                                            <th data-sort="feeStatus">Fee Status</th>
                                            <th style="width: 130px; text-align: center;">Report Card</th>
                                        </tr>
                                    </thead>`, 'students-table');

// 4. Employees table
updateTableHead('employees-table-body', `<thead>
                                            <tr>
                                                <th data-sort="employeeCode">Staff ID</th>
                                                <th data-sort="fullName">Full Name</th>
                                                <th data-sort="department">Department</th>
                                                <th data-sort="designation">Designation</th>
                                                <th data-sort="qualification">Qualification</th>
                                                <th data-sort="basicSalary" data-sort-type="number">Monthly Salary</th>
                                                <th data-sort="status">Status</th>
                                            </tr>
                                        </thead>`, 'employees-table');

// 5. Teaching faculty table
updateTableHead('teaching-table-body', `<thead>
                                        <tr>
                                            <th data-sort="employeeCode">Staff ID</th>
                                            <th data-sort="fullName">Faculty Name</th>
                                            <th data-sort="subjectSpecialization">Specialization</th>
                                            <th data-sort="assignedClass">Assigned Class</th>
                                            <th data-sort="weeklyTargetPeriods" data-sort-type="number">Weekly Target Periods</th>
                                            <th data-sort="email">Official Email</th>
                                        </tr>
                                    </thead>`, 'teaching-table');

// 6. Non-teaching staff table
updateTableHead('non-teaching-table-body', `<thead>
                                        <tr>
                                            <th data-sort="employeeCode">Staff ID</th>
                                            <th data-sort="fullName">Staff Name</th>
                                            <th data-sort="operationalDomain">Operational Wing</th>
                                            <th data-sort="shiftTiming">Shift Timing</th>
                                            <th data-sort="phone">Contact Phone</th>
                                        </tr>
                                    </thead>`, 'non-teaching-table');

// 7. Invoices table
updateTableHead('invoices-table-body', `<thead>
                                        <tr>
                                            <th data-sort="invoiceNumber">Invoice #</th>
                                            <th data-sort="studentName">Student Name</th>
                                            <th data-sort="billingPeriod">Term Period</th>
                                            <th data-sort="grossAmount" data-sort-type="number">Gross Fee</th>
                                            <th data-sort="discountAmount" data-sort-type="number">Scholarship / Concession</th>
                                            <th data-sort="netPayable" data-sort-type="number">Net Amount</th>
                                            <th data-sort="paidAmount" data-sort-type="number">Paid</th>
                                            <th data-sort="status">Status</th>
                                            <th style="width: 110px; text-align: center;">Action</th>
                                        </tr>
                                    </thead>`, 'invoices-table');

// 8. Leaves table (Admin)
updateTableHead('leaves-table-body', `<thead>
                                        <tr>
                                            <th data-sort="applicantName">Applicant</th>
                                            <th data-sort="applicantType">Category</th>
                                            <th data-sort="leaveType">Leave Type</th>
                                            <th data-sort="startDate" data-sort-type="date">Start Date</th>
                                            <th data-sort="endDate" data-sort-type="date">End Date</th>
                                            <th data-sort="reason">Reason</th>
                                            <th data-sort="status">Status</th>
                                            <th style="min-width: 160px; text-align: center;">Action</th>
                                        </tr>
                                    </thead>`, 'leaves-table');

// 9. Exam grades table
updateTableHead('exam-grades-table-body', `<thead>
                                        <tr>
                                            <th data-sort="term">Term</th>
                                            <th data-sort="studentName">Student Name</th>
                                            <th data-sort="subjectName">Subject</th>
                                            <th data-sort="marksObtained" data-sort-type="number">Marks Obtained</th>
                                            <th data-sort="maxMarks" data-sort-type="number">Maximum Marks</th>
                                            <th data-sort="letterGrade">Grade Letter</th>
                                            <th style="width: 130px; text-align: center;">Action</th>
                                        </tr>
                                    </thead>`, 'exam-grades-table');

// 10. WhatsApp table
updateTableHead('whatsapp-table-body', `<thead>
                                        <tr>
                                            <th data-sort="createdAt" data-sort-type="date">Dispatch Time</th>
                                            <th data-sort="recipientPhone">Recipient Mobile</th>
                                            <th data-sort="recipientName">Recipient Name</th>
                                            <th data-sort="templateName">Notice Template</th>
                                            <th data-sort="messageBody">Message Text</th>
                                            <th data-sort="status">Delivery Status</th>
                                            <th data-sort="providerMessageId">Reference ID</th>
                                        </tr>
                                    </thead>`, 'whatsapp-table');

// 11. Tickets table
updateTableHead('tickets-table-body', `<thead>
                                        <tr>
                                            <th data-sort="ticketNumber">Ticket #</th>
                                            <th data-sort="title">Issue Title</th>
                                            <th data-sort="category">Category</th>
                                            <th data-sort="priority">Priority SLA</th>
                                            <th data-sort="status">Current Status</th>
                                            <th data-sort="escalatedTo">Escalated Authority</th>
                                            <th style="width: 110px; text-align: center;">Action</th>
                                        </tr>
                                    </thead>`, 'tickets-table');

// 12. Teacher Work Items table
updateTableHead('teacher-workitems-body', `<thead>
                                            <tr>
                                                <th data-sort="status">Status</th>
                                                <th data-sort="title">Title & Description</th>
                                                <th data-sort="category">Category</th>
                                                <th data-sort="priority">Priority</th>
                                                <th data-sort="assignedBy">Assigned By</th>
                                                <th data-sort="dueDate" data-sort-type="date">Due Date</th>
                                                <th style="width: 90px; text-align: center;">Actions</th>
                                            </tr>
                                        </thead>`, 'teacher-workitems-table');

// 13. Teacher Students table
updateTableHead('teacher-students-body', `<thead>
                                        <tr>
                                            <th data-sort="fullName">Student Name</th>
                                            <th data-sort="guardianName">Parent Name</th>
                                            <th data-sort="guardianPhone">Mobile</th>
                                            <th data-sort="address">Address</th>
                                            <th data-sort="attendanceRate" data-sort-type="number" style="text-align: center;">Attendance %</th>
                                            <th data-sort="feeStatus">Fee Reminder</th>
                                            <th style="text-align: center;">Report Card</th>
                                            <th data-sort="notes">Notes Summary</th>
                                        </tr>
                                    </thead>`, 'assigned-class-students-table');

// 14. Teacher Marks table
updateTableHead('teacher-marks-body', `<thead>
                                        <tr>
                                            <th data-sort="admissionNo">Admission #</th>
                                            <th data-sort="studentName">Student Name</th>
                                            <th data-sort="subjectName">Subject</th>
                                            <th>Assessment Term</th>
                                            <th data-sort="maxMarks" data-sort-type="number">Max Marks</th>
                                            <th data-sort="marksObtained" data-sort-type="number">Marks Scored</th>
                                            <th data-sort="letterGrade">Grade Letter</th>
                                        </tr>
                                    </thead>`, 'teacher-marks-table');

// 15. Teacher Student Leaves table
updateTableHead('teacher-student-leaves-table-body', `<thead>
                                            <tr>
                                                <th data-sort="applicantName">Student Name & Roll</th>
                                                <th data-sort="leaveType">Leave Type</th>
                                                <th data-sort="days" data-sort-type="number">Duration</th>
                                                <th data-sort="startDate" data-sort-type="date">Absence Dates</th>
                                                <th data-sort="reason">Reason / Guardian Note</th>
                                                <th data-sort="status">Status</th>
                                                <th style="min-width: 170px;">Action / Reviewer</th>
                                            </tr>
                                        </thead>`, 'teacher-student-leaves-table');

// 16. Teacher Faculty Leaves table
updateTableHead('teacher-leaves-table-body', `<thead>
                                        <tr>
                                            <th data-sort="leaveType">Leave Type</th>
                                            <th>Duration</th>
                                            <th data-sort="startDate" data-sort-type="date">Start Date</th>
                                            <th data-sort="endDate" data-sort-type="date">End Date</th>
                                            <th data-sort="reason">Reason</th>
                                            <th data-sort="status">Approval Status</th>
                                            <th data-sort="reviewedBy">Reviewed By</th>
                                        </tr>
                                    </thead>`, 'teacher-leaves-table');

// 17. Student Fees table
updateTableHead('student-fees-table-body', `<thead>
                                        <tr>
                                            <th data-sort="invoiceNumber">Invoice #</th>
                                            <th data-sort="billingPeriod">Billing Term</th>
                                            <th data-sort="grossAmount" data-sort-type="number">Tuition Fee</th>
                                            <th data-sort="netPayable" data-sort-type="number">Net Amount</th>
                                            <th data-sort="paidAmount" data-sort-type="number">Paid Amount</th>
                                            <th data-sort="status">Status</th>
                                            <th style="width: 130px; text-align: center;">Receipt</th>
                                        </tr>
                                    </thead>`, 'student-fees-table');

// 18. Student Leaves table
updateTableHead('student-leaves-table-body', `<thead>
                                        <tr>
                                            <th data-sort="leaveType">Leave Type</th>
                                            <th data-sort="startDate" data-sort-type="date">From Date</th>
                                            <th data-sort="endDate" data-sort-type="date">To Date</th>
                                            <th data-sort="reason">Reason</th>
                                            <th data-sort="status">Status</th>
                                        </tr>
                                    </thead>`, 'student-leaves-table');

fs.writeFileSync('C:/Users/DELL/.gemini/antigravity/scratch/oxford-csms/backend/src/main/resources/static/index.html', html, 'utf8');
console.log('Successfully updated index.html with sort headers and table IDs!');
