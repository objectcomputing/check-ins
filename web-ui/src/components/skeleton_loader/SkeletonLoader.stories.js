import React from 'react';
import SkeletonLoader from './SkeletonLoader';


export default {
  component: SkeletonLoader,
  title: 'Check Ins/SkeletonLoader',
}

const Template = (args) => {
  return <SkeletonLoader {...args} />;
}

export const TeamLoader = Template.bind({});
TeamLoader.args = {
    type: "team",
}

export const GuildLoader = Template.bind({});
GuildLoader.args = {
  type: "guild",
}

export const PeopleLoader = Template.bind({});
PeopleLoader.args = {
    type: "people",
}