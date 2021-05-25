import React from "react";
import ProgressBar from "./ProgressBar";

export default {
  title: "Check Ins/ProgressBar",
  component: ProgressBar,
};
const Template = (args) => <ProgressBar {...args} />;

export const DefaultBar = Template.bind({});
DefaultBar.args = {};
