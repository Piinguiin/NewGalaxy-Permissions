package de.piinguiin.permissions.user;

import de.piinguiin.permissions.Permissions;
import de.piinguiin.permissions.database.DatabaseManager;
import de.piinguiin.permissions.database.objects.PermissionGroup;
import de.piinguiin.permissions.database.objects.PermissionUser;
import de.piinguiin.permissions.groups.PermissionGroupManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;

public abstract class PermissionUserManager {

  @Getter
  protected final Permissions permissions;
  protected final DatabaseManager databaseManager;
  protected final PermissionGroupManager groupManager;
  @Getter
  protected final Object2ObjectOpenHashMap<UUID, PermissionGroup> userGroups;
  @Getter
  protected final Object2ObjectOpenHashMap<UUID, PermissionUser> permissionUsers;


  public PermissionUserManager(Permissions permissions) {
    this.permissions = permissions;
    this.databaseManager = permissions.getDatabaseManager();
    this.groupManager = permissions.getPermissionGroupManager();
    this.userGroups = new Object2ObjectOpenHashMap<>();
    this.permissionUsers = new Object2ObjectOpenHashMap<>();
  }

  protected void addToCache(UUID uuid) {
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

  protected void removeFromCache(UUID uuid) {
    this.userGroups.remove(uuid);
    this.permissionUsers.remove(uuid);
  }

}
