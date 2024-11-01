import React from 'react';
import { createRoot } from 'react-dom/client';
import CssBaseline from '@mui/material/CssBaseline';
import './index.css';
import * as serviceWorker from './serviceWorker';
import AppFeedbackExternalRecipient from "./AppFeedbackExternalRecipient.jsx";

const container = document.getElementById('root');
const root = createRoot(container);
root.render(
  <>
    <CssBaseline />
    <AppFeedbackExternalRecipient />
  </>
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
