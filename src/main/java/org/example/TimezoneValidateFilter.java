package org.example;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@WebFilter(value = "/*")
public class TimezoneValidateFilter extends HttpFilter {
    public static final Logger log = LoggerFactory.getLogger(TimezoneValidateFilter.class);
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        // TODO get filter mapped Parameter "http://localhost:8080/time?timezone=UTC+2"
        if (req.getParameterMap().size()==0) {
            chain.doFilter(req, res);
            return;
        }
        String tz = req.getParameterMap().get("timezone")[0];
        tz = URLEncoder.encode(tz, "UTF-8").replace("%27",""); // %27UTC+2%27
        try {
            ZoneId zoneId = ZoneId.of(tz);
            DateTimeFormatter zDTFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
            ZonedDateTime zonedDateTime = LocalDateTime.now()
                    .atZone(ZoneId.systemDefault())
                    .withZoneSameInstant(zoneId);
            log.info(zDTFormatter.toString()+" "+zonedDateTime);
            chain.doFilter(req, res);
        } catch(DateTimeException e) {
            res.setStatus(400);
            res.setContentType("application/json");
            res.getWriter().write("{\"Error\": \"Invalid time zone\"}");
            res.getWriter().close();
        }
    }
}
