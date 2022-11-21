package fr.virtualworld.gglinnk.luckbot;

import fr.virtualworld.gglinnk.luckbot.exceptions.InvalidConfigurationFileException;
import fr.virtualworld.gglinnk.luckbot.managers.*;

import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

import net.luckperms.api.LuckPerms;
//import net.luckperms.api.model.user.UserManager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


@SuppressWarnings("unused")
public final class LuckBot extends JavaPlugin {

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private JDAManager jdaManager;
    private Logger logger;
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private LuckPerms luckPerms;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        logger.info("LuckBot is loading!");
        logger = this.getLogger();

        this.registerLuckPerms();
        this.initConfig();
        try {
            this.loadConfig();
        } catch (InvalidConfigurationFileException e) {
            logger.severe(e.toString());
            logger.warning("LuckBot failed to load");
            return;
        }
        this.jdaManagerInit();

        logger.info("LuckBot is loaded!");
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
        boolean discordTokenEmpty;
        boolean serverIpEmpty;
        int lastEKey;
        ArrayList<String> emptyKeys;
        String preString;

        this.config = getConfig();

        discordTokenEmpty = Objects.requireNonNull(this.config.getString("discord-token")).isEmpty();
        serverIpEmpty = Objects.requireNonNull(this.config.getString("server-ip")).isEmpty();

        emptyKeys = new ArrayList<>();
        if (discordTokenEmpty) emptyKeys.add("Discord Token");
        if (serverIpEmpty) emptyKeys.add("Server IP");

        lastEKey = emptyKeys.size() - 1;
        if (emptyKeys.size() > 1)
            preString = String.join(" and ", String.join(", ", emptyKeys.subList(0, lastEKey)), emptyKeys.get(lastEKey)) + " are ";
        else
            preString = emptyKeys.get(0) + " is ";

        logger.info(preString + "missing from config!");

        if (discordTokenEmpty) throw new InvalidConfigurationFileException("Discord Token is required for LuckBot to work!");
    }

    @Override
    public void onDisable() {
        logger.info("LuckBot is being disabled!");
        logger.info("LuckBot is disabled!");
    }
}
