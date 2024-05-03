import React from 'react';
import { Router } from 'react-router-dom';
import { ErrorBoundary } from 'react-error-boundary';
import { createBrowserHistory } from 'history';

import Routes from './components/routes/Routes';
import Menu from './components/menu/Menu';
import ErrorFallback from './pages/ErrorBoundaryPage';
import { AppContextProvider } from './context/AppContext';
import SnackBarWithContext from './components/snackbar/SnackBarWithContext';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { LocalizationProvider } from '@mui/x-date-pickers';

import {
  experimental_extendTheme as extendTheme,
  Experimental_CssVarsProvider as CssVarsProvider
} from '@mui/material/styles';

import './App.css';

function getUserColorScheme() {
  if (
    window.matchMedia &&
    window.matchMedia('(prefers-color-scheme: dark)').matches
  ) {
    return 'dark';
  } else if (
    window.matchMedia &&
    window.matchMedia('(prefers-color-scheme: light)').matches
  ) {
    return 'light';
  } else {
    return 'light';
  }
}

const theme = extendTheme({
  cssVarPrefix: 'checkins',
  colorSchemes: {
    // light: {
    //   palette: {
    //     primary: {
    //       main: '#2d519e',
    //     },
    //     background: {
    //       default: '#F5F5F6',
    //       paper: '#fff'
    //     }
    //   }
    // },
    // dark: {
    //   palette: {
    //     primary: {
    //       main: '#2d519e',
    //     },
    //   }
    // }
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
    // MuiTextField: {
    //   defaultProps: {
    //     variant: 'standard'
    //   }
    // }
  }
});

const customHistory = createBrowserHistory();

function App() {
  return (
    <CssVarsProvider theme={theme}>
      <LocalizationProvider dateAdapter={AdapterDateFns}>
        <Router history={customHistory}>
          <AppContextProvider>
            <ErrorBoundary FallbackComponent={ErrorFallback}>
              <div>
                <Menu />
                <div className="App">
                  <Routes />
                </div>
              </div>
            </ErrorBoundary>
            <SnackBarWithContext />
          </AppContextProvider>
        </Router>
      </LocalizationProvider>
    </CssVarsProvider>
  );
}

export default App;
