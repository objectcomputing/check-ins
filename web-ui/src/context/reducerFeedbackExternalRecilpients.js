import {
  SET_CSRF,
  UPDATE_TOAST,

} from './actionsFeedbackExternalRecipient.js';

export const initialState = {
  csrf: undefined,
  index: 0,
  toast: {
    severity: '',
    toast: ''
  },
};

export const reducerFeedbackExternalRecipient = (state, action) => {
  switch (action.type) {
    case SET_CSRF:
      state.csrf = action.payload;
      break;
    case UPDATE_TOAST:
      state.toast = action.payload;
      break;
    default:
  }
  return { ...state };
};
