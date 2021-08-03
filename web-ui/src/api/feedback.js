import { resolve } from "./api.js";

const feedbackSuggestionURL = "/services/feedback/suggestions";
const feedbackRequestURL = "/services/feedback/requests"

export const getFeedbackSuggestion = async (id, cookie) => {
  return resolve({
    url: `${feedbackSuggestionURL}/${id}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie }
  });
};


export const createFeedbackRequest = async (feedbackRequest, cookie) => {
  return resolve({
    method: "post",
    url: feedbackRequestURL,
    responseType: "json",
    data: feedbackRequest,
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getFeedbackRequestById = async (id, cookie) => {
  return resolve({
    url: `${feedbackRequestURL}/${id}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie }
  });
}

export const getFeedbackRequestsByCreator = async(creatorId, cookie) => {
  return resolve({
    url: feedbackRequestURL,
    params: {
      creatorId: creatorId
    },
    responseType: "json",
    headers: { "X-CSRF-Header": cookie }
  });
}