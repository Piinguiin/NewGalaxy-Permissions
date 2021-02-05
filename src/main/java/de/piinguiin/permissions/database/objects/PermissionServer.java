package de.piinguiin.permissions.database.objects;

import org.mongodb.morphia.annotations.Embedded;

@Embedded
public enum PermissionServer {

  SKYBLOCK,
  LOBBY;

}
