const fs = require('fs');
const html = fs.readFileSync('C:/Users/DELL/.gemini/antigravity/scratch/oxford-csms/backend/src/main/resources/static/index.html', 'utf8');
const tables = [...html.matchAll(/(<table[^>]*>[\s\S]*?<\/thead>)/gi)];
console.log('Total tables with thead found:', tables.length);
tables.forEach((t, i) => {
    const tableTag = t[0].match(/<table[^>]*>/i)[0];
    const ths = [...t[0].matchAll(/<th[^>]*>([\s\S]*?)<\/th>/gi)].map(m => m[1].replace(/<[^>]+>/g, '').trim());
    console.log(`\nTable ${i+1}: ${tableTag}\n  Headers: ${ths.join(' | ')}`);
});
