import React, {useCallback, useContext, useEffect, useState} from "react";
import {
  Checkbox,
  InputAdornment,
  Table,
  TableBody,
  TableCell, tableCellClasses,
  TableContainer,
  TableHead,
  TableRow,
  TextField
} from "@mui/material";
import {Search} from "@mui/icons-material";
import {
  getAllRolePermissions,
  addRolePermission,
  removeRolePermission, getAllPermissions
} from "../api/permissions";

import "./PermissionsPage.css";
import {styled} from "@mui/material/styles";
import {AppContext} from "../context/AppContext";
import {UPDATE_TOAST} from "../context/actions";

const StyledTableCell = styled(TableCell)(({theme}) => ({
  [`&.${tableCellClasses.head}`]: {
    backgroundColor: "#a5a4a8",
    color: theme.palette.common.white,
    fontWeight: "bold",
    textTransform: "uppercase",
    fontSize: 18,
    ['@media (max-width: 600px)']: { // eslint-disable-line no-useless-computed-key
      fontSize: 14
    }
  },
  [`&.${tableCellClasses.body}`]: {
    fontSize: 16,
    ['@media (max-width: 600px)']: { // eslint-disable-line no-useless-computed-key
      fontSize: 12
    }
  }
}));

const StyledTableRow = styled(TableRow)(({theme}) => ({
  '&:nth-of-type(odd)': {
    backgroundColor: theme.palette.action.hover
  },
  '&:last-child td, &:last-child th': {
    border: 0
  }
}));

const PermissionsPage = () => {
  const { state } = useContext(AppContext);
  const { csrf, roles } = state;

  const [searchText, setSearchText] = useState("");
  const [allPermissions, setAllPermissions] = useState([]);
  const [allRolePermissions, setAllRolePermissions] = useState([]);
  const [filteredPermissions, setFilteredPermissions] = useState([]);

  const formatPermissionText = useCallback((permission) => {
    let permissionText = permission.replaceAll("_", " ");
    permissionText = permissionText.toLowerCase();
    return permissionText.charAt(0).toUpperCase() + permissionText.slice(1);
  }, []);

  // Get all permissions from server and store in state
  useEffect(() => {
    const getPermissions = async () => {
      const res = await getAllPermissions(csrf);
      return res && res.payload && res.payload.data ? res.payload.data : [];
    }

    if (csrf) {
      getPermissions().then((permissions) => {
        setAllPermissions(permissions);
        setFilteredPermissions(permissions);
      })
    }
  }, [csrf]);

  // Get all role permissions from server and store in state
  useEffect(() => {
    const loadRolePermissions = async () => {
      const res = await getAllRolePermissions(csrf);
      return res && res.payload && res.payload.data ? res.payload.data : [];
    }

    if (csrf) {
      loadRolePermissions().then((rolePermissions) => {
        setAllRolePermissions(rolePermissions);
      });
    }

  }, [csrf]);

  // Filter permissions by search text
  useEffect(() => {
    let searchedPermissions = allPermissions;
    if (searchText.trim()) {
      searchedPermissions = allPermissions.filter((permission) =>
        formatPermissionText(permission.permission).toLowerCase().includes(searchText.trim().toLowerCase())
      );
    }
    setFilteredPermissions(searchedPermissions);
  }, [searchText, allPermissions, formatPermissionText]);

  const roleHasPermission = useCallback((role, permission) => {
    if (allRolePermissions.length === 0) return false;
    const matchingRole = allRolePermissions.find(roleObj => roleObj.roleId === role.id);
    const hasPermission = matchingRole.permissions?.find(permissionObj => permissionObj.id === permission.id);
    return !!hasPermission;
  }, [allRolePermissions]);

  // Event handler for changing a role permission
  const handleCheckboxChange = useCallback(async (checked, role, permission) => {
    let res;
    let toastMessage;
    if (checked) {
      res = await addRolePermission(role.id, permission.id, csrf);
      toastMessage = `Role ${role.role.toUpperCase()} has been given permission to ${permission.permission.toLowerCase()}`
    } else {
      res = await removeRolePermission(role.id, permission.id, csrf);
      toastMessage = `Role ${role.role.toUpperCase()} no longer has permission to ${permission.permission.toLowerCase()}`
    }
    if (res.payload && !res.error) {
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "success",
          toast: toastMessage
        }
      });
    }
  }, [csrf]);

  return (
    <div className="permissions-content">
      <TextField
        className="permissions-search"
        label="Search"
        placeholder="Find a permission"
        value={searchText}
        onChange={(event) => setSearchText(event.target.value)}
        InputProps={{
          endAdornment: (<InputAdornment style={{color: "gray"}} position="end"><Search/></InputAdornment>)
      }}/>
      <TableContainer sx={{maxHeight: 620}}>
        <Table stickyHeader aria-label="Permissions Table">
          <TableHead>
            <TableRow>
              <StyledTableCell style={{width: "50%"}}>Permission</StyledTableCell>
              {roles.map(role =>
                <StyledTableCell
                  key={role.id}
                  align="center">
                  {role.role}
                </StyledTableCell>
              )}
            </TableRow>
          </TableHead>
          <TableBody>
            {filteredPermissions.map(permission =>
              <StyledTableRow key={permission.id}>
                <StyledTableCell>{formatPermissionText(permission.permission)}</StyledTableCell>
                {roles.map((role) => (
                  <StyledTableCell key={`${role.id}-${permission.id}`} align="center">
                    <Checkbox
                      checked={roleHasPermission(role, permission)}
                      onChange={(event) => handleCheckboxChange(event.target.value, role, permission)}
                    />
                  </StyledTableCell>
                ))}
              </StyledTableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  )

}

export default PermissionsPage;