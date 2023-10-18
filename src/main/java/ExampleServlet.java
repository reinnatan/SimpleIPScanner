import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


@WebServlet("/ExampleServlet")
public class ExampleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("index.html");
        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder sb = new StringBuilder();
        for (String line; (line = reader.readLine()) != null;) {
            sb.append(line);
        }
        response.getWriter().println(sb);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ipStartReq = req.getParameter("ipStart");
        String ipEndReq = req.getParameter("ipEnd");
        String splitIpStart[] = ipStartReq.replace(".", "-").split("-");
        String splitIpEnd [] = ipEndReq.replace(".", "-").split("-");

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<br/><br/>");
        out.println("<html>");
        out.println("<head><title>Response Computer</title></head>");
        out.println("<body>");
        out.println("<table border='1'>");
        out.println("<caption>Computer status</caption>");
        out.println("<th>IP</th><th>Status</th>");

        Map<String, Boolean> ipStatus = new TreeMap<String, Boolean>();

        if(splitIpStart.length>0 && splitIpEnd.length>0) {
            int ipStr = Integer.parseInt(splitIpStart[splitIpStart.length - 1]);
            int ipEnd = Integer.parseInt(splitIpEnd[splitIpEnd.length - 1]);

            for(int i=ipStr; i<=ipEnd; i++) {
                String concatIP = splitIpStart[0]+"."+splitIpStart[1]+"."+splitIpStart[2]+"."+i;
                String[] argsPing = new String[]{"ping", concatIP, "-c", "2"};
                ProcessBuilder builder = new ProcessBuilder(argsPing);
                Process prcs = builder.start();
                BufferedReader is =
                        new BufferedReader(new InputStreamReader(prcs.getInputStream(  )));
                String line ="";
                while ((line = is.readLine(  )) != null) {
                    if (line.contains("transmitted") && line.contains("0.0% packet loss")) {
                        ipStatus.put(concatIP, true);
                        System.out.println(concatIP + " : " + line);
                    } else {
                        ipStatus.put(concatIP, false);
                        System.out.println(concatIP + " : " + line);
                    }
                }
            }

            for(Map.Entry<String, Boolean> keyValue: ipStatus.entrySet()){
                String connected = "Not Connected";
                if(keyValue.getValue()){
                    connected = "Connected";
                }
                out.println("<tr><td>"+keyValue.getKey()+"</td><td>"+connected+"</td></tr>");
            }
            out.println("</table>");
        }
    }
}
