import React from "react";
import { createBrowserHistory } from "history";

import { BrowserRouter } from "react-router-dom";
import { ProvideAuth } from "auth/useAuth";

import AdapterDateFns from "@mui/lab/AdapterDateFns";
import LocalizationProvider from "@mui/lab/LocalizationProvider";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import BasePage from './components/base/BasePage';

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

function App() {
  return (
    <ProvideAuth>
      <ThemeProvider theme={theme}>
        <LocalizationProvider dateAdapter={AdapterDateFns}>
          <BrowserRouter history={customHistory}>
            <BasePage />
          </BrowserRouter>
        </LocalizationProvider>
      </ThemeProvider>
    </ProvideAuth>
  );
}

export default App;
