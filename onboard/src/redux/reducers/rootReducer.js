import { combineReducers } from 'redux';
import idleTimerReducer from './idleTimer';
import loginReducer from './login';
import profileReducer from './profile';

// When further reducers need to be utilized, we can combine them here in the Root Reducer.
const rootReducer = () => {
  const reducer = combineReducers({
    idleTimer: idleTimerReducer,
    login: loginReducer,
    profile: profileReducer
  });

  // Purge data on logout by resetting store to its initial state
  return (state, action) => {
    if (action.type === 'LOGOUT') {
      state = undefined;
    }
    return reducer(state, action);
  };
};

export default rootReducer;
