import { resolve } from "./api.js";

const feedbackSuggestionURL = "/services/feedback/suggestions";
const feedbackRequestURL = "/services/feedback/requests"
const frozenTemplateURL = "/services/feedback/frozen-templates"
const questionsURL = "/services/feedback/frozen-template-questions"
const answerURL = "/services/feedback/answers"

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

export const getFrozenTemplateByRequestId = async (requestId, cookie) => {
  return resolve({
    url: `${frozenTemplateURL}`,
    responseType: "json",
    params: {
      requestId: requestId
    },
    headers: { "X-CSRF-Header": cookie }
  });

}
export const getQuestionsByFrozenTemplateId = async(frozenTemplateId, cookie) => {
  return resolve({
    url: `${questionsURL}`,
    params: {
      templateId: frozenTemplateId
    },
    responseType: "json",
    headers: { "X-CSRF-Header": cookie }
  });
}

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

  const frozenTemplateReq = getFrozenTemplateByRequestId(requestId, cookie);
  const questionsReq = frozenTemplateReq.then((frozenTemplateRes) => {
    if (frozenTemplateRes.payload && frozenTemplateRes.payload.data && !frozenTemplateRes.error ) {
      return getQuestionsByFrozenTemplateId(frozenTemplateRes.payload.data.id, cookie);
    }
  });

  return Promise.all([frozenTemplateReq, questionsReq]).then(([frozenTemplateRes, questionsRes ]) => {
    return questionsRes;
  });

}