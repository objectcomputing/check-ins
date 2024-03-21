import React, { useEffect, useContext, useState } from "react";

import { getPermissionsList } from "../api/permissions";
import {
  getRolePermissionsList,
  postRolePermissionsList,
  deleteRolePermissionsList,
} from "../api/rolepermissions";
import { Checkbox } from "@mui/material";
import { UPDATE_TOAST } from "../context/actions";
import { AppContext } from "../context/AppContext";
import { selectRoles, selectHasPermissionAssignmentPermission } from "../context/selectors";

import "./PermissionsPage.css";

const groupPermissionsByCategory = (permissions) => permissions.reduce((categories, permission) => {
  const category = permission.category;
  const existingCategory = categories.find(cat => cat.category === category);

  // If category exists, add permission to its permissions array
  if (existingCategory) {
    existingCategory.permissions.push(permission);
  } else {
    // Create a new category object and add it to categories
    categories.push({
      category,
      permissions: [permission],
    });
  }

  return categories.sort((a,b) => a.category.localeCompare(b.category));
}, []);

const PermissionEditor = ({
  permission,
  title,
  enabled,
  onChange
}) => {
  return (
    <div className="permissions">
      <Checkbox
        checked={enabled}
        id={`permission-field-${permission}`}
        onChange={onChange}
        inputProps={{ "aria-label": `checkbox ${title}` }}
      />
      <label htmlFor={`permission-field-${permission}`}>{title}</label>
    </div>
  );
};

const isPermissionEnabled = (rolePermissions, permission) => rolePermissions.some((current) => current.id === permission.id);

const EditPermissionsPage = () => {
  const { state } = useContext(AppContext);
  const { csrf } = state;
  const roles = selectRoles(state);
  const hasPermission = selectHasPermissionAssignmentPermission(state);
  const [selectedRole, setSelectedRole] = useState(roles && roles.find(()=>true));
  const [categoriesList, setCategoriesList] = useState([]);
  const [rolePermissionsList, setRolePermissionsList] = useState([]);
  const [rolePermissions, setRolePermissions] = useState([]);
  const [refresh, setRefresh] = useState(true);

  useEffect(() => {
    const getRolePermissions = async () => {
      let res = await getRolePermissionsList(csrf);
      let data =
        res.payload && res.payload.data && !res.error ? res.payload.data : null;
      if (data) {
        setRolePermissionsList(data);
      }
    };
    const getPermissions = async () => {
      let res = await getPermissionsList(csrf);
      let data =
        res.payload && res.payload.data && !res.error ? res.payload.data : null;
      if (data) {
        setCategoriesList(groupPermissionsByCategory(data));
      }
    };

    if (csrf) {
      getPermissions();
      getRolePermissions();
    }
  }, [csrf, refresh]);

  useEffect(() => {
    if(selectedRole && rolePermissionsList) {
      const rolePermissions = rolePermissionsList.find((rolePermission) => rolePermission.roleId === selectedRole.id);
      rolePermissions && setRolePermissions(rolePermissions?.permissions);
    }

  }, [selectedRole, rolePermissionsList]);

  const handleRoleChange = (event) => {
    setSelectedRole(roles.find((role) => role.id === event.target.value));
  };

  const addRolePermission = async (roleId, permissionId) => {
    let newSchema = { roleId: roleId, permissionId: permissionId };
    let res = await postRolePermissionsList(newSchema, csrf);
    let data =
        res.payload && res.payload.data && !res.error ? res.payload.data : null;
    if (data) {
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "success",
          toast: `Permission added to Role`,
        },
      });
    } else {
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "warning",
          toast: `Problem changing permission for that role`,
        },
      });
    }
  };

  const deleteRolePermission = async (roleId, permissionId) => {
    let newSchema = { roleId: roleId, permissionId: permissionId };
    let res = await deleteRolePermissionsList(newSchema, csrf);
    let data = !res.error ? "Success" : null;
    if (data) {
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "success",
          toast: `Permission removed from Role`,
        },
      });
    } else {
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "warning",
          toast: `Problem deleting permission for that role`,
        },
      });
    }
  };

  const handleChange = async (event, roleId, permissionId) => {
    if (event?.target?.checked) {
      await addRolePermission(roleId, permissionId);
      setRefresh(!refresh);
    } else {
      await deleteRolePermission(roleId, permissionId);
      setRefresh(!refresh);
    }
  };

  return (
    <div className="permissions-page">
      { hasPermission ?
      (
        <>
        <div>
          <label htmlFor="role">Select Role:</label>
          <select id="role" value={selectedRole?.id || ''} onChange={handleRoleChange}>
            <option value="">-- Please Select --</option>
            {roles?.map((role) => (
              <option key={role.id} value={role.id}>
                {role.role} - {role.description}
              </option>
            ))}
          </select>
        </div>

        { selectedRole && rolePermissions && categoriesList?.map((category) => (
          <div key={category.category} className="permissions-list">
            <h3>{category?.category}:</h3>
            { category?.permissions?.map((permission)=> (
                <PermissionEditor
                  key={permission.id}
                  permission={permission.permission}
                  title={permission.description}
                  enabled={isPermissionEnabled(rolePermissions, permission)}
                  onChange={(event) => handleChange(event, selectedRole?.id, permission.id, csrf)} />
              ))
            }
          </div>
          ))
        }
        </>
      ) : (
        <h3>You do not have permission to view this page.</h3>
      )}
    </div>
  );
};

export default EditPermissionsPage;
