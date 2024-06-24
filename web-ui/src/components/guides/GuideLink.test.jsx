import React from 'react';
import GuideLink from './GuideLink';
import { MemoryRouter } from 'react-router-dom';

it('renders correctly', () => {
  snapshot(
    <MemoryRouter>
      <GuideLink id="some-id" name="myFileName" url="/pdfs/myFileName.pdf"/>
    </MemoryRouter>
  );
});

it('renders descriptions', () => {
    snapshot(
        <MemoryRouter>
            <GuideLink id="some-id" name="myFileName" url="/pdfs/myFileName.pdf" description="My description"/>
        </MemoryRouter>
    );
});