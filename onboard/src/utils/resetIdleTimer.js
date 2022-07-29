import { IDLE_ACTIONS } from 'redux/reducers/idleTimer';

export const resetIdleTimer = (dispatch) => {
  dispatch({
    type: IDLE_ACTIONS.RESET_IDLE_TIMER
  });
};
