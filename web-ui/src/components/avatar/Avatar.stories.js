import React from 'react';
import Avatar from './Avatar';
import image from '../../logo.svg';

export default {
    title: 'Check Ins/Avatar',
    component: Avatar
};

const Template = (args) => 
    <Avatar {...args} />;

export const LoggedIn = Template.bind({});
LoggedIn.args = {
    loggedIn:  true,
    profile: {image_url: image}
};
export const LoggedOut = Template.bind({});
LoggedOut.args = {
    loggedIn:  false
};