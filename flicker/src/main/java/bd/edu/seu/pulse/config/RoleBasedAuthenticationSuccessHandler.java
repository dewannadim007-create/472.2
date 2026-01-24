package bd.edu.seu.pulse.config;

import bd.edu.seu.pulse.model.User;
import bd.edu.seu.pulse.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RoleBasedAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            response.sendRedirect("/login?error=true");
            return;
        }

        String expectedRole = request.getParameter("expectedRole");

        if (expectedRole != null && !expectedRole.isEmpty()) {
            if (!user.getRole().equalsIgnoreCase(expectedRole)) {
                String loginUrl = "/landing";
                if (expectedRole.equalsIgnoreCase("READER"))
                    loginUrl = "/login/reader";
                else if (expectedRole.equalsIgnoreCase("WRITER"))
                    loginUrl = "/login/writer";
                else if (expectedRole.equalsIgnoreCase("ADMIN"))
                    loginUrl = "/login/admin";

                request.getSession().invalidate();
                response.sendRedirect(loginUrl + "?error=true");
                return;
            }
        }

        response.sendRedirect("/dashboard");
    }
}
