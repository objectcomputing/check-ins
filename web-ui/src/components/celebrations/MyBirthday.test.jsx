import React from 'react';
import MyBirthday from './MyBirthday';
import { AppContextProvider } from '../../context/AppContext';
import { BrowserRouter } from 'react-router-dom';

const me = {
  id: '0987654321',
  name: 'Moi',
  lastName: 'Monseiur'
};

it('renders correctly', () => {
  snapshot(
    <AppContextProvider>
      <BrowserRouter>
        <MyBirthday me={me} />
      </BrowserRouter>
    </AppContextProvider>
  );
});
