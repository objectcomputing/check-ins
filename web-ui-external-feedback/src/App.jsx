import React from 'react';
import { Router } from 'react-router-dom';
import { ErrorBoundary } from 'react-error-boundary';
import { createBrowserHistory } from 'history';

import Routes from './components/routes/Routes';
import ErrorFallback from './pages/ErrorBoundaryPage';
import { AppContextProvider } from './context/AppContext';
import SnackBarWithContext from './components/snackbar/SnackBarWithContext';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { DarkMode, LightMode } from '@mui/icons-material';

import {
  useColorScheme,
  experimental_extendTheme as extendTheme,
  Experimental_CssVarsProvider as CssVarsProvider
} from '@mui/material/styles';

import './App.css';

function getUserColorScheme() {
  if (window?.matchMedia('(prefers-color-scheme: dark)').matches) {
    return 'dark';
  } else if (window?.matchMedia('(prefers-color-scheme: light)').matches) {
    return 'light';
  } else {
    return 'light';
  }
}
function SchemeToggle() {
  const { mode, setMode } = useColorScheme();
  return (
    <div
      className="Menu-modeToggle"
      onClick={() => {
        setMode(mode === 'light' ? 'dark' : 'light');
      }}
      title={`Select ${mode === 'light' ? 'dark' : 'light'} mode`}
    >
      {mode === 'light' ? <DarkMode /> : <LightMode />}
    </div>
  );
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
    MuiButtonBase: {
      defaultProps: {
        disableRipple: typeof process !== 'undefined' && !!process.env.VITEST_WORKER_ID, // No more ripple...only when testing.
      },
    },
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

const customHistory = createBrowserHistory();

getUserColorScheme();

function App() {
  return (
    <CssVarsProvider theme={theme}>
      <LocalizationProvider dateAdapter={AdapterDateFns}>
        <Router history={customHistory}>
          <AppContextProvider>
            <ErrorBoundary FallbackComponent={ErrorFallback}>
              <div>
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
