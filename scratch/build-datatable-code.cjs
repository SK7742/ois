// Script to verify OxfordDataTable implementation syntax and features
const code = `
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
            leftHtml += \`
                <div class="dt-search-box">
                    <i class="fa-solid fa-magnifying-glass"></i>
                    <input type="text" class="dt-search-input" id="\${this.id}-search" placeholder="\${escapeHtml(this.placeholder)}" value="\${escapeHtml(this.searchQuery)}">
                    <button type="button" class="dt-search-clear" id="\${this.id}-search-clear" style="display: \${this.searchQuery ? 'block' : 'none'};" title="Clear Search">
                        <i class="fa-solid fa-xmark"></i>
                    </button>
                </div>
            \`;
        }

        if (this.filterDefs && this.filterDefs.length > 0) {
            this.filterDefs.forEach(f => {
                let options = f.options;
                if (options === 'auto' && f.key) {
                    const rawVals = [...new Set(this.rawItems.map(item => item[f.key]).filter(v => v !== null && v !== undefined && v !== ''))].sort();
                    options = [{ value: '', label: f.label || 'All' }, ...rawVals.map(v => ({ value: String(v), label: String(v) }))];
                }
                leftHtml += \`
                    <select class="dt-filter-select" id="\${this.id}-filter-\${f.id}" title="\${escapeHtml(f.label || 'Filter')}">
                        \${(options || []).map(opt => \`
                            <option value="\${escapeHtml(opt.value)}" \${this.activeFilters[f.id] === opt.value ? 'selected' : ''}>
                                \${escapeHtml(opt.label)}
                            </option>
                        \`).join('')}
                    </select>
                \`;
            });
        }

        let rightHtml = '';
        if (this.enablePagination) {
            rightHtml += \`
                <div class="dt-page-size-selector">
                    <label for="\${this.id}-page-size">Show</label>
                    <select class="dt-filter-select" id="\${this.id}-page-size">
                        \${this.pageSizeOptions.map(sz => \`
                            <option value="\${sz}" \${this.pageSize === sz ? 'selected' : ''}>\${sz}</option>
                        \`).join('')}
                    </select>
                    <span>entries</span>
                </div>
            \`;
        }

        this.toolbarEl.innerHTML = \`
            <div class="dt-toolbar-left">\${leftHtml}</div>
            <div class="dt-toolbar-right">\${rightHtml}</div>
        \`;
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
                        // Avoid triggering sort if user clicks inside an input or button inside th
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
                if (e.target && e.target.id === \`\${this.id}-page-size\`) {
                    this.pageSize = parseInt(e.target.value, 10);
                    this.currentPage = 1;
                    this.apply();
                    return;
                }
                
                if (e.target && e.target.classList.contains('dt-filter-select')) {
                    const match = e.target.id.match(new RegExp(\`^\${this.id}-filter-(.+)$\`));
                    if (match) {
                        const filterId = match[1];
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
                this.tbodyEl.innerHTML = \`<tr><td colspan="\${colCount}" class="dt-empty-state"><i class="fa-solid fa-inbox"></i> \${escapeHtml(this.emptyMessage)}</td></tr>\`;
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
            : \`Showing <strong>\${start}</strong> to <strong>\${end}</strong> of <strong>\${total}</strong> entries\${isFiltered ? \` <span class="text-secondary">(filtered from \${this.rawItems.length} total)</span>\` : ''}\`;

        let controlsHtml = '';
        if (this.enablePagination && totalPages > 1) {
            const range = getPaginationRange(this.currentPage, totalPages);
            controlsHtml += \`<button type="button" class="dt-page-btn" \${this.currentPage === 1 ? 'disabled' : ''} data-page="1" title="First Page"><i class="fa-solid fa-angles-left"></i></button>\`;
            controlsHtml += \`<button type="button" class="dt-page-btn" \${this.currentPage === 1 ? 'disabled' : ''} data-page="\${this.currentPage - 1}" title="Previous Page"><i class="fa-solid fa-angle-left"></i></button>\`;

            range.forEach(p => {
                if (p === '...') {
                    controlsHtml += \`<span class="dt-page-ellipsis">&hellip;</span>\`;
                } else {
                    controlsHtml += \`<button type="button" class="dt-page-btn \${p === this.currentPage ? 'active' : ''}" data-page="\${p}">\${p}</button>\`;
                }
            });

            controlsHtml += \`<button type="button" class="dt-page-btn" \${this.currentPage === totalPages ? 'disabled' : ''} data-page="\${this.currentPage + 1}" title="Next Page"><i class="fa-solid fa-angle-right"></i></button>\`;
            controlsHtml += \`<button type="button" class="dt-page-btn" \${this.currentPage === totalPages ? 'disabled' : ''} data-page="\${totalPages}" title="Last Page"><i class="fa-solid fa-angles-right"></i></button>\`;
        }

        this.paginationBarEl.innerHTML = \`
            <div class="dt-info">\${infoHtml}</div>
            <div class="dt-pagination-controls">\${controlsHtml}</div>
        \`;
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

// Syntax validation:
try {
    new Function(code);
    console.log('OxfordDataTable script parsed successfully with NO syntax errors!');
} catch (err) {
    console.error('Syntax error:', err);
}
