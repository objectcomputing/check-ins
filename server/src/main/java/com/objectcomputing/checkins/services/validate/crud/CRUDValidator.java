package com.objectcomputing.checkins.services.validate.crud;

public interface CRUDValidator <T extends Object> {

   public void validateArgumentsCreate(T entity);
   public void validateArgumentsRead(T entity);
   public void validateArgumentsUpdate(T entity);
   public void validateArgumentsDelete(T entity);

   public void validatePermissionsCreate(T entity);
//   public void validatePermissionsRead(T entity);
//   public void validatePermissionsUpdate(T entity);
//   public void validatePermissionsDelete(T entity);

}

