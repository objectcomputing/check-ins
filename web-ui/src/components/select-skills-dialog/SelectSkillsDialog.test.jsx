import React from 'react';
import { AppContextProvider } from '../../context/AppContext';
import SelectSkillsDialog from './SelectSkillsDialog';

const currentUserProfile = {
  id: 9876,
  pdlId: 8765,
  name: 'Current User',
  firstName: 'Current',
  lastName: 'User'
};

const initialState = {
  state: {
    csrf: 'O_3eLX2-e05qpS_yOeg1ZVAs9nDhspEi',
    userProfile: {
      name: 'Current User',
      firstName: 'Current',
      lastName: 'User',
      role: ['MEMBER'],
      imageUrl:
        'https://upload.wikimedia.org/wikipedia/commons/7/74/SNL_MrBill_Doll.jpg',
      memberProfile: currentUserProfile
    },
    checkins: [],
    guilds: [],
    teams: [],
    skills: [],
    roles: [],
    memberRoles: [],
    memberSkills: [],
    index: 0,
    memberProfiles: []
  }
};

const createPortal = vi.fn((element, target) => {
  return element;
});

vi.mock('react-dom', async importOriginal => {
  const mocked = {
    ...(await importOriginal('react-dom')()),
    createPortal
  };
  return mocked;
});

vi.mock('@mui/material/Slider', () => {
  return {
    default: () => props => {
      const { onChange, 'data-testid': testId, ...rest } = props;

      return (
        <input
          data-testid={testId}
          type="range"
          onChange={event => {
            onChange(null, parseInt(event.target.value, 10));
          }}
          {...rest}
        />
      );
    }
  };
});

const skill = {
  id: 'skill-id',
  name: 'Java',
  description: 'A programming language',
  pending: false,
  extraneous: true
};

describe('SelectSkillsDialog', () => {
  afterEach(() => {
    createPortal.mockClear();
  });

  it('renders correctly', () => {
    rootSnapshot(
      <AppContextProvider value={initialState}>
        <SelectSkillsDialog
          isOpen={true}
          onClose={vi.fn()}
          selectableSkills={[skill]}
          onSave={vi.fn()}
        />
      </AppContextProvider>
    );
  });
});
