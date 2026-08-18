
package gotocr.gotocr.controller;

import gotocr.gotocr.domain.Hotel;
import gotocr.gotocr.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final HotelService hotelService;

    @GetMapping({"/", "/index"})
    public String inicio(Model model) {

        List<Hotel> hotelesDestacados = hotelService.listarHoteles()
                .stream()
                .filter(hotel -> hotel.getCalificacionPromedio() != null)
                .sorted(
                        Comparator.comparing(
                                Hotel::getCalificacionPromedio
                        ).reversed()
                )
                .limit(3)
                .toList();

        model.addAttribute(
                "hotelesDestacados",
                hotelesDestacados
        );

        return "index";
    }
}