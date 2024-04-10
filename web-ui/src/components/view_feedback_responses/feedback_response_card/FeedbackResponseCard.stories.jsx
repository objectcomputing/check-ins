import React from "react";
import FeedbackResponseCard from "./FeedbackResponseCard";
import {AppContextProvider} from "../../../context/AppContext";

export default {
  title: "Check Ins/FeedbackResponseCard",
  component: FeedbackResponseCard,
  decorators: [(FeedbackResponseCard) => {
    return (<AppContextProvider><FeedbackResponseCard/></AppContextProvider>)
  }]
};

const Template = (args) => <FeedbackResponseCard {...args} />;

export const DefaultTemplate = Template.bind({});
DefaultTemplate.args = {
  responderName: "Job Johnson",
  answer: "I love opossums. I have rehabilitated baby opossums for 25 years, and I intend to do so until my last day!",
  sentiment: 0.8
};