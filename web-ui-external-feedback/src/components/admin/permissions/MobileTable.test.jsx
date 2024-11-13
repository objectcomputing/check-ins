import React from 'react';
import MobileTable from './MobileTable';
import { roles, allPermissions, handleChange } from './sample-data';

describe('MobileTable', () => {
  it('renders correctly', () => {
    snapshot(
      <MobileTable
        roles={roles}
        allPermissions={allPermissions}
        handleChange={handleChange}
      />
    );
  });
});
