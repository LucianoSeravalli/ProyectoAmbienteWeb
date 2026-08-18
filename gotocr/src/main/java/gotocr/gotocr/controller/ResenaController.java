
package gotocr.gotocr.controller;


import gotocr.gotocr.service.ResenaHotelService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/resenas")
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaHotelService resenaHotelService;

    @PostMapping("/guardar")
    public String guardarResena(
            @RequestParam Integer idHotel,
            @RequestParam Integer calificacion,
            @RequestParam(required = false) String comentario,
            HttpSession session) {

        Integer idCliente =
                (Integer) session.getAttribute(
                        "idCliente"
                );

        if (idCliente == null) {
            return "redirect:/login";
        }

        resenaHotelService.insertarResenaHotel(
                idCliente,
                idHotel,
                calificacion,
                comentario
        );

        return "redirect:/hoteles/" + idHotel;
    }
}