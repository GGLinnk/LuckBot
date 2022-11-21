package fr.gglinnk.luckbot.managers;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Logger;

public class JDAManager {
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private JDA jda;
    private final Logger logger;
    private final FileConfiguration config;

    public JDAManager(Logger logger, FileConfiguration config) {
        this.logger = logger;
        this.config = config;

        init();
    }

    private void init() {
        JDABuilder jdaBuilder;

        jdaBuilder = JDABuilder.createDefault(this.config.getString("discord-token"));

        // Disable parts of the cache
        // jdaBuilder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        // Enable the bulk delete event
        jdaBuilder.setBulkDeleteSplittingEnabled(false);
        // Set activity (like "playing Something")
        jdaBuilder.setActivity(Activity.playing("Minecraft: " + config.getString("server-ip")));

        try {
            jda = jdaBuilder.build();
        } catch (Exception e) {
            logger.severe(e.toString());
        }
    }

    public void shutdown() {
        jda.shutdown();
    }
}
