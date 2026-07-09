package com.fred.x11restyled

import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * BspwmBridge
 *
 * Traduce gestos Android -> comandos bspc/rofi.
 * Usa `su -c` porque el dispositivo tiene root — ejecución directa sin
 * pasar por el contexto de Termux, sin sharedUserId ni intent RUN_COMMAND.
 *
 * Estos son los MISMOS comandos que sxhkdrc como puente temporal —
 * la diferencia es que ahora los dispara un gesto táctil real, no un atajo de teclado.
 */
class BspwmBridge {

    private fun runAsRoot(command: String) {
        try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            reader.forEachLine { /* log opcional */ }
            process.waitFor()
        } catch (e: Exception) {
            // TODO: mostrar toast/log de error real si el usuario deniega root
        }
    }

    fun openLauncher() {
        runAsRoot("rofi -show drun -theme ~/.config/rofi/x11-restyled.rasi")
    }

    fun switchToLastWindow() {
        runAsRoot("bspc node -f last.local")
    }

    fun showRecents() {
        runAsRoot(
            "bspc query -N -d | xargs -I{} bspc query -T -n {} " +
            "| grep -o '\"name\":\"[^\"]*\"' | rofi -dmenu -theme ~/.config/rofi/x11-restyled.rasi"
        )
    }
}
