import React from 'react';
import GuideLink from './GuideLink';
import { MemoryRouter } from 'react-router-dom';

it('renders correctly', () => {
  snapshot(
    <MemoryRouter>
      <GuideLink name="myFileName" />
    </MemoryRouter>
  );
});
