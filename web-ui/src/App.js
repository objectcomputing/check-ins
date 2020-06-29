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
import ProfileContext from "./context/ProfileContext";
import axios from "axios";

import "./App.css";

const defaultProfile = {
  bio: "It was all a dream, I used to read Word Up magazine",
  email: "Biggie@oci.com",
  name: "Christopher Wallace",
  PDL: "Tupac Shakur",
  role: "Lyrical Poet",
};

let teamMembers = [];

const getTeamMembers = async () => {
  try {
    const res = await axios({
      method: "get",
      url: "/member-profile/?pdlId=fb6424a0-b429-4edf-8f05-6927689bec5f",
      responseType: "json",
    });
    res.data.forEach((profile) => {
      teamMembers.push(profile);
    });
  } catch (error) {
    console.log(error);
  }
};

getTeamMembers();

const customHistory = createBrowserHistory();

function App() {
  return (
    <Router history={customHistory}>
      <ProfileContext.Provider
        value={{ defaultProfile: defaultProfile, teamMembers: teamMembers }}
      >
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
      </ProfileContext.Provider>
    </Router>
  );
}

export default App;
