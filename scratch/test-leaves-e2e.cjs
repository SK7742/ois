async function run() {
    console.log("=== Step 1: Login as Teacher Dr. Satish Sharma ===");
    const teacherLoginRes = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: 'teacher.sharma', password: 'Teacher@123' })
    });
    const teacherLogin = await teacherLoginRes.json();
    console.log("Teacher login success:", teacherLogin.success);
    const teacherToken = teacherLogin.data.accessToken;

    console.log("\n=== Step 2: Fetch leaves with class filter (studentClass=Class 10A) ===");
    const classLeavesRes = await fetch('http://localhost:8080/api/finance/leaves?studentClass=Class%2010A', {
        headers: { 'Authorization': 'Bearer ' + teacherToken }
    });
    const classLeaves = await classLeavesRes.json();
    console.log("Leaves for Class 10A count:", classLeaves.data?.length);
    console.log("Class 10A leaves data:", JSON.stringify(classLeaves.data, null, 2));

    console.log("\n=== Step 3: Fetch all leaves ===");
    const allLeavesRes = await fetch('http://localhost:8080/api/finance/leaves', {
        headers: { 'Authorization': 'Bearer ' + teacherToken }
    });
    const allLeaves = await allLeavesRes.json();
    const aaravLeave = allLeaves.data.find(l => l.applicantName === 'Aarav Kumar' && l.status === 'PENDING');
    console.log("Found pending Aarav Kumar leave:", aaravLeave);

    if (aaravLeave) {
        console.log(`\n=== Step 4: Teacher approves Aarav's leave request #${aaravLeave.id} ===`);
        const approveRes = await fetch(`http://localhost:8080/api/finance/leaves/${aaravLeave.id}/approve`, {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + teacherToken,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                reviewerName: `${teacherLogin.data.fullName} (Class Teacher)`,
                remarks: "Approved by Class Teacher - medical rest granted."
            })
        });
        const approveData = await approveRes.json();
        console.log("Approve response:", approveData);
    }

    console.log("\n=== Step 5: Student applies for another leave to test Admin approval ===");
    const studentLoginRes = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: 'student.aarav', password: 'Student@123' })
    });
    const studentLogin = await studentLoginRes.json();
    const studentToken = studentLogin.data.accessToken;

    const newLeaveRes = await fetch('http://localhost:8080/api/finance/leaves', {
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + studentToken,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            applicantId: studentLogin.data.studentId || 1,
            applicantName: studentLogin.data.fullName || "Aarav Kumar",
            applicantType: "STUDENT",
            leaveType: "CASUAL",
            startDate: "2026-11-01",
            endDate: "2026-11-02",
            reason: "Family function in Raxaul"
        })
    });
    const newLeave = await newLeaveRes.json();
    console.log("New student leave created:", newLeave);

    console.log("\n=== Step 6: Verify Teacher sees the new leave for Class 10A ===");
    const teacherCheckRes = await fetch('http://localhost:8080/api/finance/leaves?studentClass=Class%2010A', {
        headers: { 'Authorization': 'Bearer ' + teacherToken }
    });
    const teacherCheck = await teacherCheckRes.json();
    const pendingForTeacher = teacherCheck.data.filter(l => l.status === 'PENDING');
    console.log("Pending student leaves visible to teacher:", pendingForTeacher.map(l => ({ id: l.id, name: l.applicantName, class: l.studentClass, roll: l.rollNo, status: l.status })));

    console.log("\n=== Step 7: Admin reviews and approves the new student leave ===");
    const adminLoginRes = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: 'admin', password: 'Admin@123' })
    });
    const adminLogin = await adminLoginRes.json();
    const adminToken = adminLogin.data.accessToken;

    const adminApproveRes = await fetch(`http://localhost:8080/api/finance/leaves/${newLeave.data.id}/approve`, {
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + adminToken,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            reviewerName: "Management (Anil Kumar Gupt)",
            remarks: "Approved by Management"
        })
    });
    const adminApproveData = await adminApproveRes.json();
    console.log("Admin approval response:", adminApproveData);
}

run().catch(console.error);
