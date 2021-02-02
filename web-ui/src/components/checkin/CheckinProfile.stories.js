import React, {useContext, useEffect} from 'react';
import {AppContext, AppContextProvider, MY_PROFILE_UPDATE} from '../../context/AppContext';
import CheckinProfile from './CheckinProfile';

export default {
    title: 'Check-Ins/CheckinProfile',
    component: CheckinProfile,
    decorators: [(Story) => {
        return (<AppContextProvider><Story/></AppContextProvider>);
    }]
};

const noImageProfile =  {
    name: "Test User",
    role: "MEMBER",
    memberProfile: {
        pdlID: 123,
        workEmail: "testuser@objectcomputing.com",
        name: "Bob Jones",
        title: "Software Engineer",
    }
};

const imageProfile =  {
    ...noImageProfile,
    imageUrl: "https://thumbor.forbes.com/thumbor/960x0/https%3A%2F%2Fblogs-images.forbes.com%2Finsertcoin%2Ffiles%2F2017%2F02%2Flego-batman1.jpg",
};

const SetProfile = ({profile}) => {
    const { dispatch } = useContext(AppContext);
    useEffect(() => {
        dispatch({ type: MY_PROFILE_UPDATE, payload: profile });
    }, [profile, dispatch]);
    return "";
}

const Template = (args) => (
    <React.Fragment>
        <SetProfile profile={args.profile} />
        <CheckinProfile {...args} />
    </React.Fragment>
);

export const ProfileImage = Template.bind({});
ProfileImage.args = {
    profile: imageProfile
};

export const NoProfileImage = Template.bind({});
NoProfileImage.args = {
    profile: noImageProfile
};
