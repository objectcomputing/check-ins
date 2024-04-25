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
import { createTheme, ThemeProvider } from '@mui/material/styles';

import './App.css';

function getUserColorScheme() {
    if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
        return 'dark';
    } else if (window.matchMedia && window.matchMedia('(prefers-color-scheme: light)').matches) {
        return 'light';
    } else {
        // Default to light if no preference is set
        return 'light';
    }
}

const customHistory = createBrowserHistory();
const darkTheme = createTheme({
  palette: {
    mode: 'dark',
    primary: {
      main: '#990000'
    }
  },
});

const theme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      light: '#6085d9',
      main: '#2559a7',
      dark: '#003177',
      contrastText: '#fff'
    },
    secondary: {
      light: '#ffe8a2',
      main: '#feb672',
      dark: '#c88645',
      contrastText: '#000'
    },
    background: {
      default: '#F5F5F6',
      paper: '#fff'
    }
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
        variant: 'standard'
      }
    }
  }
});

function App() {
  return (
    <ThemeProvider theme={getUserColorScheme() === 'dark' ? darkTheme : theme}>
      <LocalizationProvider dateAdapter={AdapterDateFns}>
        <Router history={customHistory}>
          <AppContextProvider>
            <ErrorBoundary FallbackComponent={ErrorFallback}>
              <div>
                <Menu />
                <div
                  style={{
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'center'
                  }}
                  className="App"
                >
                  <Routes />
                </div>
              </div>
            </ErrorBoundary>
            <SnackBarWithContext />
          </AppContextProvider>
        </Router>
      </LocalizationProvider>
    </ThemeProvider>
  );
}

export default App;
