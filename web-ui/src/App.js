import React from "react";
import { Router } from "react-router-dom";
import { ErrorBoundary } from "react-error-boundary";
import { createBrowserHistory } from "history";

import Routes from "./components/routes/Routes";
import NewUserRoutes from "./components/routes/NewUserRoutes";
import Menu from "./components/menu/Menu";
import ErrorFallback from "./pages/ErrorBoundaryPage";
import { AppContextProvider } from "./context/AppContext";
import SnackBarWithContext from "./components/snackbar/SnackBarWithContext";
import AdapterDateFns from "@mui/lab/AdapterDateFns";
import LocalizationProvider from "@mui/lab/LocalizationProvider";
import { createTheme, ThemeProvider } from "@mui/material/styles";

import "./App.css";
import { indigo } from "@mui/material/colors";

const customHistory = createBrowserHistory();

const theme = createTheme({
  palette: {
    primary: indigo,
  },
  components: {
    MuiCssBaseline: {
      styleOverrides: {
        body: {
          fontSize: "0.875rem",
          lineHeight: 1.43,
          letterSpacing: "0.01071rem",
        },
      },
    },
    MuiTextField: {
      defaultProps: {
        variant: "standard",
      },
    },
  },
});

// TODO:
// Enable the backend to serve a webpage to a user that is not logged in
let isLoggedIn = true; // This boolean allows you to see the new web portal which is devlopment.

function App() {
  return (
    <ThemeProvider theme={theme}>
      <LocalizationProvider dateAdapter={AdapterDateFns}>
        <Router history={customHistory}>
          {isLoggedIn ? (
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
          ) : (
            <NewUserRoutes />
          )}
        </Router>
      </LocalizationProvider>
    </ThemeProvider>
  );
}

export default App;
