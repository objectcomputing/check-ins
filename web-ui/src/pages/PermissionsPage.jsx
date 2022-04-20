import React, {useEffect, useState} from "react";
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

import "./PermissionsPage.css";
import {styled} from "@mui/material/styles";

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

const mockPermissions = [
  {id: 1, permission: "Add Team Members"},
  {id: 2, permission: "Delete Team Members"},
  {id: 3, permission: "Review Check-ins"},
  {id: 4, permission: "Upload Files"},
  {id: 5, permission: "Add Team Members"},
  {id: 6, permission: "Delete Team Members"},
  {id: 7, permission: "Review Check-ins"},
  {id: 8, permission: "Upload Files"},
  {id: 9, permission: "Add Team Members"},
  {id: 10, permission: "Delete Team Members"},
  {id: 11, permission: "Review Check-ins"},
  {id: 12, permission: "Upload Files"},
  {id: 13, permission: "Add Team Members"},
  {id: 14, permission: "Delete Team Members"},
  {id: 15, permission: "Review Check-ins"},
  {id: 16, permission: "Upload Files"}
];

const PermissionsPage = () => {

  const [searchText, setSearchText] = useState("");
  const [filteredPermissions, setFilteredPermissions] = useState([]);

  useEffect(() => {
    setFilteredPermissions(mockPermissions);
  }, [])

  useEffect(() => {
    let searchedPermissions = mockPermissions;
    if (searchText.trim()) {
      searchedPermissions = mockPermissions.filter((permission) =>
        permission.permission.toLowerCase().includes(searchText.trim().toLowerCase())
      );
    }
    setFilteredPermissions(searchedPermissions);
  }, [searchText]);

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
      <TableContainer sx={{maxHeight: 600}}>
        <Table stickyHeader aria-label="Permissions Table">
          <TableHead>
            <TableRow>
              <StyledTableCell style={{width: "50%"}}>Permission</StyledTableCell>
              <StyledTableCell align="center">Member</StyledTableCell>
              <StyledTableCell align="center">PDL</StyledTableCell>
              <StyledTableCell align="center">Admin</StyledTableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {filteredPermissions.map(permission =>
              <StyledTableRow key={permission.id}>
                <StyledTableCell>{permission.permission}</StyledTableCell>
                <StyledTableCell align="center"><Checkbox/></StyledTableCell>
                <StyledTableCell align="center"><Checkbox/></StyledTableCell>
                <StyledTableCell align="center"><Checkbox/></StyledTableCell>
              </StyledTableRow>
            )}
          </TableBody>
        </Table>

      </TableContainer>
    </div>
  )

}

export default PermissionsPage;