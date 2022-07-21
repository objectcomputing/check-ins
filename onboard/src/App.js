import React from "react";
import { Switch, Route, Router } from "react-router-dom";
import { createBrowserHistory } from "history";

import Routes from './Routes';
import AdapterDateFns from '@mui/lab/AdapterDateFns';
import LocalizationProvider from '@mui/lab/LocalizationProvider';
import { createTheme, ThemeProvider } from "@mui/material/styles";

import "./App.css";
import {indigo} from "@mui/material/colors";

const customHistory = createBrowserHistory();

const theme = createTheme({
  palette: {
    primary: indigo
  },
  components: {
    MuiCssBaseline: {
      styleOverrides: {
        body: {
          fontSize: '0.875rem',
          lineHeight: 1.43,
          letterSpacing: '0.01071rem'
        }
      }
    },
    MuiTextField: {
      defaultProps: {
        variant: "standard"
      }
    }
  }
});

function App() {
  return (
  <ThemeProvider theme={theme}>
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <Router history={customHistory}>
        <Switch>
          <Route exact path="/nope">
            <div>Nope.</div>
          </Route>
          <Route>
            <Routes />
          </Route>
        </Switch>
      </Router>
    </LocalizationProvider>
  </ThemeProvider>
  );
}

export default App;
