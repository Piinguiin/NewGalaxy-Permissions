package de.piinguiin.permissions.database.objects;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mongodb.morphia.annotations.Entity;

@Data
@NoArgsConstructor
@Entity(value = "PermissionUser", noClassnameStored = true)
public class PermissionUser {

  private UUID uuid;
  private String groupId;
  private String customPrefix;

}
