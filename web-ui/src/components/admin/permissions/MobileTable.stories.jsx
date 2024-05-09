import React from 'react';
import { allPermissions, roles, handleChange } from './sample-data';
import PermissionsTableMobile from './MobileTable';

export default {
  component: PermissionsTableMobile,
  title: 'Check Ins/PermissionsTable(Mobile)'
};

const MobileTemplate = args => {
  return (
    <div style={{ maxWidth: 650 }}>
      <PermissionsTableMobile {...args} />
    </div>
  );
};

const tableProps = {
  roles: roles,
  allPermissions: allPermissions,
  handleChange: handleChange
};

export const PermissionTableOnMobile = MobileTemplate.bind({});
PermissionTableOnMobile.args = {
  ...tableProps
};
