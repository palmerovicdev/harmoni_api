package com.palmerodev.harmoni_api.helper;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service // Marca esta clase como un componente de servicio de Spring
public class VideoProcessingService {

    private static final String TEMP_PROCESSING_DIR = "temp_service_files/"; // Directorio temporal para archivos de servicio
    private static final String FFMPEG_PATH = "ffmpeg"; // Asegúrate de que FFmpeg esté en el PATH o especifica la ruta completa

    public VideoProcessingService() {
        // Asegúrate de que el directorio temporal exista al iniciar el servicio
        try {
            Files.createDirectories(Paths.get(TEMP_PROCESSING_DIR));
        } catch (IOException e) {
            System.err.println("Error al crear el directorio temporal para el servicio: " + TEMP_PROCESSING_DIR);
            e.printStackTrace();
            // Considera lanzar una RuntimeException aquí si el directorio es crítico
        }
    }

    /**
     * Procesa un MultipartFile de video para extraer su audio en formato WAV. Los archivos temporales generados (entrada y salida) se quedan en el disco. Es responsabilidad del
     * llamador gestionar y eliminar estos archivos.
     *
     * @param videoFile El MultipartFile de entrada que contiene el video.
     * @return Un objeto java.io.File que representa el archivo WAV con el audio extraído.
     * @throws IOException          Si ocurre un error de I/O al guardar/leer archivos.
     * @throws InterruptedException Si el proceso FFmpeg es interrumpido.
     * @throws RuntimeException     Si FFmpeg falla o excede el tiempo límite.
     */
    public File extractAudioToWav(MultipartFile videoFile) throws IOException, InterruptedException {
        // Generar nombres de archivo únicos para evitar colisiones
        String uniqueId = UUID.randomUUID().toString();
        String originalFileName = videoFile.getOriginalFilename();
        String fileExtension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = originalFileName.substring(dotIndex);
        }

        Path inputFilePath = Paths.get(TEMP_PROCESSING_DIR, uniqueId + "_original" + fileExtension);
        String outputFileName = uniqueId + "_extracted_audio.wav"; // Nombre único para el archivo de salida
        Path outputFilePath = Paths.get(TEMP_PROCESSING_DIR, outputFileName);

        try {
            // 1. Guardar el MultipartFile de entrada en el disco
            Files.copy(videoFile.getInputStream(), inputFilePath, StandardCopyOption.REPLACE_EXISTING);

            // 2. Construir y ejecutar el comando FFmpeg
            List<String> command = Arrays.asList(
                    FFMPEG_PATH,
                    "-i", inputFilePath.toAbsolutePath().toString(), // Archivo de entrada
                    "-vn", // Sin video
                    "-acodec", "pcm_s16le", // Codec de audio WAV
                    "-ar", "44100", // Sample rate
                    "-ac", "2", // Canales
                    "-y", // Sobreescribir si existe
                    outputFilePath.toAbsolutePath().toString() // Archivo de salida
                                                );

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.inheritIO(); // Esto enviará la salida de FFmpeg a la consola de tu aplicación
            Process process = pb.start();

            boolean finished = process.waitFor(5, TimeUnit.MINUTES); // Timeout de 5 minutos

            if (!finished) {
                process.destroyForcibly();
                throw new RuntimeException("FFmpeg excedió el tiempo límite al extraer audio.");
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                // Leer el error stream de FFmpeg si el código de salida no es 0
                String errorOutput = new String(process.getErrorStream().readAllBytes());
                throw new RuntimeException("FFmpeg falló con código de salida: " + exitCode + ". Errores: " + errorOutput);
            }

            // 3. Devolver el objeto File del audio extraído
            File outputFile = outputFilePath.toFile();
            if (!outputFile.exists() || !outputFile.canRead()) {
                throw new IOException("El archivo de audio de salida no se encontró o no se puede leer después del procesamiento: " + outputFile.getAbsolutePath());
            }
            return outputFile;

        } finally {
            // En este método de servicio, NO borramos el archivo de salida (outputFilePath)
            // porque el llamador espera recibirlo. Solo borramos el archivo de entrada temporal.
            try {
                Files.deleteIfExists(inputFilePath);
            } catch (IOException e) {
                System.err.println("Error al eliminar el archivo temporal de entrada del servicio: " + inputFilePath + " - " + e.getMessage());
            }
        }
    }

}