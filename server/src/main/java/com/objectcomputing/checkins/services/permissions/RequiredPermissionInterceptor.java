package com.objectcomputing.checkins.services.permissions;

import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.exceptions.PermissionException;

import io.micronaut.aop.InterceptorBean;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;

import jakarta.inject.Singleton;

import java.util.Optional;

@Singleton
@InterceptorBean(RequiredPermission.class)
public class RequiredPermissionInterceptor implements MethodInterceptor<Object, Object> {
  private final CurrentUserServices currentUserServices;
  RequiredPermissionInterceptor(CurrentUserServices currentUserServices) {
      this.currentUserServices = currentUserServices;
  }

  @Override
  public Object intercept(MethodInvocationContext<Object, Object> context) {
    Optional<Permission> permission =
        context.getValue(RequiredPermission.class, Permission.class);
    if (permission.isPresent() &&
        currentUserServices.hasPermission(permission.get())) {
        return context.proceed();
    } else {
        // Throw with this message, as this is what is expected by many tests.
        throw new PermissionException("Forbidden");
    }
  }
}
