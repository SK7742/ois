const fs = require('fs');
const html = fs.readFileSync('C:/Users/DELL/.gemini/antigravity/scratch/oxford-csms/backend/src/main/resources/static/index.html', 'utf8');

const tbodies = [...html.matchAll(/<tbody\s+id="([^"]+)"/g)].map(m => m[1]);
console.log('Tbodies:', tbodies);

tbodies.forEach(id => {
    const idx = html.indexOf(`id="${id}"`);
    const tableBefore = html.lastIndexOf('<table', idx);
    const tableTag = html.substring(tableBefore, html.indexOf('>', tableBefore) + 1);
    console.log(`Tbody: ${id} -> Table tag: ${tableTag}`);
});
