import React from "react";
import {
  Checkbox,
  InputAdornment,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField
} from "@mui/material";
import {Search} from "@mui/icons-material";

const mockPermissions = [
  {id: 1, permission: "Add Team Members"},
  {id: 2, permission: "Delete Team Members"},
  {id: 3, permission: "Review Check-ins"},
  {id: 4, permission: "Upload Files"}
];

const PermissionsPage = () => {

  return (
    <div className="permissions-content">
      <TextField
        label="Search"
        placeholder="Find a permission"
        InputProps={{
          endAdornment: (<InputAdornment style={{color: "gray"}} position="end"><Search/></InputAdornment>)
      }}/>
      <TableContainer>
        <Table aria-label="Permissions Table">
          <TableHead>
            <TableRow>
              <TableCell>Permission</TableCell>
              <TableCell>Member</TableCell>
              <TableCell>PDL</TableCell>
              <TableCell>Admin</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {mockPermissions.map(permission =>
              <TableRow key={permission.id}>
                <TableCell>{permission.permission}</TableCell>
                <TableCell><Checkbox/></TableCell>
                <TableCell><Checkbox/></TableCell>
                <TableCell><Checkbox/></TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>

      </TableContainer>
    </div>
  )

}

export default PermissionsPage;