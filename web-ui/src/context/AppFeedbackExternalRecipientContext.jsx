import React, { useEffect, useReducer, useMemo } from 'react';
import { reducer, initialState } from './reducer';

const AppFeedbackExternalRecipientContext = React.createContext();

const AppFeedbackExternalRecipientContextProvider = props => {
  const [state, dispatch] = useReducer(
    reducer,
    props?.value?.state || initialState
  );

  return (
    <AppFeedbackExternalRecipientContext.Provider value={props.value || value}>
      {props.children}
    </AppFeedbackExternalRecipientContext.Provider>
  );
};

export { AppFeedbackExternalRecipientContext, AppFeedbackExternalRecipientContextProvider };
