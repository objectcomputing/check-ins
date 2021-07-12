import React from 'react';
import { allPermissions, roles, handleChange } from './sample-data';
import PermissionsTableComponent from './DesktopTable';
import PermissionsTableMobile from './MobileTable';
import Permissions from './Permissions';


export default {
  component: PermissionsTableComponent,
  title: 'Check Ins/PermissionsTable',
}

const Template = (args) => {
  return <PermissionsTableComponent {...args} />;
}

const MobileTemplate = (args) => {
  return (
    <div style={{maxWidth: 650}}>
      <PermissionsTableMobile {...args} />
    </div>
  );
}

const tableProps = {
  roles: roles,
  allPermissions: allPermissions,
  handleChange: handleChange,
}

export const PermissionsTableOnDesktop = Template.bind({});
PermissionsTableOnDesktop.args = {
  ...tableProps
};

export const PermissionTableOnMobile = MobileTemplate.bind({});
PermissionTableOnMobile.args = {
  ...tableProps
}


