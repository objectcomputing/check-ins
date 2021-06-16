import FeedbackRecipientCard from "./Feedback_recipient_card";
import React from "react";
import {AppContextProvider} from "../../context/AppContext";

export default {
  title: 'FeedbackReqs/FeedbackRecipientCard',
  component: FeedbackRecipientCard,
  decorators: [(Story) => {
    return (<AppContextProvider><Story/></AppContextProvider>);
  }]
};

const Template = (args) => <FeedbackRecipientCard{...args} />;

export const DefaultUser = Template.bind({});
DefaultUser.args = {
  name: "Slim Jim",
  reason: "Recommended for being a local opossum",
}

export const OtherUser = Template.bind({});
OtherUser.args = {
  name: "Guy Fieri Barbeque Sauce",
  reason: "Recommended for being the best barbeque sauce",
}