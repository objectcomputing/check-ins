import React, { useContext } from "react";
import { Switch, Route } from "react-router-dom";

import AdminPage from "../../pages/AdminPage";

import { AppContext } from "../../context/AppContext";

import GroupIcon from "@material-ui/icons/Group";
import Header from "../header/Header";
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
import { selectIsAdmin } from "../../context/selectors";

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
        </Switch>
      )}
    </Switch>
  );
}
