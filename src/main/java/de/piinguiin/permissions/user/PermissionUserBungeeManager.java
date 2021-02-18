package de.piinguiin.permissions.user;

import de.piinguiin.permissions.Permissions;
import java.util.UUID;
import net.md_5.bungee.api.connection.ProxiedPlayer;

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

public class PermissionUserBungeeManager extends PermissionUserManager{

  public PermissionUserBungeeManager(Permissions permissions) {
    super(permissions);
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

}
