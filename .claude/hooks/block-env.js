const raw = require('fs').readFileSync(0, 'utf8');

let data;
try {
  data = JSON.parse(raw);
} catch (e) {
  process.exit(0);
}

const BLOCKED_MSG = 'Blocked: Access to .env files is not allowed.\n';
const input = data.tool_input || {};

function isEnvFile(filePath) {
  if (!filePath) return false;
  const normalized = String(filePath).replace(/\\/g, '/');
  const basename = normalized.split('/').pop();
  return basename === '.env' || /^\.env(\.\w+)?$/.test(basename);
}

// Block Read / Write / Edit on .env files
if (isEnvFile(input.file_path)) {
  process.stderr.write(BLOCKED_MSG);
  process.exit(2);
}

// Block Glob patterns that would match .env files
if (input.pattern && /\.env/.test(input.pattern)) {
  process.stderr.write(BLOCKED_MSG);
  process.exit(2);
}

// Block Bash commands referencing .env files
if (input.command && /(?<![\w-])\.env(\.\w+)?(?![\w])/.test(input.command)) {
  process.stderr.write(BLOCKED_MSG);
  process.exit(2);
}

process.exit(0);