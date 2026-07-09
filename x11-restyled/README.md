# X11 Restyled — scaffold de integración

Esto NO es un fork completo de termux-x11 (ese código vive en su repo con submódulos
de Xorg/etc. — es mucho para regenerar aquí). Es el andamiaje que le agregas ENCIMA
una vez tengas el repo clonado:

```bash
git clone --recurse-submodules https://github.com/termux/termux-x11
```

## Qué hace cada pieza

- **bspwm-config/** → va dentro de Termux (`~/.config/bspwm/`, `~/.config/sxhkd/`,
  `~/.config/rofi/`). Fuerza modo monocle fullscreen y define el tema visual del
  launcher. Esto ya es 100% funcional hoy, sin tocar Android Studio.

- **android-overlay/** → código Kotlin que va DENTRO de tu fork de termux-x11
  (o de una app separada que lo embeba usando `Loader.java`, que el propio proyecto
  documenta como punto de entrada para terceros — ver README de termux-x11,
  sección "It is possible to use Termux:X11 with 3rd party apps").

## Orden real de integración

1. Clona termux-x11 con submódulos y compílalo tal cual, sin tocar nada — confirma
   que corre en tu teléfono como hoy.
2. Copia `bspwm-config/*` a Termux y valida el modo monocle + rofi manualmente
   (con los atajos de teclado de `sxhkdrc`) — esto ya se ve y navega como el mockup.
3. En el código Kotlin del fork, ubica dónde `Loader.java` infla el Surface/View
   del servidor X — ahí es donde insertas `X11RestyledActivity.kt` (o adaptas
   esa misma Activity) para que además del Surface, escuche gestos.
4. Conecta `BspwmBridge.kt` reemplazando el `ProcessBuilder` directo por el
   mecanismo real de ejecución que uses (RUN_COMMAND intent hacia Termux, o
   ejecución directa si tu app comparte sharedUserId con com.termux).
5. Verifica cada gesto contra los atajos que ya probaste en el paso 2 —
   swipe arriba debe producir el mismo resultado que `super + space`.

## Advertencia real

El `ProcessBuilder` directo a `/data/data/com.termux/files/usr/bin/bash` en
`BspwmBridge.kt` solo funciona si tu app corre con permisos para acceder a esa
ruta (compartiendo sharedUserId con Termux, como hacen sus propios plugins) o
si usas el intent oficial `RUN_COMMAND` de Termux en su lugar. Está puesto
directo aquí para simplificar el scaffold — es el primer punto a validar
cuando lo pruebes en tu dispositivo real.
