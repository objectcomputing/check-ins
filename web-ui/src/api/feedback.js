import { resolve } from "./api.js";

const feedbackSuggestionURL = "/services/feedback/suggestions";

export const getFeedbackSuggestion = async (id, cookie) => {
  return resolve({
    url: `${feedbackSuggestionURL}/${id}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie }
  });
};