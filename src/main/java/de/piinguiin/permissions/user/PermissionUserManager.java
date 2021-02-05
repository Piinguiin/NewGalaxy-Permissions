package de.piinguiin.permissions.user;

import de.piinguiin.permissions.Permissions;
import de.piinguiin.permissions.database.DatabaseManager;
import de.piinguiin.permissions.database.objects.PermissionGroup;
import de.piinguiin.permissions.database.objects.PermissionServer;
import de.piinguiin.permissions.database.objects.PermissionUser;
import de.piinguiin.permissions.groups.PermissionGroupManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

public class PermissionUserManager {

  private final Permissions permissions;
  private final DatabaseManager databaseManager;
  private final PermissionGroupManager groupManager;
  @Getter
  private final Object2ObjectOpenHashMap<UUID, PermissionGroup> userGroups;
  @Getter
  private final Object2ObjectOpenHashMap<UUID, PermissionUser> permissionUsers;
  @Getter
  private final Object2ObjectOpenHashMap<Player, PermissionAttachment> attachments;

  public PermissionUserManager(Permissions permissions) {
    this.permissions = permissions;
    this.databaseManager = permissions.getDatabaseManager();
    this.groupManager = permissions.getPermissionGroupManager();
    this.userGroups = new Object2ObjectOpenHashMap<>();
    this.permissionUsers = new Object2ObjectOpenHashMap<>();
    this.attachments = new Object2ObjectOpenHashMap<>();
  }

  public void onPlayerJoin(Player player, JavaPlugin plugin, PermissionServer server) {
    UUID uuid = player.getUniqueId();
    addToCache(uuid);

    if (permissions.hasDefaultGroup(player.getUniqueId())) {
      return;
    }

    PermissionAttachment attachment = player.addAttachment(plugin);

    final List<String> userPermissions = permissions
        .getPermissionGroupOfPlayer(player.getUniqueId())
        .getPermissionsPerServer().get(server);
    for (String perms : userPermissions) {
      attachment.setPermission(perms, true);
    }
    this.attachments.put(player, attachment);
  }

  public void onPlayerQuit(Player player, PermissionServer server) {
    UUID uuid = player.getUniqueId();
    removeFromCache(uuid);

    if (!attachments.containsKey(player)) {
      return;
    }

    final List<String> userPermissions = permissions
        .getPermissionGroupOfPlayer(player.getUniqueId())
        .getPermissionsPerServer().get(server);

    PermissionAttachment attachment = attachments.get(player);

    for (String perms : userPermissions) {
      attachment.unsetPermission(perms);
    }
    this.attachments.remove(player);
  }

  public void onProxiedPlayerConnect(ProxiedPlayer player) {
    UUID uuid = player.getUniqueId();
    addToCache(uuid);

    if (permissions.hasDefaultGroup(player.getUniqueId())) {
      return;
    }

    permissions.getPermissionGroupOfPlayer(player.getUniqueId()).getProxyPermissions().forEach(
        perm -> player.setPermission(perm, true));

  }

  public void onProxiedPlayerDisconnect(ProxiedPlayer player) {
    UUID uuid = player.getUniqueId();
    removeFromCache(uuid);
  }

  private void addToCache(UUID uuid) {
    Optional<PermissionUser> permissionUser = databaseManager
        .findPermissionUser(uuid);

    if (permissionUser.isEmpty()) {
      return;
    }

    PermissionUser user = permissionUser.get();
    this.permissionUsers.put(uuid, user);
    String groupId = user.getGroupId();
    Optional<PermissionGroup> group = this.groupManager.getGroup(groupId);
    if (group.isEmpty()) {
      System.out.println("Cannot find group with id: " + groupId);
      return;
    }
    this.userGroups.put(uuid, group.get());
  }

  private void removeFromCache(UUID uuid) {
    this.userGroups.remove(uuid);
    this.permissionUsers.remove(uuid);
  }

}
