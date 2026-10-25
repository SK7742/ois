const fs = require('fs');
const path = 'C:/Users/DELL/.gemini/antigravity/scratch/oxford-csms/backend/src/main/resources/static/app.js';
let content = fs.readFileSync(path, 'utf8');

// Update loadApplications
const oldLoadAppsBlock = `        if (dataApps.data && dataApps.data.length > 0) {
            tbodyApps.innerHTML = dataApps.data.map(app => {
                const isDifferentClass = app.appliedGrade && app.appliedGrade !== app.targetGrade;
                const safeName = (app.studentName || '').replace(/'/g, "\\\\'");
                const appliedGrade = app.appliedGrade || app.targetGrade;
                const targetGrade = app.targetGrade || 'Class 10';
                const assignedSec = app.assignedSection || 'A';
                const isActive = app.active !== false;

                return \`
                <tr style="\${!isActive ? 'opacity: 0.65; background-color: #f8fafc;' : ''}">
                    <td><code>\${app.applicationNumber}</code></td>
                    <td>
                        <strong>\${escapeHtml(app.studentName)}</strong><br>
                        <small class="text-secondary">DOB: \${app.dateOfBirth} (\${app.gender})</small>
                    </td>
                    <td>
                        <span class="badge badge-secondary">\${escapeHtml(appliedGrade)}</span>
                    </td>
                    <td>
                        \${isDifferentClass ? \`
                            <div style="display: flex; flex-direction: column; gap: 3px;">
                                <span class="badge badge-class-adjusted" title="Recommended to lower/different grade by management">
                                    <i class="fa-solid fa-graduation-cap"></i> \${escapeHtml(targetGrade)} - \${escapeHtml(assignedSec)}
                                </span>
                                <small class="text-secondary" style="font-size: 0.73rem; max-width: 170px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" title="\${escapeHtml(app.classChangeReason || '')}">
                                    <i class="fa-solid fa-note-sticky"></i> \${escapeHtml(app.classChangeReason || 'Placement adjustment')}
                                </small>
                            </div>
                        \` : \`
                            <span class="badge badge-primary">\${escapeHtml(targetGrade)} - \${escapeHtml(assignedSec)}</span>
                        \`}
                    </td>
                    <td>
                        \${escapeHtml(app.fatherName)}<br>
                        <small class="text-secondary"><i class="fa-solid fa-phone"></i> \${escapeHtml(app.contactPhone)}</small>
                    </td>
                    <td>
                        <span class="badge \${app.documentsVerified ? 'badge-success' : 'badge-warning'}">
                            \${app.documentsVerified ? '<i class="fa-solid fa-circle-check"></i> Verified' : '<i class="fa-solid fa-clock"></i> Pending'}
                        </span>
                    </td>
                    <td>
                        <span class="badge \${app.status === 'ENROLLED' ? 'badge-purple' : (app.status === 'INACTIVE' ? 'badge-inactive' : 'badge-primary')}">
                            \${app.status}
                        </span>
                    </td>
                    <td>
                        \${isActive ? 
                            '<span class="badge badge-active"><i class="fa-solid fa-circle-dot" style="font-size: 0.6rem;"></i> Active</span>' : 
                            '<span class="badge badge-inactive"><i class="fa-solid fa-ban"></i> Inactive</span>'
                        }
                    </td>
                    <td>
                        <div style="display: flex; gap: 6px; flex-wrap: wrap;">
                            <button class="btn btn-outline btn-sm" onclick="openApplicationHistoryModal(\${app.id})" style="border-color: #2563eb; color: #2563eb;" title="Interaction history & audit comments">
                                <i class="fa-solid fa-clock-rotate-left"></i> History
                            </button>
                            \${isActive ? \`
                                <button class="btn btn-outline btn-sm" onclick="openAdjustClassModal(\${app.id}, '\${safeName}', '\${appliedGrade}', '\${targetGrade}', '\${assignedSec}')" style="border-color: #d97706; color: #d97706;" title="Adjust Class or Section with mandatory note">
                                    <i class="fa-solid fa-graduation-cap"></i> Adjust Class
                                </button>
                                \${app.status === 'ENROLLED' ? 
                                    \`<span class="badge badge-success"><i class="fa-solid fa-check"></i> Enrolled (#\${app.enrolledStudentId})</span>\` :
                                    \`<button class="btn btn-primary btn-sm" onclick="enrollCandidate(\${app.id}, '\${targetGrade}', '\${assignedSec}')">
                                        <i class="fa-solid fa-user-check"></i> Enroll
                                     </button>\`
                                }
                            \` : ''}
                            <button class="btn btn-outline btn-sm" onclick="toggleApplicationActive(\${app.id}, \${isActive})" style="border-color: \${isActive ? '#ef4444' : '#10b981'}; color: \${isActive ? '#ef4444' : '#10b981'};" title="\${isActive ? 'Mark Inactive' : 'Reactivate application'}">
                                <i class="fa-solid \${isActive ? 'fa-ban' : 'fa-rotate-left'}"></i> \${isActive ? 'Inactivate' : 'Reactivate'}
                            </button>
                        </div>
                    </td>
                </tr>
            \`;}).join('');
        } else {
            tbodyApps.innerHTML = \`<tr><td colspan="9" class="text-center text-secondary" style="padding: 24px;">No submitted applications match the selected filter criteria.</td></tr>\`;
        }`;

const newLoadAppsBlock = `        const apps = dataApps.data || [];
        renderOrUpdateDataTable('admin-applications', {
            tbodyId: 'applications-table-body',
            tableId: 'applications-table',
            data: apps,
            enableSearch: false,
            defaultSortKey: 'applicationNumber',
            renderRow: (app) => {
                const isDifferentClass = app.appliedGrade && app.appliedGrade !== app.targetGrade;
                const safeName = (app.studentName || '').replace(/'/g, "\\\\'");
                const appliedGrade = app.appliedGrade || app.targetGrade;
                const targetGrade = app.targetGrade || 'Class 10';
                const assignedSec = app.assignedSection || 'A';
                const isActive = app.active !== false;

                return \`
                <tr style="\${!isActive ? 'opacity: 0.65; background-color: #f8fafc;' : ''}">
                    <td><code>\${escapeHtml(app.applicationNumber)}</code></td>
                    <td>
                        <strong>\${escapeHtml(app.studentName)}</strong><br>
                        <small class="text-secondary">DOB: \${escapeHtml(app.dateOfBirth)} (\${escapeHtml(app.gender)})</small>
                    </td>
                    <td>
                        <span class="badge badge-secondary">\${escapeHtml(appliedGrade)}</span>
                    </td>
                    <td>
                        \${isDifferentClass ? \`
                            <div style="display: flex; flex-direction: column; gap: 3px;">
                                <span class="badge badge-class-adjusted" title="Recommended to lower/different grade by management">
                                    <i class="fa-solid fa-graduation-cap"></i> \${escapeHtml(targetGrade)} - \${escapeHtml(assignedSec)}
                                </span>
                                <small class="text-secondary" style="font-size: 0.73rem; max-width: 170px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" title="\${escapeHtml(app.classChangeReason || '')}">
                                    <i class="fa-solid fa-note-sticky"></i> \${escapeHtml(app.classChangeReason || 'Placement adjustment')}
                                </small>
                            </div>
                        \` : \`
                            <span class="badge badge-primary">\${escapeHtml(targetGrade)} - \${escapeHtml(assignedSec)}</span>
                        \`}
                    </td>
                    <td>
                        \${escapeHtml(app.fatherName)}<br>
                        <small class="text-secondary"><i class="fa-solid fa-phone"></i> \${escapeHtml(app.contactPhone)}</small>
                    </td>
                    <td>
                        <span class="badge \${app.documentsVerified ? 'badge-success' : 'badge-warning'}">
                            \${app.documentsVerified ? '<i class="fa-solid fa-circle-check"></i> Verified' : '<i class="fa-solid fa-clock"></i> Pending'}
                        </span>
                    </td>
                    <td>
                        <span class="badge \${app.status === 'ENROLLED' ? 'badge-purple' : (app.status === 'INACTIVE' ? 'badge-inactive' : 'badge-primary')}">
                            \${escapeHtml(app.status)}
                        </span>
                    </td>
                    <td>
                        \${isActive ? 
                            '<span class="badge badge-active"><i class="fa-solid fa-circle-dot" style="font-size: 0.6rem;"></i> Active</span>' : 
                            '<span class="badge badge-inactive"><i class="fa-solid fa-ban"></i> Inactive</span>'
                        }
                    </td>
                    <td>
                        <div style="display: flex; gap: 6px; flex-wrap: wrap;">
                            <button class="btn btn-outline btn-sm" onclick="openApplicationHistoryModal(\${app.id})" style="border-color: #2563eb; color: #2563eb;" title="Interaction history & audit comments">
                                <i class="fa-solid fa-clock-rotate-left"></i> History
                            </button>
                            \${isActive ? \`
                                <button class="btn btn-outline btn-sm" onclick="openAdjustClassModal(\${app.id}, '\${safeName}', '\${appliedGrade}', '\${targetGrade}', '\${assignedSec}')" style="border-color: #d97706; color: #d97706;" title="Adjust Class or Section with mandatory note">
                                    <i class="fa-solid fa-graduation-cap"></i> Adjust Class
                                </button>
                                \${app.status === 'ENROLLED' ? 
                                    \`<span class="badge badge-success"><i class="fa-solid fa-check"></i> Enrolled (#\${app.enrolledStudentId})</span>\` :
                                    \`<button class="btn btn-primary btn-sm" onclick="enrollCandidate(\${app.id}, '\${targetGrade}', '\${assignedSec}')">
                                        <i class="fa-solid fa-user-check"></i> Enroll
                                     </button>\`
                                }
                            \` : ''}
                            <button class="btn btn-outline btn-sm" onclick="toggleApplicationActive(\${app.id}, \${isActive})" style="border-color: \${isActive ? '#ef4444' : '#10b981'}; color: \${isActive ? '#ef4444' : '#10b981'};" title="\${isActive ? 'Mark Inactive' : 'Reactivate application'}">
                                <i class="fa-solid \${isActive ? 'fa-ban' : 'fa-rotate-left'}"></i> \${isActive ? 'Inactivate' : 'Reactivate'}
                            </button>
                        </div>
                    </td>
                </tr>
            \`;}
        });`;

content = content.replace(oldLoadAppsBlock, () => newLoadAppsBlock);

// Update renderAttendanceTableRows to also support quick search by student
const oldRenderAttRowsRegex = /function renderAttendanceTableRows\(\) \{[\s\S]*?tbody\.innerHTML = currentAttendanceStudents\.map[\s\S]*?\}\)\.join\(''\);\s*\}/;
const newRenderAttRows = `function renderAttendanceTableRows() {
    renderOrUpdateDataTable('teacher-attendance', {
        tbodyId: 'attendance-table-body',
        tableId: 'attendance-register-table',
        data: currentAttendanceStudents,
        enablePagination: false,
        placeholder: 'Search student in register...',
        searchFields: ['fullName', 'rollNo', 'admissionNo'],
        defaultSortKey: 'rollNo',
        renderRow: (s) => {
            const isP = s.status === 'PRESENT' ? 'active-p' : '';
            const isA = s.status === 'ABSENT' ? 'active-a' : '';
            const isL = s.status === 'LATE' ? 'active-l' : '';

            return \`
                <tr data-student-id="\${s.id}">
                    <td>
                        <div style="font-weight: 600; font-size: 0.95rem;">\${escapeHtml(s.fullName)}</div>
                        <div style="font-size: 0.8rem; color: #64748b;">
                            Roll #\${escapeHtml(s.rollNo || '-')} &bull; \${escapeHtml(s.admissionNo)}
                        </div>
                    </td>
                    <td style="text-align: center;">
                        <div class="att-toggle-group">
                            <button type="button" class="att-btn \${isP}" onclick="setAttendanceStatus(\${s.id}, 'PRESENT')">
                                <i class="fa-solid fa-check"></i> Present
                            </button>
                            <button type="button" class="att-btn \${isA}" onclick="setAttendanceStatus(\${s.id}, 'ABSENT')">
                                <i class="fa-solid fa-xmark"></i> Absent
                            </button>
                            <button type="button" class="att-btn \${isL}" onclick="setAttendanceStatus(\${s.id}, 'LATE')">
                                <i class="fa-solid fa-clock"></i> Late
                            </button>
                        </div>
                    </td>
                    <td>
                        <input type="text" 
                               class="att-remark-input" 
                               data-student-id="\${s.id}" 
                               placeholder="Optional remark..." 
                               value="\${escapeHtml(s.remarks || '')}"
                               onchange="setAttendanceRemarks(\${s.id}, this.value)"
                               style="width: 100%; max-width: 280px; padding: 6px 10px; border: 1px solid #cbd5e1; border-radius: 4px; font-size: 0.85rem;">
                    </td>
                </tr>
            \`;
        }
    });
}`;

content = content.replace(oldRenderAttRowsRegex, () => newRenderAttRows);

fs.writeFileSync(path, content, 'utf8');
console.log('Successfully enhanced applications and attendance table rows!');
