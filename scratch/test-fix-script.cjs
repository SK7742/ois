const fs = require('fs');
const path = 'C:/Users/DELL/.gemini/antigravity/scratch/oxford-csms/backend/src/main/resources/static/app.js';
let content = fs.readFileSync(path, 'utf8');

const datatableCoreCode = `
// ==============================================================
// RESPONSIVE MOBILE DRAWER & AUTO-SCALING NAVIGATION
// ==============================================================
function toggleMobileSidebar(force) {
    const sidebar = document.querySelector('.sidebar');
    const backdrop = document.getElementById('sidebar-backdrop');
    if (!sidebar) return;
    const isCurrentlyOpen = sidebar.classList.contains('mobile-open');
    const shouldOpen = typeof force === 'boolean' ? force : !isCurrentlyOpen;
    if (shouldOpen) {
        sidebar.classList.add('mobile-open');
        if (backdrop) backdrop.classList.add('active');
        document.body.classList.add('mobile-sidebar-open');
    } else {
        sidebar.classList.remove('mobile-open');
        if (backdrop) backdrop.classList.remove('active');
        document.body.classList.remove('mobile-sidebar-open');
    }
}

// ==============================================================
// ENTERPRISE DATA TABLE ENGINE (SEARCH, SORT, FILTER, PAGINATION)
// ==============================================================
function compareTableValues(a, b, type, dir) {
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

const activeDataTables = new Map();

class OxfordDataTable {
    constructor(config) {
        this.id = config.id || ('dt_' + Math.random().toString(36).substring(2, 9));
        this.tableId = config.tableId;
        this.tbodyId = config.tbodyId;
        this.rawItems = Array.isArray(config.data) ? [...config.data] : [];
        this.renderRow = config.renderRow;
        this.searchFields = config.searchFields || [];
        this.filterDefs = config.filters || [];
        this.pageSize = config.pageSize || 10;
        this.pageSizeOptions = config.pageSizeOptions || [10, 25, 50, 100];
        this.currentPage = config.initialPage || 1;
        this.sortKey = config.defaultSortKey || null;
        this.sortDir = config.defaultSortDir || 'asc';
        this.columnTypes = config.columnTypes || {};
        this.emptyMessage = config.emptyMessage || 'No matching records found.';
        this.placeholder = config.placeholder || 'Search records...';
        this.enableSearch = config.enableSearch !== false;
        this.enablePagination = config.enablePagination !== false;
        this.enableSorting = config.enableSorting !== false;
        this.wrapperClass = config.wrapperClass || '';
        this.onRender = config.onRender || null;

        this.searchQuery = '';
        this.activeFilters = {};
        this.eventsBound = false;

        this.findElements();
        if (!this.tbodyEl) {
            console.warn('OxfordDataTable: tbody not found for', this.id);
            return;
        }

        this.initStructure();
        this.bindEvents();
        this.apply();
    }

    findElements() {
        if (this.tbodyId) {
            this.tbodyEl = document.getElementById(this.tbodyId);
        }
        if (this.tableId) {
            this.tableEl = document.getElementById(this.tableId);
        }
        if (!this.tableEl && this.tbodyEl) {
            this.tableEl = this.tbodyEl.closest('table');
        }
        if (!this.tbodyEl && this.tableEl) {
            this.tbodyEl = this.tableEl.querySelector('tbody');
        }
    }

    initStructure() {
        if (!this.tableEl) return;
        
        let wrapper = this.tableEl.closest('.dt-wrapper');
        const containerToWrap = this.tableEl.closest('.table-responsive') || this.tableEl.closest('.table-container') || this.tableEl;
        
        if (!wrapper) {
            wrapper = document.createElement('div');
            wrapper.className = ('dt-wrapper ' + this.wrapperClass).trim();
            wrapper.id = this.id + '-dt-wrapper';
            
            const parent = containerToWrap.parentNode;
            parent.insertBefore(wrapper, containerToWrap);
            
            const toolbar = document.createElement('div');
            toolbar.className = 'dt-toolbar';
            toolbar.id = this.id + '-dt-toolbar';
            wrapper.appendChild(toolbar);
            
            wrapper.appendChild(containerToWrap);
            
            const paginationBar = document.createElement('div');
            paginationBar.className = 'dt-pagination-bar';
            paginationBar.id = this.id + '-dt-pagination';
            wrapper.appendChild(paginationBar);
            
            this.wrapperEl = wrapper;
            this.toolbarEl = toolbar;
            this.paginationBarEl = paginationBar;
        } else {
            this.wrapperEl = wrapper;
            this.toolbarEl = wrapper.querySelector('.dt-toolbar');
            this.paginationBarEl = wrapper.querySelector('.dt-pagination-bar');
        }
        
        this.renderToolbar();
        this.setupSortableHeaders();
    }

    renderToolbar() {
        if (!this.toolbarEl) return;
        
        let leftHtml = '';
        if (this.enableSearch) {
            leftHtml += '<div class="dt-search-box">' +
                '<i class="fa-solid fa-magnifying-glass"></i>' +
                '<input type="text" class="dt-search-input" id="' + this.id + '-search" placeholder="' + escapeHtml(this.placeholder) + '" value="' + escapeHtml(this.searchQuery) + '">' +
                '<button type="button" class="dt-search-clear" id="' + this.id + '-search-clear" style="display: ' + (this.searchQuery ? 'block' : 'none') + ';" title="Clear Search">' +
                    '<i class="fa-solid fa-xmark"></i>' +
                '</button>' +
            '</div>';
        }

        if (this.filterDefs && this.filterDefs.length > 0) {
            this.filterDefs.forEach(f => {
                let options = f.options;
                if (options === 'auto' && f.key) {
                    const rawVals = [...new Set(this.rawItems.map(item => item[f.key]).filter(v => v !== null && v !== undefined && v !== ''))].sort();
                    options = [{ value: '', label: f.label || 'All' }, ...rawVals.map(v => ({ value: String(v), label: String(v) }))];
                }
                leftHtml += '<select class="dt-filter-select" id="' + this.id + '-filter-' + f.id + '" title="' + escapeHtml(f.label || 'Filter') + '">' +
                    (options || []).map(opt => '<option value="' + escapeHtml(opt.value) + '" ' + (this.activeFilters[f.id] === opt.value ? 'selected' : '') + '>' +
                        escapeHtml(opt.label) +
                    '</option>').join('') +
                '</select>';
            });
        }

        let rightHtml = '';
        if (this.enablePagination) {
            rightHtml += '<div class="dt-page-size-selector">' +
                '<label for="' + this.id + '-page-size">Show</label>' +
                '<select class="dt-filter-select" id="' + this.id + '-page-size">' +
                    this.pageSizeOptions.map(sz => '<option value="' + sz + '" ' + (this.pageSize === sz ? 'selected' : '') + '>' + sz + '</option>').join('') +
                '</select>' +
                '<span>entries</span>' +
            '</div>';
        }

        this.toolbarEl.innerHTML = '<div class="dt-toolbar-left">' + leftHtml + '</div>' +
            '<div class="dt-toolbar-right">' + rightHtml + '</div>';
    }

    setupSortableHeaders() {
        if (!this.enableSorting || !this.tableEl) return;
        const thead = this.tableEl.querySelector('thead');
        if (!thead) return;

        const ths = thead.querySelectorAll('th');
        ths.forEach(th => {
            const sortKey = th.getAttribute('data-sort');
            if (sortKey) {
                th.classList.add('dt-sortable');
                if (!th.querySelector('.dt-sort-icon')) {
                    const icon = document.createElement('i');
                    icon.className = 'fa-solid fa-sort dt-sort-icon';
                    th.appendChild(icon);
                }
                
                if (!th._dtSortBound) {
                    th._dtSortBound = true;
                    th.addEventListener('click', (e) => {
                        if (e.target.closest('button, input, select')) return;
                        
                        const key = th.getAttribute('data-sort');
                        const colType = th.getAttribute('data-sort-type') || this.columnTypes[key] || 'string';
                        this.columnTypes[key] = colType;
                        
                        if (this.sortKey === key) {
                            this.sortDir = this.sortDir === 'asc' ? 'desc' : 'asc';
                        } else {
                            this.sortKey = key;
                            this.sortDir = 'asc';
                        }
                        this.currentPage = 1;
                        this.apply();
                    });
                }
            }
        });
        this.updateHeaderSortIndicators();
    }

    updateHeaderSortIndicators() {
        if (!this.tableEl) return;
        const thead = this.tableEl.querySelector('thead');
        if (!thead) return;

        const ths = thead.querySelectorAll('th.dt-sortable');
        ths.forEach(th => {
            const sortKey = th.getAttribute('data-sort');
            const icon = th.querySelector('.dt-sort-icon');
            if (sortKey === this.sortKey) {
                if (this.sortDir === 'asc') {
                    th.classList.add('sorted-asc');
                    th.classList.remove('sorted-desc');
                    if (icon) icon.className = 'fa-solid fa-sort-up dt-sort-icon';
                } else {
                    th.classList.add('sorted-desc');
                    th.classList.remove('sorted-asc');
                    if (icon) icon.className = 'fa-solid fa-sort-down dt-sort-icon';
                }
            } else {
                th.classList.remove('sorted-asc', 'sorted-desc');
                if (icon) icon.className = 'fa-solid fa-sort dt-sort-icon';
            }
        });
    }

    bindEvents() {
        if (this.eventsBound) return;
        this.eventsBound = true;

        if (this.toolbarEl) {
            this.toolbarEl.addEventListener('input', (e) => {
                if (e.target && e.target.classList.contains('dt-search-input')) {
                    this.searchQuery = e.target.value;
                    const clearBtn = this.toolbarEl.querySelector('.dt-search-clear');
                    if (clearBtn) clearBtn.style.display = this.searchQuery ? 'block' : 'none';
                    this.currentPage = 1;
                    this.apply();
                }
            });

            this.toolbarEl.addEventListener('click', (e) => {
                const clearBtn = e.target.closest('.dt-search-clear');
                if (clearBtn) {
                    this.searchQuery = '';
                    const searchInput = this.toolbarEl.querySelector('.dt-search-input');
                    if (searchInput) searchInput.value = '';
                    clearBtn.style.display = 'none';
                    this.currentPage = 1;
                    this.apply();
                }
            });

            this.toolbarEl.addEventListener('change', (e) => {
                if (e.target && e.target.id === this.id + '-page-size') {
                    this.pageSize = parseInt(e.target.value, 10);
                    this.currentPage = 1;
                    this.apply();
                    return;
                }
                
                if (e.target && e.target.classList.contains('dt-filter-select')) {
                    const prefix = this.id + '-filter-';
                    if (e.target.id.startsWith(prefix)) {
                        const filterId = e.target.id.substring(prefix.length);
                        this.activeFilters[filterId] = e.target.value;
                        this.currentPage = 1;
                        this.apply();
                    }
                }
            });
        }

        if (this.paginationBarEl) {
            this.paginationBarEl.addEventListener('click', (e) => {
                const pageBtn = e.target.closest('.dt-page-btn');
                if (pageBtn && !pageBtn.disabled && pageBtn.dataset.page) {
                    const page = parseInt(pageBtn.dataset.page, 10);
                    if (!isNaN(page) && page !== this.currentPage) {
                        this.currentPage = page;
                        this.apply();
                    }
                }
            });
        }
    }

    update(config) {
        if (config.data) {
            this.rawItems = [...config.data];
        }
        if (config.renderRow) {
            this.renderRow = config.renderRow;
        }
        if (config.filters) {
            this.filterDefs = config.filters;
            this.renderToolbar();
        }
        if (config.emptyMessage) {
            this.emptyMessage = config.emptyMessage;
        }
        this.setupSortableHeaders();
        this.apply();
    }

    apply() {
        let items = [...this.rawItems];

        // 1. Filter
        if (this.filterDefs && this.filterDefs.length > 0) {
            this.filterDefs.forEach(f => {
                const val = this.activeFilters[f.id];
                if (val !== undefined && val !== null && val !== '') {
                    if (typeof f.match === 'function') {
                        items = items.filter(item => f.match(item, val));
                    } else if (f.key) {
                        items = items.filter(item => String(item[f.key]) === String(val));
                    }
                }
            });
        }

        // 2. Search
        if (this.searchQuery && this.searchQuery.trim()) {
            const terms = this.searchQuery.toLowerCase().trim().split(/\\s+/).filter(Boolean);
            items = items.filter(item => {
                let text = '';
                if (this.searchFields && this.searchFields.length > 0) {
                    text = this.searchFields.map(field => {
                        return typeof field === 'function' ? field(item) : (item[field] != null ? String(item[field]) : '');
                    }).join(' ').toLowerCase();
                } else {
                    text = Object.values(item).map(v => (v != null && typeof v !== 'object') ? String(v) : '').join(' ').toLowerCase();
                }
                return terms.every(term => text.includes(term));
            });
        }

        // 3. Sort
        if (this.sortKey) {
            const key = this.sortKey;
            const dir = this.sortDir;
            const type = this.columnTypes[key] || 'string';
            items.sort((a, b) => {
                const valA = typeof key === 'function' ? key(a) : a[key];
                const valB = typeof key === 'function' ? key(b) : b[key];
                return compareTableValues(valA, valB, type, dir);
            });
        }

        // 4. Paginate
        const total = items.length;
        let pagedItems = items;
        let start = total > 0 ? 1 : 0;
        let end = total;
        let totalPages = 1;

        if (this.enablePagination) {
            totalPages = Math.max(1, Math.ceil(total / this.pageSize));
            if (this.currentPage > totalPages) this.currentPage = totalPages;
            if (this.currentPage < 1) this.currentPage = 1;
            start = total > 0 ? (this.currentPage - 1) * this.pageSize + 1 : 0;
            end = Math.min((this.currentPage - 1) * this.pageSize + this.pageSize, total);
            pagedItems = items.slice(start - 1, end);
        }

        // 5. Render tbody
        if (this.tbodyEl) {
            if (total === 0) {
                const colCount = (this.tableEl ? this.tableEl.querySelectorAll('thead th').length : 8) || 8;
                this.tbodyEl.innerHTML = '<tr><td colspan="' + colCount + '" class="dt-empty-state"><i class="fa-solid fa-inbox"></i> ' + escapeHtml(this.emptyMessage) + '</td></tr>';
            } else {
                this.tbodyEl.innerHTML = pagedItems.map((item, idx) => {
                    return this.renderRow(item, (start - 1) + idx, this);
                }).join('');
            }
        }

        // 6. Render Pagination Bar
        this.renderPagination({ start, end, total, totalPages });

        // 7. Update header sort indicators
        this.updateHeaderSortIndicators();

        if (typeof this.onRender === 'function') {
            this.onRender(pagedItems);
        }
    }

    renderPagination(info) {
        if (!this.paginationBarEl) return;
        const { start, end, total, totalPages } = info;
        const isFiltered = total < this.rawItems.length;

        const infoHtml = total === 0
            ? 'Showing 0 entries'
            : 'Showing <strong>' + start + '</strong> to <strong>' + end + '</strong> of <strong>' + total + '</strong> entries' + (isFiltered ? ' <span class="text-secondary">(filtered from ' + this.rawItems.length + ' total)</span>' : '');

        let controlsHtml = '';
        if (this.enablePagination && totalPages > 1) {
            const range = getPaginationRange(this.currentPage, totalPages);
            controlsHtml += '<button type="button" class="dt-page-btn" ' + (this.currentPage === 1 ? 'disabled' : '') + ' data-page="1" title="First Page"><i class="fa-solid fa-angles-left"></i></button>';
            controlsHtml += '<button type="button" class="dt-page-btn" ' + (this.currentPage === 1 ? 'disabled' : '') + ' data-page="' + (this.currentPage - 1) + '" title="Previous Page"><i class="fa-solid fa-angle-left"></i></button>';

            range.forEach(p => {
                if (p === '...') {
                    controlsHtml += '<span class="dt-page-ellipsis">&hellip;</span>';
                } else {
                    controlsHtml += '<button type="button" class="dt-page-btn ' + (p === this.currentPage ? 'active' : '') + '" data-page="' + p + '">' + p + '</button>';
                }
            });

            controlsHtml += '<button type="button" class="dt-page-btn" ' + (this.currentPage === totalPages ? 'disabled' : '') + ' data-page="' + (this.currentPage + 1) + '" title="Next Page"><i class="fa-solid fa-angle-right"></i></button>';
            controlsHtml += '<button type="button" class="dt-page-btn" ' + (this.currentPage === totalPages ? 'disabled' : '') + ' data-page="' + totalPages + '" title="Last Page"><i class="fa-solid fa-angles-right"></i></button>';
        }

        this.paginationBarEl.innerHTML = '<div class="dt-info">' + infoHtml + '</div>' +
            '<div class="dt-pagination-controls">' + controlsHtml + '</div>';
    }
}

function renderOrUpdateDataTable(id, config) {
    let instance = activeDataTables.get(id);
    if (instance) {
        instance.update(config);
    } else {
        instance = new OxfordDataTable({ id, ...config });
        activeDataTables.set(id, instance);
    }
    return instance;
}
`;

// Insert OxfordDataTable before ROLE_CONFIGS
content = content.replace('// Role Configuration & Navigation Schemes', () => `${datatableCoreCode}\n// Role Configuration & Navigation Schemes`);

// Close drawer in navigateTo
content = content.replace('function navigateTo(tabId) {', () => `function navigateTo(tabId) {\n    toggleMobileSidebar(false);`);

// Update loadStudents
const oldLoadStudentsRegex = /async function loadStudents\(\) \{[\s\S]*?\}\s*(?=async function loadEmployees)/;
const newLoadStudents = `async function loadStudents() {
    const grade = document.getElementById('filter-student-class')?.value;
    let url = \`\${API_BASE}/api/admin/students\`;
    if (grade) url += \`?grade=\${encodeURIComponent(grade)}\`;

    try {
        const res = await fetch(url, { headers: getAuthHeaders() });
        const d = await res.json();
        const students = d.data || [];

        renderOrUpdateDataTable('admin-students', {
            tbodyId: 'students-table-body',
            tableId: 'students-table',
            data: students,
            defaultSortKey: 'admissionNo',
            searchFields: ['admissionNo', 'firstName', 'lastName', 'fullName', 'currentClass', 'section', 'guardianPhone', 'rollNo'],
            filters: [
                {
                    id: 'feeStatus',
                    label: 'All Fee Statuses',
                    options: [
                        { value: '', label: 'All Fee Statuses' },
                        { value: 'CLEARED', label: 'Cleared' },
                        { value: 'OVERDUE', label: 'Overdue' }
                    ],
                    match: (s, val) => !val || s.feeStatus === val
                },
                {
                    id: 'grade',
                    label: 'All Classes',
                    key: 'currentClass',
                    options: 'auto'
                }
            ],
            renderRow: (s) => \`
                <tr>
                    <td><strong>\${escapeHtml(s.admissionNo)}</strong></td>
                    <td><strong>\${escapeHtml(s.firstName)} \${escapeHtml(s.lastName)}</strong><br><small class="text-secondary">\${escapeHtml(s.gender || '')}</small></td>
                    <td><span class="badge badge-primary">\${escapeHtml(s.currentClass)} - \${escapeHtml(s.section)}</span></td>
                    <td>#\${escapeHtml(s.rollNo || '-')}</td>
                    <td>\${escapeHtml(s.guardianPhone || '')}</td>
                    <td><strong>\${s.attendanceRate || 95}%</strong></td>
                    <td><span class="badge \${s.feeStatus === 'CLEARED' ? 'badge-success' : 'badge-danger'}">\${escapeHtml(s.feeStatus)}</span></td>
                    <td>
                        <button class="btn btn-outline btn-sm" onclick="generateSampleReportCard(\${s.id})">
                            <i class="fa-solid fa-file-pdf"></i> Report Card
                        </button>
                    </td>
                </tr>
            \`
        });

        const invSelect = document.getElementById('inv-student-id');
        if (invSelect && students.length > 0) {
            invSelect.innerHTML = students.map(s => \`
                <option value="\${s.id}">\${escapeHtml(s.fullName || (s.firstName + ' ' + s.lastName))} (\${escapeHtml(s.admissionNo)}) - \${escapeHtml(s.currentClass)}</option>
            \`).join('');
        }
    } catch (e) {
        console.error("Student load error:", e);
    }
}
`;
content = content.replace(oldLoadStudentsRegex, () => newLoadStudents);

// Update loadEmployees
const oldLoadEmployeesRegex = /async function loadEmployees\(\) \{[\s\S]*?\}\s*(?=async function loadFees)/;
const newLoadEmployees = `async function loadEmployees() {
    try {
        const res = await fetch(\`\${API_BASE}/api/admin/employees\`, { headers: getAuthHeaders() });
        const d = await res.json();
        const employees = d.data || [];

        renderOrUpdateDataTable('admin-employees', {
            tbodyId: 'employees-table-body',
            tableId: 'employees-table',
            data: employees,
            defaultSortKey: 'employeeCode',
            searchFields: ['employeeCode', 'fullName', 'department', 'designation', 'qualification'],
            filters: [
                {
                    id: 'dept',
                    label: 'All Departments',
                    key: 'department',
                    options: 'auto'
                }
            ],
            renderRow: (e) => \`
                <tr>
                    <td><code>\${escapeHtml(e.employeeCode)}</code></td>
                    <td><strong>\${escapeHtml(e.fullName)}</strong></td>
                    <td>\${escapeHtml(e.department)}</td>
                    <td>\${escapeHtml(e.designation)}</td>
                    <td>\${escapeHtml(e.qualification || '-')}</td>
                    <td>₹ \${(Number(e.basicSalary) + Number(e.allowances)).toLocaleString()}</td>
                    <td><span class="badge badge-success">ACTIVE</span></td>
                </tr>
            \`
        });

        const resT = await fetch(\`\${API_BASE}/api/admin/employees/teaching\`, { headers: getAuthHeaders() });
        const dT = await resT.json();
        const teaching = dT.data || [];
        renderOrUpdateDataTable('admin-teaching', {
            tbodyId: 'teaching-table-body',
            tableId: 'teaching-table',
            data: teaching,
            defaultSortKey: 'employeeCode',
            searchFields: ['employeeCode', 'fullName', 'subjectSpecialization', 'assignedClass', 'email'],
            renderRow: (t) => \`
                <tr>
                    <td><code>\${escapeHtml(t.employeeCode)}</code></td>
                    <td><strong>\${escapeHtml(t.fullName)}</strong></td>
                    <td><span class="badge badge-primary">\${escapeHtml(t.subjectSpecialization)}</span></td>
                    <td>\${escapeHtml(t.assignedClass || 'General Faculty')}</td>
                    <td>\${t.weeklyTargetPeriods} periods/week</td>
                    <td><small>\${escapeHtml(t.email)}</small></td>
                </tr>
            \`
        });

        const resNT = await fetch(\`\${API_BASE}/api/admin/employees/non-teaching\`, { headers: getAuthHeaders() });
        const dNT = await resNT.json();
        const nonTeaching = dNT.data || [];
        renderOrUpdateDataTable('admin-non-teaching', {
            tbodyId: 'non-teaching-table-body',
            tableId: 'non-teaching-table',
            data: nonTeaching,
            defaultSortKey: 'employeeCode',
            searchFields: ['employeeCode', 'fullName', 'operationalDomain', 'phone'],
            renderRow: (nt) => \`
                <tr>
                    <td><code>\${escapeHtml(nt.employeeCode)}</code></td>
                    <td><strong>\${escapeHtml(nt.fullName)}</strong></td>
                    <td><span class="badge badge-purple">\${escapeHtml(nt.operationalDomain)}</span></td>
                    <td>\${escapeHtml(nt.shiftTiming || 'General Shift')}</td>
                    <td>\${escapeHtml(nt.phone)}</td>
                </tr>
            \`
        });
    } catch (e) {
        console.error("Employee load error:", e);
    }
}
`;
content = content.replace(oldLoadEmployeesRegex, () => newLoadEmployees);

// Update loadFees
const oldLoadFeesRegex = /async function loadFees\(\) \{[\s\S]*?\}\s*(?=async function handleGenerateInvoice)/;
const newLoadFees = `async function loadFees() {
    try {
        const resSummary = await fetch(\`\${API_BASE}/api/finance/fees/summary\`, { headers: getAuthHeaders() });
        const dSummary = await resSummary.json();
        if (dSummary.data) {
            const s = dSummary.data;
            document.getElementById('fee-bar-billed').textContent = \`₹ \${Number(s.totalBilled).toLocaleString()}\`;
            document.getElementById('fee-bar-collected').textContent = \`₹ \${Number(s.totalCollected).toLocaleString()}\`;
            document.getElementById('fee-bar-pending').textContent = \`₹ \${Number(s.totalPending).toLocaleString()}\`;
            document.getElementById('fee-bar-rate').textContent = \`\${s.collectionPercentage}%\`;
        }

        const resInv = await fetch(\`\${API_BASE}/api/finance/fees/invoices\`, { headers: getAuthHeaders() });
        const dInv = await resInv.json();
        const invoices = dInv.data || [];

        renderOrUpdateDataTable('admin-fees', {
            tbodyId: 'invoices-table-body',
            tableId: 'invoices-table',
            data: invoices,
            defaultSortKey: 'invoiceNumber',
            searchFields: ['invoiceNumber', 'studentName', 'admissionNo', 'billingPeriod', 'status'],
            filters: [
                {
                    id: 'feeStatus',
                    label: 'All Payment Statuses',
                    options: [
                        { value: '', label: 'All Payment Statuses' },
                        { value: 'PAID', label: 'Paid' },
                        { value: 'PARTIALLY_PAID', label: 'Partially Paid' },
                        { value: 'OVERDUE', label: 'Overdue' }
                    ],
                    match: (inv, val) => !val || inv.status === val
                },
                {
                    id: 'term',
                    label: 'All Billing Terms',
                    key: 'billingPeriod',
                    options: 'auto'
                }
            ],
            renderRow: (inv) => \`
                <tr>
                    <td><code>\${escapeHtml(inv.invoiceNumber)}</code></td>
                    <td><strong>\${escapeHtml(inv.studentName)}</strong> (\${escapeHtml(inv.admissionNo)})</td>
                    <td>\${escapeHtml(inv.billingPeriod)}</td>
                    <td>₹ \${Number(inv.grossAmount).toLocaleString()}</td>
                    <td>
                        \${inv.discountAmount > 0 ? 
                            \`<span class="badge badge-success">- ₹ \${Number(inv.discountAmount).toLocaleString()}</span><br><small class="text-secondary">\${escapeHtml(inv.scholarshipApplied)}</small>\` : 
                            \`<span class="text-secondary">Standard</span>\`}
                    </td>
                    <td><strong>₹ \${Number(inv.netPayable).toLocaleString()}</strong></td>
                    <td>₹ \${Number(inv.paidAmount).toLocaleString()}</td>
                    <td>
                        <span class="badge \${inv.status === 'PAID' ? 'badge-success' : inv.status === 'PARTIALLY_PAID' ? 'badge-warning' : 'badge-danger'}">
                            \${escapeHtml(inv.status)}
                        </span>
                    </td>
                    <td>
                        \${inv.status !== 'PAID' ? \`
                            <button class="btn btn-primary btn-sm" onclick="openPaymentModal(\${inv.id}, '\${escapeHtml(inv.invoiceNumber)}', \${inv.netPayable - inv.paidAmount})">
                                Collect
                            </button>
                        \` : \`<span class="text-success"><i class="fa-solid fa-check"></i> Cleared</span>\`}
                    </td>
                </tr>
            \`
        });
    } catch (e) {
        console.error("Fee load error:", e);
    }
}
`;
content = content.replace(oldLoadFeesRegex, () => newLoadFees);

// Update loadLeaves
const oldLoadLeavesRegex = /async function loadLeaves\(\) \{[\s\S]*?\}\s*(?=async function decideLeave)/;
const newLoadLeaves = `async function loadLeaves() {
    try {
        const res = await fetch(\`\${API_BASE}/api/finance/leaves\`, { headers: getAuthHeaders() });
        const d = await res.json();
        const leaves = d.data || [];

        renderOrUpdateDataTable('admin-leaves', {
            tbodyId: 'leaves-table-body',
            tableId: 'leaves-table',
            data: leaves,
            defaultSortKey: 'startDate',
            defaultSortDir: 'desc',
            searchFields: ['applicantName', 'studentClass', 'rollNo', 'applicantType', 'leaveType', 'reason', 'status'],
            filters: [
                {
                    id: 'status',
                    label: 'All Statuses',
                    options: [
                        { value: '', label: 'All Statuses' },
                        { value: 'PENDING', label: 'Pending Review' },
                        { value: 'APPROVED', label: 'Approved' },
                        { value: 'REJECTED', label: 'Declined' }
                    ],
                    match: (l, val) => !val || l.status === val
                },
                {
                    id: 'type',
                    label: 'All Categories',
                    options: [
                        { value: '', label: 'All Categories' },
                        { value: 'STUDENT', label: 'Students' },
                        { value: 'FACULTY', label: 'Faculty & Staff' }
                    ],
                    match: (l, val) => !val || l.applicantType === val
                }
            ],
            renderRow: (l) => {
                const subDetails = (l.applicantType === 'STUDENT' && l.studentClass) 
                    ? \`<br><small class="text-secondary">\${escapeHtml(l.studentClass)}\${l.rollNo ? ' • Roll #' + escapeHtml(l.rollNo) : ''}</small>\` 
                    : '';
                return \`
                <tr>
                    <td><strong>\${escapeHtml(l.applicantName)}</strong>\${subDetails}</td>
                    <td><span class="badge \${l.applicantType === 'STUDENT' ? 'badge-info' : 'badge-primary'}">\${escapeHtml(l.applicantType)}</span></td>
                    <td>\${escapeHtml(l.leaveType)}</td>
                    <td>\${escapeHtml(l.startDate)}</td>
                    <td>\${escapeHtml(l.endDate)}</td>
                    <td>\${escapeHtml(l.reason)}</td>
                    <td>
                        <span class="badge \${l.status === 'APPROVED' ? 'badge-success' : l.status === 'REJECTED' ? 'badge-danger' : 'badge-warning'}">
                            \${escapeHtml(l.status)}
                        </span>
                    </td>
                    <td>
                        \${l.status === 'PENDING' ? \`
                            <div style="display: flex; gap: 0.35rem;">
                                <button class="btn btn-primary btn-sm" onclick="decideLeave(\${l.id}, 'approve')">Approve</button>
                                <button class="btn btn-outline btn-sm text-danger" onclick="decideLeave(\${l.id}, 'reject')">Decline</button>
                            </div>
                        \` : \`<div style="line-height:1.2;"><small class="text-secondary"><strong>Reviewed by:</strong><br>\${escapeHtml(l.reviewedBy || 'Authorized')}</small></div>\`}
                    </td>
                </tr>
                \`;
            }
        });
    } catch (e) {
        console.error("Leave load error:", e);
    }
}
`;
content = content.replace(oldLoadLeavesRegex, () => newLoadLeaves);

// Update loadExams
const oldLoadExamsRegex = /async function loadExams\(\) \{[\s\S]*?\}\s*(?=async function generateSampleReportCard)/;
const newLoadExams = `async function loadExams() {
    try {
        const res = await fetch(\`\${API_BASE}/api/academic/exams/terms/1/grades/class?grade=Class%2010&section=A\`, {
            headers: getAuthHeaders()
        });
        const d = await res.json();
        const grades = d.data || [];

        renderOrUpdateDataTable('admin-exams', {
            tbodyId: 'exam-grades-table-body',
            tableId: 'exam-grades-table',
            data: grades,
            defaultSortKey: 'studentName',
            searchFields: ['studentName', 'admissionNo', 'subjectName', 'letterGrade'],
            filters: [
                {
                    id: 'subject',
                    label: 'All Subjects',
                    key: 'subjectName',
                    options: 'auto'
                }
            ],
            renderRow: (g) => \`
                <tr>
                    <td>Term 1 Mid-Term Examination</td>
                    <td><strong>\${escapeHtml(g.studentName)}</strong> (\${escapeHtml(g.admissionNo)})</td>
                    <td>\${escapeHtml(g.subjectName)}</td>
                    <td><strong>\${g.marksObtained}</strong></td>
                    <td>\${g.maxMarks}</td>
                    <td><span class="badge badge-success">\${escapeHtml(g.letterGrade)}</span></td>
                    <td>
                        <button class="btn btn-outline btn-sm" onclick="generateSampleReportCard(\${g.studentId})">
                            <i class="fa-solid fa-file-pdf"></i> View Card
                        </button>
                    </td>
                </tr>
            \`
        });
    } catch (e) {
        console.error("Exams error:", e);
    }
}
`;
content = content.replace(oldLoadExamsRegex, () => newLoadExams);

// Update loadWhatsApp
const oldLoadWhatsAppRegex = /async function loadWhatsApp\(\) \{[\s\S]*?\}\s*(?=async function triggerEmergencyBroadcastPrompt)/;
const newLoadWhatsApp = `async function loadWhatsApp() {
    try {
        const res = await fetch(\`\${API_BASE}/api/communication/whatsapp/logs\`, { headers: getAuthHeaders() });
        const d = await res.json();
        const logs = d.data || [];

        renderOrUpdateDataTable('admin-whatsapp', {
            tbodyId: 'whatsapp-table-body',
            tableId: 'whatsapp-table',
            data: logs,
            defaultSortKey: 'createdAt',
            defaultSortDir: 'desc',
            searchFields: ['recipientPhone', 'recipientName', 'templateName', 'messageBody', 'providerMessageId'],
            filters: [
                {
                    id: 'status',
                    label: 'All Delivery Statuses',
                    key: 'status',
                    options: 'auto'
                }
            ],
            renderRow: (m) => \`
                <tr>
                    <td><small>\${escapeHtml(m.createdAt || 'Recent')}</small></td>
                    <td><strong>\${escapeHtml(m.recipientPhone)}</strong></td>
                    <td>\${escapeHtml(m.recipientName)}</td>
                    <td><span class="badge badge-purple">\${escapeHtml(m.templateName)}</span></td>
                    <td><small>\${escapeHtml(m.messageBody)}</small></td>
                    <td><span class="badge badge-success"><i class="fa-solid fa-check-double"></i> \${escapeHtml(m.status)}</span></td>
                    <td><code>\${escapeHtml(m.providerMessageId || 'QUEUED')}</code></td>
                </tr>
            \`
        });
    } catch (e) {
        console.error("WhatsApp error:", e);
    }
}
`;
content = content.replace(oldLoadWhatsAppRegex, () => newLoadWhatsApp);

// Update loadTickets
const oldLoadTicketsRegex = /async function loadTickets\(\) \{[\s\S]*?\}\s*(?=async function escalateTicketToManagement)/;
const newLoadTickets = `async function loadTickets() {
    try {
        const res = await fetch(\`\${API_BASE}/api/communication/tickets\`, { headers: getAuthHeaders() });
        const d = await res.json();
        const tickets = d.data || [];

        renderOrUpdateDataTable('admin-tickets', {
            tbodyId: 'tickets-table-body',
            tableId: 'tickets-table',
            data: tickets,
            defaultSortKey: 'ticketNumber',
            searchFields: ['ticketNumber', 'title', 'reportedBy', 'category', 'priority', 'status', 'escalatedTo'],
            filters: [
                {
                    id: 'status',
                    label: 'All Ticket Statuses',
                    options: [
                        { value: '', label: 'All Ticket Statuses' },
                        { value: 'OPEN', label: 'Open' },
                        { value: 'RESOLVED', label: 'Resolved' }
                    ],
                    match: (t, val) => !val || t.status === val
                },
                {
                    id: 'priority',
                    label: 'All Priorities',
                    options: [
                        { value: '', label: 'All Priorities' },
                        { value: 'CRITICAL', label: 'Critical' },
                        { value: 'HIGH', label: 'High' },
                        { value: 'MEDIUM', label: 'Medium' },
                        { value: 'LOW', label: 'Low' }
                    ],
                    match: (t, val) => !val || t.priority === val
                },
                {
                    id: 'category',
                    label: 'All Categories',
                    key: 'category',
                    options: 'auto'
                }
            ],
            renderRow: (t) => \`
                <tr>
                    <td><code>\${escapeHtml(t.ticketNumber)}</code></td>
                    <td><strong>\${escapeHtml(t.title)}</strong><br><small class="text-secondary">\${escapeHtml(t.reportedBy)}</small></td>
                    <td>\${escapeHtml(t.category)}</td>
                    <td><span class="badge \${t.priority === 'CRITICAL' ? 'badge-danger' : 'badge-warning'}">\${escapeHtml(t.priority)} (\${t.slaHours}h)</span></td>
                    <td><span class="badge \${t.status === 'RESOLVED' ? 'badge-success' : 'badge-danger'}">\${escapeHtml(t.status)}</span></td>
                    <td>
                        \${t.escalated ? \`<strong style="color: #b91c1c;">\${escapeHtml(t.escalatedTo)}</strong>\` : \`<span class="text-secondary">Standard Queue</span>\`}
                    </td>
                    <td>
                        \${!t.escalated && t.status !== 'RESOLVED' ? \`
                            <button class="btn btn-outline btn-sm" onclick="escalateTicketToManagement(\${t.id})">Escalate</button>
                        \` : \`<button class="btn btn-primary btn-sm" onclick="resolveTicket(\${t.id})">Resolve</button>\`}
                    </td>
                </tr>
            \`
        });
    } catch (e) {
        console.error("Tickets error:", e);
    }
}
`;
content = content.replace(oldLoadTicketsRegex, () => newLoadTickets);

// Update loadTeacherStudents
const oldLoadTeacherStudentsRegex = /async function loadTeacherStudents\(\) \{[\s\S]*?\}\s*(?=function renderAssignedClassStudentRow)/;
const newLoadTeacherStudents = `async function loadTeacherStudents() {
    try {
        const { assigned, grade, section } = getTeacherClassDetails();
        const headingEl = document.getElementById('teacher-students-heading');
        if (headingEl) headingEl.textContent = \`Assigned Class: \${grade} - Section \${section}\`;

        const pageTitleEl = document.getElementById('page-title');
        if (pageTitleEl) pageTitleEl.textContent = \`Assigned Class (\${grade} \${section})\`;

        const res = await fetch(\`\${API_BASE}/api/admin/students?grade=\${encodeURIComponent(grade)}\`, { headers: getAuthHeaders() });
        const d = await res.json();
        let students = (d.data || []).filter(s => !section || !s.section || s.section.toUpperCase() === section.toUpperCase());
        if (students.length === 0 && d.data && d.data.length > 0) {
            students = d.data;
        }

        currentAssignedClassStudents = students;

        renderOrUpdateDataTable('teacher-students', {
            tbodyId: 'teacher-students-body',
            tableId: 'assigned-class-students-table',
            data: students,
            defaultSortKey: 'rollNo',
            searchFields: ['fullName', 'rollNo', 'guardianName', 'guardianPhone', 'address'],
            filters: [
                {
                    id: 'feeStatus',
                    label: 'All Fee Statuses',
                    options: [
                        { value: '', label: 'All Fee Statuses' },
                        { value: 'CLEARED', label: 'Cleared' },
                        { value: 'OVERDUE', label: 'Overdue' }
                    ],
                    match: (s, val) => !val || s.feeStatus === val
                }
            ],
            renderRow: (s) => renderAssignedClassStudentRow(s)
        });
    } catch (e) {
        console.error("Error loading teacher students:", e);
    }
}
`;
content = content.replace(oldLoadTeacherStudentsRegex, () => newLoadTeacherStudents);

// Update loadTeacherWorkItems
const oldLoadTeacherWorkItemsRegex = /async function loadTeacherWorkItems\(\) \{[\s\S]*?\}\s*(?=async function loadTeacherClassNotes)/;
const newLoadTeacherWorkItems = `async function loadTeacherWorkItems() {
    const tbody = document.getElementById('teacher-workitems-body');
    if (!tbody) return;

    try {
        const res = await fetch(\`\${API_BASE}/api/admin/communication/tickets\`, { headers: getAuthHeaders() });
        const d = await res.json();
        const items = d.data || [];

        renderOrUpdateDataTable('teacher-workitems', {
            tbodyId: 'teacher-workitems-body',
            tableId: 'teacher-workitems-table',
            data: items,
            defaultSortKey: 'priority',
            searchFields: ['title', 'category', 'priority', 'status', 'reportedBy'],
            renderRow: (item) => \`
                <tr>
                    <td><span class="badge \${item.status === 'RESOLVED' ? 'badge-success' : 'badge-warning'}">\${escapeHtml(item.status)}</span></td>
                    <td><strong>\${escapeHtml(item.title)}</strong><br><small class="text-secondary">\${escapeHtml(item.category || '')}</small></td>
                    <td>\${escapeHtml(item.category || 'General')}</td>
                    <td><span class="badge \${item.priority === 'CRITICAL' ? 'badge-danger' : 'badge-warning'}">\${escapeHtml(item.priority)}</span></td>
                    <td>\${escapeHtml(item.reportedBy || 'Administration')}</td>
                    <td>\${escapeHtml(item.createdAt ? item.createdAt.substring(0, 10) : 'Immediate')}</td>
                    <td>
                        <button class="btn btn-outline btn-sm" onclick="resolveTicket(\${item.id})">Action</button>
                    </td>
                </tr>
            \`
        });
    } catch (e) {
        console.error("Error loading teacher work items:", e);
    }
}
`;
content = content.replace(oldLoadTeacherWorkItemsRegex, () => newLoadTeacherWorkItems);

// Update loadTeacherMarks
const oldLoadTeacherMarksRegex = /async function loadTeacherMarks\(\) \{[\s\S]*?\}\s*(?=async function saveTeacherMarks)/;
const newLoadTeacherMarks = `async function loadTeacherMarks() {
    try {
        const res = await fetch(\`\${API_BASE}/api/academic/exams/terms/1/grades/class?grade=Class%2010&section=A\`, {
            headers: getAuthHeaders()
        });
        const d = await res.json();
        const marks = d.data || [];

        renderOrUpdateDataTable('teacher-marks', {
            tbodyId: 'teacher-marks-body',
            tableId: 'teacher-marks-table',
            data: marks,
            pageSize: 25,
            defaultSortKey: 'admissionNo',
            searchFields: ['admissionNo', 'studentName', 'subjectName', 'letterGrade'],
            filters: [
                {
                    id: 'subject',
                    label: 'All Subjects',
                    key: 'subjectName',
                    options: 'auto'
                }
            ],
            renderRow: (g) => \`
                <tr>
                    <td><code>\${escapeHtml(g.admissionNo)}</code></td>
                    <td><strong>\${escapeHtml(g.studentName)}</strong></td>
                    <td>\${escapeHtml(g.subjectName)}</td>
                    <td>Term 1 Mid-Term 2026</td>
                    <td>\${g.maxMarks}</td>
                    <td>
                        <input type="number" 
                               class="teacher-mark-input" 
                               data-id="\${g.id}" 
                               data-student-id="\${g.studentId}" 
                               data-student-name="\${escapeHtml(g.studentName)}" 
                               data-admission="\${escapeHtml(g.admissionNo)}" 
                               data-grade="\${escapeHtml(g.grade || 'Class 10')}" 
                               data-section="\${escapeHtml(g.section || 'A')}" 
                               data-subject="\${escapeHtml(g.subjectName)}" 
                               data-max="\${g.maxMarks}" 
                               value="\${g.marksObtained}" 
                               min="0" max="\${g.maxMarks}" 
                               style="width: 75px; padding: 4px 8px; border: 1px solid #94a3b8; border-radius: 4px; font-weight: 600;">
                    </td>
                    <td><span class="badge \${g.marksObtained >= 80 ? 'badge-success' : (g.marksObtained >= 60 ? 'badge-primary' : 'badge-warning')}">\${escapeHtml(g.letterGrade)}</span></td>
                </tr>
            \`
        });
    } catch (e) {
        console.error("Error loading marks:", e);
    }
}
`;
content = content.replace(oldLoadTeacherMarksRegex, () => newLoadTeacherMarks);

// Update renderTeacherStudentLeaves
const oldRenderTeacherStudentLeavesRegex = /function renderTeacherStudentLeaves\(\) \{[\s\S]*?\}\s*(?=async function loadTeacherLeaves)/;
const newRenderTeacherStudentLeaves = `function renderTeacherStudentLeaves() {
    renderOrUpdateDataTable('teacher-student-leaves', {
        tbodyId: 'teacher-student-leaves-table-body',
        tableId: 'teacher-student-leaves-table',
        data: teacherStudentLeavesCache,
        defaultSortKey: 'startDate',
        defaultSortDir: 'desc',
        searchFields: ['applicantName', 'studentClass', 'rollNo', 'leaveType', 'reason', 'status'],
        filters: [
            {
                id: 'status',
                label: 'All Statuses',
                options: [
                    { value: '', label: 'All Statuses' },
                    { value: 'PENDING', label: 'Pending Authorization' },
                    { value: 'APPROVED', label: 'Approved' },
                    { value: 'REJECTED', label: 'Declined' }
                ],
                match: (l, val) => !val || l.status === val
            }
        ],
        renderRow: (l) => {
            const rollDisplay = l.rollNo ? \` • Roll #\${escapeHtml(l.rollNo)}\` : '';
            const clsDisplay = l.studentClass ? escapeHtml(l.studentClass) : 'Class 10A';
            return \`
                <tr>
                    <td>
                        <strong>\${escapeHtml(l.applicantName)}</strong>
                        <div style="font-size: 0.78rem; color: #64748b; margin-top: 2px;">
                            <span class="badge badge-secondary" style="font-size: 0.72rem; padding: 2px 6px;">\${clsDisplay}</span>\${rollDisplay}
                        </div>
                    </td>
                    <td><span class="badge badge-purple">\${escapeHtml(l.leaveType)}</span></td>
                    <td><strong>\${escapeHtml(formatDateRange(l.startDate, l.endDate))}</strong></td>
                    <td><small class="text-secondary">\${escapeHtml(l.startDate)} &rarr; \${escapeHtml(l.endDate)}</small></td>
                    <td><span style="font-size: 0.85rem;">\${escapeHtml(l.reason || 'Personal / Medical')}</span></td>
                    <td>
                        <span class="badge \${l.status === 'APPROVED' ? 'badge-success' : (l.status === 'PENDING' ? 'badge-warning' : 'badge-danger')}">
                            \${l.status === 'PENDING' ? '<i class="fa-solid fa-clock"></i> PENDING' : l.status}
                        </span>
                    </td>
                    <td>
                        \${l.status === 'PENDING' ? \`
                            <div style="display: flex; gap: 0.35rem; align-items: center;">
                                <button class="btn btn-primary btn-sm" onclick="decideLeave(\${l.id}, 'approve')" style="padding: 4px 10px; font-size: 0.78rem;">
                                    <i class="fa-solid fa-check"></i> Approve
                                </button>
                                <button class="btn btn-outline btn-sm text-danger" onclick="decideLeave(\${l.id}, 'reject')" style="padding: 4px 8px; font-size: 0.78rem;">
                                    <i class="fa-solid fa-xmark"></i> Decline
                                </button>
                            </div>
                        \` : \`
                            <div style="font-size: 0.78rem; color: #475569; line-height: 1.25;">
                                <strong>Reviewed by:</strong><br>\${escapeHtml(l.reviewedBy || 'Authorized')}
                            </div>
                        \`}
                    </td>
                </tr>
            \`;
        }
    });
}
`;
content = content.replace(oldRenderTeacherStudentLeavesRegex, () => newRenderTeacherStudentLeaves);

// In loadTeacherLeaves, update faculty leaves table
const oldTeacherLeavesTbodyRegex = /const myLeaves = allLeaves\.filter[\s\S]*?myTbody\.innerHTML = `<tr><td colspan="7"[\s\S]*?\}\s*\}/;
const newTeacherLeavesTbody = `const myLeaves = allLeaves.filter(l => l.applicantType === 'FACULTY' && (l.applicantId == empId || (l.applicantName && currentUser?.fullName && l.applicantName.toLowerCase().includes(currentUser.fullName.toLowerCase()))));
        renderOrUpdateDataTable('teacher-my-leaves', {
            tbodyId: 'teacher-leaves-table-body',
            tableId: 'teacher-leaves-table',
            data: myLeaves,
            defaultSortKey: 'startDate',
            defaultSortDir: 'desc',
            searchFields: ['leaveType', 'reason', 'status', 'reviewedBy'],
            renderRow: (l) => \`
                <tr>
                    <td><strong>\${escapeHtml(l.leaveType)}</strong></td>
                    <td>\${formatDateRange(l.startDate, l.endDate)}</td>
                    <td>\${escapeHtml(l.startDate)}</td>
                    <td>\${escapeHtml(l.endDate)}</td>
                    <td>\${escapeHtml(l.reason)}</td>
                    <td>
                        <span class="badge \${l.status === 'APPROVED' ? 'badge-success' : (l.status === 'PENDING' ? 'badge-warning' : 'badge-danger')}">
                            \${l.status}
                        </span>
                    </td>
                    <td><small class="text-secondary">\${escapeHtml(l.reviewedBy || 'Pending Management Authorization')}</small></td>
                </tr>
            \`
        });
    }`;
content = content.replace(oldTeacherLeavesTbodyRegex, () => newTeacherLeavesTbody);

// Update loadStudentFees
const oldLoadStudentFeesRegex = /async function loadStudentFees\(\) \{[\s\S]*?\}\s*(?=async function loadStudentTransit)/;
const newLoadStudentFees = `async function loadStudentFees() {
    const studentId = currentUser?.studentId || 1;
    try {
        const res = await fetch(\`\${API_BASE}/api/finance/fees/invoices?studentId=\${studentId}\`, {
            headers: getAuthHeaders()
        });
        const d = await res.json();
        const invoices = d.data || [];

        renderOrUpdateDataTable('student-fees', {
            tbodyId: 'student-fees-table-body',
            tableId: 'student-fees-table',
            data: invoices,
            defaultSortKey: 'invoiceNumber',
            searchFields: ['invoiceNumber', 'billingPeriod', 'status'],
            renderRow: (inv) => \`
                <tr>
                    <td><code>\${escapeHtml(inv.invoiceNumber)}</code></td>
                    <td>\${escapeHtml(inv.billingPeriod)}</td>
                    <td>₹ \${Number(inv.grossAmount).toLocaleString()}</td>
                    <td><strong>₹ \${Number(inv.netPayable).toLocaleString()}</strong></td>
                    <td>₹ \${Number(inv.paidAmount).toLocaleString()}</td>
                    <td>
                        <span class="badge \${inv.status === 'PAID' ? 'badge-success' : 'badge-danger'}">
                            \${escapeHtml(inv.status)}
                        </span>
                    </td>
                    <td>
                        \${inv.status === 'PAID' ? 
                            \`<button class="btn btn-outline btn-sm" onclick="showToast('Official Fee Receipt', 'Receipt #\${escapeHtml(inv.invoiceNumber)} | Cleared: ₹\${inv.paidAmount}', 'success')">
                                <i class="fa-solid fa-receipt"></i> View Receipt
                             </button>\` : 
                            \`<button class="btn btn-primary btn-sm" onclick="openPaymentModal(\${inv.id}, '\${escapeHtml(inv.invoiceNumber)}', \${inv.netPayable - inv.paidAmount})">
                                <i class="fa-solid fa-credit-card"></i> Pay Now
                             </button>\`
                        }
                    </td>
                </tr>
            \`
        });
    } catch (e) {
        console.error("Student fees error:", e);
    }
}
`;
content = content.replace(oldLoadStudentFeesRegex, () => newLoadStudentFees);

// Update loadStudentLeaves
const oldLoadStudentLeavesRegex = /async function loadStudentLeaves\(\) \{[\s\S]*?\}\s*(?=async function loadStudentTimetable)/;
const newLoadStudentLeaves = `async function loadStudentLeaves() {
    const studentId = currentUser?.studentId || 1;
    try {
        const res = await fetch(\`\${API_BASE}/api/finance/leaves?applicantId=\${studentId}&applicantType=STUDENT\`, {
            headers: getAuthHeaders()
        });
        const d = await res.json();
        const leaves = d.data || [];

        renderOrUpdateDataTable('student-leaves', {
            tbodyId: 'student-leaves-table-body',
            tableId: 'student-leaves-table',
            data: leaves,
            defaultSortKey: 'startDate',
            defaultSortDir: 'desc',
            searchFields: ['leaveType', 'reason', 'status', 'startDate', 'endDate'],
            renderRow: (l) => \`
                <tr>
                    <td><strong>\${escapeHtml(l.leaveType)}</strong></td>
                    <td>\${escapeHtml(l.startDate)}</td>
                    <td>\${escapeHtml(l.endDate)}</td>
                    <td>\${escapeHtml(l.reason)}</td>
                    <td>
                        <span class="badge \${l.status === 'APPROVED' ? 'badge-success' : (l.status === 'PENDING' ? 'badge-warning' : 'badge-danger')}">
                            \${escapeHtml(l.status)}
                        </span>
                    </td>
                </tr>
            \`
        });
    } catch (e) {
        console.error("Student leaves load error:", e);
    }
}
`;
content = content.replace(oldLoadStudentLeavesRegex, () => newLoadStudentLeaves);

// Update loadInquiries
const oldLoadInquiriesRegex = /async function loadInquiries\(\) \{[\s\S]*?\}\s*(?=function openNewInquiryModal)/;
const newLoadInquiries = `async function loadInquiries() {
    try {
        const resInq = await fetch(\`\${API_BASE}/api/admin/admissions/inquiries\`, { headers: getAuthHeaders() });
        const dataInq = await resInq.json();
        const inquiries = dataInq.data || [];

        renderOrUpdateDataTable('admin-inquiries', {
            tbodyId: 'inquiries-table-body',
            tableId: 'inquiries-table',
            data: inquiries,
            defaultSortKey: 'studentName',
            searchFields: ['studentName', 'grade', 'parentName', 'parentPhone', 'previousSchool', 'notes', 'status'],
            renderRow: (inq) => \`
                <tr>
                    <td><strong>\${escapeHtml(inq.studentName)}</strong></td>
                    <td><span class="badge badge-primary">\${escapeHtml(inq.grade)}</span></td>
                    <td>\${escapeHtml(inq.parentName)}<br><small class="text-secondary">\${escapeHtml(inq.parentPhone)}</small></td>
                    <td>\${escapeHtml(inq.previousSchool || 'N/A')}</td>
                    <td><span class="badge badge-success">\${escapeHtml(inq.status)}</span></td>
                    <td><small>\${escapeHtml(inq.notes || '')}</small></td>
                    <td>
                        <button class="btn btn-outline btn-sm" onclick="showToast('Follow Up', 'Dialing parent \${escapeHtml(inq.parentPhone)}...', 'info')">
                            <i class="fa-solid fa-phone"></i>
                        </button>
                    </td>
                </tr>
            \`
        });
    } catch (e) {
        console.error("Inquiry load error:", e);
    }
}
`;
content = content.replace(oldLoadInquiriesRegex, () => newLoadInquiries);

fs.writeFileSync(path, content, 'utf8');
console.log('Successfully wrote updated app.js!');
