import React from 'react';
import renderer from 'react-test-renderer';
import DesktopTable from './DesktopTable';
import { roles, allPermissions, handleChange } from './sample-data';

describe('DesktopTable', () => {
  it('renders correctly', () => {
    snapshot(
      <DesktopTable
        roles={roles}
        allPermissions={allPermissions}
        handleChange={handleChange}
      />
    );
  });
});
