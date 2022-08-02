export const PROFILE_ACTIONS = {
    LOAD_PROFILE: 'LOAD_PROFILE',
    RESET_PROFILE: 'RESET_PROFILE'
  };
  
  // For our account's initial state, we initialize with an empty object.
  const initialState = {};
  
  // The account reducer allows various actions based on action type passed in to update the state in the store
  function profileReducer(state = initialState, action) {
    switch (action.type) {
      case PROFILE_ACTIONS.LOAD_PROFILE: {
        return action.payload;
      }
      case PROFILE_ACTIONS.RESET_PROFILE:
      default:
        return state;
    }
  }
  
  export default profileReducer;
  