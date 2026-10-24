// Oxford International School - Comprehensive School Management System (CSMS)
// Production Authentication & Strict Role-Based Portal Controller

const API_BASE = '';
let currentUser = null;
let authToken = null;

// ==============================================================
// ENTERPRISE NOTIFICATION & CUSTOM MODAL DIALOG SYSTEM
// ==============================================================

function escapeHtml(str) {
    if (!str) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

function showToast(title, message = '', type = 'success', duration = 4000) {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `app-toast toast-${type}`;

    let iconClass = 'fa-circle-check';
    if (type === 'error') iconClass = 'fa-circle-xmark';
    else if (type === 'warning') iconClass = 'fa-triangle-exclamation';
    else if (type === 'info') iconClass = 'fa-circle-info';

    toast.innerHTML = `
        <div class="toast-icon"><i class="fa-solid ${iconClass}"></i></div>
        <div class="toast-body">
            <div class="toast-title">${escapeHtml(title)}</div>
            ${message ? `<div class="toast-desc">${escapeHtml(message)}</div>` : ''}
        </div>
        <button class="toast-close" onclick="dismissToast(this.parentElement)">&times;</button>
        <div class="toast-progress"></div>
    `;

    container.appendChild(toast);

    const timer = setTimeout(() => {
        dismissToast(toast);
    }, duration);

    toast._timer = timer;
}

function dismissToast(toastEl) {
    if (!toastEl || toastEl.classList.contains('toast-hiding')) return;
    clearTimeout(toastEl._timer);
    toastEl.classList.add('toast-hiding');
    setTimeout(() => {
        toastEl.remove();
    }, 300);
}

function showCustomDialog({ title, message, type = 'info', confirmText = 'OK', cancelText = null, confirmBtnClass = 'btn-primary' }) {
    return new Promise((resolve) => {
        const modal = document.getElementById('modal-custom-dialog');
        const titleEl = document.getElementById('dialog-title');
        const msgEl = document.getElementById('dialog-message');
        const iconWrapper = document.getElementById('dialog-icon-wrapper');
        const iconEl = document.getElementById('dialog-icon');
        const actionsEl = document.getElementById('dialog-actions');

        if (!modal) {
            resolve(true);
            return;
        }

        titleEl.textContent = title || 'Institutional Notice';
        msgEl.textContent = message || '';

        iconWrapper.className = `custom-dialog-icon dialog-icon-${type}`;
        let iconClass = 'fa-circle-info';
        if (type === 'success') iconClass = 'fa-circle-check';
        else if (type === 'warning') iconClass = 'fa-triangle-exclamation';
        else if (type === 'error') iconClass = 'fa-circle-xmark';
        iconEl.className = `fa-solid ${iconClass}`;

        actionsEl.innerHTML = '';

        if (cancelText) {
            const cancelBtn = document.createElement('button');
            cancelBtn.type = 'button';
            cancelBtn.className = 'btn btn-outline';
            cancelBtn.textContent = cancelText;
            cancelBtn.onclick = () => {
                closeModal('modal-custom-dialog');
                resolve(false);
            };
            actionsEl.appendChild(cancelBtn);
        }

        const confirmBtn = document.createElement('button');
        confirmBtn.type = 'button';
        confirmBtn.className = `btn ${confirmBtnClass}`;
        confirmBtn.textContent = confirmText;
        confirmBtn.onclick = () => {
            closeModal('modal-custom-dialog');
            resolve(true);
        };
        actionsEl.appendChild(confirmBtn);

        openModal('modal-custom-dialog');
        confirmBtn.focus();
    });
}

function appAlert(title, message, type = 'info') {
    return showCustomDialog({ title, message, type, confirmText: 'Understood' });
}

function appConfirm(title, message, confirmText = 'Confirm', confirmBtnClass = 'btn-primary') {
    return showCustomDialog({ title, message, type: 'warning', confirmText, cancelText: 'Cancel', confirmBtnClass });
}

// Role Configuration & Navigation Schemes
const ROLE_CONFIGS = {
    ADMIN: {
        roleTitle: 'ADMINISTRATOR (SUPER USER)',
        portalTitle: 'Institution Dashboard',
        subcaption: 'Oxford International School • Campus Scale: 2,000 Capacity • Super User Access',
        defaultTab: 'admin-dashboard',
        menu: [
            { id: 'admin-dashboard', icon: 'fa-chart-pie', label: 'Executive Dashboard' },
            { id: 'admissions', icon: 'fa-user-plus', label: 'Admissions & Intake' },
            { id: 'students', icon: 'fa-users', label: 'Student Directory' },
            { id: 'employees', icon: 'fa-chalkboard-user', label: 'Faculty & Staff' },
            { id: 'fees', icon: 'fa-file-invoice-dollar', label: 'Fee Operations' },
            { id: 'leaves', icon: 'fa-calendar-check', label: 'Leave Approvals' },
            { id: 'transport', icon: 'fa-bus', label: 'Transportation & Routes' },
            { id: 'timetable', icon: 'fa-calendar-days', label: 'Master Timetable' },
            { id: 'activities', icon: 'fa-icons', label: 'Clubs & Events' },
            { id: 'exams', icon: 'fa-award', label: 'Exams & Gradebook' },
            { id: 'whatsapp', icon: 'fa-brands fa-whatsapp', label: 'WhatsApp Alerts' },
            { id: 'tickets', icon: 'fa-headset', label: 'Campus Helpdesk' }
        ]
    },
    TEACHER: {
        roleTitle: 'TEACHER & FACULTY PORTAL',
        portalTitle: 'Faculty Workspace',
        subcaption: 'Oxford International School • Classroom & Assigned Class Management',
        defaultTab: 'teacher-dashboard',
        menu: [
            { id: 'teacher-dashboard', icon: 'fa-chalkboard', label: 'Faculty Dashboard' },
            { id: 'teacher-students', icon: 'fa-chalkboard-user', label: 'Assigned Class' },
            { id: 'teacher-attendance', icon: 'fa-clipboard-user', label: "Today's Attendance" },
            { id: 'teacher-marks', icon: 'fa-pen-to-square', label: 'Gradebook & Assessment' },
            { id: 'teacher-timetable', icon: 'fa-calendar-week', label: 'My Teaching Schedule' },
            { id: 'activities', icon: 'fa-icons', label: 'Clubs & Activities' },
            { id: 'teacher-leave', icon: 'fa-plane-departure', label: 'Apply Faculty Leave' },
            { id: 'tickets', icon: 'fa-headset', label: 'Faculty Helpdesk' }
        ]
    },
    STUDENT: {
        roleTitle: 'STUDENT ACADEMIC PORTAL',
        portalTitle: 'Student Academic Portal',
        subcaption: 'Oxford International School • Self-Service Portal',
        defaultTab: 'student-dashboard',
        menu: [
            { id: 'student-dashboard', icon: 'fa-graduation-cap', label: 'My Academic Dashboard' },
            { id: 'student-report-card', icon: 'fa-award', label: 'My Report Card' },
            { id: 'student-fees', icon: 'fa-file-invoice-dollar', label: 'My Fee Invoices' },
            { id: 'student-timetable', icon: 'fa-calendar-days', label: 'Class 10A Timetable' },
            { id: 'student-transit', icon: 'fa-bus', label: 'My Bus Route & Stop' },
            { id: 'activities', icon: 'fa-icons', label: 'Campus Clubs' },
            { id: 'student-leave', icon: 'fa-envelope-open-text', label: 'Submit Absence Note' },
            { id: 'tickets', icon: 'fa-circle-question', label: 'Student Support' }
        ]
    }
};

// ==============================================================
// 1. APPLICATION LIFECYCLE & SESSION MANAGEMENT
// ==============================================================

document.addEventListener('DOMContentLoaded', () => {
    checkActiveSession();
});

function checkActiveSession() {
    const savedToken = localStorage.getItem('oxford_token');
    const savedUserJson = localStorage.getItem('oxford_user');

    if (savedToken && savedUserJson) {
        try {
            authToken = savedToken;
            currentUser = JSON.parse(savedUserJson);
            displayPortal(currentUser);
            return;
        } catch (e) {
            console.error("Session parse failed, requiring login", e);
        }
    }

    displayLoginScreen();
}

function displayLoginScreen() {
    document.getElementById('login-screen').style.display = 'flex';
    document.getElementById('app').style.display = 'none';
    const err = document.getElementById('login-error-msg');
    if (err) err.style.display = 'none';
}

function displayPortal(user) {
    document.getElementById('login-screen').style.display = 'none';
    document.getElementById('app').style.display = 'flex';

    const userType = user.userType || 'ADMIN';
    const config = ROLE_CONFIGS[userType] || ROLE_CONFIGS.ADMIN;

    // Update Header and Identity elements
    document.getElementById('role-display-text').textContent = config.roleTitle;
    document.getElementById('page-title').textContent = config.portalTitle;
    document.getElementById('page-subcaption').textContent = config.subcaption;

    document.getElementById('header-user-name').textContent = user.fullName || user.username;
    document.getElementById('header-user-role').textContent = userType === 'ADMIN' ? 'Super User' : (user.assignedClass || user.department || userType);
    document.getElementById('header-avatar').textContent = (user.fullName || user.username || 'U').substring(0, 2).toUpperCase();

    document.getElementById('sidebar-logged-user').textContent = user.fullName || user.username;
    document.getElementById('sidebar-logged-privilege').textContent = userType === 'ADMIN' ? 'Super User Privileges' : (userType === 'TEACHER' ? 'Faculty & Reportees' : 'Student Access');

    // If student, customize hero banner
    if (userType === 'STUDENT') {
        const heroName = document.getElementById('student-hero-name');
        const heroClass = document.getElementById('student-hero-class');
        const heroAvatar = document.getElementById('student-hero-avatar');
        if (heroName) heroName.textContent = `Welcome back, ${user.fullName}!`;
        if (heroClass) heroClass.innerHTML = `Class: <strong>${user.assignedClass || 'Class 10A'}</strong> &bull; Admission No: <strong>${user.admissionNo || 'OIS-2026-1001'}</strong>`;
        if (heroAvatar) heroAvatar.textContent = (user.fullName || 'AK').substring(0, 2).toUpperCase();
    }

    // Build role-segregated sidebar
    renderSidebarMenu(config.menu);

    // Navigate to role's default view
    navigateTo(config.defaultTab);
}

function renderSidebarMenu(menuItems) {
    const nav = document.getElementById('role-nav-menu');
    nav.innerHTML = menuItems.map(item => `
        <a href="#${item.id}" class="nav-item" data-tab="${item.id}" onclick="handleNavClick(event, '${item.id}')">
            <i class="fa-solid ${item.icon}"></i> ${item.label}
        </a>
    `).join('');
}

const TAB_TITLES = {
    'admin-dashboard': 'Executive Dashboard',
    'admissions': 'Admissions & Intake Management',
    'students': 'Master Student Directory',
    'employees': 'Faculty & Staff Directory',
    'fees': 'Institutional Fee Management',
    'leaves': 'Leave Management & Approvals',
    'transport': 'School Transportation & Fleet Routes',
    'timetable': 'Master Academic Timetable',
    'activities': 'Co-Curricular Clubs & Events',
    'exams': 'Examination Management & Gradebook',
    'whatsapp': 'Automated WhatsApp Alerts & Broadcasts',
    'tickets': 'Campus Support & Grievances Helpdesk',

    'teacher-dashboard': 'Faculty Workspace Dashboard',
    'teacher-attendance': "Today's Classroom Attendance Register",
    'teacher-students': 'Assigned Class',
    'teacher-marks': 'Gradebook & Assessment Entry',
    'teacher-timetable': 'My Teaching Schedule',
    'teacher-leave': 'Faculty Leave Applications',

    'student-dashboard': 'My Academic Dashboard',
    'student-report-card': 'My Official CBSE Report Card',
    'student-fees': 'My Fee Invoices & Payment Receipts',
    'student-timetable': 'My Classroom Period Schedule',
    'student-transit': 'My Bus Route & Assigned Stop',
    'student-leave': 'Submit Absence / Leave Request'
};

function handleNavClick(e, tabId) {
    e.preventDefault();
    navigateTo(tabId);
}

function navigateTo(tabId) {
    document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));
    document.querySelectorAll('.view-panel').forEach(el => el.classList.remove('active'));

    const navTarget = document.querySelector(`.nav-item[data-tab="${tabId}"]`);
    const panelTarget = document.getElementById(`view-${tabId}`);

    if (navTarget) navTarget.classList.add('active');
    if (panelTarget) {
        panelTarget.classList.add('active');
        const title = TAB_TITLES[tabId] || (navTarget ? navTarget.textContent.trim() : 'Dashboard');
        const titleEl = document.getElementById('page-title');
        if (titleEl) titleEl.textContent = title;
        window.scrollTo({ top: 0, behavior: 'smooth' });
        try {
            loadTabContent(tabId);
        } catch (err) {
            console.error(`Error loading tab content for ${tabId}:`, err);
        }
    } else {
        console.warn(`Panel view-${tabId} not found in DOM`);
    }
}

// ==============================================================
// 2. AUTHENTICATION & LOGIN / LOGOUT HANDLERS
// ==============================================================

async function handleUserLogin(event) {
    event.preventDefault();
    const usernameInput = document.getElementById('login-username').value.trim();
    const passwordInput = document.getElementById('login-password').value;
    const errorEl = document.getElementById('login-error-msg');
    const submitBtn = document.getElementById('btn-login-submit');

    submitBtn.disabled = true;
    submitBtn.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> Authenticating...`;
    errorEl.style.display = 'none';

    try {
        const res = await fetch(`${API_BASE}/api/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username: usernameInput, password: passwordInput })
        });
        const data = await res.json();

        if (data.success && data.data) {
            authToken = data.data.accessToken;
            currentUser = data.data;

            localStorage.setItem('oxford_token', authToken);
            localStorage.setItem('oxford_user', JSON.stringify(currentUser));

            displayPortal(currentUser);
        } else {
            errorEl.textContent = data.message || "Invalid credentials. Please verify username and password.";
            errorEl.style.display = 'flex';
        }
    } catch (e) {
        errorEl.textContent = "Unable to connect to Oxford CSMS authentication service.";
        errorEl.style.display = 'flex';
    } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = `<i class="fa-solid fa-right-to-bracket"></i> Sign In to Account`;
    }
}

function quickFill(user, pass, autoSubmit = true) {
    document.getElementById('login-username').value = user;
    document.getElementById('login-password').value = pass;
    if (autoSubmit) {
        const fakeEvent = { preventDefault: () => {} };
        handleUserLogin(fakeEvent);
    } else {
        document.getElementById('btn-login-submit').focus();
    }
}

async function handleLogout() {
    const confirmed = await appConfirm("Confirm Sign Out", "Are you sure you want to end your current Oxford CSMS session?", "Sign Out", "btn-outline");
    if (confirmed) {
        localStorage.removeItem('oxford_token');
        localStorage.removeItem('oxford_user');
        authToken = null;
        currentUser = null;
        displayLoginScreen();
        showToast("Signed Out", "You have been securely signed out of CSMS portal.", "info");
    }
}

function getAuthHeaders() {
    const headers = { 'Content-Type': 'application/json' };
    if (authToken) {
        headers['Authorization'] = `Bearer ${authToken}`;
    }
    return headers;
}

// ==============================================================
// 3. TAB CONTENT ROUTER
// ==============================================================

function loadTabContent(tabId) {
    switch (tabId) {
        // Admin
        case 'admin-dashboard': loadAdminDashboard(); break;
        case 'admissions': loadAdmissions(); break;
        case 'students': loadStudents(); break;
        case 'employees': loadEmployees(); break;
        case 'fees': loadFees(); break;
        case 'leaves': loadLeaves(); break;
        case 'transport': loadTransport(); break;
        case 'timetable': loadTimetable(); break;
        case 'activities': loadActivities(); break;
        case 'exams': loadExams(); break;
        case 'whatsapp': loadWhatsApp(); break;
        case 'tickets': loadTickets(); break;

        // Teacher
        case 'teacher-dashboard': loadTeacherDashboard(); break;
        case 'teacher-attendance': loadTeacherAttendance(); break;
        case 'teacher-students': loadTeacherStudents(); break;
        case 'teacher-marks': loadTeacherMarks(); break;
        case 'teacher-timetable': loadTeacherTimetable(); break;
        case 'teacher-leave': loadTeacherLeaves(); break;

        // Student
        case 'student-dashboard': loadStudentDashboard(); break;
        case 'student-report-card': loadStudentReportCard(); break;
        case 'student-fees': loadStudentFees(); break;
        case 'student-timetable': loadStudentTimetable(); break;
        case 'student-transit': loadStudentTransit(); break;
        case 'student-leave': loadStudentLeaves(); break;
    }
}

// ==============================================================
// 4. ADMIN MODULE IMPLEMENTATIONS (SUPER USER)
// ==============================================================

async function loadAdminDashboard() {
    try {
        const res = await fetch(`${API_BASE}/api/admin/dashboard/overview`, { headers: getAuthHeaders() });
        const resData = await res.json();
        if (resData.success && resData.data) {
            const d = resData.data;
            document.getElementById('kpi-students').textContent = d.activeStudents;
            document.getElementById('kpi-capacity-pct').textContent = d.capacityUtilizationPercentage;
            document.getElementById('kpi-collected').textContent = `₹ ${Number(d.totalCollected).toLocaleString()}`;
            document.getElementById('kpi-billed').textContent = `₹ ${Number(d.totalBilled).toLocaleString()}`;
            document.getElementById('kpi-staff').textContent = d.activeStaff;
            document.getElementById('kpi-escalated').textContent = d.escalatedTickets;
        }
    } catch (e) {
        console.error("Dashboard metrics error:", e);
    }
}

let appSearchTimeout = null;
function debounceAppSearch() {
    clearTimeout(appSearchTimeout);
    appSearchTimeout = setTimeout(() => {
        loadApplications();
    }, 300);
}

function clearApplicationFilters() {
    const search = document.getElementById('filter-app-search');
    const grade = document.getElementById('filter-app-grade');
    const status = document.getElementById('filter-app-status');
    const active = document.getElementById('filter-app-active');
    if (search) search.value = '';
    if (grade) grade.value = '';
    if (status) status.value = '';
    if (active) active.value = 'true';
    loadApplications();
}

async function loadAdmissions() {
    await Promise.all([loadApplications(), loadInquiries()]);
}

async function loadApplications() {
    try {
        const query = document.getElementById('filter-app-search')?.value.trim() || '';
        const grade = document.getElementById('filter-app-grade')?.value || '';
        const status = document.getElementById('filter-app-status')?.value || '';
        const active = document.getElementById('filter-app-active')?.value || '';

        const params = new URLSearchParams();
        if (query) params.append('query', query);
        if (grade) params.append('grade', grade);
        if (status) params.append('status', status);
        if (active !== '') params.append('active', active);

        const url = `${API_BASE}/api/admin/admissions/applications${params.toString() ? '?' + params.toString() : ''}`;
        const resApps = await fetch(url, { headers: getAuthHeaders() });
        const dataApps = await resApps.json();
        const tbodyApps = document.getElementById('applications-table-body');
        
        if (dataApps.data && dataApps.data.length > 0) {
            tbodyApps.innerHTML = dataApps.data.map(app => {
                const isDifferentClass = app.appliedGrade && app.appliedGrade !== app.targetGrade;
                const safeName = (app.studentName || '').replace(/'/g, "\\'");
                const appliedGrade = app.appliedGrade || app.targetGrade;
                const targetGrade = app.targetGrade || 'Class 10';
                const assignedSec = app.assignedSection || 'A';
                const isActive = app.active !== false;

                return `
                <tr style="${!isActive ? 'opacity: 0.65; background-color: #f8fafc;' : ''}">
                    <td><code>${app.applicationNumber}</code></td>
                    <td>
                        <strong>${escapeHtml(app.studentName)}</strong><br>
                        <small class="text-secondary">DOB: ${app.dateOfBirth} (${app.gender})</small>
                    </td>
                    <td>
                        <span class="badge badge-secondary">${escapeHtml(appliedGrade)}</span>
                    </td>
                    <td>
                        ${isDifferentClass ? `
                            <div style="display: flex; flex-direction: column; gap: 3px;">
                                <span class="badge badge-class-adjusted" title="Recommended to lower/different grade by management">
                                    <i class="fa-solid fa-graduation-cap"></i> ${escapeHtml(targetGrade)} - ${escapeHtml(assignedSec)}
                                </span>
                                <small class="text-secondary" style="font-size: 0.73rem; max-width: 170px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" title="${escapeHtml(app.classChangeReason || '')}">
                                    <i class="fa-solid fa-note-sticky"></i> ${escapeHtml(app.classChangeReason || 'Placement adjustment')}
                                </small>
                            </div>
                        ` : `
                            <span class="badge badge-primary">${escapeHtml(targetGrade)} - ${escapeHtml(assignedSec)}</span>
                        `}
                    </td>
                    <td>
                        ${escapeHtml(app.fatherName)}<br>
                        <small class="text-secondary"><i class="fa-solid fa-phone"></i> ${escapeHtml(app.contactPhone)}</small>
                    </td>
                    <td>
                        <span class="badge ${app.documentsVerified ? 'badge-success' : 'badge-warning'}">
                            ${app.documentsVerified ? '<i class="fa-solid fa-circle-check"></i> Verified' : '<i class="fa-solid fa-clock"></i> Pending'}
                        </span>
                    </td>
                    <td>
                        <span class="badge ${app.status === 'ENROLLED' ? 'badge-purple' : (app.status === 'INACTIVE' ? 'badge-inactive' : 'badge-primary')}">
                            ${app.status}
                        </span>
                    </td>
                    <td>
                        ${isActive ? 
                            '<span class="badge badge-active"><i class="fa-solid fa-circle-dot" style="font-size: 0.6rem;"></i> Active</span>' : 
                            '<span class="badge badge-inactive"><i class="fa-solid fa-ban"></i> Inactive</span>'
                        }
                    </td>
                    <td>
                        <div style="display: flex; gap: 6px; flex-wrap: wrap;">
                            <button class="btn btn-outline btn-sm" onclick="openApplicationHistoryModal(${app.id})" style="border-color: #2563eb; color: #2563eb;" title="Interaction history & audit comments">
                                <i class="fa-solid fa-clock-rotate-left"></i> History
                            </button>
                            ${isActive ? `
                                <button class="btn btn-outline btn-sm" onclick="openAdjustClassModal(${app.id}, '${safeName}', '${appliedGrade}', '${targetGrade}', '${assignedSec}')" style="border-color: #d97706; color: #d97706;" title="Adjust Class or Section with mandatory note">
                                    <i class="fa-solid fa-graduation-cap"></i> Adjust Class
                                </button>
                                ${app.status === 'ENROLLED' ? 
                                    `<span class="badge badge-success"><i class="fa-solid fa-check"></i> Enrolled (#${app.enrolledStudentId})</span>` :
                                    `<button class="btn btn-primary btn-sm" onclick="enrollCandidate(${app.id}, '${targetGrade}', '${assignedSec}')">
                                        <i class="fa-solid fa-user-check"></i> Enroll
                                     </button>`
                                }
                            ` : ''}
                            <button class="btn btn-outline btn-sm" onclick="toggleApplicationActive(${app.id}, ${isActive})" style="border-color: ${isActive ? '#ef4444' : '#10b981'}; color: ${isActive ? '#ef4444' : '#10b981'};" title="${isActive ? 'Mark Inactive' : 'Reactivate application'}">
                                <i class="fa-solid ${isActive ? 'fa-ban' : 'fa-rotate-left'}"></i> ${isActive ? 'Inactivate' : 'Reactivate'}
                            </button>
                        </div>
                    </td>
                </tr>
            `;}).join('');
        } else {
            tbodyApps.innerHTML = `<tr><td colspan="9" class="text-center text-secondary" style="padding: 24px;">No submitted applications match the selected filter criteria.</td></tr>`;
        }

        const btnApps = document.getElementById('btn-subtab-apps');
        if (btnApps) {
            const count = (dataApps.data || []).length;
            btnApps.innerHTML = `<i class="fa-solid fa-file-lines"></i> Submitted Applications <span class="badge badge-primary" style="margin-left: 6px;">${count}</span>`;
        }
    } catch (e) {
        console.error("Applications fetch error:", e);
    }
}

async function loadInquiries() {
    try {
        const resInq = await fetch(`${API_BASE}/api/admin/admissions/inquiries`, { headers: getAuthHeaders() });
        const dataInq = await resInq.json();
        const tbodyInq = document.getElementById('inquiries-table-body');
        if (dataInq.data && dataInq.data.length > 0) {
            tbodyInq.innerHTML = dataInq.data.map(inq => {
                const safeName = (inq.candidateName || '').replace(/'/g, "\\'");
                const safeParent = (inq.parentName || '').replace(/'/g, "\\'");
                return `
                <tr>
                    <td><strong>${escapeHtml(inq.candidateName)}</strong></td>
                    <td><span class="badge badge-primary">${escapeHtml(inq.targetGrade)}</span></td>
                    <td><strong>${escapeHtml(inq.parentName)}</strong><br><small class="text-secondary"><i class="fa-solid fa-phone"></i> ${escapeHtml(inq.contactPhone)}</small></td>
                    <td>${escapeHtml(inq.previousSchool || 'N/A')}</td>
                    <td><span class="badge ${inq.status === 'CONTACTED' ? 'badge-success' : 'badge-warning'}">${inq.status}</span></td>
                    <td><small>${escapeHtml(inq.notes || '-')}</small></td>
                    <td>
                        <div style="display: flex; gap: 6px; flex-wrap: wrap;">
                            <button class="btn btn-outline btn-sm" onclick="openInquiryHistoryModal(${inq.id})" style="border-color: #2563eb; color: #2563eb;">
                                <i class="fa-solid fa-clock-rotate-left"></i> History & Notes
                            </button>
                            ${inq.status !== 'CONTACTED' ? 
                                `<button class="btn btn-outline btn-sm" onclick="markInquiryContacted(${inq.id})">
                                    <i class="fa-solid fa-phone"></i> Contacted
                                 </button>` : 
                                `<span class="badge badge-success"><i class="fa-solid fa-check"></i> Contacted</span>`
                            }
                            <button class="btn btn-primary btn-sm" onclick="convertInquiryToApplication(${inq.id}, '${safeName}', '${inq.targetGrade}', '${safeParent}', '${inq.contactPhone}')">
                                <i class="fa-solid fa-file-signature"></i> Create Application
                            </button>
                        </div>
                    </td>
                </tr>
            `;}).join('');
        } else {
            tbodyInq.innerHTML = `<tr><td colspan="7" class="text-center text-secondary">No prospect inquiries recorded yet.</td></tr>`;
        }

        const btnInqs = document.getElementById('btn-subtab-inquiries');
        if (btnInqs) {
            const count = (dataInq.data || []).length;
            btnInqs.innerHTML = `<i class="fa-solid fa-clipboard-question"></i> Inquiry Records <span class="badge badge-warning" style="margin-left: 6px;">${count}</span>`;
        }
    } catch (e) {
        console.error("Inquiries fetch error:", e);
    }
}

async function enrollCandidate(applicationId, targetClass, assignedSection) {
    const sec = assignedSection || 'A';
    const confirmed = await appConfirm('Authorize Student Enrollment', `Authorize student enrollment into ${targetClass} (Section ${sec}) at Oxford International School?`, 'Enroll Student');
    if (!confirmed) return;
    try {
        const res = await fetch(`${API_BASE}/api/admin/admissions/applications/${applicationId}/enroll`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify({ targetClass, assignedSection: sec })
        });
        const data = await res.json();
        if (data.success) {
            showToast('Student Enrolled', `Assigned Admission No: ${data.data.admissionNo} (${data.data.currentClass} ${data.data.section})`, 'success');
            await loadAdmissions();
        } else {
            showToast('Enrollment Failed', data.message, 'error');
        }
    } catch (e) {
        showToast('Error', e.message, 'error');
    }
}

async function toggleApplicationActive(applicationId, currentlyActive) {
    const actionLabel = currentlyActive ? 'Mark Inactive' : 'Reactivate';
    const message = currentlyActive ? 
        'Are you sure you want to mark this application as INACTIVE? The student application will be archived from the active enrollment register.' :
        'Reactivate this student application back to the active admissions queue?';
    
    const confirmed = await appConfirm(`${actionLabel} Application`, message, actionLabel);
    if (!confirmed) return;

    try {
        const res = await fetch(`${API_BASE}/api/admin/admissions/applications/${applicationId}/active`, {
            method: 'PATCH',
            headers: getAuthHeaders(),
            body: JSON.stringify({
                active: !currentlyActive,
                reason: currentlyActive ? 'Marked inactive by administrator' : 'Reactivated to active pipeline by administrator',
                authorName: currentUser?.fullName || 'Administrator'
            })
        });
        const d = await res.json();
        if (d.success) {
            showToast('Status Updated', currentlyActive ? 'Application marked inactive.' : 'Application reactivated successfully.', 'success');
            await loadApplications();
        } else {
            showToast('Action Failed', d.message, 'error');
        }
    } catch (e) {
        showToast('Error', e.message, 'error');
    }
}

async function loadStudents() {
    const grade = document.getElementById('filter-student-class')?.value;
    let url = `${API_BASE}/api/admin/students`;
    if (grade) url += `?grade=${encodeURIComponent(grade)}`;

    try {
        const res = await fetch(url, { headers: getAuthHeaders() });
        const d = await res.json();
        const tbody = document.getElementById('students-table-body');
        if (d.data && d.data.length > 0) {
            tbody.innerHTML = d.data.map(s => `
                <tr>
                    <td><strong>${s.admissionNo}</strong></td>
                    <td><strong>${s.firstName} ${s.lastName}</strong><br><small class="text-secondary">${s.gender}</small></td>
                    <td><span class="badge badge-primary">${s.currentClass} - ${s.section}</span></td>
                    <td>#${s.rollNo || '-'}</td>
                    <td>${s.guardianPhone}</td>
                    <td><strong>${s.attendanceRate || 95}%</strong></td>
                    <td><span class="badge ${s.feeStatus === 'CLEARED' ? 'badge-success' : 'badge-danger'}">${s.feeStatus}</span></td>
                    <td>
                        <button class="btn btn-outline btn-sm" onclick="generateSampleReportCard(${s.id})">
                            <i class="fa-solid fa-file-pdf"></i> Report Card
                        </button>
                    </td>
                </tr>
            `).join('');

            const invSelect = document.getElementById('inv-student-id');
            if (invSelect) {
                invSelect.innerHTML = d.data.map(s => `
                    <option value="${s.id}">${s.fullName} (${s.admissionNo}) - ${s.currentClass}</option>
                `).join('');
            }
        }
    } catch (e) {
        console.error("Student load error:", e);
    }
}

async function loadEmployees() {
    try {
        const res = await fetch(`${API_BASE}/api/admin/employees`, { headers: getAuthHeaders() });
        const d = await res.json();
        const tbody = document.getElementById('employees-table-body');
        if (d.data && d.data.length > 0) {
            tbody.innerHTML = d.data.map(e => `
                <tr>
                    <td><code>${e.employeeCode}</code></td>
                    <td><strong>${e.fullName}</strong></td>
                    <td>${e.department}</td>
                    <td>${e.designation}</td>
                    <td>${e.qualification || '-'}</td>
                    <td>₹ ${(Number(e.basicSalary) + Number(e.allowances)).toLocaleString()}</td>
                    <td><span class="badge badge-success">ACTIVE</span></td>
                </tr>
            `).join('');
        }

        const resT = await fetch(`${API_BASE}/api/admin/employees/teaching`, { headers: getAuthHeaders() });
        const dT = await resT.json();
        document.getElementById('teaching-table-body').innerHTML = (dT.data || []).map(t => `
            <tr>
                <td><code>${t.employeeCode}</code></td>
                <td><strong>${t.fullName}</strong></td>
                <td><span class="badge badge-primary">${t.subjectSpecialization}</span></td>
                <td>${t.assignedClass || 'General Faculty'}</td>
                <td>${t.weeklyTargetPeriods} periods/week</td>
                <td><small>${t.email}</small></td>
            </tr>
        `).join('');

        const resNT = await fetch(`${API_BASE}/api/admin/employees/non-teaching`, { headers: getAuthHeaders() });
        const dNT = await resNT.json();
        document.getElementById('non-teaching-table-body').innerHTML = (dNT.data || []).map(nt => `
            <tr>
                <td><code>${nt.employeeCode}</code></td>
                <td><strong>${nt.fullName}</strong></td>
                <td><span class="badge badge-purple">${nt.operationalDomain}</span></td>
                <td>${nt.shiftTiming || 'General Shift'}</td>
                <td>${nt.phone}</td>
            </tr>
        `).join('');
    } catch (e) {
        console.error("Employee load error:", e);
    }
}

async function loadFees() {
    try {
        const resSummary = await fetch(`${API_BASE}/api/finance/fees/summary`, { headers: getAuthHeaders() });
        const dSummary = await resSummary.json();
        if (dSummary.data) {
            const s = dSummary.data;
            document.getElementById('fee-bar-billed').textContent = `₹ ${Number(s.totalBilled).toLocaleString()}`;
            document.getElementById('fee-bar-collected').textContent = `₹ ${Number(s.totalCollected).toLocaleString()}`;
            document.getElementById('fee-bar-pending').textContent = `₹ ${Number(s.totalPending).toLocaleString()}`;
            document.getElementById('fee-bar-rate').textContent = `${s.collectionPercentage}%`;
        }

        const resInv = await fetch(`${API_BASE}/api/finance/fees/invoices`, { headers: getAuthHeaders() });
        const dInv = await resInv.json();
        const tbody = document.getElementById('invoices-table-body');
        if (dInv.data && dInv.data.length > 0) {
            tbody.innerHTML = dInv.data.map(inv => `
                <tr>
                    <td><code>${inv.invoiceNumber}</code></td>
                    <td><strong>${inv.studentName}</strong> (${inv.admissionNo})</td>
                    <td>${inv.billingPeriod}</td>
                    <td>₹ ${Number(inv.grossAmount).toLocaleString()}</td>
                    <td>
                        ${inv.discountAmount > 0 ? 
                            `<span class="badge badge-success">- ₹ ${Number(inv.discountAmount).toLocaleString()}</span><br><small class="text-secondary">${inv.scholarshipApplied}</small>` : 
                            `<span class="text-secondary">Standard</span>`}
                    </td>
                    <td><strong>₹ ${Number(inv.netPayable).toLocaleString()}</strong></td>
                    <td>₹ ${Number(inv.paidAmount).toLocaleString()}</td>
                    <td>
                        <span class="badge ${inv.status === 'PAID' ? 'badge-success' : inv.status === 'PARTIALLY_PAID' ? 'badge-warning' : 'badge-danger'}">
                            ${inv.status}
                        </span>
                    </td>
                    <td>
                        ${inv.status !== 'PAID' ? `
                            <button class="btn btn-primary btn-sm" onclick="openPaymentModal(${inv.id}, '${inv.invoiceNumber}', ${inv.netPayable - inv.paidAmount})">
                                Collect
                            </button>
                        ` : `<span class="text-success"><i class="fa-solid fa-check"></i> Cleared</span>`}
                    </td>
                </tr>
            `).join('');
        }
    } catch (e) {
        console.error("Fee load error:", e);
    }
}

async function handleGenerateInvoice(event) {
    event.preventDefault();
    const studentId = document.getElementById('inv-student-id').value;
    const billingPeriod = document.getElementById('inv-billing-period').value;
    const scholarshipType = document.getElementById('inv-scholarship-type').value;

    const res = await fetch(`${API_BASE}/api/finance/fees/invoices/generate`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({ studentId, billingPeriod, scholarshipType })
    });
    const d = await res.json();
    if (d.success) {
        closeModal('modal-generate-invoice');
        loadFees();
        showToast("Invoice Issued", `Invoice #${d.data.invoiceNumber} issued. Net Payable: ₹${d.data.netPayable}`, "success");
    }
}

async function bulkGenerateClass10() {
    const confirmed = await appConfirm("Generate Bulk Invoices", "Generate Term 1 fee invoices for all Class 10 students?", "Generate Invoices");
    if (!confirmed) return;
    try {
        const res = await fetch(`${API_BASE}/api/finance/fees/invoices/generate-bulk`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify({ grade: 'Class 10', billingPeriod: 'Term 1 - 2026' })
        });
        const d = await res.json();
        showToast("Invoices Generated", `Term invoices issued for ${d.data.invoicesGenerated} students.`, "success");
        loadFees();
    } catch (e) {
        showToast("Generation Error", e.message, "error");
    }
}

function openPaymentModal(invoiceId, invoiceNumber, balance) {
    document.getElementById('pay-invoice-id').value = invoiceId;
    document.getElementById('pay-invoice-num').value = invoiceNumber;
    document.getElementById('pay-amount').value = balance;
    openModal('modal-pay-invoice');
}

async function handlePayInvoice(event) {
    event.preventDefault();
    const invoiceId = document.getElementById('pay-invoice-id').value;
    const amount = document.getElementById('pay-amount').value;
    const paymentMethod = document.getElementById('pay-method').value;
    const transactionReference = document.getElementById('pay-ref').value;

    const res = await fetch(`${API_BASE}/api/finance/fees/invoices/${invoiceId}/pay`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({
            amount,
            paymentMethod,
            transactionReference,
            recordedBy: 'Accounts Counter',
            remarks: 'Oxford International School Fee Clearance'
        })
    });
    const d = await res.json();
    if (d.success) {
        closeModal('modal-pay-invoice');
        loadFees();
        showToast("Payment Confirmed", `Receipt #${d.data.receiptNumber} recorded for ₹${d.data.amountPaid}`, "success");
    }
}

async function loadLeaves() {
    try {
        const res = await fetch(`${API_BASE}/api/finance/leaves`, { headers: getAuthHeaders() });
        const d = await res.json();
        const tbody = document.getElementById('leaves-table-body');
        if (d.data && d.data.length > 0) {
            tbody.innerHTML = d.data.map(l => `
                <tr>
                    <td><strong>${l.applicantName}</strong></td>
                    <td>${l.applicantType}</td>
                    <td>${l.leaveType}</td>
                    <td>${l.startDate}</td>
                    <td>${l.endDate}</td>
                    <td>${l.reason}</td>
                    <td>
                        <span class="badge ${l.status === 'APPROVED' ? 'badge-success' : l.status === 'REJECTED' ? 'badge-danger' : 'badge-warning'}">
                            ${l.status}
                        </span>
                    </td>
                    <td>
                        ${l.status === 'PENDING' ? `
                            <button class="btn btn-primary btn-sm" onclick="decideLeave(${l.id}, 'approve')">Approve</button>
                            <button class="btn btn-outline btn-sm" onclick="decideLeave(${l.id}, 'reject')">Decline</button>
                        ` : `<small class="text-secondary">${l.reviewedBy || 'Authorized'}</small>`}
                    </td>
                </tr>
            `).join('');
        }
    } catch (e) {
        console.error("Leave load error:", e);
    }
}

async function decideLeave(id, action) {
    const endpoint = `${API_BASE}/api/finance/leaves/${id}/${action}`;
    const res = await fetch(endpoint, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({ reviewerName: 'Management (Anil Kumar Gupt)', remarks: 'Reviewed and authorized' })
    });
    const d = await res.json();
    if (d.success) {
        loadLeaves();
    }
}

async function loadTransport() {
    try {
        const res = await fetch(`${API_BASE}/api/finance/transportation/routes`, { headers: getAuthHeaders() });
        const d = await res.json();
        const container = document.getElementById('routes-container');
        if (d.data && d.data.length > 0) {
            container.innerHTML = d.data.map(r => `
                <div class="route-card">
                    <div style="display: flex; justify-content: space-between; align-items: flex-start;">
                        <h3><i class="fa-solid fa-bus"></i> ${r.routeName}</h3>
                        <span class="badge badge-success">₹ ${r.monthlyFare}/month</span>
                    </div>
                    <p style="font-size: 0.84rem; color: var(--text-secondary); margin-bottom: 12px;">
                        <strong>Origin:</strong> ${r.startPoint} &bull; <strong>Destination:</strong> ${r.endPoint}
                    </p>
                    <div class="stop-timeline">
                        ${(r.stops || []).map(s => `
                            <div class="stop-point">
                                <strong>${s.stopName}</strong><br>
                                <small class="text-secondary">Pickup: ${s.morningPickupTime} | Drop: ${s.eveningDropTime}</small>
                            </div>
                        `).join('')}
                    </div>
                </div>
            `).join('');
        }
    } catch (e) {
        console.error("Transport load error:", e);
    }
}

async function loadTimetable() {
    const grade = document.getElementById('timetable-grade')?.value || 'Class 10';
    const section = document.getElementById('timetable-section')?.value || 'A';
    try {
        const res = await fetch(`${API_BASE}/api/academic/timetable/class?grade=${encodeURIComponent(grade)}&section=${encodeURIComponent(section)}`, {
            headers: getAuthHeaders()
        });
        const d = await res.json();
        renderTimetableGrid('timetable-container', d.data || []);
    } catch (e) {
        console.error("Timetable load error:", e);
    }
}

function renderTimetableGrid(containerId, slots) {
    const days = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"];
    const container = document.getElementById(containerId);
    if (!container) return;

    if (!slots || slots.length === 0) {
        container.innerHTML = `<div style="text-align: center; padding: 40px; color: var(--text-secondary);">No periods scheduled yet.</div>`;
        return;
    }

    let html = `
        <table class="timetable-table">
            <thead>
                <tr>
                    <th>Day</th>
                    <th>Period 1<br><small>08:30-09:15</small></th>
                    <th>Period 2<br><small>09:15-10:00</small></th>
                    <th>Period 3<br><small>10:15-11:00</small></th>
                    <th>Period 4<br><small>11:00-11:45</small></th>
                    <th>Period 5<br><small>12:15-01:00</small></th>
                    <th>Period 6<br><small>01:00-01:45</small></th>
                    <th>Period 7<br><small>01:45-02:30</small></th>
                </tr>
            </thead>
            <tbody>
    `;

    days.forEach(day => {
        html += `<tr><td><strong>${day}</strong></td>`;
        for (let p = 1; p <= 7; p++) {
            const slot = slots.find(s => s.dayOfWeek === day && s.periodNumber === p);
            if (slot) {
                html += `
                    <td>
                        <div class="slot-box">
                            <strong>${slot.subject}</strong>
                            <small>${slot.teacherName || 'Faculty'}</small>
                            <small class="badge badge-primary">${slot.roomNumber || 'Room 101'}</small>
                        </div>
                    </td>
                `;
            } else {
                html += `<td><span class="text-secondary">-</span></td>`;
            }
        }
        html += `</tr>`;
    });

    html += `</tbody></table>`;
    container.innerHTML = html;
}

async function triggerAutoTimetable() {
    const grade = document.getElementById('timetable-grade').value;
    const section = document.getElementById('timetable-section').value;
    const res = await fetch(`${API_BASE}/api/academic/timetable/auto-generate`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({ grade, section, roomNumber: `Room 101` })
    });
    const d = await res.json();
    if (d.success) {
        showToast("Timetable Generated", `Academic schedule for ${grade} ${section} successfully mapped without teacher conflicts.`, "success");
        loadTimetable();
    }
}

async function loadActivities() {
    try {
        const resClubs = await fetch(`${API_BASE}/api/academic/activities/clubs`, { headers: getAuthHeaders() });
        const dClubs = await resClubs.json();
        const clubsContainer = document.getElementById('clubs-container');
        if (clubsContainer) {
            clubsContainer.innerHTML = (dClubs.data || []).map(c => `
                <div class="club-card">
                    <span class="badge badge-purple">${c.category}</span>
                    <h4 style="margin: 8px 0; color: var(--primary); font-size: 1.1rem;">${c.clubName}</h4>
                    <p style="font-size: 0.84rem; color: var(--text-secondary);">${c.description}</p>
                    <div style="font-size: 0.78rem; margin-top: 10px;">
                        <strong>Staff Lead:</strong> ${c.inchargeTeacherName}<br>
                        <strong>Meeting Schedule:</strong> ${c.meetingSchedule}
                    </div>
                </div>
            `).join('');
        }

        const resEvents = await fetch(`${API_BASE}/api/academic/activities/events`, { headers: getAuthHeaders() });
        const dEvents = await resEvents.json();
        const eventsContainer = document.getElementById('events-container');
        if (eventsContainer) {
            eventsContainer.innerHTML = (dEvents.data || []).map(e => `
                <div class="event-item">
                    <div class="event-date-box">${e.eventDate}</div>
                    <div>
                        <h4 style="margin: 0; color: var(--primary-dark); font-size: 1.05rem;">${e.title}</h4>
                        <p style="margin: 4px 0; font-size: 0.84rem; color: var(--text-secondary);">${e.description}</p>
                        <small>Location: <strong>${e.venue}</strong> &bull; Audience: ${e.targetAudience}</small>
                    </div>
                </div>
            `).join('');
        }
    } catch (e) {
        console.error("Activities error:", e);
    }
}

async function loadExams() {
    try {
        const res = await fetch(`${API_BASE}/api/academic/exams/terms/1/grades/class?grade=Class%2010&section=A`, {
            headers: getAuthHeaders()
        });
        const d = await res.json();
        const tbody = document.getElementById('exam-grades-table-body');
        if (d.data && d.data.length > 0) {
            tbody.innerHTML = d.data.map(g => `
                <tr>
                    <td>Term 1 Mid-Term Examination</td>
                    <td><strong>${g.studentName}</strong> (${g.admissionNo})</td>
                    <td>${g.subjectName}</td>
                    <td><strong>${g.marksObtained}</strong></td>
                    <td>${g.maxMarks}</td>
                    <td><span class="badge badge-success">${g.letterGrade}</span></td>
                    <td>
                        <button class="btn btn-outline btn-sm" onclick="generateSampleReportCard(${g.studentId})">
                            <i class="fa-solid fa-file-pdf"></i> View Card
                        </button>
                    </td>
                </tr>
            `).join('');
        }
    } catch (e) {
        console.error("Exams error:", e);
    }
}

async function generateSampleReportCard(studentId) {
    try {
        const targetId = studentId || (currentUser?.studentId) || 1;
        const res = await fetch(`${API_BASE}/api/academic/exams/terms/1/students/${targetId}/report-card`, {
            headers: getAuthHeaders()
        });
        const d = await res.json();
        if (d.success && d.data) {
            const rc = d.data;
            const container = document.getElementById('printable-report-card');
            container.innerHTML = `
                <div class="rc-header">
                    <h2>${rc.institutionName}</h2>
                    <p>${rc.institutionLocation} &bull; CBSE Curriculum</p>
                    <p style="font-weight: 700; margin-top: 6px; color: #1e3a8a;">OFFICIAL ACADEMIC PROGRESS REPORT &bull; ${rc.termName}</p>
                </div>
                <div class="rc-meta">
                    <div><strong>Student Full Name:</strong> ${rc.studentName}</div>
                    <div><strong>Admission Number:</strong> ${rc.admissionNo}</div>
                    <div><strong>Class & Section:</strong> ${rc.grade} - ${rc.section}</div>
                    <div><strong>Roll Number:</strong> ${rc.rollNo}</div>
                    <div><strong>Academic Session:</strong> ${rc.academicYear}</div>
                    <div><strong>Term Attendance:</strong> ${rc.attendancePercentage}%</div>
                </div>
                <table class="data-table" style="margin-bottom: 20px;">
                    <thead>
                        <tr>
                            <th>Subject</th>
                            <th>Maximum Marks</th>
                            <th>Marks Scored</th>
                            <th>Grade Letter</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${rc.subjectGrades.map(sg => `
                            <tr>
                                <td><strong>${sg.subjectName}</strong></td>
                                <td>${sg.maxMarks}</td>
                                <td>${sg.marksObtained}</td>
                                <td><span class="badge badge-primary">${sg.letterGrade}</span></td>
                            </tr>
                        `).join('')}
                        <tr style="background: #f1f5f9; font-weight: bold;">
                            <td>TOTAL AGGREGATE</td>
                            <td>${rc.totalMaxMarks}</td>
                            <td>${rc.totalMarksObtained}</td>
                            <td>${rc.overallGrade}</td>
                        </tr>
                    </tbody>
                </table>
                <div style="background: #eff6ff; padding: 14px; border-radius: 6px; font-size: 0.88rem; margin-bottom: 24px;">
                    <strong>Aggregate Percentage:</strong> ${rc.percentage}% &bull; 
                    <strong>Evaluation:</strong> <span class="badge badge-success">${rc.overallResult}</span><br>
                    <strong>Faculty Assessment:</strong> <em>"${rc.remarks}"</em>
                </div>
                <div class="rc-signatures">
                    <div class="sign-block">
                        <div class="sign-line"></div>
                        <span>Class Teacher</span>
                    </div>
                    <div class="sign-block">
                        <div class="sign-line"></div>
                        <span>Authorized Management<br><strong>${rc.authorizedBy}</strong></span>
                    </div>
                    <div class="sign-block">
                        <div class="sign-line"></div>
                        <span>Principal's Seal</span>
                    </div>
                </div>
            `;
            openModal('modal-report-card');
        }
    } catch (e) {
        showToast("Report Card Error", e.message, "error");
    }
}

async function loadWhatsApp() {
    try {
        const res = await fetch(`${API_BASE}/api/communication/whatsapp/logs`, { headers: getAuthHeaders() });
        const d = await res.json();
        const tbody = document.getElementById('whatsapp-table-body');
        if (d.data && d.data.length > 0) {
            tbody.innerHTML = d.data.map(m => `
                <tr>
                    <td><small>${m.createdAt || 'Recent'}</small></td>
                    <td><strong>${m.recipientPhone}</strong></td>
                    <td>${m.recipientName}</td>
                    <td><span class="badge badge-purple">${m.templateName}</span></td>
                    <td><small>${m.messageBody}</small></td>
                    <td><span class="badge badge-success"><i class="fa-solid fa-check-double"></i> ${m.status}</span></td>
                    <td><code>${m.providerMessageId || 'QUEUED'}</code></td>
                </tr>
            `).join('');
        }
    } catch (e) {
        console.error("WhatsApp error:", e);
    }
}

async function triggerEmergencyBroadcastPrompt() {
    const text = "Heavy rain advisory: Oxford International School will remain closed tomorrow as per district administration guidelines.";
    const confirmed = await appConfirm("Broadcast Emergency Alert", `Dispatch WhatsApp notification to all registered parent numbers:\n\n"${text}"`, "Broadcast WhatsApp Alert", "btn-primary");
    if (!confirmed) return;
    try {
        const res = await fetch(`${API_BASE}/api/communication/whatsapp/trigger/emergency-broadcast`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify({ message: text })
        });
        const d = await res.json();
        showToast("Broadcast Dispatched", `Emergency WhatsApp alert successfully delivered to ${d.data.recipients} parents.`, "success");
        loadWhatsApp();
    } catch (e) {
        showToast("Broadcast Failed", e.message, "error");
    }
}

async function triggerTestFeeAlert() {
    try {
        const res = await fetch(`${API_BASE}/api/communication/whatsapp/trigger/fee-due/2`, {
            method: 'POST',
            headers: getAuthHeaders()
        });
        const d = await res.json();
        showToast("Fee Alert Dispatched", "WhatsApp Fee Reminder notification sent to parent.", "info");
        loadWhatsApp();
    } catch (e) {
        showToast("Alert Error", e.message, "error");
    }
}

async function loadTickets() {
    try {
        const res = await fetch(`${API_BASE}/api/communication/tickets`, { headers: getAuthHeaders() });
        const d = await res.json();
        const tbody = document.getElementById('tickets-table-body');
        if (d.data && d.data.length > 0) {
            tbody.innerHTML = d.data.map(t => `
                <tr>
                    <td><code>${t.ticketNumber}</code></td>
                    <td><strong>${t.title}</strong><br><small class="text-secondary">${t.reportedBy}</small></td>
                    <td>${t.category}</td>
                    <td><span class="badge ${t.priority === 'CRITICAL' ? 'badge-danger' : 'badge-warning'}">${t.priority} (${t.slaHours}h)</span></td>
                    <td><span class="badge ${t.status === 'RESOLVED' ? 'badge-success' : 'badge-danger'}">${t.status}</span></td>
                    <td>
                        ${t.escalated ? `<strong style="color: #b91c1c;">${t.escalatedTo}</strong>` : `<span class="text-secondary">Standard Queue</span>`}
                    </td>
                    <td>
                        ${!t.escalated && t.status !== 'RESOLVED' ? `
                            <button class="btn btn-outline btn-sm" onclick="escalateTicketToManagement(${t.id})">Escalate</button>
                        ` : `<button class="btn btn-primary btn-sm" onclick="resolveTicket(${t.id})">Resolve</button>`}
                    </td>
                </tr>
            `).join('');
        }
    } catch (e) {
        console.error("Tickets error:", e);
    }
}

async function escalateTicketToManagement(ticketId) {
    const confirmed = await appConfirm("Escalate Campus Helpdesk Ticket", "Escalate ticket to Higher Management (Anil Kumar Gupt)?", "Escalate to Management", "btn-warning");
    if (!confirmed) return;
    const res = await fetch(`${API_BASE}/api/communication/tickets/${ticketId}/escalate`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({ reason: 'Direct administrative intervention required' })
    });
    const d = await res.json();
    if (d.success) {
        showToast("Ticket Escalated", "Ticket escalated to Higher Management (Anil Kumar Gupt).", "warning");
        loadTickets();
    }
}

async function resolveTicket(ticketId) {
    const res = await fetch(`${API_BASE}/api/communication/tickets/${ticketId}/status`, {
        method: 'PATCH',
        headers: getAuthHeaders(),
        body: JSON.stringify({ status: 'RESOLVED', notes: 'Service verified on campus.' })
    });
    const d = await res.json();
    if (d.success) {
        showToast("Ticket Resolved", "Helpdesk ticket closed successfully.", "success");
        loadTickets();
    }
}

async function triggerAutoSlaCheck() {
    const res = await fetch(`${API_BASE}/api/communication/tickets/auto-escalation-check`, {
        method: 'POST',
        headers: getAuthHeaders()
    });
    const d = await res.json();
    showToast("SLA Inspection Complete", `Escalated tickets: ${d.data.escalatedCount}`, "info");
    loadTickets();
}

// ==============================================================
// 5. TEACHER PORTAL & REPORTEES IMPLEMENTATION
// ==============================================================

function getTeacherClassDetails() {
    let assigned = currentUser?.assignedClass || 'Class 10A';
    let grade = 'Class 10';
    let section = 'A';
    const m = assigned.match(/^(.*?)(?:\s*[-\s]?\s*)([A-Z])$/i);
    if (m && m[1] && m[2]) {
        grade = m[1].trim();
        section = m[2].trim().toUpperCase();
    } else if (assigned) {
        grade = assigned.trim();
    }
    return { assigned, grade, section };
}

let currentAttendanceStudents = [];

async function loadTeacherDashboard() {
    try {
        const { assigned, grade, section } = getTeacherClassDetails();
        
        const classBadgeEl = document.getElementById('teacher-dash-class');
        if (classBadgeEl) classBadgeEl.textContent = assigned || `${grade} - ${section}`;

        const dashClassBadgeEl = document.getElementById('dash-assigned-class-badge');
        if (dashClassBadgeEl) dashClassBadgeEl.textContent = assigned || `${grade} - ${section}`;

        const nameEl = document.getElementById('teacher-dash-name');
        if (nameEl) nameEl.textContent = currentUser?.fullName || 'Dr. Satish Sharma';

        const todayStr = new Date().toISOString().split('T')[0];

        // Fetch students of this assigned class
        const res = await fetch(`${API_BASE}/api/admin/students?grade=${encodeURIComponent(grade)}`, { headers: getAuthHeaders() });
        const d = await res.json();
        const students = (d.data || []).filter(s => !section || !s.section || s.section.toUpperCase() === section.toUpperCase());
        const strength = students.length > 0 ? students.length : (d.data ? d.data.length : 10);
        
        const strengthEl = document.getElementById('teacher-class-strength');
        if (strengthEl) strengthEl.textContent = strength;

        // Sort students by rollNo
        students.sort((a, b) => {
            const numA = parseInt(a.rollNo, 10);
            const numB = parseInt(b.rollNo, 10);
            if (isNaN(numA) || isNaN(numB)) return (a.fullName || '').localeCompare(b.fullName || '');
            return numA - numB;
        });

        currentAssignedClassStudents = students;

        // Load assigned work items, directives & notes for this faculty member
        await loadTeacherWorkItems();

        // Fetch today's attendance summary
        try {
            const sumRes = await fetch(`${API_BASE}/api/academic/attendance/summary?grade=${encodeURIComponent(grade)}&section=${encodeURIComponent(section)}&date=${todayStr}`, { headers: getAuthHeaders() });
            const sumData = await sumRes.json();
            const sum = sumData.data;
            const rateEl = document.getElementById('teacher-today-att-rate');
            const summaryFooterEl = document.getElementById('teacher-today-att-summary');
            
            if (sum && (sum.totalStudents > 0 || sum.totalRecorded > 0)) {
                if (rateEl) rateEl.textContent = `${sum.attendancePercentage}%`;
                if (summaryFooterEl) {
                    summaryFooterEl.innerHTML = `<strong>${sum.presentCount} Present</strong> &bull; ${sum.absentCount} Absent &bull; ${sum.lateCount} Late`;
                }
            } else {
                if (rateEl) rateEl.textContent = 'Not Taken';
                if (summaryFooterEl) {
                    summaryFooterEl.innerHTML = `Total: <strong>${strength}</strong> students &bull; <span style="color:#2563eb; font-weight:600;">Take Attendance &rarr;</span>`;
                }
            }
        } catch (sumErr) {
            console.error("Error loading attendance summary:", sumErr);
        }
    } catch (e) {
        console.error("Error in loadTeacherDashboard:", e);
    }
}

async function loadTeacherAttendance() {
    const dateInput = document.getElementById('att-date-picker');
    if (dateInput && !dateInput.value) {
        dateInput.value = new Date().toISOString().split('T')[0];
    }
    const selectedDate = dateInput ? dateInput.value : new Date().toISOString().split('T')[0];

    const { assigned, grade, section } = getTeacherClassDetails();

    const badgeEl = document.getElementById('att-class-badge');
    if (badgeEl) badgeEl.innerHTML = `<i class="fa-solid fa-chalkboard"></i> ${grade} - Section ${section}`;

    const dateDisplayEl = document.getElementById('att-display-date');
    if (dateDisplayEl) {
        const todayIso = new Date().toISOString().split('T')[0];
        if (selectedDate === todayIso) {
            dateDisplayEl.textContent = `Today (${selectedDate})`;
        } else {
            dateDisplayEl.textContent = selectedDate;
        }
    }

    const tbody = document.getElementById('attendance-table-body');
    if (tbody) tbody.innerHTML = `<tr><td colspan="4" class="text-center" style="padding: 28px; color: #64748b;"><i class="fa-solid fa-spinner fa-spin"></i> Fetching student register and attendance logs...</td></tr>`;

    try {
        // 1. Fetch Students
        const stuRes = await fetch(`${API_BASE}/api/admin/students?grade=${encodeURIComponent(grade)}`, { headers: getAuthHeaders() });
        const stuData = await stuRes.json();
        let students = (stuData.data || []).filter(s => !section || !s.section || s.section.toUpperCase() === section.toUpperCase());
        if (students.length === 0 && stuData.data && stuData.data.length > 0) {
            students = stuData.data;
        }

        // 2. Fetch Existing Attendance for this date
        let existingRecordsMap = {};
        try {
            const attRes = await fetch(`${API_BASE}/api/academic/attendance/class?grade=${encodeURIComponent(grade)}&section=${encodeURIComponent(section)}&date=${selectedDate}`, { headers: getAuthHeaders() });
            const attData = await attRes.json();
            if (attData.data && Array.isArray(attData.data)) {
                attData.data.forEach(r => {
                    existingRecordsMap[r.studentId] = r;
                });
            }
        } catch (attErr) {
            console.error("Error fetching class attendance:", attErr);
        }

        // 3. Populate currentAttendanceStudents state
        currentAttendanceStudents = students.map(s => {
            const existing = existingRecordsMap[s.id];
            const status = existing ? existing.status : 'PRESENT';
            // Remarks apply only if student is not present
            const remarks = (status !== 'PRESENT' && existing) ? (existing.remarks || '') : '';
            return {
                studentId: s.id,
                rollNo: s.rollNo || '-',
                admissionNo: s.admissionNo,
                studentName: s.fullName,
                guardianPhone: s.guardianPhone || '-',
                cumulativeRate: s.attendanceRate != null ? s.attendanceRate : 95.0,
                status: status,
                remarks: remarks
            };
        });

        // Sort by rollNo
        currentAttendanceStudents.sort((a, b) => {
            const numA = parseInt(a.rollNo, 10);
            const numB = parseInt(b.rollNo, 10);
            if (isNaN(numA) || isNaN(numB)) return a.studentName.localeCompare(b.studentName);
            return numA - numB;
        });

        renderAttendanceTableRows();
        updateAttendanceMetricsDisplay();

    } catch (err) {
        console.error("Error loading teacher attendance:", err);
        if (tbody) tbody.innerHTML = `<tr><td colspan="3" class="text-center text-danger"><i class="fa-solid fa-triangle-exclamation"></i> Error loading register: ${escapeHtml(err.message)}</td></tr>`;
    }
}

function getStudentInitials(name) {
    if (!name) return 'ST';
    const parts = name.trim().split(/\s+/);
    if (parts.length >= 2) return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
    return parts[0].substring(0, 2).toUpperCase();
}

function getStudentAvatarColor(name) {
    const palettes = [
        { bg: '#dbeafe', text: '#1e40af' }, // blue
        { bg: '#ede9fe', text: '#6d28d9' }, // purple
        { bg: '#dcfce7', text: '#15803d' }, // green
        { bg: '#ffedd5', text: '#c2410c' }, // orange
        { bg: '#fce7f3', text: '#be185d' }, // pink
        { bg: '#e0f2fe', text: '#0369a1' }, // sky
        { bg: '#ccfbf1', text: '#0f766e' }  // teal
    ];
    let hash = 0;
    for (let i = 0; i < (name || '').length; i++) hash = name.charCodeAt(i) + ((hash << 5) - hash);
    return palettes[Math.abs(hash) % palettes.length];
}

function getRemarkPlaceholder(status) {
    if (status === 'ABSENT') return 'Required: Reason for absence (e.g. sick fever, family function)...';
    if (status === 'LATE') return 'Reason for late arrival (e.g. transit delay, doctor visit)...';
    if (status === 'EXCUSED') return 'Approved permission (e.g. official sports meet, medical slip)...';
    return '';
}

function renderRemarkCellContent(studentId, status, remarks) {
    if (status === 'PRESENT') {
        return `
            <div class="remark-na-chip" title="Remarks are not applicable for present students">
                <i class="fa-solid fa-circle-check"></i>
                <span>Present &bull; No remark</span>
            </div>
        `;
    }

    const highlightClass = status === 'ABSENT' ? 'remark-highlight-absent' : (status === 'LATE' ? 'remark-highlight-late' : 'remark-highlight-excused');
    return `
        <div class="remark-input-container">
            <i class="fa-solid fa-pen-nib remark-icon"></i>
            <input type="text" 
                   class="form-control att-remark-input ${highlightClass}" 
                   id="att-remark-${studentId}"
                   placeholder="${getRemarkPlaceholder(status)}" 
                   value="${escapeHtml(remarks || '')}"
                   oninput="updateStudentAttendanceRemark(${studentId}, this.value)">
        </div>
    `;
}

function renderAttendanceTableRows() {
    const tbody = document.getElementById('attendance-table-body');
    if (!tbody) return;

    if (currentAttendanceStudents.length === 0) {
        tbody.innerHTML = `<tr><td colspan="3" class="text-center" style="padding: 30px; color: #64748b;">No students found for this class section.</td></tr>`;
        return;
    }

    tbody.innerHTML = currentAttendanceStudents.map(s => {
        const isP = s.status === 'PRESENT' ? 'active-p' : '';
        const isA = s.status === 'ABSENT' ? 'active-a' : '';
        const isL = s.status === 'LATE' ? 'active-l' : '';
        const isE = s.status === 'EXCUSED' ? 'active-e' : '';

        const avatarColor = getStudentAvatarColor(s.studentName);
        const initials = getStudentInitials(s.studentName);

        const rowStatusClass = `att-row-${s.status ? s.status.toLowerCase() : 'present'}`;

        return `
            <tr data-student-id="${s.studentId}" class="${rowStatusClass}">
                <td>
                    <div class="student-profile-cell">
                        <div class="student-avatar-badge" style="background: ${avatarColor.bg}; color: ${avatarColor.text};">
                            ${initials}
                        </div>
                        <div class="student-meta">
                            <span class="student-name-text">${escapeHtml(s.studentName)}</span>
                            <span class="student-sub-badge"><i class="fa-solid fa-id-badge"></i> Roll #${s.rollNo}</span>
                        </div>
                    </div>
                </td>
                <td style="text-align: center;">
                    <div class="btn-att-group" data-student-id="${s.studentId}">
                        <button type="button" class="btn-att ${isP}" data-status="PRESENT" onclick="setStudentAttendanceStatus(${s.studentId}, 'PRESENT')" title="Mark Present">
                            <i class="fa-solid fa-check"></i> Present
                        </button>
                        <button type="button" class="btn-att ${isA}" data-status="ABSENT" onclick="setStudentAttendanceStatus(${s.studentId}, 'ABSENT')" title="Mark Absent">
                            <i class="fa-solid fa-xmark"></i> Absent
                        </button>
                        <button type="button" class="btn-att ${isL}" data-status="LATE" onclick="setStudentAttendanceStatus(${s.studentId}, 'LATE')" title="Mark Late">
                            <i class="fa-regular fa-clock"></i> Late
                        </button>
                        <button type="button" class="btn-att ${isE}" data-status="EXCUSED" onclick="setStudentAttendanceStatus(${s.studentId}, 'EXCUSED')" title="Mark Excused">
                            <i class="fa-solid fa-shield-halved"></i> Excused
                        </button>
                    </div>
                </td>
                <td>
                    <div class="remark-field-container" id="att-remark-box-${s.studentId}">
                        ${renderRemarkCellContent(s.studentId, s.status, s.remarks)}
                    </div>
                </td>
            </tr>
        `;
    }).join('');
}

function setStudentAttendanceStatus(studentId, status) {
    const student = currentAttendanceStudents.find(s => s.studentId === studentId);
    if (!student) return;
    student.status = status;

    if (status === 'PRESENT') {
        student.remarks = ''; // Clear remark if student is present
    }

    const tr = document.querySelector(`tr[data-student-id="${studentId}"]`);
    if (tr) {
        tr.className = `att-row-${status.toLowerCase()}`;
    }

    const group = document.querySelector(`.btn-att-group[data-student-id="${studentId}"]`);
    if (group) {
        group.querySelectorAll('.btn-att').forEach(btn => {
            btn.classList.remove('active-p', 'active-a', 'active-l', 'active-e');
            const btnStatus = btn.getAttribute('data-status');
            if (btnStatus === status) {
                if (status === 'PRESENT') btn.classList.add('active-p');
                else if (status === 'ABSENT') btn.classList.add('active-a');
                else if (status === 'LATE') btn.classList.add('active-l');
                else if (status === 'EXCUSED') btn.classList.add('active-e');
            }
        });
    }

    const remarkBox = document.getElementById(`att-remark-box-${studentId}`);
    if (remarkBox) {
        remarkBox.innerHTML = renderRemarkCellContent(studentId, status, student.remarks);
        if (status !== 'PRESENT') {
            const input = document.getElementById(`att-remark-${studentId}`);
            if (input) input.focus();
        }
    }

    updateAttendanceMetricsDisplay();
}

function updateStudentAttendanceRemark(studentId, remark) {
    const student = currentAttendanceStudents.find(s => s.studentId === studentId);
    if (student) {
        student.remarks = remark;
    }
}

function markAllAttendanceStatus(status) {
    currentAttendanceStudents.forEach(s => {
        s.status = status;
        if (status === 'PRESENT') {
            s.remarks = '';
        }
    });
    renderAttendanceTableRows();
    updateAttendanceMetricsDisplay();
    showToast("Status Updated", `All students set to ${status}. Click 'Save Register' to persist changes.`, "info");
}

function updateAttendanceMetricsDisplay() {
    const total = currentAttendanceStudents.length;
    let present = 0;
    let absent = 0;
    let late = 0;
    let excused = 0;

    currentAttendanceStudents.forEach(s => {
        if (s.status === 'PRESENT') present++;
        else if (s.status === 'ABSENT') absent++;
        else if (s.status === 'LATE') late++;
        else if (s.status === 'EXCUSED') excused++;
    });

    const attendedCount = present + late;
    const rate = total > 0 ? Math.round(((attendedCount) / total) * 1000) / 10 : 0;

    const totalEl = document.getElementById('att-stat-total');
    const presentEl = document.getElementById('att-stat-present');
    const absentEl = document.getElementById('att-stat-absent');
    const lateEl = document.getElementById('att-stat-late');
    const excusedEl = document.getElementById('att-stat-excused');
    const rateEl = document.getElementById('att-stat-rate');

    if (totalEl) totalEl.textContent = total;
    if (presentEl) presentEl.textContent = present;
    if (absentEl) absentEl.textContent = absent;
    if (lateEl) lateEl.textContent = late;
    if (excusedEl) excusedEl.textContent = excused;
    if (rateEl) rateEl.textContent = `${rate}%`;
}

async function saveClassAttendance() {
    if (currentAttendanceStudents.length === 0) {
        showToast("Cannot Save", "No student attendance records to save.", "warning");
        return;
    }

    const saveBtn = document.getElementById('btn-save-attendance');
    const dateInput = document.getElementById('att-date-picker');
    const sendWhatsAppCheckbox = document.getElementById('att-send-whatsapp');
    const sendWhatsApp = sendWhatsAppCheckbox ? sendWhatsAppCheckbox.checked : false;

    const selectedDate = dateInput && dateInput.value ? dateInput.value : new Date().toISOString().split('T')[0];
    const { assigned, grade, section } = getTeacherClassDetails();

    const originalBtnHtml = saveBtn ? saveBtn.innerHTML : '';
    if (saveBtn) {
        saveBtn.disabled = true;
        saveBtn.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> Saving...`;
    }

    try {
        const payload = {
            grade: grade,
            section: section,
            date: selectedDate,
            recordedBy: `${currentUser?.fullName || 'Teacher'} (${currentUser?.username || 'faculty'})`,
            entries: currentAttendanceStudents.map(s => ({
                studentId: s.studentId,
                status: s.status,
                remarks: s.status === 'PRESENT' ? '' : (s.remarks || ''),
                sendAlert: sendWhatsApp && (s.status === 'ABSENT')
            }))
        };

        const res = await fetch(`${API_BASE}/api/academic/attendance/batch`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(payload)
        });

        const d = await res.json();
        if (res.ok && d.success) {
            const absentCount = currentAttendanceStudents.filter(s => s.status === 'ABSENT').length;
            let successMsg = `Daily attendance recorded for ${currentAttendanceStudents.length} students.`;
            if (sendWhatsApp && absentCount > 0) {
                successMsg += ` Automated WhatsApp absence alerts triggered for ${absentCount} parent(s).`;
            }
            showToast("Register Successfully Saved", successMsg, "success");

            // Refresh view to get updated cumulative rates
            await loadTeacherAttendance();
        } else {
            showToast("Save Failed", d.message || "Failed to record attendance", "error");
        }
    } catch (err) {
        console.error("Error saving attendance:", err);
        showToast("Network Error", err.message, "error");
    } finally {
        if (saveBtn) {
            saveBtn.disabled = false;
            saveBtn.innerHTML = originalBtnHtml;
        }
    }
}

let currentAssignedClassStudents = [];

function renderAssignedClassStudentRow(s) {
    const avatarColor = getStudentAvatarColor(s.fullName);
    const initials = getStudentInitials(s.fullName);
    const rateVal = s.attendanceRate != null ? s.attendanceRate : 95.0;
    // Attendance %age: Red if below 70%, else Green
    const isBelow70 = rateVal < 70;
    const rateBadgeClass = isBelow70 ? 'rate-danger' : 'rate-good';
    const rateIcon = isBelow70 ? 'fa-solid fa-triangle-exclamation' : 'fa-solid fa-circle-check';

    const feeStatus = s.feeStatus || 'PENDING';
    const isCleared = feeStatus === 'CLEARED';

    return `
        <tr data-student-id="${s.id}">
            <td>
                <div class="student-profile-cell">
                    <div class="student-avatar-badge" style="background: ${avatarColor.bg}; color: ${avatarColor.text};">
                        ${initials}
                    </div>
                    <div class="student-meta">
                        <span class="student-name-text">${escapeHtml(s.fullName)}</span>
                        <span class="student-sub-badge"><i class="fa-solid fa-id-badge"></i> Roll #${s.rollNo || '-'} &bull; ${escapeHtml(s.admissionNo)}</span>
                    </div>
                </div>
            </td>
            <td>
                <div style="font-weight: 600; color: #1e293b;">${escapeHtml(s.guardianName || 'Guardian')}</div>
                <small style="color: #64748b;">Parent / Guardian</small>
            </td>
            <td>
                <a href="tel:${escapeHtml(s.guardianPhone || '')}" class="guardian-phone-chip" title="Call or Message Parent">
                    <i class="fa-solid fa-phone"></i> ${escapeHtml(s.guardianPhone || '-')}
                </a>
            </td>
            <td>
                <div class="address-cell-box" title="${escapeHtml(s.residentialAddress || 'Harpur, Raxaul, Bihar')}">
                    <i class="fa-solid fa-location-dot"></i>
                    <span>${escapeHtml(s.residentialAddress || 'Harpur, Raxaul, Bihar')}</span>
                </div>
            </td>
            <td style="text-align: center;">
                <span class="att-rate-badge-pill ${rateBadgeClass}">
                    <i class="${rateIcon}"></i> ${rateVal}%
                </span>
            </td>
            <td>
                <div class="fee-reminder-cell">
                    ${isCleared 
                        ? `<span class="badge badge-success"><i class="fa-solid fa-check"></i> Cleared</span>
                           <button class="btn-fee-remind btn-fee-cleared" disabled title="No outstanding fee dues">
                               <i class="fa-solid fa-check-double"></i> Cleared
                           </button>`
                        : `<span class="badge ${feeStatus === 'OVERDUE' ? 'badge-danger' : 'badge-warning'}">
                               <i class="fa-solid fa-clock"></i> ${feeStatus}
                           </span>
                           <button class="btn-fee-remind" onclick="triggerFeeReminderWhatsApp(${s.id})">
                               <i class="fa-brands fa-whatsapp"></i> Fee Reminder
                           </button>`
                    }
                </div>
            </td>
            <td style="text-align: center;">
                <button class="btn btn-sm btn-outline" style="border-color: #2563eb; color: #2563eb; font-weight: 600;" onclick="generateSampleReportCard(${s.id})">
                    <i class="fa-solid fa-file-invoice"></i> View Card
                </button>
            </td>
            <td>
                <div class="notes-summary-cell">
                    <div class="notes-preview-text ${!s.notesSummary ? 'notes-preview-empty' : ''}" id="notes-preview-${s.id}">
                        ${escapeHtml(s.notesSummary || 'No notes added')}
                    </div>
                    <button class="btn-edit-notes" onclick="openStudentNotesModal(${s.id})">
                        <i class="fa-solid fa-pen-to-square"></i> ${s.notesSummary ? 'Edit Notes' : 'Add Note'}
                    </button>
                </div>
            </td>
        </tr>
    `;
}

async function loadTeacherStudents() {
    try {
        const { assigned, grade, section } = getTeacherClassDetails();
        const headingEl = document.getElementById('teacher-students-heading');
        if (headingEl) headingEl.textContent = `Assigned Class: ${grade} - Section ${section}`;

        const pageTitleEl = document.getElementById('page-title');
        if (pageTitleEl) pageTitleEl.textContent = `Assigned Class (${grade} ${section})`;

        const res = await fetch(`${API_BASE}/api/admin/students?grade=${encodeURIComponent(grade)}`, { headers: getAuthHeaders() });
        const d = await res.json();
        const tbody = document.getElementById('teacher-students-body');
        if (!tbody) return;

        let students = (d.data || []).filter(s => !section || !s.section || s.section.toUpperCase() === section.toUpperCase());
        if (students.length === 0 && d.data && d.data.length > 0) {
            students = d.data;
        }

        // Sort students by rollNo
        students.sort((a, b) => {
            const numA = parseInt(a.rollNo, 10);
            const numB = parseInt(b.rollNo, 10);
            if (isNaN(numA) || isNaN(numB)) return (a.fullName || '').localeCompare(b.fullName || '');
            return numA - numB;
        });

        currentAssignedClassStudents = students;

        if (students.length === 0) {
            tbody.innerHTML = `<tr><td colspan="8" class="text-center" style="padding: 30px; color: #64748b;">No assigned students found for ${grade} ${section}.</td></tr>`;
            return;
        }

        tbody.innerHTML = students.map(s => renderAssignedClassStudentRow(s)).join('');
    } catch (e) {
        console.error("Error loading teacher students:", e);
        const tbody = document.getElementById('teacher-students-body');
        if (tbody) tbody.innerHTML = `<tr><td colspan="8" class="text-center text-danger">Error loading reportees: ${escapeHtml(e.message)}</td></tr>`;
    }
}

async function triggerFeeReminderWhatsApp(studentId) {
    const student = currentAssignedClassStudents.find(s => s.id === studentId);
    if (!student) return;

    const confirmed = await appConfirm(
        "Confirm Fee Reminder",
        `Dispatch automated WhatsApp fee reminder to ${student.guardianName || 'parent'} (${student.guardianPhone || 'registered number'}) for student ${student.fullName}?`,
        "Send Fee Reminder",
        "btn-warning"
    );
    if (!confirmed) return;

    try {
        const res = await fetch(`${API_BASE}/api/communication/whatsapp/trigger/fee-reminder/${studentId}`, {
            method: 'POST',
            headers: getAuthHeaders()
        });
        const d = await res.json();
        if (res.ok && d.success) {
            showToast("Fee Reminder Dispatched", `Notification sent to parent of ${student.fullName} via WhatsApp.`, "success");
        } else {
            showToast("Reminder Failed", d.message || "Failed to dispatch WhatsApp fee reminder.", "error");
        }
    } catch (e) {
        showToast("Network Error", e.message, "error");
    }
}

function openStudentNotesModal(studentId) {
    const student = currentAssignedClassStudents.find(s => s.id === studentId);
    if (!student) return;

    document.getElementById('notes-student-id').value = student.id;
    document.getElementById('notes-modal-title').textContent = `Notes: ${student.fullName}`;
    document.getElementById('notes-modal-subtitle').textContent = `Class ${student.currentClass} ${student.section} • Roll #${student.rollNo || '-'} • Adm: ${student.admissionNo}`;
    document.getElementById('notes-student-textarea').value = student.notesSummary || '';
    openModal('modal-student-notes');
}

async function saveStudentNotes() {
    const studentId = document.getElementById('notes-student-id').value;
    const notesText = document.getElementById('notes-student-textarea').value.trim();
    const saveBtn = document.getElementById('btn-save-notes');
    const origHtml = saveBtn ? saveBtn.innerHTML : '';
    if (saveBtn) {
        saveBtn.disabled = true;
        saveBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Saving...';
    }

    try {
        const res = await fetch(`${API_BASE}/api/admin/students/${studentId}/notes`, {
            method: 'PATCH',
            headers: getAuthHeaders(),
            body: JSON.stringify({ notesSummary: notesText })
        });
        const d = await res.json();
        if (res.ok && d.success) {
            showToast("Notes Updated", "Student academic notes summary saved successfully.", "success");
            closeModal('modal-student-notes');

            // Update local memory and DOM element directly
            const student = currentAssignedClassStudents.find(s => s.id == studentId);
            if (student) {
                student.notesSummary = notesText;
            }
            document.querySelectorAll(`[id='notes-preview-${studentId}']`).forEach(previewEl => {
                previewEl.textContent = notesText || 'No notes added';
                if (!notesText) {
                    previewEl.classList.add('notes-preview-empty');
                } else {
                    previewEl.classList.remove('notes-preview-empty');
                }
            });
        } else {
            showToast("Error Saving Notes", d.message || "Failed to update notes.", "error");
        }
    } catch (e) {
        showToast("Network Error", e.message, "error");
    } finally {
        if (saveBtn) {
            saveBtn.disabled = false;
            saveBtn.innerHTML = origHtml;
        }
    }
}

/* ==========================================================================
   FACULTY WORK ITEMS, NOTES & REMINDERS LOGIC
   ========================================================================== */
let currentTeacherWorkItems = [];
let activeWorkItemFilter = 'ALL';

async function loadTeacherWorkItems() {
    const tbody = document.getElementById('teacher-workitems-body');
    if (!tbody) return;

    try {
        const username = currentUser?.username;
        const res = await fetch(`${API_BASE}/api/admin/work-items`, { headers: getAuthHeaders() });
        const d = await res.json();
        let items = d.data || [];

        // If faculty, filter to items assigned to user or created by user
        const isFaculty = currentUser?.roles?.includes('ROLE_TEACHER');
        if (isFaculty && username) {
            items = items.filter(item => 
                (item.assignedToUsername && item.assignedToUsername.toLowerCase() === username.toLowerCase()) ||
                (item.assignedByUsername && item.assignedByUsername.toLowerCase() === username.toLowerCase())
            );
        }

        currentTeacherWorkItems = items;
        updateWorkItemCounters();
        renderTeacherWorkItems();
    } catch (e) {
        console.error("Error loading work items:", e);
        tbody.innerHTML = `<tr><td colspan="7" class="text-center" style="padding: 24px; color: #ef4444;">Failed to load work items. Please refresh.</td></tr>`;
    }
}

function updateWorkItemCounters() {
    const allCount = currentTeacherWorkItems.length;
    const pendingCount = currentTeacherWorkItems.filter(i => i.status !== 'COMPLETED').length;
    const completedCount = currentTeacherWorkItems.filter(i => i.status === 'COMPLETED').length;

    const elAll = document.getElementById('count-workitem-all');
    const elPending = document.getElementById('count-workitem-pending');
    const elCompleted = document.getElementById('count-workitem-completed');

    if (elAll) elAll.textContent = allCount;
    if (elPending) elPending.textContent = pendingCount;
    if (elCompleted) elCompleted.textContent = completedCount;
}

function filterTeacherWorkItems(filter) {
    activeWorkItemFilter = filter;
    document.querySelectorAll('.workitem-filter-group .btn-filter-chip').forEach(btn => btn.classList.remove('active'));
    const targetBtn = document.getElementById(`filter-workitem-${filter.toLowerCase()}`);
    if (targetBtn) targetBtn.classList.add('active');
    renderTeacherWorkItems();
}

function renderTeacherWorkItems() {
    const tbody = document.getElementById('teacher-workitems-body');
    if (!tbody) return;

    let items = currentTeacherWorkItems;
    if (activeWorkItemFilter === 'PENDING') {
        items = items.filter(i => i.status !== 'COMPLETED');
    } else if (activeWorkItemFilter === 'COMPLETED') {
        items = items.filter(i => i.status === 'COMPLETED');
    }

    if (items.length === 0) {
        let msg = 'No work items found.';
        if (activeWorkItemFilter === 'PENDING') msg = 'No pending work items. Great job!';
        if (activeWorkItemFilter === 'COMPLETED') msg = 'No completed work items yet.';
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center" style="padding: 36px 20px; color: #64748b;">
                    <div style="font-size: 2rem; margin-bottom: 8px; color: #cbd5e1;"><i class="fa-solid fa-clipboard-check"></i></div>
                    <div style="font-weight: 600; color: #475569; margin-bottom: 4px;">${msg}</div>
                    <button class="btn btn-outline btn-sm" style="margin-top: 8px;" onclick="openAddWorkItemModal()">
                        <i class="fa-solid fa-plus"></i> Add Work Item
                    </button>
                </td>
            </tr>`;
        return;
    }

    const todayStr = new Date().toISOString().split('T')[0];

    tbody.innerHTML = items.map(item => {
        const isCompleted = item.status === 'COMPLETED';
        const isOverdue = item.dueDate && item.dueDate < todayStr && !isCompleted;

        // Type Pill
        let typeBadge = '';
        switch (item.itemType) {
            case 'NOTE':
                typeBadge = `<span class="type-pill type-pill-note"><i class="fa-regular fa-note-sticky"></i> Note</span>`;
                break;
            case 'REMINDER':
                typeBadge = `<span class="type-pill type-pill-reminder"><i class="fa-regular fa-bell"></i> Reminder</span>`;
                break;
            case 'ACADEMIC_DUTY':
                typeBadge = `<span class="type-pill type-pill-duty"><i class="fa-solid fa-graduation-cap"></i> Duty</span>`;
                break;
            default:
                typeBadge = `<span class="type-pill type-pill-task"><i class="fa-solid fa-check"></i> Task</span>`;
        }

        // Priority Pill
        let priorityBadge = '';
        switch (item.priority) {
            case 'URGENT':
                priorityBadge = `<span class="priority-pill priority-urgent"><i class="fa-solid fa-circle-exclamation"></i> Urgent</span>`;
                break;
            case 'HIGH':
                priorityBadge = `<span class="priority-pill priority-high"><i class="fa-solid fa-arrow-up"></i> High</span>`;
                break;
            case 'LOW':
                priorityBadge = `<span class="priority-pill priority-low">Low</span>`;
                break;
            default:
                priorityBadge = `<span class="priority-pill priority-medium">Medium</span>`;
        }

        // Assigned By Badge
        let assignedBadge = '';
        const role = (item.assignedByRole || 'SELF').toUpperCase();
        if (role === 'MANAGEMENT') {
            assignedBadge = `<span class="assigned-by-badge assigned-mgmt"><i class="fa-solid fa-building-columns"></i> Management</span>`;
        } else if (role === 'ADMIN') {
            assignedBadge = `<span class="assigned-by-badge assigned-admin"><i class="fa-solid fa-shield-halved"></i> Admin</span>`;
        } else {
            assignedBadge = `<span class="assigned-by-badge assigned-self"><i class="fa-solid fa-user"></i> Self</span>`;
        }
        const assignerName = item.assignedByName || (item.assignedByUsername ? escapeHtml(item.assignedByUsername) : 'Direct');

        // Due date display
        let dueDateDisplay = '-';
        if (item.dueDate) {
            dueDateDisplay = `<span class="due-date-text ${isOverdue ? 'overdue' : ''}">
                ${escapeHtml(item.dueDate)} ${isOverdue ? '<i class="fa-solid fa-triangle-exclamation" title="Overdue"></i>' : ''}
            </span>`;
        }

        return `
            <tr class="workitem-row ${isCompleted ? 'is-completed' : ''}" id="workitem-row-${item.id}">
                <td style="text-align: center;">
                    <button type="button" class="workitem-toggle-btn ${isCompleted ? 'checked' : ''}" 
                            onclick="toggleWorkItemStatus(${item.id})" title="${isCompleted ? 'Mark Pending' : 'Mark Completed'}">
                        <i class="fa-solid fa-check"></i>
                    </button>
                </td>
                <td>
                    <div class="workitem-title-box">
                        <div class="workitem-title-text">${escapeHtml(item.title || '')}</div>
                        ${item.description ? `<div class="workitem-desc-text">${escapeHtml(item.description)}</div>` : ''}
                        ${isCompleted && item.completedAt ? `<div style="font-size: 0.72rem; color: #10b981; margin-top: 2px;"><i class="fa-solid fa-circle-check"></i> Completed</div>` : ''}
                    </div>
                </td>
                <td>${typeBadge}</td>
                <td>${priorityBadge}</td>
                <td>
                    <div style="display: flex; flex-direction: column; gap: 2px;">
                        ${assignedBadge}
                        <span style="font-size: 0.76rem; color: #64748b;">${escapeHtml(assignerName)}</span>
                    </div>
                </td>
                <td>${dueDateDisplay}</td>
                <td style="text-align: center;">
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteWorkItem(${item.id})" title="Delete work item">
                        <i class="fa-solid fa-trash-can"></i>
                    </button>
                </td>
            </tr>
        `;
    }).join('');
}

async function toggleWorkItemStatus(id) {
    try {
        const res = await fetch(`${API_BASE}/api/admin/work-items/${id}/toggle`, {
            method: 'PATCH',
            headers: getAuthHeaders()
        });
        const d = await res.json();
        if (d.success && d.data) {
            const idx = currentTeacherWorkItems.findIndex(i => i.id === id);
            if (idx >= 0) {
                currentTeacherWorkItems[idx] = d.data;
            }
            updateWorkItemCounters();
            renderTeacherWorkItems();
            showToast(d.data.status === 'COMPLETED' ? "Work item completed!" : "Work item marked pending", "success");
        } else {
            showToast(d.message || "Failed to update status", "error");
        }
    } catch (e) {
        console.error("Error toggling status:", e);
        showToast("Error updating work item status", "error");
    }
}

async function deleteWorkItem(id) {
    const confirmed = await appConfirm("Delete Work Item", "Are you sure you want to delete this work item / note?", "Delete", "btn-danger");
    if (!confirmed) return;

    try {
        const res = await fetch(`${API_BASE}/api/admin/work-items/${id}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        const d = await res.json();
        if (d.success) {
            currentTeacherWorkItems = currentTeacherWorkItems.filter(i => i.id !== id);
            updateWorkItemCounters();
            renderTeacherWorkItems();
            showToast("Work item deleted", "info");
        } else {
            showToast(d.message || "Failed to delete work item", "error");
        }
    } catch (e) {
        console.error("Error deleting work item:", e);
        showToast("Failed to delete work item", "error");
    }
}

function openAddWorkItemModal(preselectedAssignee) {
    const form = document.getElementById('form-add-workitem');
    if (form) form.reset();

    const titleEl = document.getElementById('workitem-modal-title');
    const subtitleEl = document.getElementById('workitem-modal-subtitle');
    const editIdEl = document.getElementById('workitem-edit-id');
    if (editIdEl) editIdEl.value = '';

    const isAdminOrMgmt = currentUser?.roles?.includes('ROLE_ADMIN') || currentUser?.roles?.includes('ROLE_MANAGEMENT');
    const assigneeLabel = document.getElementById('workitem-assignee-label');
    const assigneeControl = document.getElementById('workitem-assignee-control');

    if (isAdminOrMgmt) {
        if (titleEl) titleEl.textContent = 'Work Items';
        if (subtitleEl) subtitleEl.textContent = 'Assign academic tasks, reminders, or directives to Oxford faculty & staff';
        if (assigneeLabel) assigneeLabel.innerHTML = 'Assign To Faculty / Staff <span style="color:#ef4444;">*</span>';
        if (assigneeControl) {
            assigneeControl.innerHTML = `
                <select id="workitem-assigned-to" class="form-control" required>
                    <option value="teacher.sharma">Dr. Satish Sharma (Class 10A Teacher - Math)</option>
                    <option value="teacher.verma">Mrs. Pooja Verma (Class 9B Teacher - Science)</option>
                    <option value="sundram.kumar">Er. Sundram Kumar (Lead Architect)</option>
                    <option value="anil.gupt">Anil Kumar Gupt (Management)</option>
                    <option value="admin">System Administrator</option>
                </select>
            `;
            if (preselectedAssignee) {
                const sel = document.getElementById('workitem-assigned-to');
                if (sel) sel.value = preselectedAssignee;
            }
        }
    } else {
        // Teacher adding their own workitem / note
        if (titleEl) titleEl.textContent = 'Work Items';
        if (subtitleEl) subtitleEl.textContent = 'Keep track of your personal tasks, notes, and reminders';
        if (assigneeLabel) assigneeLabel.textContent = 'Assignee';
        if (assigneeControl) {
            assigneeControl.innerHTML = `
                <input type="hidden" id="workitem-assigned-to" value="${currentUser?.username || 'teacher.sharma'}">
                <div style="padding: 9px 12px; background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; font-size: 0.9rem; color: #1e293b; display: flex; align-items: center; gap: 8px;">
                    <i class="fa-solid fa-user-check" style="color: #10b981;"></i>
                    <span><strong>Self</strong> (${currentUser?.fullName || 'Dr. Satish Sharma'})</span>
                </div>
            `;
        }
    }

    // Default due date to 3 days from now
    const targetDate = new Date();
    targetDate.setDate(targetDate.getDate() + 3);
    const dateEl = document.getElementById('workitem-due-date');
    if (dateEl) dateEl.value = targetDate.toISOString().split('T')[0];

    openModal('modal-add-workitem');
}

async function handleSaveWorkItem(event) {
    if (event) event.preventDefault();

    const title = document.getElementById('workitem-title')?.value?.trim();
    if (!title) {
        showToast("Please enter a title or directive", "warning");
        return;
    }

    const itemType = document.getElementById('workitem-type')?.value || 'TASK';
    const priority = document.getElementById('workitem-priority')?.value || 'MEDIUM';
    const dueDate = document.getElementById('workitem-due-date')?.value || null;
    const description = document.getElementById('workitem-description')?.value?.trim() || '';
    const assignedToUsername = document.getElementById('workitem-assigned-to')?.value || currentUser?.username || 'teacher.sharma';

    // Map assignee name
    let assignedToName = 'Faculty Member';
    if (assignedToUsername === 'teacher.sharma') assignedToName = 'Dr. Satish Sharma';
    else if (assignedToUsername === 'teacher.verma') assignedToName = 'Mrs. Pooja Verma';
    else if (assignedToUsername === 'sundram.kumar') assignedToName = 'Er. Sundram Kumar';
    else if (assignedToUsername === 'anil.gupt') assignedToName = 'Anil Kumar Gupt (Management)';
    else if (assignedToUsername === 'admin') assignedToName = 'System Administrator';
    else if (currentUser && currentUser.username === assignedToUsername) assignedToName = currentUser.fullName;

    // Assigned by role
    const isAdmin = currentUser?.roles?.includes('ROLE_ADMIN');
    const isMgmt = currentUser?.roles?.includes('ROLE_MANAGEMENT');
    let assignedByRole = 'SELF';
    if (isMgmt) assignedByRole = 'MANAGEMENT';
    else if (isAdmin) assignedByRole = 'ADMIN';

    const payload = {
        title,
        description,
        itemType,
        priority,
        status: 'PENDING',
        assignedToUsername,
        assignedToName,
        assignedByUsername: currentUser?.username || 'admin',
        assignedByName: currentUser?.fullName || (isMgmt ? 'Management' : 'Administrator'),
        assignedByRole,
        dueDate: dueDate || null
    };

    const submitBtn = document.getElementById('btn-submit-workitem');
    if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Saving...';
    }

    try {
        const res = await fetch(`${API_BASE}/api/admin/work-items`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(payload)
        });
        const d = await res.json();
        if (d.success) {
            closeModal('modal-add-workitem');
            showToast("Work item saved successfully!", "success");
            await loadTeacherWorkItems();
        } else {
            showToast(d.message || "Failed to create work item", "error");
        }
    } catch (e) {
        console.error("Error saving work item:", e);
        showToast("Error saving work item", "error");
    } finally {
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<i class="fa-solid fa-paper-plane"></i> Save Work Item';
        }
    }
}

async function triggerAbsentWhatsApp(studentId, studentName) {
    const confirmed = await appConfirm("Confirm Absence Alert", `Mark ${studentName} as absent today and dispatch automated WhatsApp alert to parent?`, "Record & Alert Parent", "btn-warning");
    if (!confirmed) return;
    try {
        // Record absence in student profile (-0.5% attendance penalty)
        await fetch(`${API_BASE}/api/admin/students/${studentId}/attendance`, {
            method: 'PATCH',
            headers: getAuthHeaders(),
            body: JSON.stringify({ delta: -0.5 })
        });

        // Trigger WhatsApp alert to parent
        const res = await fetch(`${API_BASE}/api/communication/whatsapp/trigger/absent/${studentId}`, {
            method: 'POST',
            headers: getAuthHeaders()
        });
        const d = await res.json();
        showToast("Absence Alert Dispatched", `Notification sent to parent of ${studentName} via WhatsApp.`, "success");
        loadTeacherStudents();
    } catch (e) {
        showToast("Action Error", e.message, "error");
    }
}

async function loadTeacherMarks() {
    try {
        const res = await fetch(`${API_BASE}/api/academic/exams/terms/1/grades/class?grade=Class%2010&section=A`, {
            headers: getAuthHeaders()
        });
        const d = await res.json();
        const tbody = document.getElementById('teacher-marks-body');
        if (d.data && d.data.length > 0) {
            tbody.innerHTML = d.data.map(g => `
                <tr>
                    <td><code>${g.admissionNo}</code></td>
                    <td><strong>${g.studentName}</strong></td>
                    <td>${g.subjectName}</td>
                    <td>Term 1 Mid-Term 2026</td>
                    <td>${g.maxMarks}</td>
                    <td>
                        <input type="number" 
                               class="teacher-mark-input" 
                               data-id="${g.id}" 
                               data-student-id="${g.studentId}" 
                               data-student-name="${g.studentName}" 
                               data-admission="${g.admissionNo}" 
                               data-grade="${g.grade || 'Class 10'}" 
                               data-section="${g.section || 'A'}" 
                               data-subject="${g.subjectName}" 
                               data-max="${g.maxMarks}" 
                               value="${g.marksObtained}" 
                               min="0" max="${g.maxMarks}" 
                               style="width: 75px; padding: 4px 8px; border: 1px solid #94a3b8; border-radius: 4px; font-weight: 600;">
                    </td>
                    <td><span class="badge ${g.marksObtained >= 80 ? 'badge-success' : (g.marksObtained >= 60 ? 'badge-primary' : 'badge-warning')}">${g.letterGrade}</span></td>
                </tr>
            `).join('');
        }
    } catch (e) {
        console.error("Error loading marks:", e);
    }
}

async function saveTeacherMarks() {
    const inputs = document.querySelectorAll('.teacher-mark-input');
    if (!inputs || inputs.length === 0) return;

    const payload = Array.from(inputs).map(inp => ({
        id: Number(inp.dataset.id),
        examTermId: 1,
        studentId: Number(inp.dataset.studentId),
        studentName: inp.dataset.studentName,
        admissionNo: inp.dataset.admission,
        grade: inp.dataset.grade,
        section: inp.dataset.section,
        subjectName: inp.dataset.subject,
        marksObtained: parseFloat(inp.value) || 0.0,
        maxMarks: parseFloat(inp.dataset.max) || 100.0
    }));

    try {
        const res = await fetch(`${API_BASE}/api/academic/exams/grades/batch`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(payload)
        });
        const d = await res.json();
        if (d.success) {
            showToast("Grade Sheet Saved", "Official marks recorded and committed to academic database.", "success");
            loadTeacherMarks();
        } else {
            showToast("Save Failed", d.message, "error");
        }
    } catch (e) {
        showToast("Error", "Unable to save grades: " + e.message, "error");
    }
}

async function loadTeacherLeaves() {
    try {
        const empId = currentUser?.employeeId || 1;
        const res = await fetch(`${API_BASE}/api/finance/leaves?applicantId=${empId}&applicantType=FACULTY`, {
            headers: getAuthHeaders()
        });
        const d = await res.json();
        const tbody = document.getElementById('teacher-leaves-table-body');
        if (tbody) {
            if (d.data && d.data.length > 0) {
                tbody.innerHTML = d.data.map(l => `
                    <tr>
                        <td><strong>${l.leaveType}</strong></td>
                        <td>${l.startDate} to ${l.endDate}</td>
                        <td>${l.startDate}</td>
                        <td>${l.endDate}</td>
                        <td>${l.reason}</td>
                        <td>
                            <span class="badge ${l.status === 'APPROVED' ? 'badge-success' : (l.status === 'PENDING' ? 'badge-warning' : 'badge-danger')}">
                                ${l.status}
                            </span>
                        </td>
                    </tr>
                `).join('');
            } else {
                tbody.innerHTML = `<tr><td colspan="6" class="text-center text-secondary">No leave applications on file.</td></tr>`;
            }
        }
    } catch (e) {
        console.error("Error loading teacher leaves:", e);
    }
}

async function loadTeacherTimetable() {
    try {
        const res = await fetch(`${API_BASE}/api/academic/timetable/class?grade=Class%2010&section=A`, {
            headers: getAuthHeaders()
        });
        const d = await res.json();
        renderTimetableGrid('teacher-timetable-container', d.data || []);
    } catch (e) {}
}

// ==============================================================
// 6. STUDENT PORTAL (OWN PRIVILEGE ONLY - LIVE DATA)
// ==============================================================

async function loadStudentDashboard() {
    const studentId = currentUser?.studentId || 1;
    try {
        // 1. Fetch live student demographic record
        const resStudent = await fetch(`${API_BASE}/api/admin/students/${studentId}`, { headers: getAuthHeaders() });
        const dataStudent = await resStudent.json();
        if (dataStudent.success && dataStudent.data) {
            const s = dataStudent.data;
            const heroName = document.getElementById('student-hero-name');
            const heroClass = document.getElementById('student-hero-class');
            const heroAvatar = document.getElementById('student-hero-avatar');
            if (heroName) heroName.textContent = `Welcome back, ${s.fullName}!`;
            if (heroClass) heroClass.innerHTML = `Class: <strong>${s.currentClass} ${s.section}</strong> &bull; Roll Number: <strong>#${s.rollNo}</strong> &bull; Admission No: <strong>${s.admissionNo}</strong>`;
            if (heroAvatar) heroAvatar.textContent = (s.fullName || 'AK').substring(0, 2).toUpperCase();

            // Update KPI cards
            const kpiAttendance = document.querySelector('#view-student-dashboard .accent-blue .kpi-value');
            if (kpiAttendance) kpiAttendance.textContent = `${s.attendanceRate}%`;

            const kpiFee = document.querySelector('#view-student-dashboard .accent-green .kpi-value');
            if (kpiFee) {
                kpiFee.textContent = s.feeStatus;
                kpiFee.className = `kpi-value ${s.feeStatus === 'CLEARED' ? 'text-success' : 'text-danger'}`;
            }
        }

        // 2. Fetch live report card aggregate
        const resReport = await fetch(`${API_BASE}/api/academic/exams/terms/1/students/${studentId}/report-card`, { headers: getAuthHeaders() });
        const dataReport = await resReport.json();
        if (dataReport.success && dataReport.data) {
            const rc = dataReport.data;
            const kpiAggregate = document.querySelector('#view-student-dashboard .accent-purple .kpi-value');
            const kpiAggregateFooter = document.querySelector('#view-student-dashboard .accent-purple .kpi-footer');
            if (kpiAggregate) kpiAggregate.textContent = `${rc.percentage}%`;
            if (kpiAggregateFooter) kpiAggregateFooter.innerHTML = `Overall Grade: <strong>${rc.overallGrade} (${rc.overallResult})</strong>`;
        }
    } catch (e) {
        console.error("Student dashboard live sync error:", e);
    }
}

async function loadStudentReportCard() {
    const studentId = currentUser?.studentId || 1;
    try {
        const res = await fetch(`${API_BASE}/api/academic/exams/terms/1/students/${studentId}/report-card`, {
            headers: getAuthHeaders()
        });
        const d = await res.json();
        const tbody = document.getElementById('student-report-card-body');
        if (d.success && d.data && tbody) {
            const rc = d.data;
            tbody.innerHTML = rc.subjectGrades.map(sg => `
                <tr>
                    <td><strong>${sg.subjectName}</strong></td>
                    <td>${sg.maxMarks}</td>
                    <td><strong>${sg.marksObtained}</strong></td>
                    <td><span class="badge ${sg.marksObtained >= 80 ? 'badge-success' : 'badge-primary'}">${sg.letterGrade}</span></td>
                    <td>${sg.marksObtained >= 80 ? 'Distinction' : (sg.marksObtained >= 60 ? 'Satisfactory' : 'Needs Practice')}</td>
                </tr>
            `).join('') + `
                <tr style="background: #f1f5f9; font-weight: bold;">
                    <td>TOTAL AGGREGATE</td>
                    <td>${rc.totalMaxMarks}</td>
                    <td>${rc.totalMarksObtained}</td>
                    <td><span class="badge badge-purple">${rc.overallGrade} (${rc.percentage}%)</span></td>
                    <td><span class="text-success">${rc.overallResult}</span></td>
                </tr>
            `;
        }
    } catch (e) {
        console.error("Student report card load error:", e);
    }
}

async function loadStudentFees() {
    const studentId = currentUser?.studentId || 1;
    try {
        const res = await fetch(`${API_BASE}/api/finance/fees/invoices?studentId=${studentId}`, {
            headers: getAuthHeaders()
        });
        const d = await res.json();
        const tbody = document.getElementById('student-fees-table-body');
        if (tbody) {
            if (d.data && d.data.length > 0) {
                tbody.innerHTML = d.data.map(inv => `
                    <tr>
                        <td><code>${inv.invoiceNumber}</code></td>
                        <td>${inv.billingPeriod}</td>
                        <td>₹ ${Number(inv.grossAmount).toLocaleString()}</td>
                        <td><strong>₹ ${Number(inv.netPayable).toLocaleString()}</strong></td>
                        <td>₹ ${Number(inv.paidAmount).toLocaleString()}</td>
                        <td>
                            <span class="badge ${inv.status === 'PAID' ? 'badge-success' : 'badge-danger'}">
                                ${inv.status}
                            </span>
                        </td>
                        <td>
                            ${inv.status === 'PAID' ? 
                                `<button class="btn btn-outline btn-sm" onclick="showToast('Official Fee Receipt', 'Receipt #${inv.invoiceNumber} | Cleared: ₹${inv.paidAmount}', 'success')">
                                    <i class="fa-solid fa-receipt"></i> View Receipt
                                 </button>` : 
                                `<button class="btn btn-primary btn-sm" onclick="openPaymentModal(${inv.id}, '${inv.invoiceNumber}', ${inv.netPayable - inv.paidAmount})">
                                    <i class="fa-solid fa-credit-card"></i> Pay Now
                                 </button>`
                            }
                        </td>
                    </tr>
                `).join('');
            } else {
                tbody.innerHTML = `<tr><td colspan="7" class="text-center text-secondary">No outstanding fee statements.</td></tr>`;
            }
        }
    } catch (e) {
        console.error("Student fees error:", e);
    }
}

async function loadStudentTransit() {
    const studentId = currentUser?.studentId || 1;
    try {
        const res = await fetch(`${API_BASE}/api/finance/transportation/transit/student/${studentId}`, {
            headers: getAuthHeaders()
        });
        const d = await res.json();
        const container = document.getElementById('student-transit-card');
        if (d.success && d.data && container) {
            const transit = d.data;
            const resRoute = await fetch(`${API_BASE}/api/finance/transportation/routes/${transit.routeId}`, {
                headers: getAuthHeaders()
            });
            const dRoute = await resRoute.json();
            if (dRoute.success && dRoute.data) {
                const r = dRoute.data;
                container.innerHTML = `
                    <h3><i class="fa-solid fa-bus"></i> ${r.routeName}</h3>
                    <p style="font-size: 0.88rem; color: var(--text-secondary); margin: 8px 0 16px;">
                        <strong>Origin:</strong> ${r.startLocation} &bull; <strong>Destination:</strong> ${r.endLocation}<br>
                        <strong>Transit Fee:</strong> ₹${r.monthlyFare} / Month &bull; <strong>Commuter Status:</strong> <span class="badge badge-success">ACTIVE</span>
                    </p>
                    <div class="stop-timeline">
                        ${r.stops.map(st => `
                            <div class="stop-point ${st.id === transit.assignedStopId ? 'highlighted-stop' : ''}" style="${st.id === transit.assignedStopId ? 'background: #eff6ff; padding: 8px; border-radius: 6px; border-left: 3px solid #2563eb;' : ''}">
                                <strong>${st.stopName} ${st.id === transit.assignedStopId ? '<span class="badge badge-primary">MY ASSIGNED STOP</span>' : ''}</strong><br>
                                <small class="text-secondary">Morning Pickup: ${st.pickupTime} &bull; Afternoon Drop: ${st.dropTime}</small>
                            </div>
                        `).join('')}
                    </div>
                `;
            }
        }
    } catch (e) {
        console.error("Student transit load error:", e);
    }
}

async function loadStudentLeaves() {
    const studentId = currentUser?.studentId || 1;
    try {
        const res = await fetch(`${API_BASE}/api/finance/leaves?applicantId=${studentId}&applicantType=STUDENT`, {
            headers: getAuthHeaders()
        });
        const d = await res.json();
        const tbody = document.getElementById('student-leaves-table-body');
        if (tbody) {
            if (d.data && d.data.length > 0) {
                tbody.innerHTML = d.data.map(l => `
                    <tr>
                        <td><strong>${l.leaveType}</strong></td>
                        <td>${l.startDate}</td>
                        <td>${l.endDate}</td>
                        <td>${l.reason}</td>
                        <td>
                            <span class="badge ${l.status === 'APPROVED' ? 'badge-success' : (l.status === 'PENDING' ? 'badge-warning' : 'badge-danger')}">
                                ${l.status}
                            </span>
                        </td>
                    </tr>
                `).join('');
            } else {
                tbody.innerHTML = `<tr><td colspan="5" class="text-center text-secondary">No leave applications submitted.</td></tr>`;
            }
        }
    } catch (e) {
        console.error("Student leaves load error:", e);
    }
}

async function loadStudentTimetable() {
    try {
        const res = await fetch(`${API_BASE}/api/academic/timetable/class?grade=Class%2010&section=A`, {
            headers: getAuthHeaders()
        });
        const d = await res.json();
        renderTimetableGrid('student-timetable-container', d.data || []);
    } catch (e) {}
}

// ==============================================================
// 7. FORM SUBMISSIONS & MODAL HANDLERS (PERSISTENT DATA)
// ==============================================================

async function handleCreateStudent(event) {
    event.preventDefault();
    const payload = {
        admissionNo: `OIS-2026-${Math.floor(1000 + Math.random() * 9000)}`,
        firstName: document.getElementById('std-first-name').value.trim(),
        lastName: document.getElementById('std-last-name').value.trim(),
        currentClass: document.getElementById('std-class').value,
        section: document.getElementById('std-section').value,
        rollNo: parseInt(document.getElementById('std-roll-no').value) || 1,
        guardianName: document.getElementById('std-guardian-name').value.trim(),
        guardianPhone: document.getElementById('std-guardian-phone').value.trim(),
        residentialAddress: document.getElementById('std-address').value.trim(),
        dateOfBirth: '2011-06-15',
        gender: 'Male',
        attendanceRate: 95.0,
        feeStatus: 'CLEARED'
    };

    try {
        const res = await fetch(`${API_BASE}/api/admin/students`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(payload)
        });
        const d = await res.json();
        if (d.success) {
            closeModal('modal-add-student');
            document.getElementById('form-add-student').reset();
            showToast("Student Enrolled", `Assigned Admission No: ${d.data.admissionNo} (${d.data.currentClass} ${d.data.section})`, "success");
            loadStudents();
        } else {
            showToast("Enrollment Failed", d.message, "error");
        }
    } catch (e) {
        showToast("Server Error", e.message, "error");
    }
}

async function handleCreateInquiry(event) {
    event.preventDefault();
    const candidateName = document.getElementById('inq-candidate-name').value.trim();
    const targetGrade = document.getElementById('inq-target-grade').value;
    const parentName = document.getElementById('inq-parent-name').value.trim();
    const contactPhone = document.getElementById('inq-contact-phone').value.trim();
    const notes = document.getElementById('inq-notes').value.trim();

    const payload = {
        candidateName,
        targetGrade,
        parentName,
        contactPhone,
        notes
    };

    try {
        const res = await fetch(`${API_BASE}/api/admin/admissions/inquiries`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(payload)
        });
        const d = await res.json();
        if (d.success) {
            closeModal('modal-new-inquiry');
            document.getElementById('form-new-inquiry').reset();
            await loadAdmissions();
            
            // Automatically switch to the "Inquiry Records" subtab so the user immediately sees the record!
            const inqBtn = document.getElementById('btn-subtab-inquiries');
            if (inqBtn) {
                switchSubtab('adm-inquiries', inqBtn);
            }
            showToast("Inquiry Registered", `Prospect inquiry for "${candidateName}" saved to MySQL admissions database.`, "success");
        } else {
            showToast("Registration Failed", d.message || "Unknown error", "error");
        }
    } catch (e) {
        showToast("Server Error", e.message, "error");
    }
}

async function markInquiryContacted(id) {
    try {
        const res = await fetch(`${API_BASE}/api/admin/admissions/inquiries/${id}/status`, {
            method: 'PATCH',
            headers: getAuthHeaders(),
            body: JSON.stringify({ status: 'CONTACTED', notes: 'Contacted by admissions desk' })
        });
        const d = await res.json();
        if (d.success) {
            showToast("Status Updated", "Inquiry marked as CONTACTED and logged to audit trail.", "success");
            await loadAdmissions();
            const inqBtn = document.getElementById('btn-subtab-inquiries');
            if (inqBtn) switchSubtab('adm-inquiries', inqBtn);
        }
    } catch (e) {
        console.error("Error updating inquiry status:", e);
    }
}

async function convertInquiryToApplication(id, name, grade, parent, phone) {
    document.getElementById('form-new-application').reset();
    document.getElementById('app-student-name').value = name;
    document.getElementById('app-applied-grade').value = grade;
    document.getElementById('app-target-grade').value = grade;
    delete document.getElementById('app-target-grade').dataset.userChanged;
    document.getElementById('app-father-name').value = parent;
    document.getElementById('app-contact-phone').value = phone;
    document.getElementById('app-doc-verified').checked = true;
    checkGradeChangeWarning();
    openModal('modal-new-application');
}

function handleAppliedGradeChange() {
    const applied = document.getElementById('app-applied-grade').value;
    const target = document.getElementById('app-target-grade');
    if (!target.dataset.userChanged) {
        target.value = applied;
    }
    checkGradeChangeWarning();
}

function handleRecommendedGradeChange() {
    const target = document.getElementById('app-target-grade');
    target.dataset.userChanged = 'true';
    checkGradeChangeWarning();
}

function checkGradeChangeWarning() {
    const applied = document.getElementById('app-applied-grade').value;
    const target = document.getElementById('app-target-grade').value;
    const box = document.getElementById('app-class-change-box');
    const reason = document.getElementById('app-class-change-reason');
    if (!box || !reason) return;
    if (applied !== target) {
        box.style.display = 'flex';
        document.getElementById('text-orig-applied').textContent = applied;
        document.getElementById('text-rec-assigned').textContent = target;
        reason.required = true;
    } else {
        box.style.display = 'none';
        reason.required = false;
    }
}

async function handleCreateApplication(event) {
    event.preventDefault();
    const appliedGrade = document.getElementById('app-applied-grade').value;
    const targetGrade = document.getElementById('app-target-grade').value;
    const assignedSection = document.getElementById('app-assigned-section').value;
    const classChangeReason = document.getElementById('app-class-change-reason')?.value.trim() || '';

    if (appliedGrade !== targetGrade && !classChangeReason) {
        showToast('Mandatory Rationale', 'Please provide a pedagogical explanation for recommending a different class.', 'warning');
        return;
    }

    const payload = {
        studentName: document.getElementById('app-student-name').value.trim(),
        dateOfBirth: document.getElementById('app-dob').value,
        gender: document.getElementById('app-gender').value,
        appliedGrade: appliedGrade,
        targetGrade: targetGrade,
        assignedSection: assignedSection,
        classChangeReason: classChangeReason || null,
        fatherName: document.getElementById('app-father-name').value.trim(),
        motherName: document.getElementById('app-mother-name').value.trim() || 'Parent',
        contactPhone: document.getElementById('app-contact-phone').value.trim(),
        address: document.getElementById('app-address').value.trim() || 'Harpur, Raxaul, Bihar',
        documentsVerified: document.getElementById('app-doc-verified').checked,
        status: document.getElementById('app-doc-verified').checked ? 'DOCUMENTS_VERIFIED' : 'SUBMITTED',
        active: true
    };

    try {
        const res = await fetch(`${API_BASE}/api/admin/admissions/applications`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(payload)
        });
        const d = await res.json();
        if (d.success) {
            closeModal('modal-new-application');
            document.getElementById('form-new-application').reset();
            delete document.getElementById('app-target-grade').dataset.userChanged;
            checkGradeChangeWarning();
            await loadAdmissions();
            const appsBtn = document.getElementById('btn-subtab-apps');
            if (appsBtn) switchSubtab('adm-applications', appsBtn);
            showToast("Application Registered", `Application ${d.data.applicationNumber} created for ${payload.studentName}!`, "success");
        } else {
            showToast("Submission Failed", d.message || "Unknown error", "error");
        }
    } catch (e) {
        showToast("Error", e.message, "error");
    }
}

// ==============================================================
// CLASS ADJUSTMENT MODAL CONTROLLER
// ==============================================================
function openAdjustClassModal(appId, studentName, appliedGrade, currentGrade, currentSection) {
    document.getElementById('form-adjust-class').reset();
    document.getElementById('adjust-app-id').value = appId;
    document.getElementById('adjust-student-name').textContent = studentName;
    document.getElementById('adjust-applied-grade').textContent = appliedGrade || currentGrade;
    document.getElementById('adjust-current-assigned').textContent = `${currentGrade} (${currentSection || 'A'})`;
    document.getElementById('adjust-target-grade').value = currentGrade;
    document.getElementById('adjust-section').value = currentSection || 'A';
    openModal('modal-adjust-class');
}

async function handleSaveClassAdjustment(event) {
    event.preventDefault();
    const appId = document.getElementById('adjust-app-id').value;
    const targetGrade = document.getElementById('adjust-target-grade').value;
    const section = document.getElementById('adjust-section').value;
    const reason = document.getElementById('adjust-reason').value.trim();

    if (!reason) {
        showToast('Mandatory Note', 'Pedagogical justification comment is strictly required to adjust class or section.', 'warning');
        return;
    }

    try {
        const payload = {
            targetGrade,
            section,
            reason,
            authorName: currentUser?.fullName || 'Academic Management'
        };
        const res = await fetch(`${API_BASE}/api/admin/admissions/applications/${appId}/class-adjustment`, {
            method: 'PATCH',
            headers: getAuthHeaders(),
            body: JSON.stringify(payload)
        });
        const d = await res.json();
        if (d.success) {
            closeModal('modal-adjust-class');
            showToast('Placement Adjusted', `Academic placement updated to ${targetGrade} (${section}) with justification logged.`, 'success');
            await loadApplications();
        } else {
            showToast('Adjustment Failed', d.message, 'error');
        }
    } catch (e) {
        showToast('Error', e.message, 'error');
    }
}

// ==============================================================
// APPLICATION COMMENT & INTERACTION TIMELINE CONTROLLER
// ==============================================================
async function openApplicationHistoryModal(appId) {
    try {
        const resApp = await fetch(`${API_BASE}/api/admin/admissions/applications/${appId}`, { headers: getAuthHeaders() });
        const dApp = await resApp.json();
        if (!dApp.success || !dApp.data) {
            showToast('Error', 'Unable to retrieve application details', 'error');
            return;
        }

        const app = dApp.data;
        document.getElementById('app-comment-application-id').value = app.id;
        document.getElementById('app-hist-app-no').textContent = app.applicationNumber;
        document.getElementById('app-hist-candidate').textContent = app.studentName;
        document.getElementById('app-hist-applied-grade').textContent = app.appliedGrade || app.targetGrade;
        document.getElementById('app-hist-assigned-grade').textContent = `${app.targetGrade} (${app.assignedSection || 'A'})`;
        document.getElementById('app-hist-parent').textContent = `${app.fatherName} (${app.contactPhone})`;

        const activeBadge = document.getElementById('app-hist-active-badge');
        if (app.active !== false) {
            activeBadge.textContent = 'ACTIVE';
            activeBadge.className = 'badge badge-active';
        } else {
            activeBadge.textContent = 'INACTIVE';
            activeBadge.className = 'badge badge-inactive';
        }

        const statusBadge = document.getElementById('app-hist-status');
        statusBadge.textContent = app.status;
        statusBadge.className = `badge ${app.status === 'ENROLLED' ? 'badge-purple' : (app.status === 'INACTIVE' ? 'badge-inactive' : 'badge-warning')}`;

        document.getElementById('form-application-comment').reset();
        document.getElementById('app-comment-application-id').value = app.id;

        openModal('modal-application-history');
        await loadApplicationComments(appId);
    } catch (e) {
        showToast('Error', e.message, 'error');
    }
}

async function loadApplicationComments(appId) {
    const feed = document.getElementById('application-timeline-feed');
    const pill = document.getElementById('app-comments-count-pill');
    feed.innerHTML = `<div style="text-align: center; color: #94a3b8; padding: 16px;"><i class="fa-solid fa-spinner fa-spin"></i> Loading application history...</div>`;

    try {
        const res = await fetch(`${API_BASE}/api/admin/admissions/applications/${appId}/comments`, { headers: getAuthHeaders() });
        const d = await res.json();
        const comments = d.data || [];
        pill.textContent = `${comments.length} entries`;

        if (comments.length === 0) {
            feed.innerHTML = `
                <div style="text-align: center; color: #64748b; padding: 24px 12px; background: #f8fafc; border-radius: 8px;">
                    <i class="fa-regular fa-comment-dots" style="font-size: 2rem; color: #cbd5e1; margin-bottom: 8px;"></i>
                    <p style="margin: 0; font-size: 0.88rem; font-weight: 600;">No management comments or audit notes recorded yet.</p>
                    <small style="color: #94a3b8;">Use the form above to add pedagogical evaluation, document verification notes, or counseling records.</small>
                </div>
            `;
            return;
        }

        feed.innerHTML = comments.map(c => {
            let dotClass = 'dot-system';
            let dotIcon = 'fa-comment';
            let categoryLabel = 'General Note';

            if (c.interactionType === 'PEDAGOGICAL_EVALUATION') {
                dotClass = 'dot-assessment';
                dotIcon = 'fa-graduation-cap';
                categoryLabel = 'Pedagogical Evaluation';
            } else if (c.interactionType === 'DOCUMENT_AUDIT') {
                dotClass = 'dot-doc';
                dotIcon = 'fa-file-circle-check';
                categoryLabel = 'Document Audit';
            } else if (c.interactionType === 'PARENT_COUNSELING') {
                dotClass = 'dot-visit';
                dotIcon = 'fa-users';
                categoryLabel = 'Parent Counseling';
            } else if (c.interactionType === 'FEE_CONCESSION') {
                dotClass = 'dot-visit';
                dotIcon = 'fa-indian-rupee-sign';
                categoryLabel = 'Fee / Concession';
            } else if (c.interactionType === 'ADMINISTRATIVE_ACTION') {
                dotClass = 'dot-system';
                dotIcon = 'fa-bolt';
                categoryLabel = 'Administrative Action';
            }

            const timeStr = c.createdAt ? new Date(c.createdAt).toLocaleString('en-IN', {
                month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit'
            }) : 'Recent';

            const hasClassChange = c.previousClass && c.recommendedClass && c.previousClass !== c.recommendedClass;

            return `
                <div class="timeline-item">
                    <div class="timeline-dot ${dotClass}">
                        <i class="fa-solid ${dotIcon}"></i>
                    </div>
                    <div class="timeline-card">
                        <div class="timeline-meta">
                            <span class="timeline-author">
                                <i class="fa-solid fa-user-shield" style="color: #64748b; font-size: 0.8rem;"></i> ${escapeHtml(c.authorName)}
                                ${c.authorRole ? `<small style="font-weight: 500; color: #64748b;">(${escapeHtml(c.authorRole)})</small>` : ''}
                            </span>
                            <div style="display: flex; align-items: center; gap: 8px;">
                                <span class="badge badge-primary" style="font-size: 0.72rem; padding: 2px 8px;">${categoryLabel}</span>
                                ${c.newStatus ? `<span class="badge badge-warning" style="font-size: 0.72rem; padding: 2px 8px;">${c.newStatus}</span>` : ''}
                                <span class="timeline-time">${timeStr}</span>
                            </div>
                        </div>
                        ${hasClassChange ? `
                            <div style="margin-bottom: 8px;">
                                <span class="badge badge-class-adjusted">
                                    <i class="fa-solid fa-arrows-split-up-and-left"></i> Placement Change: ${escapeHtml(c.previousClass)} &rarr; ${escapeHtml(c.recommendedClass)}
                                </span>
                            </div>
                        ` : ''}
                        <p class="timeline-text">${escapeHtml(c.commentText)}</p>
                    </div>
                </div>
            `;
        }).join('');
    } catch (e) {
        feed.innerHTML = `<div style="text-align: center; color: #ef4444; padding: 16px;">Failed to load history: ${e.message}</div>`;
    }
}

async function handleAddApplicationComment(event) {
    event.preventDefault();
    const appId = document.getElementById('app-comment-application-id').value;
    const interactionType = document.getElementById('app-comment-type').value;
    const newStatus = document.getElementById('app-comment-status').value;
    const commentText = document.getElementById('app-comment-text').value.trim();
    const btn = document.getElementById('btn-post-app-comment');

    if (!commentText) {
        showToast('Required', 'Please enter your comment or audit note', 'warning');
        return;
    }

    btn.disabled = true;
    btn.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> Saving...`;

    try {
        const payload = {
            authorName: currentUser?.fullName || 'Admissions Administrator',
            authorRole: currentUser?.userType === 'ADMIN' ? 'Management / Admin' : 'Staff',
            interactionType,
            commentText,
            newStatus: newStatus || null
        };

        const res = await fetch(`${API_BASE}/api/admin/admissions/applications/${appId}/comments`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(payload)
        });

        const d = await res.json();
        if (d.success) {
            document.getElementById('app-comment-text').value = '';
            showToast('Audit Logged', 'Comment recorded to application interaction timeline.', 'success');
            await loadApplicationComments(appId);
            await loadApplications();
        } else {
            showToast('Error', d.message || 'Failed to add comment', 'error');
        }
    } catch (e) {
        showToast('Error', e.message, 'error');
    } finally {
        btn.disabled = false;
        btn.innerHTML = `<i class="fa-solid fa-paper-plane"></i> Post Comment to History`;
    }
}

// ==============================================================
// INQUIRY COMMENT & INTERACTION TIMELINE CONTROLLER
// ==============================================================

async function openInquiryHistoryModal(inquiryId) {
    try {
        const resInq = await fetch(`${API_BASE}/api/admin/admissions/inquiries/${inquiryId}`, { headers: getAuthHeaders() });
        const dInq = await resInq.json();
        if (!dInq.success || !dInq.data) {
            showToast('Error', 'Unable to retrieve inquiry details', 'error');
            return;
        }

        const inq = dInq.data;
        document.getElementById('inq-comment-inquiry-id').value = inq.id;
        document.getElementById('inq-hist-candidate').textContent = inq.candidateName;
        document.getElementById('inq-hist-grade').textContent = inq.targetGrade;
        document.getElementById('inq-hist-parent').textContent = inq.parentName;
        document.getElementById('inq-hist-phone').textContent = inq.contactPhone;

        const statusPill = document.getElementById('inq-hist-status');
        statusPill.textContent = inq.status;
        statusPill.className = `badge ${inq.status === 'CONTACTED' ? 'badge-success' : (inq.status === 'QUALIFIED' ? 'badge-purple' : 'badge-warning')}`;

        document.getElementById('inq-comment-stage').value = inq.status || 'CONTACTED';
        document.getElementById('form-inquiry-comment').reset();
        document.getElementById('inq-comment-inquiry-id').value = inq.id;

        openModal('modal-inquiry-history');
        await loadInquiryComments(inquiryId);
    } catch (e) {
        showToast('Error', e.message, 'error');
    }
}

async function loadInquiryComments(inquiryId) {
    const feed = document.getElementById('inquiry-timeline-feed');
    const pill = document.getElementById('inq-comments-count-pill');
    feed.innerHTML = `<div style="text-align: center; color: #94a3b8; padding: 16px;"><i class="fa-solid fa-spinner fa-spin"></i> Loading interaction history...</div>`;

    try {
        const res = await fetch(`${API_BASE}/api/admin/admissions/inquiries/${inquiryId}/comments`, { headers: getAuthHeaders() });
        const d = await res.json();
        const comments = d.data || [];

        pill.textContent = `${comments.length} entries`;

        if (comments.length === 0) {
            feed.innerHTML = `
                <div style="text-align: center; color: #64748b; padding: 24px 12px; background: #f8fafc; border-radius: 8px;">
                    <i class="fa-regular fa-comment-dots" style="font-size: 2rem; color: #cbd5e1; margin-bottom: 8px;"></i>
                    <p style="margin: 0; font-size: 0.88rem; font-weight: 600;">No prior interaction notes logged for this candidate.</p>
                    <small style="color: #94a3b8;">Use the form above to record counselor phone calls or campus visits.</small>
                </div>
            `;
            return;
        }

        feed.innerHTML = comments.map(c => {
            let dotClass = 'dot-system';
            let dotIcon = 'fa-comment';
            let categoryLabel = 'General Note';

            if (c.interactionType === 'PHONE_CALL') {
                dotClass = 'dot-phone';
                dotIcon = 'fa-phone';
                categoryLabel = 'Phone Discussion';
            } else if (c.interactionType === 'CAMPUS_VISIT') {
                dotClass = 'dot-visit';
                dotIcon = 'fa-school';
                categoryLabel = 'Campus Visit';
            } else if (c.interactionType === 'DOCUMENT_FOLLOWUP') {
                dotClass = 'dot-doc';
                dotIcon = 'fa-file-invoice';
                categoryLabel = 'Document Follow-up';
            } else if (c.interactionType === 'ASSESSMENT') {
                dotClass = 'dot-assessment';
                dotIcon = 'fa-pen-clip';
                categoryLabel = 'Assessment';
            } else if (c.interactionType === 'FEE_DISCUSSION') {
                dotClass = 'dot-visit';
                dotIcon = 'fa-indian-rupee-sign';
                categoryLabel = 'Fee Discussion';
            }

            const timeStr = c.createdAt ? new Date(c.createdAt).toLocaleString('en-IN', {
                month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit'
            }) : 'Recent';

            return `
                <div class="timeline-item">
                    <div class="timeline-dot ${dotClass}">
                        <i class="fa-solid ${dotIcon}"></i>
                    </div>
                    <div class="timeline-card">
                        <div class="timeline-meta">
                            <span class="timeline-author">
                                <i class="fa-solid fa-user-tie" style="color: #64748b; font-size: 0.8rem;"></i> ${escapeHtml(c.authorName)}
                                ${c.authorRole ? `<small style="font-weight: 500; color: #64748b;">(${escapeHtml(c.authorRole)})</small>` : ''}
                            </span>
                            <div style="display: flex; align-items: center; gap: 8px;">
                                <span class="badge badge-primary" style="font-size: 0.72rem; padding: 2px 8px;">${categoryLabel}</span>
                                ${c.stage ? `<span class="badge ${c.stage === 'CONTACTED' ? 'badge-success' : 'badge-warning'}" style="font-size: 0.72rem; padding: 2px 8px;">${c.stage}</span>` : ''}
                                <span class="timeline-time">${timeStr}</span>
                            </div>
                        </div>
                        <p class="timeline-text">${escapeHtml(c.commentText)}</p>
                    </div>
                </div>
            `;
        }).join('');
    } catch (e) {
        feed.innerHTML = `<div style="text-align: center; color: #ef4444; padding: 16px;">Failed to load history: ${e.message}</div>`;
    }
}

async function handleAddInquiryComment(event) {
    event.preventDefault();
    const inquiryId = document.getElementById('inq-comment-inquiry-id').value;
    const interactionType = document.getElementById('inq-comment-type').value;
    const stage = document.getElementById('inq-comment-stage').value;
    const commentText = document.getElementById('inq-comment-text').value.trim();
    const btn = document.getElementById('btn-post-comment');

    if (!commentText) {
        showToast('Required', 'Please enter your interaction note', 'warning');
        return;
    }

    btn.disabled = true;
    btn.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> Saving...`;

    try {
        const payload = {
            authorName: currentUser?.fullName || 'Admissions Counselor',
            authorRole: currentUser?.userType === 'ADMIN' ? 'Administrator' : 'Staff',
            interactionType,
            stage,
            commentText
        };

        const res = await fetch(`${API_BASE}/api/admin/admissions/inquiries/${inquiryId}/comments`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(payload)
        });

        const d = await res.json();
        if (d.success) {
            document.getElementById('inq-comment-text').value = '';
            showToast('Note Recorded', 'Interaction added to student inquiry history.', 'success');
            await loadInquiryComments(inquiryId);
            await loadAdmissions();
        } else {
            showToast('Error', d.message || 'Failed to add comment', 'error');
        }
    } catch (e) {
        showToast('Error', e.message, 'error');
    } finally {
        btn.disabled = false;
        btn.innerHTML = `<i class="fa-solid fa-paper-plane"></i> Log Interaction Note`;
    }
}

async function handleApplyLeave(event) {
    event.preventDefault();
    const isTeacher = currentUser?.userType === 'TEACHER';
    const payload = {
        applicantId: currentUser?.studentId || currentUser?.employeeId || 1,
        applicantName: currentUser?.fullName || 'Aarav Kumar',
        applicantType: isTeacher ? 'FACULTY' : 'STUDENT',
        leaveType: document.getElementById('leave-type').value,
        startDate: document.getElementById('leave-start-date').value,
        endDate: document.getElementById('leave-end-date').value,
        reason: document.getElementById('leave-reason').value
    };

    const res = await fetch(`${API_BASE}/api/finance/leaves`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(payload)
    });
    const d = await res.json();
    if (d.success) {
        closeModal('modal-apply-leave');
        showToast("Leave Submitted", "Leave request submitted for Management authorization.", "success");
        if (currentUser?.userType === 'ADMIN') loadLeaves();
        if (currentUser?.userType === 'TEACHER') loadTeacherLeaves();
        if (currentUser?.userType === 'STUDENT') loadStudentLeaves();
    }
}

async function handleCreateTicket(event) {
    event.preventDefault();
    const payload = {
        title: document.getElementById('ticket-title').value,
        category: document.getElementById('ticket-category').value,
        priority: document.getElementById('ticket-priority').value,
        description: document.getElementById('ticket-description').value,
        reportedBy: currentUser?.fullName ? `${currentUser.fullName} (${currentUser.userType})` : 'Administrative Staff',
        reporterContact: currentUser?.email || '+91-9801445566'
    };

    const res = await fetch(`${API_BASE}/api/communication/tickets`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(payload)
    });
    const d = await res.json();
    if (d.success) {
        closeModal('modal-create-ticket');
        showToast("Ticket Created", `Campus Service Ticket #${d.data.ticketNumber} created (SLA: ${d.data.slaHours}h)`, "success");
        if (currentUser?.userType === 'ADMIN') loadTickets();
    }
}

function switchSubtab(subtabId, btn) {
    if (!btn) return;
    const parent = btn.closest('.card-body') || btn.parentElement.parentElement;
    parent.querySelectorAll('.tabs-subnav .subtab-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');

    const targetId = subtabId.startsWith('subtab-') ? subtabId : `subtab-${subtabId}`;
    const subtabs = parent.querySelectorAll('[id^="subtab-"]');
    subtabs.forEach(tab => tab.style.display = 'none');
    const target = document.getElementById(targetId);
    if (target) {
        target.style.display = 'block';
    } else {
        console.warn('Subtab container not found:', targetId);
    }
}

function openModal(modalId) {
    document.getElementById(modalId).classList.add('active');
}

function closeModal(modalId) {
    document.getElementById(modalId).classList.remove('active');
}
