import React from 'react';
import Avatar from './Avatar';
import image from '../../logo.svg';

export default {
    title: 'Check Ins/Avatar',
    component: Avatar
};

const Template = (args) => 
    <Avatar {...args} />;

export const ProfileImage = Template.bind({});
ProfileImage.args = {
    imageUrl: image
};
export const NoProfileImage = Template.bind({});
NoProfileImage.args = {};