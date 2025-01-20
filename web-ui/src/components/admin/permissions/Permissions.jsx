import { useMediaQuery } from '@mui/material';
import React from 'react';
import DesktopTable from './DesktopTable';
import MobileTable from './MobileTable';
import { AppContext } from '../../../context/AppContext';
import {
  selectHasPermissionAssignmentPermission,
  noPermission,
} from '../../../context/selectors';

export default function Permissions() {
  const { state } = useContext(AppContext);
  const showDesktop = useMediaQuery('(min-width:650px)', { noSsr: true });

  return selectHasPermissionAssignmentPermission(state) ?
           (<div>{showDesktop ? <DesktopTable /> : <MobileTable />}</div>) :
           (<h3>{noPermission}</h3>);
}
