package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;

@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {
    private static final String DATE_TIME_UTC = "<p>%s UTC</p>";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter writeOut = resp.getWriter();
        writeOut.print("<html><boby>");
        writeOut.print("<h3>It is my first servlet with dateTime in UTC format</h3>");
        Map<String, String[]> parameterMap = req.getParameterMap();
        if (parameterMap.size()>0) {
            String tz = req.getParameterMap().get("timezone")[0];
            if (tz.length()>1) {
                tz = String.format("<p>%s</p>", updateDateTimeForTimeZone(tz));
                writeOut.print(tz);
            }
        }  else {
            OffsetDateTime now = OffsetDateTime.now( ZoneOffset.UTC );
            String dts = now.toString().replace("T", " ").substring(0,19);
            writeOut.print(String.format(DATE_TIME_UTC, dts));
        }
        // TODO get query parameters
        parameterMap.forEach((name, value) -> {
            writeOut.print("<p>ParamName = ${name}; ParamValue = ${value}</p>"
                    .replace("${name}", name)
                    .replace("${value}", Arrays.toString(value)));
        });

        writeOut.print("</boby></html>");
        writeOut.close();
    }
    private static String updateDateTimeForTimeZone(String tZone) throws UnsupportedEncodingException {
        String tz = URLEncoder.encode(tZone, "UTF-8").replace("%27",""); // %27UTC+2%27
        try {
            ZoneId zoneId = ZoneId.of(tz);
            DateTimeFormatter zDTFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
            ZonedDateTime zonedDateTime = LocalDateTime.now()
                    .atZone(ZoneId.systemDefault())
                    .withZoneSameInstant(zoneId);

            String utcFormatter = zonedDateTime.format(zDTFormatter);
            return utcFormatter;
        } catch(DateTimeException e) {
            return "Invalid time zone! Exception: "+e.getMessage();
        }
    }
}
