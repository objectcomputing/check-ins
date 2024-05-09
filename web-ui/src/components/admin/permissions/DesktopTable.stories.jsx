import React from 'react';
import { allPermissions, roles, handleChange } from './sample-data';
import DesktopTable from './DesktopTable';

export default {
  component: DesktopTable,
  title: 'Check Ins/PermissionsTable(Desktop)'
};

const Template = args => {
  return <DesktopTable {...args} />;
};

const tableProps = {
  roles: roles,
  allPermissions: allPermissions,
  handleChange: handleChange
};

export const PermissionsTableOnDesktop = Template.bind({});
PermissionsTableOnDesktop.args = {
  ...tableProps
};
