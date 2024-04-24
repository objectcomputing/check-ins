import React from 'react';
import Birthdays from './Birthdays';
import { AppContextProvider } from '../../context/AppContext';
import { BrowserRouter } from 'react-router-dom';

const birthdays = [
  {
    name: 'SumanMaroju',
    birthDay: '12/29',
    userId: '1b4f99da-ef70-4a76-9b37-8bb783b749ad'
  },
  {
    name: 'MohitBhatia',
    birthDay: '12/29',
    userId: 'b2d35288-7f1e-4549-aa2b-68396b162490'
  }
];
const hideMyBirthday = false;

it('renders correctly', () => {
  snapshot(
    <AppContextProvider>
      <BrowserRouter>
        <Birthdays birthdays={birthdays} hideMyBirthday={hideMyBirthday} />
      </BrowserRouter>
    </AppContextProvider>
  );
});
