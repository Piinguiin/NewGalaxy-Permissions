package de.piinguiin.permissions.user;

import de.piinguiin.permissions.Permissions;
import de.piinguiin.permissions.database.objects.PermissionServer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

/*******************************************************
 * Copyright (C) 2015-2021 Piinguiin/ThrowablePenguin
 * Mail: neuraxhd@gmail.com
 *
 * This file is part of NewGalaxy-Permissions and was created at the 18.02.21
 *
 * NewGalaxy-Permissions can not be copied and/or distributed without the express
 * permission of the owner.
 *
 *******************************************************/

public class PermissionUserSpigotManager extends PermissionUserManager{

  @Getter
  private final Object2ObjectOpenHashMap<Player, PermissionAttachment> attachments;

  public PermissionUserSpigotManager(Permissions permissions) {
    super(permissions);
    attachments = new Object2ObjectOpenHashMap<>();
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

}
