import React from 'react';
import DoubleCelebration from './DoubleCelebration';
import { AppContextProvider } from '../../context/AppContext';
import { BrowserRouter } from 'react-router-dom';

const hideMyAnniversary = false;
const hideMyBirthday = false;
const me = {
  bioText: 'Superior Engineer',
  birthDate: [2022, 12, 27],
  employeeId: '123123410',
  firstName: 'Suman',
  id: '1b4f99da-ef70-4a76-9b37-8bb783b749ad',
  lastName: 'Maroju',
  location: 'St. Louis',
  pdlId: '7a6a2d4e-e435-4ec9-94d8-f1ed7c779498',
  startDate: [2012, 12, 29],
  title: 'Software Engineer',
  workEmail: 'marojus@objectcomputing.com'
};

const myAnniversary = {
  name: 'Suman Maroju',
  anniversary: '12/29/2012',
  yearsOfService: 10,
  userId: '1b4f99da-ef70-4a76-9b37-8bb783b749ad'
};

it('renders correctly', () => {
  snapshot(
    <AppContextProvider>
      <BrowserRouter>
        <DoubleCelebration
          me={me}
          hideMyBirthday={hideMyBirthday}
          hideMyAnniversary={hideMyAnniversary}
          myAnniversary={myAnniversary}
        />
      </BrowserRouter>
    </AppContextProvider>
  );
});
