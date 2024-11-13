import {
  Paper,
  Table,
  TableBody,
  TableCell,
  TableRow,
  TableHead,
  TableContainer,
  Checkbox,
  IconButton,
  TextField
} from '@mui/material';
import ArrowForwardIosIcon from '@mui/icons-material/ArrowForwardIos';
import ArrowBackIosIcon from '@mui/icons-material/ArrowBackIos';
import React, { useState } from 'react';
import Autocomplete from '@mui/material/Autocomplete';

export default function MobileTable({ roles, allPermissions, handleChange }) {
  const [roleIndex, setRoleIndex] = useState(0);
  const [searchPermission, setSearchPermission] = useState('');

  const selectedRole = roles[roleIndex];
  const matchedPermission = searchPermission
    ? allPermissions.find(permission => permission === searchPermission)
    : null;

  const nextRole = () => {
    if (roleIndex !== roles.length - 1) {
      setRoleIndex(prevIndex => prevIndex + 1);
    }
  };

  const prevRole = () => {
    if (roleIndex !== 0) {
      setRoleIndex(prevIndex => prevIndex - 1);
    }
  };

  return (
    <TableContainer component={Paper}>
      <Table aria-label="simple table">
        <TableHead>
          <TableRow>
            <TableCell>
              <Autocomplete
                size="small"
                style={{ minWidth: 200, maxWidth: 350 }}
                id="permissions-autocomplete"
                onChange={(event, value) => setSearchPermission(value)}
                options={allPermissions}
                getOptionLabel={option => option}
                renderInput={params => (
                  <TextField
                    {...params}
                    label="Find a permission"
                    variant="outlined"
                  />
                )}
              />
            </TableCell>
            <TableCell align="right" padding="none">
              <IconButton
                size="small"
                aria-label="arrow-backward"
                disabled={roleIndex === 0 ? true : false}
                onClick={prevRole}
              >
                <ArrowBackIosIcon />
              </IconButton>
              <IconButton
                size="small"
                aria-label="arrow-foward"
                disabled={roleIndex === roles.length - 1 ? true : false}
                onClick={nextRole}
              >
                <ArrowForwardIosIcon />
              </IconButton>
            </TableCell>
          </TableRow>
        </TableHead>
        <TableHead>
          <TableRow>
            <TableCell style={{ fontSize: '1.1rem' }}>Permissions</TableCell>
            <TableCell
              key={selectedRole.id}
              style={{ fontSize: '1.1rem', width: '140px' }}
              align="right"
            >
              {selectedRole.name}
            </TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {matchedPermission ? (
            <TableRow key={matchedPermission}>
              <TableCell component="th" scope="row">
                {matchedPermission}
              </TableCell>
              <TableCell key={selectedRole.id} align="right">
                <Checkbox
                  checked={
                    selectedRole.permissions.indexOf(matchedPermission) !== -1
                      ? true
                      : false
                  }
                  onChange={() => handleChange(selectedRole, matchedPermission)}
                  inputProps={{ 'aria-label': 'primary checkbox' }}
                />
              </TableCell>
            </TableRow>
          ) : (
            allPermissions.map(permission => (
              <TableRow key={permission}>
                <TableCell component="th" scope="row">
                  {permission}
                </TableCell>
                <TableCell key={selectedRole.id} align="right">
                  <Checkbox
                    checked={
                      selectedRole.permissions.indexOf(permission) !== -1
                        ? true
                        : false
                    }
                    onChange={() => handleChange(selectedRole, permission)}
                    inputProps={{ 'aria-label': 'primary checkbox' }}
                  />
                </TableCell>
              </TableRow>
            ))
          )}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
