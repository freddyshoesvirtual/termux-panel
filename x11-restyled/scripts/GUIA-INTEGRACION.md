# Guía de Integración X11 Restyled

## Paso 1 — Clonar termux-x11

```bash
./scripts/clone-termux-x11.sh
```

Esto clona el repo con submódulos a `~/termux-x11` y busca `Loader.java` automáticamente.

Peso estimado: ~2GB con submódulos.

## Paso 2 — Compilar sin modificar

```bash
cd ~/termux-x11
./gradlew assembleDebug
```

Esto compila la app oficial sin cambios. Confirma que el entorno build funciona.

Si falla por falta de SDK, crear `local.properties`:
```
sdk.dir=/path/to/android/sdk
```

## Paso 3 — Ubicar el punto de integración

Dentro de `~/termux-x11`, buscar dónde se infla el Surface X11:

```bash
grep -rn "setContentView\|SurfaceView\|Surface" app/src/main/java/ --include="*.java" --include="*.kt" | grep -i "x11\|touch\|render" | head -20
```

El README oficial de termux-x11 dice que `Loader.java` es el punto de entrada para apps de terceros.

El método clave suele ser algo como:
```java
// com.termux.x11.Loader
public static View createX11Surface(Context context) { ... }
```

## Paso 4 — Fusionar X11RestyledActivity

Una vez ubicado el método que devuelve el SurfaceView de X11:

1. Abre `X11RestyledActivity.kt` en el editor
2. En la línea 41-44, reemplaza el TODO por:
   ```kotlin
   val x11Surface = Loader.createX11Surface(this)
   setContentView(x11Surface.view)
   ```
   (Ajusta nombre de método y paquete según lo que encuentres en Loader.java)

3. Copia `X11RestyledActivity.kt` y `BspwmBridge.kt` dentro del proyecto de termux-x11 (mismo package o ajusta imports)

## Paso 5 — Probar gestos vs teclado

Los gestos duplican estos atajos de `sxhkdrc`:

| Gesto | Comando | Equivalente teclado |
|-------|---------|---------------------|
| Swipe arriba | `rofi -show drun -theme ...` | `super + space` |
| Swipe lateral | `bspc node -f last.local` | `super + Tab` |
| Long press | lista ventanas en rofi | (no hay, solo táctil) |

Valida cada atajo por teclado primero (en Termux con bspwm corriendo) antes de probar el gesto.

## Paso 6 (futuro) — Overlay Compose

El launcher con grid de iconos + dock será una capa Compose dibujada **encima** del Surface X11, no dentro de X11. Eso permite abrir/cerrar apps sin re-renderizar el servidor X.

Estructura tentativa:
```
X11RestyledActivity (FrameLayout raíz)
├── X11SurfaceView (capa fondo — render termux-x11)
└── LauncherOverlay (capa frontal — Compose, transparente cuando oculto)
```
