package gotocr.gotocr.service.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

public final class ImagenUtil {

    private static final long TAMANO_MAXIMO =
            5L * 1024 * 1024;

    private static final Set<String> TIPOS_PERMITIDOS =
            Set.of(
                    "image/jpeg",
                    "image/png",
                    "image/webp"
            );

    private ImagenUtil() {
    }

    public static void validar(
            MultipartFile archivo) {

        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una imagen."
            );
        }

        if (archivo.getSize() > TAMANO_MAXIMO) {
            throw new IllegalArgumentException(
                    "La imagen no puede superar los 5 MB."
            );
        }

        String tipo = archivo.getContentType();

        if (tipo == null ||
                !TIPOS_PERMITIDOS.contains(tipo)) {

            throw new IllegalArgumentException(
                    "Solo se permiten imágenes JPG, PNG o WEBP."
            );
        }
    }

    public static byte[] obtenerBytes(
            MultipartFile archivo) {

        validar(archivo);

        try {
            return archivo.getBytes();

        } catch (IOException e) {

            throw new IllegalStateException(
                    "No fue posible leer la imagen.",
                    e
            );
        }
    }
}