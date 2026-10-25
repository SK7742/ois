// Test sort logic
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

const list = [
    { name: 'Rohan Gupta', roll: 3, salary: '₹ 45,000', date: '2026-10-20' },
    { name: 'Aarav Kumar', roll: 1, salary: '₹ 65,000', date: '2026-10-15' },
    { name: 'Ananya Singh', roll: 2, salary: '₹ 55,000', date: '2026-10-25' }
];

list.sort((a, b) => compareValues(a.name, b.name, 'string', 'asc'));
console.log('Sorted by name asc:', list.map(x => x.name));

list.sort((a, b) => compareValues(a.roll, b.roll, 'number', 'asc'));
console.log('Sorted by roll asc:', list.map(x => x.roll));

list.sort((a, b) => compareValues(a.salary, b.salary, 'number', 'desc'));
console.log('Sorted by salary desc:', list.map(x => x.salary));
