const fs = require('fs');

const html = fs.readFileSync('C:/Users/DELL/.gemini/antigravity/scratch/oxford-csms/backend/src/main/resources/static/index.html', 'utf8');

const views = [];
const regex = /<section\s+id="([^"]+)"\s+class="view-panel"/g;
let match;
while ((match = regex.exec(html)) !== null) {
    views.push(match[1]);
}

console.log('Total Views:', views.length);
console.log(views);

// Find all data tables and tables in index.html
const tableMatches = [...html.matchAll(/<table[^>]*id="([^"]+)"[^>]*>/g)];
console.log('\nTables with IDs:');
tableMatches.forEach(m => console.log('Table ID:', m[1]));

// Also find all tbodies
const tbodyMatches = [...html.matchAll(/<tbody[^>]*id="([^"]+)"[^>]*>/g)];
console.log('\nTbodies with IDs:');
tbodyMatches.forEach(m => console.log('Tbody ID:', m[1]));
