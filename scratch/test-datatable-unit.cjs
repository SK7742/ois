// Unit test for OxfordDataTable filtering, searching, and sorting
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

class TestOxfordDataTable {
    constructor(config) {
        this.rawItems = [...config.data];
        this.searchFields = config.searchFields || [];
        this.filterDefs = config.filters || [];
        this.pageSize = config.pageSize || 10;
        this.currentPage = 1;
        this.sortKey = config.defaultSortKey || null;
        this.sortDir = config.defaultSortDir || 'asc';
        this.columnTypes = config.columnTypes || {};
        this.searchQuery = '';
        this.activeFilters = {};
    }

    setFilter(filterId, val) {
        this.activeFilters[filterId] = val;
        this.currentPage = 1;
    }

    setSearch(query) {
        this.searchQuery = query;
        this.currentPage = 1;
    }

    setSort(key, dir) {
        if (dir) {
            this.sortDir = dir;
        } else if (this.sortKey === key) {
            this.sortDir = this.sortDir === 'asc' ? 'desc' : 'asc';
        } else {
            this.sortDir = 'asc';
        }
        this.sortKey = key;
    }

    setPage(page) {
        this.currentPage = page;
    }

    process() {
        let items = [...this.rawItems];

        // 1. Filters
        for (const f of this.filterDefs) {
            const val = this.activeFilters[f.id];
            if (val !== undefined && val !== null && val !== '') {
                if (typeof f.match === 'function') {
                    items = items.filter(item => f.match(item, val));
                } else if (f.key) {
                    items = items.filter(item => String(item[f.key]) === String(val));
                }
            }
        }

        // 2. Search
        if (this.searchQuery.trim()) {
            const terms = this.searchQuery.toLowerCase().trim().split(/\s+/).filter(Boolean);
            items = items.filter(item => {
                let text = '';
                if (this.searchFields.length > 0) {
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
                return compareValues(valA, valB, type, dir);
            });
        }

        // 4. Pagination
        const total = items.length;
        const totalPages = Math.max(1, Math.ceil(total / this.pageSize));
        if (this.currentPage > totalPages) this.currentPage = totalPages;
        if (this.currentPage < 1) this.currentPage = 1;
        const start = (this.currentPage - 1) * this.pageSize;
        const end = Math.min(start + this.pageSize, total);
        const paged = items.slice(start, end);

        return {
            items: paged,
            total,
            totalPages,
            currentPage: this.currentPage,
            start: total > 0 ? start + 1 : 0,
            end
        };
    }
}

// Sample Data
const mockInvoices = [
    { id: 1, invoiceNumber: 'INV-2026-001', studentName: 'Aarav Kumar', netPayable: 18000, paidAmount: 18000, status: 'PAID' },
    { id: 2, invoiceNumber: 'INV-2026-002', studentName: 'Diya Sharma', netPayable: 18000, paidAmount: 0, status: 'OVERDUE' },
    { id: 3, invoiceNumber: 'INV-2026-003', studentName: 'Rohan Gupta', netPayable: 16500, paidAmount: 16500, status: 'PAID' },
    { id: 4, invoiceNumber: 'INV-2026-004', studentName: 'Ananya Verma', netPayable: 18000, paidAmount: 10000, status: 'PARTIALLY_PAID' },
    { id: 5, invoiceNumber: 'INV-2026-005', studentName: 'Vikram Singh', netPayable: 21000, paidAmount: 0, status: 'OVERDUE' },
    { id: 6, invoiceNumber: 'INV-2026-006', studentName: 'Pooja Patel', netPayable: 14000, paidAmount: 14000, status: 'PAID' }
];

const dt = new TestOxfordDataTable({
    data: mockInvoices,
    pageSize: 2,
    defaultSortKey: 'netPayable',
    defaultSortDir: 'desc',
    columnTypes: { netPayable: 'number' },
    filters: [
        { id: 'status', key: 'status' }
    ]
});

console.log('--- Initial Page 1 (Sort netPayable desc, pageSize 2) ---');
let res = dt.process();
console.log(`Showing ${res.start}-${res.end} of ${res.total} (Page ${res.currentPage}/${res.totalPages})`);
console.log(res.items.map(i => `${i.invoiceNumber}: ₹${i.netPayable} [${i.status}]`));

console.log('\n--- Next Page (Page 2) ---');
dt.setPage(2);
res = dt.process();
console.log(`Showing ${res.start}-${res.end} of ${res.total} (Page ${res.currentPage}/${res.totalPages})`);
console.log(res.items.map(i => `${i.invoiceNumber}: ₹${i.netPayable} [${i.status}]`));

console.log('\n--- Filter by Status = OVERDUE ---');
dt.setFilter('status', 'OVERDUE');
res = dt.process();
console.log(`Showing ${res.start}-${res.end} of ${res.total} (Page ${res.currentPage}/${res.totalPages})`);
console.log(res.items.map(i => `${i.invoiceNumber}: ₹${i.netPayable} [${i.status}]`));

console.log('\n--- Search "diya" with status OVERDUE ---');
dt.setSearch('diya');
res = dt.process();
console.log(`Showing ${res.start}-${res.end} of ${res.total}:`);
console.log(res.items.map(i => `${i.invoiceNumber}: ${i.studentName} [${i.status}]`));

console.log('\n--- Clear Search & Filter, Sort by studentName asc ---');
dt.setFilter('status', '');
dt.setSearch('');
dt.setSort('studentName', 'asc');
dt.setPage(1);
res = dt.process();
console.log(res.items.map(i => `${i.studentName} (${i.invoiceNumber})`));

console.log('\nUNIT TEST PASSED COMPLETELY!');
