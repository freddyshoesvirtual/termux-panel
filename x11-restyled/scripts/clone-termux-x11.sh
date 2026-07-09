#!/data/data/com.termux/files/usr/bin/bash
# ============================================================
# Clonar termux-x11 con submódulos y compilarlo sin modificar
# ============================================================
# Requisitos:
#   - Termux con x11-repo activo
#   - Gradle + Android SDK (para compilar)
#   - Espacio suficiente (~2GB con submódulos)
# ============================================================

set -e

REPO_URL="https://github.com/termux/termux-x11"
TARGET_DIR="${1:-$HOME/termux-x11}"

echo "=== Clonando termux-x11 con submódulos ==="
echo "Destino: $TARGET_DIR"
echo ""

if [ -d "$TARGET_DIR" ]; then
    echo "El directorio $TARGET_DIR ya existe."
    echo "Si quieres clonar de nuevo, bórralo primero: rm -rf $TARGET_DIR"
    exit 1
fi

git clone --recurse-submodules "$REPO_URL" "$TARGET_DIR"

echo ""
echo "=== Clonación completada ==="
echo "Pesos estimados:"
du -sh "$TARGET_DIR"
du -sh "$TARGET_DIR/.git/modules" 2>/dev/null || echo "(sin submódulos detectados)"
echo ""

# ============================================================
# Ubicar Loader.java
# ============================================================
echo "=== Buscando Loader.java ==="
LOADER=$(find "$TARGET_DIR" -name "Loader.java" -type f 2>/dev/null | head -5)
if [ -n "$LOADER" ]; then
    echo "Encontrado en:"
    echo "$LOADER" | while read -r f; do
        echo "  $f"
        echo "    Líneas: $(wc -l < "$f")"
    done
else
    echo "NO ENCONTRADO (puede llamarse distinto o estar en rutas compiladas)"
fi

echo ""
echo "=== Buscando Activity principal ==="
find "$TARGET_DIR" -name "*Activity*.kt" -o -name "*Activity*.java" 2>/dev/null | head -10

echo ""
echo "=== Listo ==="
echo "Próximo paso manual: abrir $TARGET_DIR en Android Studio o editor"
echo "y buscar dónde Loader.java infla el Surface/View del servidor X11."
echo "Ese es el punto donde se inserta X11RestyledActivity."
