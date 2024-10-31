import React from 'react';
import Birthdays from './Birthdays';
import { formatBirthday } from '../../helpers/celebration';
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
  },
  {
    name: 'JackKeller',
    birthDay: '2/3',
    userId: '6000ba9c-7a3c-4836-a434-f3fe52992868'
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

it('should format a "##/##" birthday to "MONTH ##(ordinal)', () => {
  const user1 = formatBirthday(birthdays[0].birthDay);
  const user3 = formatBirthday(birthdays[2].birthDay);
  expect(user1).toBe('December 29th');
  expect(user3).toBe('February 3rd');
});
