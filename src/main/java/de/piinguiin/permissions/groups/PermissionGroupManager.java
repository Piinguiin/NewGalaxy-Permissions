package de.piinguiin.permissions.groups;

import de.piinguiin.permissions.database.DatabaseManager;
import de.piinguiin.permissions.database.objects.PermissionGroup;
import de.piinguiin.permissions.database.objects.PermissionServer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;

public class PermissionGroupManager {

  private final DatabaseManager databaseManager;
  private final Object2ObjectOpenHashMap<String, PermissionGroup> groups;
  @Getter
  private final PermissionGroup fallbackGroup;

  public PermissionGroupManager(DatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
    this.groups = databaseManager.getAllGroups();

    if(groups.isEmpty()){
      createGroup("member");
    }

    this.fallbackGroup = groups.getOrDefault("member", null);
    if (this.fallbackGroup == null) {
      throw new NullPointerException("No fallback group found! Please add a 'member' group!");
    }
  }

  public Optional<PermissionGroup> getGroup(String id){
    return Optional.of(this.groups.get(id));
  }

  public boolean createGroup(String id) {

    if(this.groups.containsKey(id)){
      return false;
    }

    PermissionGroup group = new PermissionGroup();
    group.setGroupId(id);
    group.setColor("§5");
    group.setChatPrefix(id.concat("_CHAT"));
    group.setTabPrefix(id.concat("_TAB"));
    group.setProxyPermissions(Arrays.asList("proxy.perm1", "proxy.perm2"));
    group.setPermissionsPerServer(
        Map.of(PermissionServer.SKYBLOCK, Arrays.asList("skyblock.perm1", "skyblock.perm2"),
            PermissionServer.LOBBY, Arrays.asList("lobby.perm1", "lobby.perm2")));
    return databaseManager.createPermissionGroup(group);
  }

  public boolean removeGroup(String id){
    Optional<PermissionGroup> group = getGroup(id);
    if(group.isEmpty()){
      return false;
    }
    this.groups.remove(id);
    return this.databaseManager.removePermissionGroup(group.get());
  }

  public boolean existsId(String id){
    return this.groups.containsKey(id);
  }

  public String getListOfAllGroups(){

    if(groups.isEmpty()){
      return "§cEs existieren keine Gruppen.";
    }

    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append("§7Liste aller Gruppen:");
    stringBuilder.append("§7Gruppe : Id");
    for (PermissionGroup value : this.groups.values()) {
      stringBuilder.append("§\n").append(value.getColoredChatPrefix()).append("§8 : §f").append(value.getGroupId());
    }
    return stringBuilder.toString();
  }

}
