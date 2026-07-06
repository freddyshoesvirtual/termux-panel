# Termux Panel — Backend

## Instalación en Termux

```bash
pkg install nodejs -y
npm install -g pm2
cd ~/termux-panel-backend   # copia aquí server.js y package.json
npm install
```

## Migrar tus scripts a pm2 (necesario para que /api/scripts funcione)

En vez de correr tus scripts sueltos, regístralos con pm2:

```bash
pm2 start baileys-watch.js --name baileys-watch
pm2 start sync-sheets.py --name sync-sheets --interpreter python3
pm2 save
```

## Levantar el backend

```bash
node server.js
```

Deberías ver: `Termux Panel backend escuchando en http://127.0.0.1:8199`

## Que sobreviva a Android matando procesos en segundo plano

```bash
termux-wake-lock
```

Y para que arranque solo al reiniciar el teléfono, instala **Termux:Boot** desde F-Droid (mismo publisher que Termux) y crea:

```bash
mkdir -p ~/.termux/boot
cat > ~/.termux/boot/start-panel.sh << 'EOF'
#!/data/data/com.termux/files/usr/bin/sh
termux-wake-lock
cd ~/termux-panel-backend
node server.js &
EOF
chmod +x ~/.termux/boot/start-panel.sh
```

## Probar que responde

```bash
curl http://127.0.0.1:8199/api/server
```

Deberías recibir un JSON con `ram_used_pct`, `load_avg`, `uptime_days`, `tunnel_status`.

## Importante — cómo la APK Android llega a este puerto

Termux y la APK corren en el **mismo dispositivo**, pero son apps separadas — Android aísla cada app en su propio sandbox de red. `127.0.0.1:8199` desde la APK **sí** llega a este servidor porque ambas comparten la misma interfaz de loopback del dispositivo (esto no es como dos contenedores distintos). No necesitas configurar nada de red adicional — solo que el backend esté corriendo cuando abras la app.
