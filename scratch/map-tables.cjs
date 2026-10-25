const fs = require('fs');
const html = fs.readFileSync('C:/Users/DELL/.gemini/antigravity/scratch/oxford-csms/backend/src/main/resources/static/index.html', 'utf8');

// Find all <table> elements and print their surrounding context (section id or card title) and the thead HTML
const regex = /<section[^>]*id="([^"]+)"[\s\S]*?(<table[\s\S]*?<\/thead>)/gi;
let match;
const results = [];
while ((match = regex.exec(html)) !== null) {
    results.push({
        sectionId: match[1],
        tableSnippet: match[2]
    });
}

console.log(`Found ${results.length} tables mapped to sections:`);
results.forEach((r, i) => {
    console.log(`\n--- [${i + 1}] Section: ${r.sectionId} ---`);
    console.log(r.tableSnippet.trim());
});
