import React, { useEffect, useContext, useState } from "react";

import { getPermissionsList } from "../api/permissions";
import {
  getRolePermissionsList,
  postRolePermission,
  deleteRolePermission,
} from "../api/rolepermissions";
import { Checkbox, FormControl, MenuItem, Select, InputLabel } from "@mui/material";
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
      if(rolePermissionsList.length === 0){
        getPermissions();
      }
      getRolePermissions();
    }
  }, [csrf, refresh, rolePermissionsList.length]);

  useEffect(() => {
    if(selectedRole && rolePermissionsList) {
      const rolePermissions = rolePermissionsList.find((rolePermission) => rolePermission.roleId === selectedRole.id);
      rolePermissions && setRolePermissions(rolePermissions?.permissions);
    }

  }, [selectedRole, rolePermissionsList]);

  const handleRoleChange = (event) => {
    setSelectedRole(roles.find((role) => role.id === event.target.value));
  };

  const addPermissionForRole = async (role, permission) => {
    let newSchema = { roleId: role.id, permissionId: permission.id };
    let res = await postRolePermission(newSchema, csrf);
    const snackPayload = res.error
          ? { severity: "warning", toast: `Problem adding ${permission.description} to ${role.role}` }
          : { severity: "success", toast: `${permission.description} added to ${role.role}` };
        window.snackDispatch({
          type: UPDATE_TOAST,
          payload: snackPayload,
        });
  };

  const deletePermissionForRole = async (role, permission) => {
    let newSchema = { roleId: role.id, permissionId: permission.id };
    let res = await deleteRolePermission(newSchema, csrf);
    const snackPayload = res.error
      ? { severity: "warning", toast: `Problem deleting ${permission.description} from ${role.role}` }
      : { severity: "success", toast: `${permission.description} removed from ${role.role}` };
    window.snackDispatch({
      type: UPDATE_TOAST,
      payload: snackPayload,
    });
  };

  const handleChange = async (event, role, permission) => {
    if (event?.target?.checked) {
      await addPermissionForRole(role, permission);
      setRefresh(!refresh);
    } else {
      await deletePermissionForRole(role, permission);
      setRefresh(!refresh);
    }
  };

  return (
    <div className="permissions-page">
      { hasPermission ?
      (
        <>
        <div>
          <FormControl>
            <InputLabel id="select-role-label">Select Role</InputLabel>
            <Select
              labelId="select-role-label"
              label="Select Role"
              value={selectedRole?.id || ''}
              onChange={handleRoleChange}
            >
              <MenuItem value="">-- Please Select --</MenuItem>
              {roles?.map((role) => (
                <MenuItem key={role.id} value={role.id}>{role.role} - {role.description}</MenuItem>
              ))}
            </Select>
          </FormControl>
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
                  onChange={(event) => handleChange(event, selectedRole, permission)} />
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
