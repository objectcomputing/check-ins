import { 
  makeStyles, 
  Paper, 
  Table, 
  TableBody, TableCell, 
  TableRow, 
  TableHead, 
  TableContainer,
  Checkbox,
} from '@material-ui/core'

import React from 'react'

const useStyles = makeStyles({
  table: {
    minWidth: 650,
  },
});


export default function DesktopTable({roles, allPermissions, handleChange}) {
  const classes = useStyles();

  return (
    <TableContainer component={Paper}>
      <Table className={classes.table} aria-label="simple table">
        <TableHead>
          <TableRow>
            <TableCell style={{fontSize: '1.1rem'}} >Permissions</TableCell>
            {roles.map(role => (
              <TableCell 
                key={role.id} 
                style={{fontSize: '1.1rem'}} 
                align="right">{role.name}
              </TableCell>
            ))}
          </TableRow>
        </TableHead>
        <TableBody>
          {allPermissions.map(permission => (
            <TableRow key={permission}>
              <TableCell component="th" scope="row">
                {permission}
              </TableCell>
              {roles.map(role => (
                <TableCell key={role.id} align="right">
                  <Checkbox 
                    checked={role.permissions.indexOf(permission) !== -1? true : false}
                    onChange={() => handleChange(role, permission)}
                    inputProps={{ 'aria-label': 'primary checkbox' }}
                  />
                </TableCell>
              ))}
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  )
}
