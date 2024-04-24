import React, { useContext, useEffect } from 'react';
import {
  AppContext,
  AppContextProvider,
  MY_PROFILE_UPDATE,
  UPDATE_CHECKINS
} from '../../../context/AppContext';
import CheckinDocs from './CheckinDocs';

export default {
  title: 'Check-Ins/CheckinDocs',
  component: CheckinDocs,
  decorators: [
    Story => {
      return (
        <AppContextProvider>
          <Story />
        </AppContextProvider>
      );
    }
  ]
};

const memberProfile = {
  name: 'Test User',
  role: 'MEMBER',
  memberProfile: {
    pdlID: 123,
    workEmail: 'testuser@objectcomputing.com',
    name: 'Bob Jones',
    title: 'Software Engineer'
  },
  imageUrl:
    'https://thumbor.forbes.com/thumbor/960x0/https%3A%2F%2Fblogs-images.forbes.com%2Finsertcoin%2Ffiles%2F2017%2F02%2Flego-batman1.jpg'
};

const pdlProfile = {
  ...memberProfile,
  role: 'PDL'
};

const SetProfile = ({ profile }) => {
  const { dispatch } = useContext(AppContext);
  useEffect(() => {
    dispatch({ type: MY_PROFILE_UPDATE, payload: profile });
  }, [profile, dispatch]);
  return '';
};

const checkins = [
  {
    id: 'bbc3db2a-181d-4ddb-a2e4-7a9842cdfd78',
    teamMemberId: '43ee8e79-b33d-44cd-b23c-e183894ebfef',
    pdlId: '2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d',
    checkInDate: [2020, 9, 29, 11, 32, 29, 40000000],
    completed: true
  }
];

const SetCheckins = ({ checkins }) => {
  const { dispatch } = useContext(AppContext);
  useEffect(() => {
    dispatch({ type: UPDATE_CHECKINS, payload: checkins });
  }, [checkins, dispatch]);
  return '';
};

const Template = args => (
  <React.Fragment>
    <SetProfile profile={args.profile} />
    <SetCheckins checkins={args.checkins} />
    <CheckinDocs {...args} />
  </React.Fragment>
);

export const PDLNoCurrentDocs = Template.bind({});
PDLNoCurrentDocs.args = {
  profile: pdlProfile,
  checkins
};

export const MemberDocs = Template.bind({});
MemberDocs.args = {
  profile: memberProfile,
  checkins
};
