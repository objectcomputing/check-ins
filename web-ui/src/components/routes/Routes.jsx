import React, { useContext } from "react";
import { Switch, Route } from "react-router-dom";

import { AppContext } from "../../context/AppContext";

import BirthdayAnniversaryReportPage from "../../pages/BirthdayAnniversaryReportPage";
import CheckinsPage from "../../pages/CheckinsPage";
import CheckinsReportPage from "../../pages/CheckinsReportPage";
import EditSkillsPage from "../../pages/EditSkillsPage";
import GroupIcon from "@mui/icons-material/Group";
import GuildsPage from "../../pages/GuildsPage";
import Header from "../header/Header";
import HomePage from "../../pages/HomePage";
import PeoplePage from "../../pages/PeoplePage";
import MemberProfilePage from "../../pages/MemberProfilePage";
import Roles from "../admin/roles/Roles";
import SkillReportPage from "../../pages/SkillReportPage";
import TeamsPage from "../../pages/TeamsPage";
import TeamSkillReportPage from "../../pages/TeamSkillReportPage";
import AnnualReviewReportPage from "../../pages/AnnualReviewReportPage";
import Users from "../admin/users/Users";

import { selectIsAdmin } from "../../context/selectors";
import FeedbackRequestConfirmation from "../feedback_request_confirmation/FeedbackRequestConfirmation";
import FeedbackRequestPage from "../../pages/FeedbackRequestPage";
import ViewFeedbackPage from "../../pages/ViewFeedbackPage";
import ViewFeedbackResponses from "../view_feedback_responses/ViewFeedbackResponses";
import FeedbackSubmitConfirmation from "../feedback_submit_confirmation/FeedbackSubmitConfirmation";
import FeedbackSubmitPage from "../../pages/FeedbackSubmitPage";
import ReceivedRequestsPage from "../../pages/ReceivedRequestsPage";
import EmailPage from "../../pages/EmailPage";
import PermissionsPage from "../../pages/PermissionsPage";
import OnboardProgressPage from "../../pages/OnboardProgressPage";
import OnboardProgressDetailPage from "../../pages/OnboardProgressDetailPage";
import KudosPage from "../../pages/KudosPage";

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
      <Route exact path="/">
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
      <Route path="/feedback/received-requests">
        <ReceivedRequestsPage />
      </Route>
      <Route exact path="/kudos">
        <KudosPage />
      </Route>

      {isAdmin && (
        <Switch>
          <Route path="/admin/edit-skills">
            <Header title="Skills" />
            <EditSkillsPage />
          </Route>
          <Route path="/checkins-reports">
            <Header title="Check-in Report" />
            <CheckinsReportPage />
          </Route>
          <Route path="/skills-reports">
            <Header title="Skill Report" />
            <SkillReportPage />
          </Route>
          <Route path="/team-skills-reports">
            <Header title="Team Skill Report" />
            <TeamSkillReportPage />
          </Route>
          <Route path="/birthday-anniversary-reports">
            <Header title="Birthday & Anniversary Report" />
            <BirthdayAnniversaryReportPage />
          </Route>
          <Route path="/annual-review-reports">
            <Header title="Annual Review Report" />
            <AnnualReviewReportPage />
          </Route>
          <Route path="/admin/roles">
            <Header title="Roles"></Header>
            <Roles />
          </Route>
          <Route path="/admin/permissions">
            <Header title="Permissions" />
            <PermissionsPage />
          </Route>
          <Route path="/admin/users">
            <Header title="Users"></Header>
            <Users />
          </Route>
          <Route path="/admin/email">
            <Header title="Send Email"></Header>
            <EmailPage />
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
          <Route exact path="/onboard/progress">
            <Header title="Onboarding Progress" />
            <OnboardProgressPage />
          </Route>
          <Route path="/onboard/progress/:onboardId?">
            <OnboardProgressDetailPage />
          </Route>
        </Switch>
      )}
    </Switch>
  );
}
