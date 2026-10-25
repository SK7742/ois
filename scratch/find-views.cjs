const fs = require('fs');
const lines = fs.readFileSync('C:/Users/DELL/.gemini/antigravity/scratch/oxford-csms/backend/src/main/resources/static/index.html', 'utf8').split('\n');
lines.forEach((l, i) => {
    if (l.includes('class="view-panel"')) {
        console.log(`Line ${i + 1}: ${l.trim()}`);
    }
});
