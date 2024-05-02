import React, { useContext } from 'react';
import { Switch, Route } from 'react-router-dom';

import { AppContext } from '../../context/AppContext';

import AnniversaryReportPage from '../../pages/AnniversaryReportPage';
import BirthdayReportPage from '../../pages/BirthdayReportPage';
import CheckinsPage from '../../pages/CheckinsPage';
import CheckinsReportPage from '../../pages/CheckinsReportPage';
import CheckinsReportEnhancedPage from '../../pages/CheckinsReportEnhancedPage';
import EditSkillsPage from '../../pages/EditSkillsPage';
import EditPermissionsPage from '../../pages/PermissionsPage';
import GroupIcon from '@mui/icons-material/Group';
import GuildsPage from '../../pages/GuildsPage';
import Header from '../header/Header';
import HomePage from '../../pages/HomePage';
import PeoplePage from '../../pages/PeoplePage';
import MemberProfilePage from '../../pages/MemberProfilePage';
import Roles from '../admin/roles/Roles';
import SkillReportPage from '../../pages/SkillReportPage';
import TeamsPage from '../../pages/TeamsPage';
import TeamSkillReportPage from '../../pages/TeamSkillReportPage';
import AnnualReviewReportPage from '../../pages/AnnualReviewReportPage';
import Users from '../admin/users/Users';

import { selectIsAdmin } from '../../context/selectors';
import FeedbackRequestConfirmation from '../feedback_request_confirmation/FeedbackRequestConfirmation';
import FeedbackRequestPage from '../../pages/FeedbackRequestPage';
import ViewFeedbackPage from '../../pages/ViewFeedbackPage';
import ViewFeedbackResponses from '../view_feedback_responses/ViewFeedbackResponses';
import FeedbackSubmitConfirmation from '../feedback_submit_confirmation/FeedbackSubmitConfirmation';
import FeedbackSubmitPage from '../../pages/FeedbackSubmitPage';
import ReceivedRequestsPage from '../../pages/ReceivedRequestsPage';
import EmailPage from '../../pages/EmailPage';
import ReviewsPage from '../../pages/ReviewsPage';
import SelfReviewsPage from '../../pages/SelfReviewsPage';
import SkillCategoriesPage from '../../pages/SkillCategoriesPage';
import SkillCategoryEditPage from '../../pages/SkillCategoryEditPage';

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
        <Header title="Guilds & Communities" />
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
      <Route path="/feedback/reviews">
        <ReviewsPage />
      </Route>
      <Route path="/feedback/self-reviews">
        <SelfReviewsPage />
      </Route>

      {isAdmin && (
        <Switch>
          <Route path="/admin/edit-skills">
            <Header title="Skills" />
            <EditSkillsPage />
          </Route>
          <Route path="/admin/permissions">
            <Header title="Permissions" />
            <EditPermissionsPage />
          </Route>
          <Route path="/admin/skill-categories/:categoryId">
            <SkillCategoryEditPage />
          </Route>
          <Route path="/admin/skill-categories">
            <SkillCategoriesPage />
          </Route>
          <Route path="/checkins-reports">
            <Header title="Check-in Report" />
            <CheckinsReportPage />
          </Route>
          <Route path="/checkins-reports-enhanced">
            <Header title="Enhanced Check-in Report" />
            <CheckinsReportEnhancedPage />
          </Route>
          <Route path="/skills-reports">
            <Header title="Skill Report" />
            <SkillReportPage />
          </Route>
          <Route path="/team-skills-reports">
            <Header title="Team Skill Report" />
            <TeamSkillReportPage />
          </Route>
          <Route path="/anniversary-reports">
            <Header title="Anniversary Report" />
            <AnniversaryReportPage />
          </Route>
          <Route path="/birthday-reports">
            <Header title="Birthday Report" />
            <BirthdayReportPage />
          </Route>
          <Route path="/annual-review-reports">
            <Header title="Annual Review Report" />
            <AnnualReviewReportPage />
          </Route>
          <Route path="/admin/roles">
            <Header title="Roles"></Header>
            <Roles />
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
        </Switch>
      )}
    </Switch>
  );
}
