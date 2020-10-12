import React from 'react';
import CheckinProfile from './CheckinProfile';

export default {
    title: 'Check-Ins/CheckinProfile',
    component: CheckinProfile
};

const Template = (args) =>
    <CheckinProfile {...args} />;

export const Profile = Template.bind({});
Profile.args = {
    state: {
        userProfile: {
            name: "Test User",
            imageUrl: "https://thumbor.forbes.com/thumbor/960x0/https%3A%2F%2Fblogs-images.forbes.com%2Finsertcoin%2Ffiles%2F2017%2F02%2Flego-batman1.jpg",
            memberProfile: {
                pdlID: 123,
                workEmail: "testuser@objectcomputing.com",
                role: "Software Engineer",
            }
        }
    }
};

export const NoProfile = Template.bind({});
NoProfile.args = {

}

