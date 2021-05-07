import React from "react";
import { Router, Switch, Route } from "react-router-dom";
import { createBrowserHistory } from "history";

//import EditPDLPage from "./pages/EditPDLPage";
import Menu from "./components/menu/Menu";
import Header from "./components/header/Header";
import ProfilePage from "./pages/ProfilePage";
import TeamsPage from "./pages/TeamsPage";
import GuildsPage from "./pages/GuildsPage";
import CheckinsPage from "./pages/CheckinsPage";
import DirectoryPage from "./pages/DirectoryPage";
import MemberProfilePage from "./pages/MemberProfilePage";
import PendingSkillsPage from "./pages/PendingSkillsPage";
import SkillReportPage from "./pages/SkillReportPage";
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
                <Route path="/pending-skills">
                  <Header title="Pending Skills" />
                  <PendingSkillsPage />
                </Route>
                <Route path="/skills">
                  <Header title="Skills" />
                  <SkillReportPage />
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
