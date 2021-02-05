package de.piinguiin.permissions;

import com.mongodb.MongoClient;
import de.piinguiin.permissions.database.DatabaseManager;
import de.piinguiin.permissions.database.objects.PermissionGroup;
import de.piinguiin.permissions.database.objects.PermissionUser;
import de.piinguiin.permissions.groups.PermissionGroupManager;
import de.piinguiin.permissions.user.PermissionUserManager;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import org.mongodb.morphia.Morphia;

public class Permissions {

  @Getter
  private final DatabaseManager databaseManager;
  @Getter
  private final PermissionGroupManager permissionGroupManager;
  @Getter
  private final PermissionUserManager permissionUserManager;

  public Permissions(MongoClient mongoClient, Morphia morphia) {
    this.databaseManager = new DatabaseManager(mongoClient, morphia);
    this.permissionGroupManager = new PermissionGroupManager(databaseManager);
    this.permissionUserManager = new PermissionUserManager(this);
  }

  public Optional<PermissionUser> getPermissionUserOfPlayer(UUID uuid) {
    return Optional.ofNullable(this.permissionUserManager.getPermissionUsers().get(uuid));
  }

  public PermissionGroup getPermissionGroupOfPlayer(UUID uuid) {
    return this.permissionUserManager.getUserGroups()
        .getOrDefault(uuid, this.permissionGroupManager.getFallbackGroup());
  }

  public boolean hasDefaultGroup(UUID uuid){
    return getPermissionGroupOfPlayer(uuid).getGroupId().equalsIgnoreCase("member");
  }

}
