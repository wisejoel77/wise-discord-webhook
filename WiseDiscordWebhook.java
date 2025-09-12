import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;

public class WiseDiscordWebhook {
    public static void main(String[] args) {
        try {
            // Read message from message.txt
            String message = new String(Files.readAllBytes(Paths.get("message.txt"))).trim();

            // Escape JSON-breaking characters (quotes + newlines)
            String safeMessage = message
                    .replace("\"", "\\\"")   // escape quotes
                    .replace("\n", "\\n");   // escape newlines

            // Build JSON payload
            String payload = String.format("{\"content\": \"%s\"}", safeMessage);

            // Debug output to check payload in GitHub logs
            System.out.println("DEBUG Payload: " + payload);

            // Get webhook URL from environment variable
            String webhookUrl = System.getenv("DISCORD_WEBHOOK");
            if (webhookUrl == null || webhookUrl.isEmpty()) {
                System.err.println("Error: DISCORD_WEBHOOK environment variable not set.");
                return;
            }

            // Open connection
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Send payload
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == 204) {
                System.out.println("✅ Message sent successfully to Discord.");
            } else {
                System.out.println("❌ Failed to send message. Response code: " + responseCode);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
