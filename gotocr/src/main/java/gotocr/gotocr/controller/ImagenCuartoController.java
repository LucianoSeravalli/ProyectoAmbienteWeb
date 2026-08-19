package gotocr.gotocr.controller;

import gotocr.gotocr.domain.ImagenCuarto;
import gotocr.gotocr.service.ImagenCuartoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/imagenes-cuartos")
@RequiredArgsConstructor
public class ImagenCuartoController {

    private final ImagenCuartoService imagenCuartoService;

    @GetMapping("/{idImagen}")
    @ResponseBody
    public ResponseEntity<byte[]> obtenerImagen(
            @PathVariable Integer idImagen) {

        ImagenCuarto imagen =
                imagenCuartoService
                        .buscarPorId(idImagen)
                        .orElse(null);

        if (imagen == null
                || imagen.getImagen() == null
                || imagen.getImagen().length == 0) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity
                .ok()
                .header(
                        "Content-Type",
                        imagen.getTipoImagen()
                )
                .body(
                        imagen.getImagen()
                );
    }
}