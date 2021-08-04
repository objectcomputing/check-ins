import { resolve } from "./api.js";
import {getFeedbackTemplateWithQuestions} from './feedbacktemplate.js'

const feedbackSuggestionURL = "/services/feedback/suggestions";
const feedbackRequestURL = "/services/feedback/requests";
const answerURL = "/services/feedback/answers";

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
};

export const getAnswersByQuestionId = async(questionId, cookie) => {
  return resolve({
    url: `${answerURL}`,
    responseType: "json",
    params: {
      questionId: questionId
    },
    headers: { "X-CSRF-Header": cookie }
  });

}
export const getQuestionsByRequestId = async (requestId, cookie) => {
  const requestReq = getFeedbackRequestById(requestId, cookie);
  let getFeedbackReq = requestReq.then((requestRes) => {
    if (requestRes.payload && requestRes.payload.data && !requestRes.error ) {
      return getFeedbackTemplateWithQuestions(requestRes.payload.data.templateId, cookie)
    }
  });

  return Promise.all([requestReq, getFeedbackReq]).then(([requestRes, getFeedbackRes]) => {
    return getFeedbackRes;
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