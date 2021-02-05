package de.piinguiin.permissions.api;

import de.piinguiin.permissions.Permissions;
import de.piinguiin.permissions.groups.PermissionGroupManager;
import de.piinguiin.permissions.uuid.UUIDFetcher;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermissionSpigotCommand extends Command {

  private final Permissions permissions;
  private final PermissionGroupManager groupManager;

  public PermissionSpigotCommand(Permissions permissions) {
    super("Permission");
    this.permissions = permissions;
    this.groupManager = permissions.getPermissionGroupManager();
  }

  @Override
  public boolean execute(CommandSender commandSender, String s, String[] args) {

    if (!commandSender.hasPermission("permission.edit")) {
      return false;
    }

    if (args.length == 0) {
      sendAllCommands(commandSender);
      return true;
    }

    String sub = args[0];

    if (sub.equalsIgnoreCase("group")) {

      if (args.length == 1) {
        sendGroupCommands(commandSender);
        return true;
      }

      String groupSub = args[1];

      if (groupSub.equalsIgnoreCase("list")) {
        commandSender.sendMessage(groupManager.getListOfAllGroups());
        return true;
      }

      if (args.length != 3) {
        commandSender.sendMessage("§cBitte gib einen Namen/GroupId an.");
        return true;
      }

      String param = args[2];

      if (groupSub.equalsIgnoreCase("create")) {

        if (groupManager.createGroup(param)) {
          commandSender.sendMessage("§aDie Gruppe mit der Id " + param + " wurde erstellt!");
          commandSender.sendMessage("§aBitte bearbeite sie in der Datenbank.");
        } else {
          commandSender.sendMessage("§cEs existiert bereits eine Gruppe mit dieser Id.");
        }
        return true;
      }

      if (groupSub.equalsIgnoreCase("remove")) {

        if (groupManager.removeGroup(param)) {
          commandSender.sendMessage("§aDie Gruppe mit der Id " + param + " wurde gelöscht.");
          commandSender.sendMessage("§aBitte beachte dass alle Spieler dieser Gruppe nun");
          commandSender.sendMessage("§aMember sind.");
        } else {
          commandSender.sendMessage("§cEs existiert keine Gruppe mit dieser Id.");
        }

      }

      sendGroupCommands(commandSender);
      return true;
    }

    if (sub.equalsIgnoreCase("user")) {

      if (args.length != 4) {
        sendUserCommands(commandSender);
        return true;
      }

      String userSub = args[1];
      String playerName = args[2];
      String param = args[3];

      UUID uuid = UUIDFetcher.getUUID(playerName);

      if (uuid == null) {
        commandSender.sendMessage("§cEs konnte kein Spieler mit diesem Namen gefunden werden.");
        return true;
      }

      if (userSub.equalsIgnoreCase("group")) {

        if (param.equalsIgnoreCase("info")) {
          commandSender.sendMessage("§a" + playerName + " hat den Rang: §r" + permissions
              .getPermissionGroupOfPlayer(uuid).getGroupId());
          return true;
        }

        if (!groupManager.existsId(param)) {
          commandSender.sendMessage("§CEs existiert keine Gruppe mit dieser Id.");
          return true;
        }

        permissions.getDatabaseManager().updateUser(uuid, "groupId", param);
        commandSender.sendMessage(
            "§aDem Spieler " + playerName + " wurde die Gruppe " + param + " zugewiesen.");

        Player onlinePlayer = Bukkit.getPlayer(playerName);
        if (onlinePlayer != null) {
          onlinePlayer.kickPlayer("§ADir wurde ein neuer Rang zugewiesen.");
        }
        return true;
      }

      if (userSub.equalsIgnoreCase("prefix")) {
        permissions.getDatabaseManager().updateUser(uuid, "prefix", param);
        commandSender.sendMessage("§aDer Spieler hat nun einen neuen Prefix.");
        Player onlinePlayer = Bukkit.getPlayer(playerName);
        if (onlinePlayer != null) {
          onlinePlayer.kickPlayer("§ADir wurde ein neuer Rang zugewiesen.");
        }
        return true;
      }
      sendUserCommands(commandSender);
      return true;
    }

    sendAllCommands(commandSender);
    return true;
  }

  private void sendAllCommands(CommandSender commandSender) {
    commandSender.sendMessage("§cGruppen auflisten: /Permission group list");
    commandSender.sendMessage("§cGruppen erstellen: /Permission group create <name>");
    commandSender.sendMessage("§cGruppen löschen: /Permission group remove <name>");
    commandSender
        .sendMessage("§cSpieler Gruppe zuweisen: /Permission user group <Spieler> <Gruppe>");
    commandSender
        .sendMessage("§cSpieler Gruppe anzeigen: /Permission user group <Spieler> info");
    commandSender
        .sendMessage("§cSpieler Prefix zuweisen: /Permission user prefix <Spieler> <Prefix>");
  }

  private void sendUserCommands(CommandSender commandSender) {
    commandSender.sendMessage("§cSpieler Gruppe zuweisen: /Permission group <Spieler> <Gruppe>");
    commandSender.sendMessage("§cSpieler Gruppe anzeigen: /Permission group <Spieler> info");
    commandSender.sendMessage("§cSpieler Prefix zuweisen: /Permission prefix <Spieler> <Prefix>");
  }

  private void sendGroupCommands(CommandSender commandSender) {
    commandSender.sendMessage("§cGruppen auflisten: /Permission group list");
    commandSender.sendMessage("§cGruppen erstellen: /Permission group create <name>");
    commandSender.sendMessage("§cGruppen löschen: /Permission group remove <name>");
  }
}
