#!/bin/bash

echo "🔍 Verificando API de emociones..."
echo ""

# Verificar conectividad básica
echo "1. Verificando conectividad con localhost:8000..."
if curl -s --connect-timeout 10 http://localhost:8000/health > /dev/null 2>&1; then
    echo "✅ Servidor responde en puerto 8000"
else
    echo "❌ No se puede conectar a localhost:8000"
    echo "   Verifica que el servidor de la API de emociones esté corriendo"
    exit 1
fi

echo ""

# Verificar endpoint específico
echo "2. Verificando endpoint /predict_audio..."
response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8000/predict_audio)
if [ "$response" -eq 405 ] || [ "$response" -eq 422 ]; then
    echo "✅ Endpoint /predict_audio existe (código: $response)"
elif [ "$response" -eq 404 ]; then
    echo "❌ Endpoint /predict_audio no encontrado (código: 404)"
else
    echo "⚠️  Endpoint /predict_audio responde con código: $response"
fi

echo ""

# Verificar archivos temporales
echo "3. Verificando archivos temporales..."
temp_dir="temp_service_files"
if [ -d "$temp_dir" ]; then
    file_count=$(ls -1 "$temp_dir"/*.wav 2>/dev/null | wc -l)
    if [ "$file_count" -gt 0 ]; then
        echo "✅ Encontrados $file_count archivos .wav en $temp_dir"
        
        # Mostrar información del primer archivo
        first_file=$(ls -1 "$temp_dir"/*.wav | head -1)
        file_size=$(stat -f%z "$first_file" 2>/dev/null || stat -c%s "$first_file" 2>/dev/null)
        file_size_mb=$((file_size / 1024 / 1024))
        echo "   Ejemplo: $(basename "$first_file") - ${file_size_mb}MB"
        
        # Verificar si el archivo se puede leer
        if [ -r "$first_file" ]; then
            echo "   ✅ Archivo se puede leer correctamente"
        else
            echo "   ❌ Problema de permisos para leer el archivo"
        fi
    else
        echo "⚠️  No se encontraron archivos .wav en $temp_dir"
    fi
else
    echo "❌ Directorio $temp_dir no existe"
fi

echo ""

# Verificar logs recientes
echo "4. Verificando logs recientes..."
if [ -f "logs/application.log" ]; then
    echo "✅ Archivo de logs encontrado"
    recent_errors=$(tail -50 logs/application.log | grep -i "error\|exception" | wc -l)
    if [ "$recent_errors" -gt 0 ]; then
        echo "   ⚠️  Encontrados $recent_errors errores recientes en los logs"
    else
        echo "   ✅ No hay errores recientes en los logs"
    fi
else
    echo "⚠️  No se encontró archivo de logs"
fi

echo ""
echo "✅ Verificación completada. Si hay problemas, revisa los puntos marcados con ❌" 