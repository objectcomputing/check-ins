import React from 'react';
import AvatarComponent from './Avatar';
import { AppContextProvider } from '../../context/AppContext';
import renderer from 'react-test-renderer';

const initialState = {
  state: {
    userProfile: {
      memberProfile: {
        id: '912834091823'
      }
    }
  }
};

describe('AvatarComponent', () => {
  it('renders correctly', () => {
    snapshot(
      <AppContextProvider value={initialState}>
        <AvatarComponent />
      </AppContextProvider>
    );
  });
});
