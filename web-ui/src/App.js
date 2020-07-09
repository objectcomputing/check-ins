import React from "react";
import { Router, Switch, Route } from "react-router-dom";
import { createBrowserHistory } from "history";

import MyTeamPage from "./pages/MyTeamPage";
import ResourcesPage from "./pages/ResourcesPage";
import UploadNotesPage from "./pages/UploadNotesPage";
import HomePage from "./pages/HomePage";
import Menu from "./components/menu/Menu";
import Header from "./components/header/Header";
import Profile from "./components/profile/Profile";
import { SkillsContextProvider } from "./context/SkillsContext";

import "./App.css";

const customHistory = createBrowserHistory();

function App() {
  return (
    <Router history={customHistory}>
      <SkillsContextProvider>
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
              <Route path="/team">
                <Header title="My Team" />
                <MyTeamPage />
              </Route>
              <Route path="/resources">
                <Header title="Resources" />
                <ResourcesPage />
              </Route>
              <Route path="/upload">
                <Header title="Upload Notes" />
                <UploadNotesPage />
              </Route>
              <Route path="/profile">
                <Header title="Profile" />
                <Profile />
              </Route>
              <Route path="/">
                <Header title="Professional Development @ OCI" />
                <HomePage />
              </Route>
            </Switch>
          </div>
        </div>
      </SkillsContextProvider>
    </Router>
  );
}

export default App;
