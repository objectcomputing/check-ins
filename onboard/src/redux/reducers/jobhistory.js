export const JOBHISTORY_ACTIONS = {
    LOAD_JOBHISTORY: 'LOAD_JOBHISTORY',
    ADD_JOBHISTORY: 'ADD_JOBHISTORY',
    INVALID_JOBHISTORY_LOAD: 'INVALID_JOBHISTORY_LOAD',
    RESET_JOBHISTORY: 'RESET_JOBHISTORY'
  };
  
  // For our Job History's initial state, we initialize with an empty array.
  const initialState = [];
  
  // The job history reducer allows various actions based on action type passed in to update the state in the store
  function jobHistoryReducer(state = initialState, action) {
    switch (action.type) {
      case JOBHISTORY_ACTIONS.LOAD_JOBHISTORY: {
        return action.payload;
      }
      case JOBHISTORY_ACTIONS.ADD_JOBHISTORY: {
        const filteredState = state.filter(currentObjs => currentObjs.id === action.payload?.id);
        const newState = [...filteredState, action.payload]; 
        return newState;
      }
      case JOBHISTORY_ACTIONS.INVALID_JOBHISTORY_LOAD: {
        let invalidLoad = { ...state, status: action.payload.status }
        return invalidLoad;
      }
      case JOBHISTORY_ACTIONS.RESET_JOBHISTORY:
      default:
        return state;
    }
  }
  
  export default jobHistoryReducer;