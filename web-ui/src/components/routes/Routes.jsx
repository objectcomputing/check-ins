import React, { useContext } from 'react'
import { Switch, Route } from "react-router-dom";
import AdminPage from "../../pages/AdminPage";

import { AppContext } from "../../context/AppContext";

import GroupIcon from "@material-ui/icons/Group";
import Header from "../header/Header";
import ProfilePage from "../../pages/ProfilePage";
import HomePage from "../../pages/HomePage";
import TeamsPage from "../../pages/TeamsPage";
import GuildsPage from "../../pages/GuildsPage";
import CheckinsPage from "../../pages/CheckinsPage";
import CheckinsReportPage from "../../pages/CheckinsReportPage";
import PeoplePage from "../../pages/PeoplePage";
import MemberProfilePage from "../../pages/MemberProfilePage";
import EditSkillsPage from "../../pages/EditSkillsPage";
import SkillReportPage from "../../pages/SkillReportPage";
import TeamSkillReportPage from "../../pages/TeamSkillReportPage";
import BirthdayAnniversaryReportPage from "../../pages/BirthdayAnniversaryReportPage";
import { selectIsAdmin } from '../../context/selectors';
import FeedbackRequestConfirmation from "../feedback_request_confirmation/FeedbackRequestConfirmation";
import FeedbackRequestPage from "../../pages/FeedbackRequestPage";
import ViewFeedbackPage from "../../pages/ViewFeedbackPage";
import ViewFeedbackResponses from "../view_feedback_responses/ViewFeedbackResponses";
import FeedbackSubmitConfirmation from "../feedback_submit_confirmation/FeedbackSubmitConfirmation";
import FeedbackSubmitPage from "../../pages/FeedbackSubmitPage";


export default function Routes() {
  const { state } = useContext(AppContext);
  
  const isAdmin = selectIsAdmin(state);
  
  return (
    <Switch>
      <Route path="/teams">
        <Header title="Teams">
          <GroupIcon fontSize="large" />
        </Header>
        <TeamsPage />
      </Route>
      <Route path="/guilds">
        <Header title="Guilds">
          <GroupIcon fontSize="large" />
        </Header>
        <GuildsPage />
      </Route>
      <Route path="/home">
        <Header />
        <HomePage />
      </Route>
      <Route path="/people">
        <Header title="People" />
        <PeoplePage />
      </Route>
      <Route path="/checkins/:memberId?/:checkinId?">
        <Header title="Check-ins" />
        <CheckinsPage />
      </Route>
      <Route path="/profile/:memberId?">
        <Header title="Member Profile" />
        <MemberProfilePage />
      </Route>
      <Route exact path="/feedback/request/confirmation">
        <FeedbackRequestConfirmation />
      </Route>
      <Route path="/feedback/request">
        <FeedbackRequestPage />
      </Route>
      <Route exact path="/feedback/view">
        <ViewFeedbackPage />
      </Route>
      <Route exact path="/feedback/view/responses">
        <ViewFeedbackResponses />
      </Route>
      <Route exact path="/feedback/submit/confirmation">
        <FeedbackSubmitConfirmation />
      </Route>
      <Route path="/feedback/submit">
        <FeedbackSubmitPage />
      </Route>

      {isAdmin && (
        <Switch>
          <Route path="/edit-skills">
            <Header title="Skills" />
            <EditSkillsPage />
          </Route>
          <Route path="/checkins-reports">
            <Header title="Check-in Report" />
            <CheckinsReportPage />
          </Route>
          <Route path="/skills-reports">
            <Header title="Skill Reports" />
            <SkillReportPage />
          </Route>
          <Route path="/team-skills-reports">
            <Header title="Team Skill Reports" />
            <TeamSkillReportPage />
          </Route>
          <Route path="/birthday-anniversary-reports">
            <Header title="Birthday & Anniversary Reports" />
            <BirthdayAnniversaryReportPage />
          </Route>
          <Route path="/admin">
            <Header title="Admin">
              <GroupIcon fontSize="large" />
            </Header>
            <AdminPage />
          </Route>
          <Route exact path="/feedback/request/confirmation">
            <FeedbackRequestConfirmation />
          </Route>
          <Route path="/feedback/request">
            <FeedbackRequestPage />
          </Route>
          <Route exact path="/feedback/view">
            <ViewFeedbackPage />
          </Route>
          <Route exact path="/feedback/view/responses">
            <ViewFeedbackResponses />
          </Route>
          <Route exact path="/feedback/submit/confirmation">
            <FeedbackSubmitConfirmation />
          </Route>
          <Route path="/feedback/submit">
            <FeedbackSubmitPage />
          </Route>
        </Switch>
        )
      }
    
      <Route path="/">
        <Header />
        <ProfilePage />
      </Route>

  </Switch>
  )
}