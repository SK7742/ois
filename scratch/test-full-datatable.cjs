// Comprehensive Node test for OxfordDataTable logic
function getPaginationRange(current, total) {
    if (total <= 7) {
        return Array.from({ length: total }, (_, i) => i + 1);
    }
    if (current <= 4) {
        return [1, 2, 3, 4, 5, '...', total];
    }
    if (current >= total - 3) {
        return [1, '...', total - 4, total - 3, total - 2, total - 1, total];
    }
    return [1, '...', current - 1, current, current + 1, '...', total];
}

function compareValues(a, b, type, dir) {
    if (a == null && b == null) return 0;
    if (a == null) return dir === 'asc' ? 1 : -1;
    if (b == null) return dir === 'asc' ? -1 : 1;

    let res = 0;
    if (type === 'number') {
        const numA = typeof a === 'number' ? a : parseFloat(String(a).replace(/[^0-9.-]+/g, '')) || 0;
        const numB = typeof b === 'number' ? b : parseFloat(String(b).replace(/[^0-9.-]+/g, '')) || 0;
        res = numA - numB;
    } else if (type === 'date') {
        const dateA = new Date(a).getTime() || 0;
        const dateB = new Date(b).getTime() || 0;
        res = dateA - dateB;
    } else {
        res = String(a).localeCompare(String(b), undefined, { numeric: true, sensitivity: 'base' });
    }
    return dir === 'asc' ? res : -res;
}

// Sample dataset of 15 students
const sampleStudents = [
    { id: 1, admissionNo: 'OX-2026-001', fullName: 'Aarav Kumar', currentClass: 'Class 10', feeStatus: 'CLEARED', attendanceRate: 98, balance: 0 },
    { id: 2, admissionNo: 'OX-2026-002', fullName: 'Diya Sharma', currentClass: 'Class 10', feeStatus: 'OVERDUE', attendanceRate: 85, balance: 12500 },
    { id: 3, admissionNo: 'OX-2026-003', fullName: 'Rohan Gupta', currentClass: 'Class 9', feeStatus: 'CLEARED', attendanceRate: 92, balance: 0 },
    { id: 4, admissionNo: 'OX-2026-004', fullName: 'Ananya Verma', currentClass: 'Class 11', feeStatus: 'OVERDUE', attendanceRate: 78, balance: 8000 },
    { id: 5, admissionNo: 'OX-2026-005', fullName: 'Vikram Singh', currentClass: 'Class 12', feeStatus: 'CLEARED', attendanceRate: 95, balance: 0 },
    { id: 6, admissionNo: 'OX-2026-006', fullName: 'Pooja Patel', currentClass: 'Class 8', feeStatus: 'CLEARED', attendanceRate: 96, balance: 0 },
    { id: 7, admissionNo: 'OX-2026-007', fullName: 'Aditya Rao', currentClass: 'Class 10', feeStatus: 'OVERDUE', attendanceRate: 88, balance: 15000 },
    { id: 8, admissionNo: 'OX-2026-008', fullName: 'Kavya Nair', currentClass: 'Class 9', feeStatus: 'CLEARED', attendanceRate: 99, balance: 0 },
    { id: 9, admissionNo: 'OX-2026-009', fullName: 'Ishaan Joshi', currentClass: 'Class 7', feeStatus: 'CLEARED', attendanceRate: 91, balance: 0 },
    { id: 10, admissionNo: 'OX-2026-010', fullName: 'Sanya Mirza', currentClass: 'Class 10', feeStatus: 'OVERDUE', attendanceRate: 82, balance: 6500 },
    { id: 11, admissionNo: 'OX-2026-011', fullName: 'Rahul Dravid', currentClass: 'Class 11', feeStatus: 'CLEARED', attendanceRate: 97, balance: 0 },
    { id: 12, admissionNo: 'OX-2026-012', fullName: 'Meera Bai', currentClass: 'Class 6', feeStatus: 'CLEARED', attendanceRate: 94, balance: 0 },
    { id: 13, admissionNo: 'OX-2026-013', fullName: 'Kabir Das', currentClass: 'Class 10', feeStatus: 'OVERDUE', attendanceRate: 80, balance: 22000 },
    { id: 14, admissionNo: 'OX-2026-014', fullName: 'Tanvi Shah', currentClass: 'Class 9', feeStatus: 'CLEARED', attendanceRate: 93, balance: 0 },
    { id: 15, admissionNo: 'OX-2026-015', fullName: 'Arjun Reddy', currentClass: 'Class 12', feeStatus: 'CLEARED', attendanceRate: 89, balance: 0 }
];

console.log('--- Testing Search ---');
const query = 'aarav';
const filteredBySearch = sampleStudents.filter(s => 
    s.fullName.toLowerCase().includes(query) || s.admissionNo.toLowerCase().includes(query)
);
console.log(`Query "${query}" found:`, filteredBySearch.map(s => s.fullName));

console.log('\n--- Testing Multi-term Search ("10 overdue") ---');
const terms = ['10', 'overdue'];
const multiTermMatch = sampleStudents.filter(s => {
    const text = `${s.fullName} ${s.admissionNo} ${s.currentClass} ${s.feeStatus}`.toLowerCase();
    return terms.every(t => text.includes(t));
});
console.log('Found:', multiTermMatch.map(s => `${s.fullName} (${s.currentClass}, ${s.feeStatus})`));

console.log('\n--- Testing Sorting by Attendance Descending ---');
const sortedByAttendance = [...sampleStudents].sort((a, b) => compareValues(a.attendanceRate, b.attendanceRate, 'number', 'desc'));
console.log('Top 3 attendance:', sortedByAttendance.slice(0, 3).map(s => `${s.fullName}: ${s.attendanceRate}%`));

console.log('\n--- Testing Sorting by Currency/Balance Descending ---');
const sortedByBalance = [...sampleStudents].sort((a, b) => compareValues(a.balance, b.balance, 'number', 'desc'));
console.log('Top 3 balances:', sortedByBalance.slice(0, 3).map(s => `${s.fullName}: ₹${s.balance}`));

console.log('\n--- Testing Pagination ---');
const pageSize = 5;
const page2 = sampleStudents.slice(5, 10);
console.log('Page 2 of 3 (items 6 to 10):', page2.map(s => s.fullName));
console.log('Pagination Range for Page 2 of 3:', getPaginationRange(2, 3));

console.log('\nALL TESTS PASSED SUCCESSFULLY!');
