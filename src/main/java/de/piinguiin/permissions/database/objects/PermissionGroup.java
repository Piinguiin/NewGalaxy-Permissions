package de.piinguiin.permissions.database.objects;

import java.util.List;
import java.util.Map;
import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Data
@Entity(value = "PermissionGroups",noClassnameStored = true)
public class PermissionGroup {

  @Id
  private String groupId;
  private String color;
  private String chatPrefix;
  private String tabPrefix;
  private List<String> proxyPermissions;
  private Map<PermissionServer, List<String>> permissionsPerServer;

  public String getColoredChatPrefix(){
    return this.color.concat(this.chatPrefix);
  }

  public String getColoredTabPrefix(){
    return this.color.concat(this.tabPrefix);
  }

}
