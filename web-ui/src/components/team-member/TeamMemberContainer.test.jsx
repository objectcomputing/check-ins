import React from 'react';
import TeamMemberContainer from './TeamMemberContainer';
import { AppContextProvider } from '../../context/AppContext';

const testProfile = [
  { name: 'holmes', image_url: '' },
  { name: 'homie', image_url: '' }
];

it('renders correctly', () => {
  snapshot(
    <AppContextProvider>
      <TeamMemberContainer profiles={testProfile} />
    </AppContextProvider>
  );
});
