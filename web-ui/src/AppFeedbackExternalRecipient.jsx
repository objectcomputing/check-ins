import React from 'react';
import { Router } from 'react-router-dom';
import { ErrorBoundary } from 'react-error-boundary';
import { createBrowserHistory } from 'history';

import RoutesFeedbackExternalRecipient from './components/routes/RoutesFeedbackExternalRecipient';
import ErrorFallback from './pages/ErrorBoundaryPage';
import SnackBarWithContext from './components/snackbar/SnackBarWithContext';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { DarkMode, LightMode } from '@mui/icons-material';

import {
  useColorScheme,
  experimental_extendTheme as extendTheme,
  Experimental_CssVarsProvider as CssVarsProvider
} from '@mui/material/styles';

import './AppFeedbackExternalRecipient.css';
import {AppFeedbackExternalRecipientContextProvider} from "./context/AppFeedbackExternalRecipientContext.jsx";

function getUserColorScheme() {
  if (window?.matchMedia('(prefers-color-scheme: dark)').matches) {
    return 'dark';
  } else if (window?.matchMedia('(prefers-color-scheme: light)').matches) {
    return 'light';
  } else {
    return 'light';
  }
}


const theme = extendTheme({
  cssVarPrefix: 'checkins',
  colorSchemes: {
    light: {
      palette: {
        secondary: {
          main: '#76c8d4'
        }
      }
    },
    dark: {
      palette: {
        secondary: {
          main: '#76c8d4'
        }
      }
    }
  },
  components: {
    MuiCssBaseline: {
      styleOverrides: {
        secondary: {
          main: '#f8b576'
        },
        body: {
          fontSize: '0.875rem',
          lineHeight: 1.43,
          letterSpacing: '0.01071rem'
        }
      }
    }
  }
});


getUserColorScheme();

function AppFeedbackExternalRecipient() {
  return (
    <CssVarsProvider theme={theme}>
      <LocalizationProvider dateAdapter={AdapterDateFns}>
        <Router>
          <AppFeedbackExternalRecipientContextProvider>
            <ErrorBoundary FallbackComponent={ErrorFallback}>
              <div>
                <div className="AppFeedbackExternalRecipient">
                  <RoutesFeedbackExternalRecipient />
                </div>
              </div>
            </ErrorBoundary>
            <SnackBarWithContext />
          </AppFeedbackExternalRecipientContextProvider>
        </Router>
      </LocalizationProvider>
    </CssVarsProvider>
  );
}

export default AppFeedbackExternalRecipient;
