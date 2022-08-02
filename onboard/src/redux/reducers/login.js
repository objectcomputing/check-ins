export const ACTIONS = {
    LOAD_USER: 'LOAD_USER',
    INVALID_LOAD: 'INVALID_LOAD',
    RESET_USER: 'RESET_USER'
  };
  
  // For our account's initial state, we initialize with an object and empty values.
  const initialState = {
    accessToken: '',
    email: '',
    expiration: '',
    firstName: '',
    lastName: '',
    status: ''
  };
  
  // The account reducer allows various actions based on action type passed in to update the state in the store
  function loginReducer(state = initialState, action) {
    switch (action.type) {
      // We load the user from the store
      case ACTIONS.LOAD_USER: {
        // console.log(action.payload);
        return action.payload;
      }
      // In case of error loading / bad password auth (status 401, etc)
      case ACTIONS.INVALID_LOAD: {
        // console.log(action.payload);
        let invalidLoad = { ...state, status: action.payload.status }
        return invalidLoad;
      }
      case ACTIONS.RESET_USER:
      default:
        return state;
    }
  }
  
  export default loginReducer;
  