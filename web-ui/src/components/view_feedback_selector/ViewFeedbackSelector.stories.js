import React from "react";
import { BrowserRouter } from "react-router-dom";
import ViewFeedbackSelector from "./ViewFeedbackSelector";
import {AppContextProvider} from "../../context/AppContext";

export default {
  title: "Check Ins/ViewFeedbackSelector",
  component: ViewFeedbackSelector,
  decorators: [(ViewFeedbackSelector) => {
    return (<AppContextProvider><BrowserRouter><ViewFeedbackSelector/></BrowserRouter></AppContextProvider>);
  }]
};
const Template = (args) => <ViewFeedbackSelector {...args} />;

export const DefaultTemplate = Template.bind({});
DefaultTemplate.args = {};

