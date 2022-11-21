package fr.gglinnk.luckbot;

import fr.gglinnk.luckbot.exceptions.InvalidConfigurationFileException;
import fr.gglinnk.luckbot.managers.JDAManager;

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
    private final Logger logger = this.getLogger();
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private LuckPerms luckPerms;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        this.logger.info("LuckBot is loading!");

        this.registerLuckPerms();
        this.initConfig();
        try {
            this.loadConfig();
        } catch (InvalidConfigurationFileException e) {
            this.logger.severe(e.toString());
            this.logger.warning("LuckBot failed to load");
            return;
        }
        this.jdaManagerInit();

        this.logger.info("LuckBot is loaded!");
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
        ArrayList<String> emptyKeys = new ArrayList<>();
        String preString = "";
        boolean discordTokenEmpty;
        boolean serverIpEmpty;
        int lastEKey;

        this.config = getConfig();

        discordTokenEmpty = Objects.requireNonNull(this.config.getString("discord-token")).isEmpty();
        serverIpEmpty = Objects.requireNonNull(this.config.getString("server-ip")).isEmpty();

        if (discordTokenEmpty) emptyKeys.add("Discord Token");
        if (serverIpEmpty) emptyKeys.add("Server IP");

        lastEKey = emptyKeys.size() - 1;
        if (emptyKeys.size() > 1)
            preString = String.join(" and ", String.join(", ", emptyKeys.subList(0, lastEKey)), emptyKeys.get(lastEKey)) + " are ";
        else if (emptyKeys.size() == 1)
            preString = emptyKeys.get(0) + " is ";

        if (emptyKeys.size() > 0) this.logger.info(preString + "missing from config!");

        if (discordTokenEmpty) throw new InvalidConfigurationFileException("Discord Token is required for LuckBot to work!");
    }

    @Override
    public void onDisable() {
        this.logger.info("LuckBot is being disabled!");
        jdaManager.shutdown();
        this.logger.info("LuckBot is disabled!");
    }
}
