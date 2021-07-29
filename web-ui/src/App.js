import React from "react";
import { Router } from "react-router-dom";
import { ErrorBoundary } from "react-error-boundary";
import { createBrowserHistory } from "history";

import Routes from "./components/routes/Routes";
import Menu from "./components/menu/Menu";
import { AppContextProvider } from "./context/AppContext";
import SnackBarWithContext from "./components/snackbar/SnackBarWithContext";

import { MuiPickersUtilsProvider } from "@material-ui/pickers";
import DateFnsUtils from "@date-io/date-fns";

import "./App.css";

const customHistory = createBrowserHistory();

function App() {
  const ErrorFallback = ({ error }) => {
    return (
      <div role="alert">
        <p>Something went wrong :/</p>
        <pre style={{ color: "red" }}>{error.message}</pre>
      </div>
    );
  };
  return (
    <MuiPickersUtilsProvider utils={DateFnsUtils}>
      <Router history={customHistory}>
        <AppContextProvider>
          <ErrorBoundary FallbackComponent={ErrorFallback}>
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
                <Routes />
              </div>
              <SnackBarWithContext />
            </div>
          </ErrorBoundary>
        </AppContextProvider>
      </Router>
    </MuiPickersUtilsProvider>
  );
}

export default App;
