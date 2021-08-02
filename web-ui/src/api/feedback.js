import { resolve } from "./api.js";
import { getFeedbackTemplateWithQuestions } from './feedbacktemplate.js'

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

export const getAnswerByRequestAndQuestionId = async (requestId, questionId, cookie) => {
  return resolve({
    url: `${answerURL}`,
    responseType: "json",
    params: {
      questionId: questionId,
      requestId: requestId,
    },
    headers: { "X-CSRF-Header": cookie }
  });

}

export const getAllAnswersFromRequestAndQuestionId = async (questions, cookie) => {
  let answerReqs = []
  for (let i = 0; i < questions.length; ++i) {
    console.log("Question element: " + JSON.stringify(questions[i]))
    answerReqs.push(getAnswerByQuestionId(questions[i].id, cookie))
  }

  return Promise.all([answerReqs]).then(([answerRes]) => {
    return answerRes
  })

}

export const getQuestionsByRequestId = async (requestId, cookie) => {
  const requestReq = getFeedbackRequestById(requestId, cookie);
  let getFeedbackReq = requestReq.then((requestRes) => {
    if (requestRes.payload && requestRes.payload.data && !requestRes.error) {
      return getFeedbackTemplateWithQuestions(requestRes.payload.data.templateId, cookie)
    }
  });

  return Promise.all([requestReq, getFeedbackReq]).then(([requestRes, getFeedbackRes]) => {
    return getFeedbackRes;
  });

}

export const updateSingleAnswer = (answer, cookie) => {
  return resolve({
    url: answerURL,
    method: "put",
    responseType: "json",
    params: {
      data: answer
    },
    headers: { "X-CSRF-Header": cookie }
  });
}

export const saveAllAnswers = (answers, cookie) => {
  const answerReqs = [];
  answers.forEach((answer) => {
    answerReqs.push(resolve({
      method: "post",
      url: answerURL,
      responseType: "json",
      data: answer,
      headers: { "X-CSRF-Header": cookie }
    }));
  });

  Promise.all(answerReqs).then((res) => {
    return res;
  });
}