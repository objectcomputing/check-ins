import React from "react";
import { BrowserRouter } from "react-router-dom";
import FeedbackRequestCard from "./FeedbackRequestCard";
import {AppContextProvider} from "../../context/AppContext";

export default {
  title: "Check Ins/FeedbackRequestCard",
  component: FeedbackRequestCard,
  decorators: [(ViewFeedbackSelector) => {
    return (<AppContextProvider><BrowserRouter><ViewFeedbackSelector/></BrowserRouter></AppContextProvider>);
  }]
};
const Template = (args) => <FeedbackRequestCard {...args} />;

export const DefaultTemplate = Template.bind({});
DefaultTemplate.args = {};

