import React from 'react';
import CheckinCycle from './CheckinCycle';
import {AppContextProvider} from "../../context/AppContext";

export default {
  title: 'Check-Ins/CheckinCycle',
  component: CheckinCycle,
  decorators: [(Story) => {
    return (<AppContextProvider><Story/></AppContextProvider>);
  }]
};

const Template = (args) => <CheckinCycle {...args} />;

export const NoDimensions = Template.bind({});
NoDimensions.args = {
    style: undefined
}

export const SetDimensions = Template.bind({});
SetDimensions.args = {
    style: {height: "5rem", width: "5rem"}
};

