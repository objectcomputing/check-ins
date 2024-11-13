import React from 'react';
import Anniversaries from './Anniversaries';
import { AppContextProvider } from '../../context/AppContext';
import { BrowserRouter } from 'react-router-dom';

const anniversaries = [
  {
    name: 'Jesse Hanner',
    anniversary: '12/20/2012',
    yearsOfService: 10.03,
    userId: '67dc3a3b-5bfa-4759-997a-fb6bac98dcf3'
  },
  {
    name: 'Mohit Bhatia',
    anniversary: '12/10/2016',
    yearsOfService: 6.06,
    userId: 'b2d35288-7f1e-4549-aa2b-68396b162490'
  },
  {
    name: 'Zack Brown',
    anniversary: '12/29/2019',
    yearsOfService: 3.01,
    userId: '43ee8e79-b33d-44cd-b23c-e183894ebfef'
  }
];

it('renders correctly', () => {
  snapshot(
    <AppContextProvider>
      <BrowserRouter>
        <Anniversaries anniversaries={anniversaries} />
      </BrowserRouter>
    </AppContextProvider>
  );
});
