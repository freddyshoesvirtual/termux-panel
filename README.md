# Termux Panel — Proyecto completo (paso 1)

Dos partes:

```
termux-panel/
├── backend/          → corre DENTRO de Termux (Node.js)
└── android-app/       → proyecto Android Studio (Kotlin + Compose)
```

## 1. Backend (Termux)

Copia la carpeta `backend/` a tu Termux (por ejemplo con `termux-setup-storage` + copiar,
o clonándolo si lo subes a un repo tuyo). Sigue `backend/README.md` — ahí está todo el
setup: pm2, termux-wake-lock, Termux:Boot.

## 2. App Android (android-app/)

1. Abre la carpeta `android-app/` completa en Android Studio ("Open" → selecciona la carpeta).
2. Deja que Gradle sincronice (te pedirá descargar el SDK 34 si no lo tienes).
3. Conecta tu teléfono (o el mismo donde corre Termux) con depuración USB, o genera un APK
   con Build → Build Bundle(s)/APK(s) → Build APK(s).
4. Instala el APK en el mismo dispositivo donde corre Termux — **es obligatorio que sea
   el mismo teléfono**, porque la app habla con `127.0.0.1:8199`, que solo existe en ese
   dispositivo.

## Orden de arranque para probarlo

1. En Termux: `node server.js` (dentro de `backend/`)
2. Verifica con `curl http://127.0.0.1:8199/api/server` que responde.
3. Abre la app Android — deberías ver el RAM/uptime real de tu servidor en la pantalla Home.

## Qué falta / próximos pasos reales

- Ajusta `/api/bot` en `server.js` al nombre exacto de tu proceso pm2 del bot de WhatsApp.
- El switch de la pantalla Bot no dispara acción real todavía — hay un comentario
  (`TODO`) donde conectar `postAction("restart", ...)`.
- No hay ícono personalizado ni splash — usa el default de Android por ahora.
- Cuando esto funcione end-to-end, seguimos con el paso 2 (bspwm + rofi en Termux:X11).
