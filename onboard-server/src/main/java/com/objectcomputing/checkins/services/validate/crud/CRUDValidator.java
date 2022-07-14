package com.objectcomputing.checkins.services.validate.crud;

import java.util.UUID;

public interface CRUDValidator <T extends Object> {

   void validateArgumentsCreate(T entity);
   void validateArgumentsRead(T entity);
   void validateArgumentsUpdate(T entity);
   void validateArgumentsDelete(T entity);

   void validatePermissionsCreate(T entity);
   void validatePermissionsRead(T entity);
   void validatePermissionsUpdate(T entity);
   void validatePermissionsFindByFields(UUID entity, UUID secondEntity);
   void validatePermissionsDelete(T entity);

}

