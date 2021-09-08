import FeedbackRecipientCard from "./FeedbackRecipientCard";
import React, {useContext, useEffect} from "react";
import {AppContext, AppContextProvider} from "../../context/AppContext";
import {UPDATE_MEMBER_PROFILES} from "../../context/actions";

export default {
  title: 'FeedbackReqs/FeedbackRecipientCard',
  component: FeedbackRecipientCard,
  decorators: [(Story) => {
    return (<AppContextProvider><Story/></AppContextProvider>);
  }]
};

const profile =  {
  id: "12342345678",
  pdlID: 123,
  workEmail: "kimberlinm@objectcomputing.com",
  name: "Bob Jones",
  title: "Software Engineer",
}

const SetProfiles = ({profiles}) => {
  const { dispatch } = useContext(AppContext);
  useEffect(() => {
    dispatch({ type: UPDATE_MEMBER_PROFILES, payload: profiles });
  }, [profiles, dispatch]);
  return "";
}

const Template = (args) => (
  <React.Fragment>
    <SetProfiles profiles={args.profiles} />
    <FeedbackRecipientCard{...args} />
  </React.Fragment>
);

export const DefaultUser = Template.bind({});
DefaultUser.args = {
  profileId: profile.id,
  reason: "Recommended for being a local opossum",
  profiles:[profile]
};