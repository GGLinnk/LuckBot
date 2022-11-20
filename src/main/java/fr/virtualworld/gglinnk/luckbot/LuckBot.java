package fr.virtualworld.gglinnk.luckbot;

import com.google.common.primitives.Booleans;
import fr.virtualworld.gglinnk.luckbot.exceptions.InvalidConfigurationFileException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Logger;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import net.luckperms.api.LuckPerms;

import net.luckperms.api.model.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


class JDAManager {
    private JDA jda;

    private Logger logger;
    private FileConfiguration config;


    protected JDAManager(Logger logger, FileConfiguration config) {
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
            logger.warning(e.toString());
            logger.warning("LuckBot cannot continue. Plugin will disable itself.");
        }
    }
}

public final class LuckBot extends JavaPlugin {
    private boolean loaded = false;

    private JDAManager jdaManager;
    private Logger logger;
    private LuckPerms luckPerms;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        logger = this.getLogger();

        this.registerLuckPerms();
        this.initConfig();
        this.loadConfig();
        this.jdaManagerInit();

        this.loaded = true;
    }

    private void jdaManagerInit() {
        this.jdaManager = new JDAManager(this.logger, this.config);
    }

    private void registerLuckPerms() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);

        if (provider != null) {
            this.luckPerms = provider.getProvider();
            // MOVEME UserManager userManager = this.luckPerms.getUserManager();
        }
    }

    private void initConfig() {
        saveDefaultConfig();
    }

    private void loadConfig() throws InvalidConfigurationFileException {
        boolean discordTokenMissing;
        boolean serverIpMissing;
        ArrayList<String> emptyKeys;
        Consumer<String> log;
        String preString;

        this.config = getConfig();

        discordTokenMissing = Objects.requireNonNull(this.config.getString("discord-token")).isEmpty();
        serverIpMissing = Objects.requireNonNull(this.config.getString("server-ip")).isEmpty();

        emptyKeys = new ArrayList<String>();
        if (discordTokenMissing) emptyKeys.add("Discord Token");
        if (serverIpMissing) emptyKeys.add("Server IP");

        log = discordTokenMissing ? logger::warning : logger::info;

        preString = String.join(", ", emptyKeys);
        preString += (emptyKeys.size() > 1) ? " are " : " is ";

        log.accept(preString + "missing from config!");

        if (discordTokenMissing) throw new InvalidConfigurationFileException("Discord Token is required for LuckBot to work!");
    }

    @Override
    public void onDisable() {
        if (!this.loaded) return;

    }
}
