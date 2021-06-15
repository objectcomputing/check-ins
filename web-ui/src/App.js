import React from "react";
import { Router, Switch, Route } from "react-router-dom";
import { createBrowserHistory } from "history";

import Menu from "./components/menu/Menu";
import Header from "./components/header/Header";
import ProfilePage from "./pages/ProfilePage";
import TeamsPage from "./pages/TeamsPage";
import GuildsPage from "./pages/GuildsPage";
import CheckinsPage from "./pages/CheckinsPage";
import CheckinsReportPage from "./pages/CheckinsReportPage";
import DirectoryPage from "./pages/DirectoryPage";
import MemberProfilePage from "./pages/MemberProfilePage";
import EditSkillsPage from "./pages/EditSkillsPage";
import SkillReportPage from "./pages/SkillReportPage";
import TeamSkillReportPage from "./pages/TeamSkillReportPage";
import { AppContextProvider } from "./context/AppContext";

import SnackBarWithContext from "./components/snackbar/SnackBarWithContext";

import GroupIcon from "@material-ui/icons/Group";
import { MuiPickersUtilsProvider } from "@material-ui/pickers";
import DateFnsUtils from "@date-io/date-fns";

import "./App.css";

const customHistory = createBrowserHistory();

function App() {
  return (
    <MuiPickersUtilsProvider utils={DateFnsUtils}>
      <Router history={customHistory}>
        <AppContextProvider>
          <div>
            <Menu />
            <div
              style={{
                display: "flex",
                flexDirection: "column",
                justifyContent: "center",
              }}
              className="App"
            >
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
                  <ProfilePage />
                </Route>
                <Route path="/directory">
                  <Header title="Member Directory" />
                  <DirectoryPage />
                </Route>
                <Route path="/checkins/:memberId?/:checkinId?">
                  <Header title="Check-ins" />
                  <CheckinsPage />
                </Route>
                <Route path="/profile/:memberId?">
                  <Header title="Member Profile" />
                  <MemberProfilePage />
                </Route>
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
                <Route path="/">
                  <Header />
                  <ProfilePage />
                </Route>
              </Switch>
            </div>
            <SnackBarWithContext />
          </div>
        </AppContextProvider>
      </Router>
    </MuiPickersUtilsProvider>
  );
}

export default App;
