import React from 'react';
import MyAnniversary from './MyAnniversary';
import { AppContextProvider } from '../../context/AppContext';
import { BrowserRouter } from 'react-router-dom';

const hideMyAnniversary = false;
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
        <MyAnniversary
          hideMyAnniversary={hideMyAnniversary}
          myAnniversary={myAnniversary}
        />
      </BrowserRouter>
    </AppContextProvider>
  );
});
