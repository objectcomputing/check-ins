import React from "react";
import { Router, Switch, Route } from "react-router-dom";
import { createBrowserHistory } from "history";

//import EditPDLPage from "./pages/EditPDLPage";
import Menu from "./components/menu/Menu";
import Header from "./components/header/Header";
import Profile from "./components/profile/Profile";
import TeamsPage from "./pages/TeamsPage";
import CheckinsPage from "./pages/CheckinsPage";
import DirectoryPage from './pages/DirectoryPage'
import PendingSkillsPage from './pages/PendingSkillsPage'
import { AppContextProvider } from "./context/AppContext";
import SnackBarWithContext from "./components/snackbar/SnackBarWithContext";

import GroupIcon from '@material-ui/icons/Group';

import "./App.css";

const customHistory = createBrowserHistory();

function App() {

  return (
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
              {/* <Route path="/admin">
                <Header title="Edit Team" />
                <EditPDLPage />
              </Route> */}
              <Route path="/teams">
                <Header title="Teams">
                  <GroupIcon fontSize="large"/>
                </Header>
                <TeamsPage />
              </Route>
              <Route path="/profile">
                <Header title="My Profile" />
                <Profile />
              </Route>
              <Route path="/directory">
                <Header title="Member Directory" />
                <DirectoryPage />
              </Route>
              <Route path="/checkins">
                <Header title="Check-ins" />
                <CheckinsPage history={customHistory} />
              </Route>
              <Route path="/pending-skills">
                <Header title="Pending Skills" />
                <PendingSkillsPage />
              </Route>
              <Route path="/">
                <Header title="My Profile" />
                <Profile />
              </Route>
            </Switch>
          </div>
          <SnackBarWithContext />
        </div>
      </AppContextProvider>
    </Router>
  );
}

export default App;
