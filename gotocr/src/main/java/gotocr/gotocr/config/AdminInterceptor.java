package gotocr.gotocr.config;

import gotocr.gotocr.domain.Cliente;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    private static final int ID_ROL_ADMIN = 2;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("idCliente") == null) {

            response.sendRedirect("/login");
            return false;
        }

        Integer idRol = (Integer) session.getAttribute("idRol");

        if (idRol == null) {
            Object clienteSesion = session.getAttribute("clienteSesion");

            if (clienteSesion instanceof Cliente cliente
                    && cliente.getRol() != null) {

                idRol = cliente.getRol().getIdRol();
                session.setAttribute("idRol", idRol);
            }
        }

        if (idRol == null || idRol != ID_ROL_ADMIN) {

            // Para la vista se redirige; para operaciones AJAX se devuelve 403.
            if (request.getRequestURI().equals("/admin")
                    || request.getRequestURI().equals("/admin/")) {

                response.sendRedirect("/?sinPermiso=1");

            } else {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "No tiene permisos de administrador"
                );
            }

            return false;
        }

        return true;
    }
}
