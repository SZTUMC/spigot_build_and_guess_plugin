package org.mick.build_and_guess;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.mick.build_and_guess.events.ChatHandler;
import org.mick.build_and_guess.events.CommandWatcher;
import org.bukkit.command.CommandException;

import java.util.Objects;
import java.util.logging.Logger;

public final class Build_and_guess extends JavaPlugin {

    public ChatHandler chatHandler;

    public CommandWatcher commandWatcher;

    public Server server = this.getServer();

    public Logger logger = this.getLogger();

    /**
     * 指令执行器: 对终端指令执行入口进行的封装
     * @param command  - 命令 + 参数. 例如: test abc 123
     * @return found 若命令未找到返回false
     * @throws CommandException - 当执行命令期间出现未捕获的异常时抛出
     */
    public boolean commandExecutor(String command) throws CommandException{
        return server.dispatchCommand(server.getConsoleSender(), command);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        // 初始化参数
        chatHandler = new ChatHandler(logger, server, this);
        commandWatcher = new CommandWatcher(logger, server);

        // 注册指令事件
        Bukkit.getPluginManager().registerEvents(chatHandler, this);
        Bukkit.getPluginManager().registerEvents(commandWatcher, this);

        Objects.requireNonNull(this.getCommand("chat_manger")).setExecutor(this);
        boolean found = commandExecutor("say Build and Guess Plugin on!");
        logger.info("commandExecutor:" + found);

    }

    /**
     * chat_manger: 聊天控制器
     * @param sender    指令发送者，可以是玩家、远程控制台、本地控制台、命令方块、其他实现了Permissible接口的实现类
     * @param command   指令:就是this.getCommand("chat_manger")的返回值，存储着在plugin.yml中声明的各种指令信息
     * @param label     指令的名称:如/chat_manger的指令label就是"chat_manger"
     * @param args      指令的参数:加入输入的完整指令是/chat_manger 1 2 3，那么该args数组就是[1，2，3]
     * @return bool
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!label.equalsIgnoreCase("chat_manger")){
            logger.info("command" + command.getName());
            return false;
        }

        // 仅控制台指令
        if(sender instanceof ConsoleCommandSender) {
            if(args.length != 1) {
                sender.sendMessage("error args num");
                return false;
            }

            // 参数决定聊天是否开启
            switch (args[0]){
                case "on":
                    this.chatHandler.setChatEnabled(true);
                    sender.sendMessage("set chat on now");
                    break;
                case "off":
                    this.chatHandler.setChatEnabled(false);
                    sender.sendMessage("set chat off now");
                    break;
                default:
                    sender.sendMessage("error arg");
                    break;
            }
        } else {
            sender.sendMessage("Only use in console command");
        }
        return true;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}