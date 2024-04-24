import React from 'react';
import { MemoryRouter } from 'react-router-dom';
import GuidesPanel from './GuidesPanel';

it('renders correctly', () => {
  snapshot(
    <MemoryRouter>
      <GuidesPanel />
    </MemoryRouter>
  );
});
