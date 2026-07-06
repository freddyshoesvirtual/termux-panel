// Termux Panel — backend local
// Corre esto dentro de Termux con: node server.js
// Expone datos reales (pm2, cpu/ram, cloudflared) por HTTP en localhost.

const express = require('express');
const { exec } = require('child_process');
const fs = require('fs');
const util = require('util');
const execAsync = util.promisify(exec);

const app = express();
const PORT = 8199; // solo localhost — no expongas este puerto a la red

app.use(express.json());

// Middleware simple: solo acepta conexiones desde localhost
app.use((req, res, next) => {
  const ip = req.socket.remoteAddress || '';
  if (!ip.includes('127.0.0.1') && !ip.includes('::1')) {
    return res.status(403).json({ error: 'solo localhost' });
  }
  next();
});

// ---------- /api/scripts — estado de pm2 ----------
app.get('/api/scripts', async (req, res) => {
  try {
    const { stdout } = await execAsync('pm2 jlist');
    const list = JSON.parse(stdout);
    const scripts = list.map(p => ({
      name: p.name,
      pid: p.pid,
      status: p.pm2_env.status, // online, stopped, errored
      uptime: p.pm2_env.pm_uptime,
      restarts: p.pm2_env.restart_time,
    }));
    res.json({ scripts });
  } catch (e) {
    res.status(500).json({ error: 'pm2 no disponible o sin procesos', detail: String(e) });
  }
});

// ---------- /api/server — CPU / RAM / uptime ----------
app.get('/api/server', async (req, res) => {
  try {
    const meminfo = fs.readFileSync('/proc/meminfo', 'utf8');
    const totalKb = parseInt(meminfo.match(/MemTotal:\s+(\d+)/)[1]);
    const availKb = parseInt(meminfo.match(/MemAvailable:\s+(\d+)/)[1]);
    const usedPct = Math.round(((totalKb - availKb) / totalKb) * 100);

    const loadavg = fs.readFileSync('/proc/loadavg', 'utf8').split(' ')[0];
    const uptimeSec = parseFloat(fs.readFileSync('/proc/uptime', 'utf8').split(' ')[0]);
    const uptimeDays = (uptimeSec / 86400).toFixed(1);

    let tunnelStatus = 'desconocido';
    try {
      const { stdout } = await execAsync('cloudflared tunnel list 2>&1');
      tunnelStatus = stdout.includes('healthy') ? 'activo' : 'revisar';
    } catch (_) { tunnelStatus = 'cloudflared no encontrado'; }

    res.json({
      ram_used_pct: usedPct,
      load_avg: loadavg,
      uptime_days: uptimeDays,
      tunnel_status: tunnelStatus,
    });
  } catch (e) {
    res.status(500).json({ error: 'no se pudo leer estado del server', detail: String(e) });
  }
});

// ---------- /api/bot — estado de Baileys (ajusta al nombre real de tu proceso pm2) ----------
app.get('/api/bot', async (req, res) => {
  try {
    const { stdout } = await execAsync('pm2 jlist');
    const list = JSON.parse(stdout);
    const bot = list.find(p => p.name.toLowerCase().includes('baileys') || p.name.toLowerCase().includes('bot'));
    res.json({
      connected: bot ? bot.pm2_env.status === 'online' : false,
      name: bot ? bot.name : null,
    });
  } catch (e) {
    res.status(500).json({ error: 'no se pudo leer estado del bot', detail: String(e) });
  }
});

// ---------- /api/action — acciones simples (restart, etc) ----------
app.post('/api/action', async (req, res) => {
  const { type, target } = req.body;
  try {
    if (type === 'restart' && target) {
      await execAsync(`pm2 restart ${target}`);
      return res.json({ ok: true });
    }
    res.status(400).json({ error: 'acción no reconocida' });
  } catch (e) {
    res.status(500).json({ error: String(e) });
  }
});

app.listen(PORT, '127.0.0.1', () => {
  console.log(`Termux Panel backend escuchando en http://127.0.0.1:${PORT}`);
});
