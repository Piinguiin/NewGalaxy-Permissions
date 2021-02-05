package de.piinguiin.permissions.database;

import com.mongodb.MongoClient;
import de.piinguiin.permissions.database.objects.PermissionGroup;
import de.piinguiin.permissions.database.objects.PermissionUser;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.UpdateOperations;

public class DatabaseManager {

  @Getter
  private final Datastore groupsDatastore;
  @Getter
  private final Datastore usersDatastore;

  public DatabaseManager(MongoClient mongoClient, Morphia morphia) {
    morphia.map(PermissionGroup.class, PermissionUser.class);
    this.groupsDatastore = morphia.createDatastore(mongoClient, "PermissionGroups");
    this.usersDatastore = morphia.createDatastore(mongoClient, "PermissionUsers");
  }

  public Optional<PermissionGroup> findPermissionGroup(String id) {
    return Optional
        .ofNullable(groupsDatastore.find(PermissionGroup.class).filter("groupId", id).get());
  }

  public boolean createPermissionGroup(PermissionGroup permissionGroup) {
    groupsDatastore.save(permissionGroup);
    return true;
  }

  public boolean removePermissionGroup(PermissionGroup permissionGroup) {
    groupsDatastore.delete(permissionGroup);
    return true;
  }

  public Optional<PermissionUser> findPermissionUser(UUID uuid) {
    return Optional
        .ofNullable(usersDatastore.find(PermissionUser.class).filter("uuid", uuid).get());
  }

  public void updateUser(UUID uuid,String key, String value){

    Optional<PermissionUser> permissionUser = findPermissionUser(uuid);
    boolean exists = permissionUser.isPresent();

    if(exists){
      UpdateOperations<PermissionUser> operations = usersDatastore.createUpdateOperations(PermissionUser.class)
          .set(key,value);
      usersDatastore.update(permissionUser.get(),operations);
    }else{

      PermissionUser user = new PermissionUser();
      if(key.equalsIgnoreCase("groupId")) {
        user.setGroupId(value);
      }else{
        user.setGroupId("member");
      }
      user.setUuid(uuid);
      if(key.equalsIgnoreCase("prefix")) {
        user.setCustomPrefix(value);
      }else{
        user.setCustomPrefix("");
      }
      usersDatastore.save(user);
    }

  }

  public void savePermissionUser(PermissionUser user) {

    boolean exists = findPermissionUser(user.getUuid()).isPresent();

    if(exists){
      UpdateOperations<PermissionUser> operation = usersDatastore.createUpdateOperations(PermissionUser.class)
          .set("groupId",user.getGroupId()).set("customPrefix",user.getCustomPrefix());
      usersDatastore.update(user,operation);
    }else{
      usersDatastore.save(user);
    }

  }

  public boolean removePermissionUser(PermissionUser user) {

    if(findPermissionUser(user.getUuid()).isPresent()){
      usersDatastore.delete(user);
      return true;
    }
    return false;
  }

  public Object2ObjectOpenHashMap<String, PermissionGroup> getAllGroups(){

    Object2ObjectOpenHashMap<String, PermissionGroup> groups = new Object2ObjectOpenHashMap<>();

    groupsDatastore.find(PermissionGroup.class).asList()
        .forEach(permGroup -> groups.put(permGroup.getGroupId(),permGroup));

    return groups;
  }


}
