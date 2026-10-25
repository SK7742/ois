// Test script for OxfordDataTable logic
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

console.log('Pages 5, current 3:', getPaginationRange(3, 5));
console.log('Pages 20, current 2:', getPaginationRange(2, 20));
console.log('Pages 20, current 10:', getPaginationRange(10, 20));
console.log('Pages 20, current 18:', getPaginationRange(18, 20));
