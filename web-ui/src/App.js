import React from "react";
import { Router } from "react-router-dom";
import { ErrorBoundary } from "react-error-boundary";
import { createBrowserHistory } from "history";

import Routes from "./components/routes/Routes";
import Menu from "./components/menu/Menu";
import ErrorFallback from "./pages/ErrorBoundaryPage";
import { AppContextProvider } from "./context/AppContext";
import SnackBarWithContext from "./components/snackbar/SnackBarWithContext";
import AdapterDateFns from '@mui/lab/AdapterDateFns';
import LocalizationProvider from '@mui/lab/LocalizationProvider';
import { createTheme, ThemeProvider } from "@mui/material/styles";

import "./App.css";

const customHistory = createBrowserHistory();

const theme = createTheme();

function App() {
  return (
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <Router history={customHistory}>
        <ThemeProvider theme={theme}>
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
            </div>
          </ErrorBoundary>
          <SnackBarWithContext />
        </AppContextProvider>
        </ThemeProvider>
      </Router>
    </LocalizationProvider>
  );
}

export default App;
