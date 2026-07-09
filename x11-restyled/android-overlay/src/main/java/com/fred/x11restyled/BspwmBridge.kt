package com.fred.x11restyled

import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * BspwmBridge
 *
 * Traduce gestos Android -> comandos bspc/rofi.
 * bspc habla con bspwm por un socket unix local — no requiere root para esto,
 * solo que el proceso de bspwm ya esté corriendo dentro de Termux con el
 * mismo usuario (com.termux), lo cual ya es el caso en tu setup actual.
 *
 * Estos son los MISMOS comandos que hoy tienes en sxhkdrc como puente temporal —
 * la diferencia es que ahora los dispara un gesto táctil real, no un atajo de teclado.
 */
class BspwmBridge {

    private fun runInTermux(command: String) {
        try {
            // Requiere que la app tenga permitido ejecutar en el contexto de Termux
            // (mismo sharedUserId o vía `termux-shell` si decides integrarlo así).
            val process = ProcessBuilder(
                "/data/data/com.termux/files/usr/bin/bash", "-c", command
            ).redirectErrorStream(true).start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            reader.forEachLine { /* log opcional */ }
            process.waitFor()
        } catch (e: Exception) {
            // TODO: mostrar toast/log de error real — por ahora silencioso
        }
    }

    fun openLauncher() {
        runInTermux("rofi -show drun -theme ~/.config/rofi/x11-restyled.rasi")
    }

    fun switchToLastWindow() {
        runInTermux("bspc node -f last.local")
    }

    fun showRecents() {
        // bspc no tiene "recientes" nativo — esto lista ventanas abiertas
        // y las pasa a rofi en modo custom para simular el overlay del mockup.
        runInTermux(
            "bspc query -N -d | xargs -I{} bspc query -T -n {} " +
            "| grep -o '\"name\":\"[^\"]*\"' | rofi -dmenu -theme ~/.config/rofi/x11-restyled.rasi"
        )
    }
}
