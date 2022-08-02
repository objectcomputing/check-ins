export const IDLE_ACTIONS = {
    RESET_IDLE_TIMER: 'RESET_IDLE_TIMER'
  };
  
  // For our account's initial state, we initialize with an object and empty values.
  const initialState = {
    idleTimer: 0 // Delimiter will be seconds, not milliseconds.
  };
  
  // The account reducer allows various actions based on action type passed in to update the state in the store
  function idleTimerReducer(state = initialState, action) {
    switch (action.type) {
      // We reset the timer to 15 min again.
      case IDLE_ACTIONS.RESET_IDLE_TIMER: {
        return { idleTimer: 900 }; // This is equivalent to 15 min. 60 sec = 1 min.
      }
      default:
        return state;
    }
  }
  
  export default idleTimerReducer;
  