package com.objectcomputing.checkins.services.validate.crud;

public interface CRUDValidator <T extends Object> {

   public void validateCreate(T entity);
   public void validateRead(T entity);
   public void validateUpdate(T entity);
//   public void validateFindByFields(T entity);
   public void validateDelete(T entity);

}
