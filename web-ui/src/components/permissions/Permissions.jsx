import { useMediaQuery } from '@material-ui/core';
import React, { useState } from 'react'
import DesktopTable from "./DesktopTable"
import MobileTable from "./MobileTable"
import { allPermissions, roles } from "./sample-data"


export default function Permissions() {
  // https://material-ui.com/components/use-media-query/#testing
  const showDesktop = useMediaQuery('(min-width:650px)', { noSsr: true });
  
  return (
    <div>
      {showDesktop? 
        <DesktopTable /> 
        : 
        <MobileTable />
      }
    </div>
  )
}
