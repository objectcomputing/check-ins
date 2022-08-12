export const EDUCATION_ACTIONS = {
    LOAD_EDUCATION: 'LOAD_EDUCATION',
    ADD_EDUCATION: 'ADD_EDUCATION',
    INVALID_EDUCATION_LOAD: 'INVALID_EDUCATION_LOAD',
    RESET_EDUCATION: 'RESET_EDUCATION'
  };
  
  // For our education's initial state, we initialize with an empty array.
  const initialState = [];
  
  // The education reducer allows various actions based on action type passed in to update the state in the store
  function educationReducer(state = initialState, action) {
    switch (action.type) {
      case EDUCATION_ACTIONS.LOAD_EDUCATION: {
        return action.payload;
      }
      case EDUCATION_ACTIONS.ADD_EDUCATION: {
        const filteredState = state.filter(currentObjs => currentObjs.id === action.payload?.id);
        const newState = [...filteredState, action.payload]; 
        return newState;
      }
      case EDUCATION_ACTIONS.INVALID_EDUCATION_LOAD: {
        let invalidLoad = { ...state, status: action.payload.status }
        return invalidLoad;
      }
      case EDUCATION_ACTIONS.RESET_EDUCATION:
      default:
        return state;
    }
  }
  
  export default educationReducer;