import { resolve } from './apiFeedbackExternalRecipient.js';

const feedbackExternalRecipientsURL = '/services/feedback/requests/external/recipients';

export const getFeedbackRequest = async (cookie, id) => {
  return resolve({
    url: `${feedbackExternalRecipientsURL}/${id}`,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const getFeedbackRequestNoCookie = async (id) => {
  return resolve({
    url: `${feedbackExternalRecipientsURL}/${id}`,
    headers: { Accept: 'application/json' }
  });
};
