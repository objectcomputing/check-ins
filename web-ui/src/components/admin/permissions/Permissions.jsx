import { useMediaQuery } from '@mui/material';
import React from 'react';
import DesktopTable from './DesktopTable';
import MobileTable from './MobileTable';

export default function Permissions() {
  const showDesktop = useMediaQuery('(min-width:650px)', { noSsr: true });

  return <div>{showDesktop ? <DesktopTable /> : <MobileTable />}</div>;
}
