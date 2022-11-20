package fr.virtualworld.gglinnk.luckbot;

import fr.virtualworld.gglinnk.luckbot.exceptions.InvalidConfigurationFileException;
import fr.virtualworld.gglinnk.luckbot.managers.*;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Logger;

import net.luckperms.api.LuckPerms;
//import net.luckperms.api.model.user.UserManager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


@SuppressWarnings("unused")
public final class LuckBot extends JavaPlugin {
    private boolean loaded = false;

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private JDAManager jdaManager;
    private Logger logger;
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
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
            // MoveMe // UserManager userManager = this.luckPerms.getUserManager();
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

        emptyKeys = new ArrayList<>();
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
        logger.info("LuckBot is being disabled!");
    }
}
