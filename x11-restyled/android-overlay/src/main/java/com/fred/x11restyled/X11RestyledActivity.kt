package com.fred.x11restyled

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import kotlin.math.abs

/**
 * X11RestyledActivity
 *
 * Esta Activity NO reemplaza termux-x11 — lo EMBEBE.
 * El render del Surface X11 lo sigue haciendo el código nativo de termux-x11
 * (vía com.termux.x11.Loader, pensado por el propio proyecto para uso de terceros).
 * Lo único que agregamos es:
 *   1. Un GestureDetector nativo de Android (cero relación con X11/libinput).
 *   2. Un puente de comandos hacia bspwm/rofi por su socket de control.
 *
 * NOTA DE INTEGRACIÓN REAL:
 * Reemplaza `X11SurfaceHost` por la clase real que expone termux-x11 tras
 * clonar el repo con submódulos (`Loader.java` es el punto de entrada que
 * el propio proyecto documenta para apps de terceros). Aquí se deja como
 * interfaz para que la conectes sin bloquear el resto del código.
 */
class X11RestyledActivity : ComponentActivity() {

    private lateinit var gestureDetector: GestureDetector
    private lateinit var bspwmBridge: BspwmBridge

    companion object {
        private const val SWIPE_MIN_DISTANCE = 120
        private const val SWIPE_MAX_OFF_PATH = 150
        private const val SWIPE_THRESHOLD_VELOCITY = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bspwmBridge = BspwmBridge()

        // TODO integración real: inicializar aquí el X11SurfaceHost de termux-x11
        // (equivalente a lo que hace com.termux.x11.Loader al arrancar su Activity)
        // val x11Surface = X11SurfaceHost(this)
        // setContentView(x11Surface.view)

        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {

            override fun onFling(
                e1: MotionEvent?, e2: MotionEvent,
                velocityX: Float, velocityY: Float
            ): Boolean {
                if (e1 == null) return false
                val dx = e2.x - e1.x
                val dy = e2.y - e1.y

                // swipe hacia arriba desde el borde inferior -> abrir launcher (rofi)
                if (abs(dy) > SWIPE_MIN_DISTANCE && abs(velocityY) > SWIPE_THRESHOLD_VELOCITY && dy < 0) {
                    bspwmBridge.openLauncher()
                    return true
                }
                // swipe lateral -> cambiar a la última ventana (bspc node -f last.local)
                if (abs(dx) > SWIPE_MIN_DISTANCE && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
                    && abs(dy) < SWIPE_MAX_OFF_PATH) {
                    bspwmBridge.switchToLastWindow()
                    return true
                }
                return false
            }

            override fun onLongPress(e: MotionEvent) {
                // long-press en pantalla -> vista de "recientes"
                bspwmBridge.showRecents()
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }
}
