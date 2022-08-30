// Enum for all permissions
export const Permissions = {
  CAN_VIEW_FEEDBACK_REQUEST: "CAN_VIEW_FEEDBACK_REQUEST",
  CAN_CREATE_FEEDBACK_REQUEST: "CAN_CREATE_FEEDBACK_REQUEST",
  CAN_DELETE_FEEDBACK_REQUEST: "CAN_DELETE_FEEDBACK_REQUEST",
  CAN_VIEW_FEEDBACK_ANSWER: "CAN_VIEW_FEEDBACK_ANSWER",
  CAN_DELETE_ORGANIZATION_MEMBERS: "CAN_DELETE_ORGANIZATION_MEMBERS",
  CAN_CREATE_ORGANIZATION_MEMBERS: "CAN_CREATE_ORGANIZATION_MEMBERS",
  CAN_VIEW_ROLE_PERMISSIONS: "CAN_VIEW_ROLE_PERMISSIONS",
  CAN_VIEW_PERMISSIONS: "CAN_VIEW_PERMISSIONS"
};

// Restrict access to a specific route with a permission
export const RoutePermissions = {
  "/admin/users": Permissions.CAN_CREATE_ORGANIZATION_MEMBERS,
  "/feedback/view": Permissions.CAN_VIEW_FEEDBACK_REQUEST,
};

export const userHasPermissionForRoute = (path, userPermissions) => {
  const requiredPermission = RoutePermissions[path];
  if (userPermissions && requiredPermission) {
    const hasPermission = userPermissions.find(permissionObj => permissionObj.permission === requiredPermission);
    if (!hasPermission) {
      return false;
    }
  }
  return true;
}