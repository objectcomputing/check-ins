import React from 'react';
import {
  Router,
  Switch,
  Route
} from "react-router-dom"
import { createBrowserHistory } from "history";
import './App.css';
import MyTeamPage from './pages/MyTeamPage';
import ResourcesPage from './pages/ResourcesPage';
import UploadNotesPage from './pages/UploadNotesPage';
import HomePage from './pages/HomePage';
import Menu from './components/menu/Menu';

const customHistory = createBrowserHistory();

function App() {
  return (
    <Router history={customHistory}>
      <div>
        <Menu />
        <div style={{ display: "flex", flexDirection: "row", justifyContent: "center" }} className="App">
          <Switch>
            <Route path="/team">
              <MyTeamPage />
            </Route>
            <Route path="/resources">
              <ResourcesPage />
            </Route>
            <Route path="/upload">
              <UploadNotesPage />
            </Route>
            <Route path="/">
              <HomePage />
            </Route>
          </Switch>
        </div>
      </div>
    </Router>
  );
}

export default App;
