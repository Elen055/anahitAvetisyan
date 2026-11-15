import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class WebServer {

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
        );

        // Open HTML and CSS files
        server.createContext("/", exchange -> {

            String path = exchange.getRequestURI().getPath();

            String fileName = path.equals("/")
                    ? "index.html"
                    : path.substring(1);

            if (!fileName.equals("index.html") &&
                    !fileName.equals("style.css")) {

                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }

            Path file = Path.of("website", fileName);
            byte[] content = Files.readAllBytes(file);

            String type = fileName.endsWith(".css")
                    ? "text/css; charset=UTF-8"
                    : "text/html; charset=UTF-8";

            exchange.getResponseHeaders().set("Content-Type", type);
            exchange.sendResponseHeaders(200, content.length);

            try (OutputStream output = exchange.getResponseBody()) {
                output.write(content);
            }
        });

        // Employees
        server.createContext("/employees", exchange -> {

            StringBuilder data = new StringBuilder();

            try (Connection connection = ConnectionDB.connect();
                 Statement statement = connection.createStatement();
                 ResultSet result = statement.executeQuery(
                         "SELECT * FROM Employees")) {

                data.append("<h2>Employees</h2>");
                data.append("<table border='1'>");
                data.append("<tr><th>ID</th><th>Name</th><th>Surname</th><th>Salary</th></tr>");

                while (result.next()) {
                    data.append("<tr>");
                    data.append("<td>").append(result.getInt("ID")).append("</td>");
                    data.append("<td>").append(result.getString("First Name")).append("</td>");
                    data.append("<td>").append(result.getString("Last Name")).append("</td>");
                    data.append("<td>").append(result.getString("Salary")).append("</td>");
                    data.append("</tr>");
                }

                data.append("</table>");

            } catch (Exception e) {
                data.append("Error: ").append(e.getMessage());
            }

            sendResponse(exchange, data.toString());
        });

        // Contacts
        server.createContext("/contacts", exchange -> {

            StringBuilder data = new StringBuilder();

            try (Connection connection = ConnectionDB.connect();
                 Statement statement = connection.createStatement();
                 ResultSet result = statement.executeQuery(
                         "SELECT * FROM EmployeeContacts")) {

                data.append("<h2>Employee Contacts</h2>");
                data.append("<table border='1'>");
                data.append("<tr><th>Contact ID</th><th>Employee ID</th><th>Contact Info</th><th>Contact Type</th></tr>");

                while (result.next()) {
                    data.append("<tr>");
                    data.append("<td>").append(result.getInt("ContactID")).append("</td>");
                    data.append("<td>").append(result.getInt("EmployeeID")).append("</td>");
                    data.append("<td>").append(result.getString("ContactInfo")).append("</td>");
                    data.append("<td>").append(result.getString("ContactType")).append("</td>");
                    data.append("</tr>");
                }

                data.append("</table>");

            } catch (Exception e) {
                data.append("Error: ").append(e.getMessage());
            }

            sendResponse(exchange, data.toString());
        });

        // Payments
        server.createContext("/payments", exchange -> {

            StringBuilder data = new StringBuilder();

            try (Connection connection = ConnectionDB.connect();
                 Statement statement = connection.createStatement();
                 ResultSet result = statement.executeQuery(
                         "SELECT * FROM EmployeeFees")) {

                data.append("<h2>Employee Payments</h2>");
                data.append("<table border='1'>");
                data.append("<tr><th>ID</th><th>Employee ID</th><th>Payment Date</th><th>Amount</th><th>Cheque</th></tr>");

                while (result.next()) {
                    data.append("<tr>");
                    data.append("<td>").append(result.getInt("ID")).append("</td>");
                    data.append("<td>").append(result.getInt("EmployeeID")).append("</td>");
                    data.append("<td>").append(result.getString("PaymentDate")).append("</td>");
                    data.append("<td>").append(result.getString("Amount")).append("</td>");
                    data.append("<td>").append(result.getString("Cheque")).append("</td>");
                    data.append("</tr>");
                }

                data.append("</table>");

            } catch (Exception e) {
                data.append("Error: ").append(e.getMessage());
            }

            sendResponse(exchange, data.toString());
        });

        server.start();

        System.out.println("Website running!");
        System.out.println("Open http://localhost:8080");
    }

    // Send information back to the website
    public static void sendResponse(
            com.sun.net.httpserver.HttpExchange exchange,
            String text
    ) throws java.io.IOException {

        byte[] response = text.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type", "text/html; charset=UTF-8"
        );

        exchange.sendResponseHeaders(200, response.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(response);
        }
    }
}
