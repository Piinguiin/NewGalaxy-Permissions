package de.piinguiin.permissions.api;

import de.piinguiin.permissions.Permissions;
import de.piinguiin.permissions.groups.PermissionGroupManager;
import de.piinguiin.permissions.user.PermissionUserManager;
import de.piinguiin.permissions.uuid.UUIDFetcher;
import java.util.UUID;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;


public class PermissionProxyCommand extends Command {

  private final Permissions permissions;
  private final Plugin plugin;
  private final PermissionGroupManager groupManager;
  private final PermissionUserManager userManager;

  public PermissionProxyCommand(Permissions permissions, Plugin plugin) {
    super("Permission","permission.edit","permissions");
    this.permissions = permissions;
    this.plugin = plugin;
    this.groupManager = permissions.getPermissionGroupManager();
    this.userManager = permissions.getPermissionUserManager();
  }

  @Override
  public void execute(CommandSender commandSender, String[] args) {

    if(!commandSender.hasPermission("permission.edit")){
      return;
    }

    if(args.length == 0){
      sendAllCommands(commandSender);
      return;
    }

    String sub = args[0];

    if(sub.equalsIgnoreCase("group")){

      if(args.length == 1){
        sendGroupCommands(commandSender);
        return;
      }

      String groupSub = args[1];

      if(groupSub.equalsIgnoreCase("list")){
        commandSender.sendMessage(new TextComponent(groupManager.getListOfAllGroups()));
        return;
      }

      if(args.length != 3){
        commandSender.sendMessage(new TextComponent("§cBitte gib einen Namen/GroupId an."));
        return;
      }

      String param = args[2];

      if(groupSub.equalsIgnoreCase("create")) {

        if(groupManager.createGroup(param)){
          commandSender.sendMessage(new TextComponent("§aDie Gruppe mit der Id "+param+" wurde erstellt!"));
          commandSender.sendMessage(new TextComponent("§aBitte bearbeite sie in der Datenbank."));
        }else{
          commandSender.sendMessage(new TextComponent("§cEs existiert bereits eine Gruppe mit dieser Id."));
        }
        return;
      }

      if(groupSub.equalsIgnoreCase("remove")){

        if(groupManager.removeGroup(param)){
          commandSender.sendMessage(new TextComponent("§aDie Gruppe mit der Id "+param+" wurde gelöscht."));
          commandSender.sendMessage(new TextComponent("§aBitte beachte dass alle Spieler dieser Gruppe nun"));
          commandSender.sendMessage(new TextComponent("§aMember sind."));
        }else{
          commandSender.sendMessage(new TextComponent("§cEs existiert keine Gruppe mit dieser Id."));
        }

      }

      sendGroupCommands(commandSender);
      return;
    }

    if(sub.equalsIgnoreCase("user")){

      if(args.length != 4){
        sendUserCommands(commandSender);
        return;
      }

      String userSub = args[1];
      String playerName = args[2];
      String param = args[3];

      UUID uuid = UUIDFetcher.getUUID(playerName);

      if(uuid == null){
        commandSender.sendMessage(new TextComponent("§cEs konnte kein Spieler mit diesem Namen gefunden werden."));
        return;
      }

      if(userSub.equalsIgnoreCase("group")){

        if(param.equalsIgnoreCase("info")){
          commandSender.sendMessage(new TextComponent("§a"+playerName+" hat den Rang: §r"+permissions.getPermissionGroupOfPlayer(uuid)));
          return;
        }

        if(!groupManager.existsId(param)){
          commandSender.sendMessage(new TextComponent("§CEs existiert keine Gruppe mit dieser Id."));
          return;
        }

        permissions.getDatabaseManager().updateUser(uuid,"groupId",param);
        commandSender.sendMessage(new TextComponent("§aDem Spieler "+playerName+" wurde die Gruppe "+param+" zugewiesen."));

        ProxiedPlayer onlinePlayer = plugin.getProxy().getPlayer(playerName);
        if(onlinePlayer != null){
          onlinePlayer.disconnect(new TextComponent("§ADir wurde ein neuer Rang zugewiesen."));
        }
        return;
      }

      if(userSub.equalsIgnoreCase("prefix")){
        permissions.getDatabaseManager().updateUser(uuid,"prefix",param);
        commandSender.sendMessage(new TextComponent("§aDer Spieler hat nun einen neuen Prefix."));
        ProxiedPlayer onlinePlayer = plugin.getProxy().getPlayer(playerName);
        if(onlinePlayer != null){
          onlinePlayer.disconnect(new TextComponent("§ADir wurde ein neuer Rang zugewiesen."));
        }
        return;
      }
      sendUserCommands(commandSender);
      return;
    }

    sendAllCommands(commandSender);
  }

  private void sendAllCommands(CommandSender commandSender){
    commandSender.sendMessage(new TextComponent("§cGruppen auflisten: /Permission group list <Proxy|Server>"));
    commandSender.sendMessage(new TextComponent("§cGruppen erstellen: /Permission group create <name>"));
    commandSender.sendMessage(new TextComponent("§cGruppen löschen: /Permission group remove <name>"));
    commandSender.sendMessage(new TextComponent("§cSpieler Gruppe zuweisen: /Permission user group <Spieler> <Gruppe>"));
    commandSender.sendMessage(new TextComponent("§cSpieler Prefix zuweisen: /Permission user prefix <Spieler> <Prefix>"));
  }

  private void sendUserCommands(CommandSender commandSender){
    commandSender.sendMessage(new TextComponent("§cSpieler Gruppe zuweisen: /Permission group <Spieler> <Gruppe>"));
    commandSender.sendMessage(new TextComponent("§cSpieler Gruppe zuweisen: /Permission group <Spieler> info"));
    commandSender.sendMessage(new TextComponent("§cSpieler Prefix zuweisen: /Permission prefix <Spieler> <Prefix>"));
  }

  private void sendGroupCommands(CommandSender commandSender){
    commandSender.sendMessage(new TextComponent("§cGruppen auflisten: /Permission group list"));
    commandSender.sendMessage(new TextComponent("§cGruppen erstellen: /Permission group create <name>"));
    commandSender.sendMessage(new TextComponent("§cGruppen löschen: /Permission group remove <name>"));
  }
}
