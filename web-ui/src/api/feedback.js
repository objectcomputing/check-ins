import { resolve } from "./api.js";
import { getFeedbackTemplateWithQuestions } from './feedbacktemplate.js'

const feedbackSuggestionURL = "/services/feedback/suggestions";
const feedbackRequestURL = "/services/feedback/requests";
const answerURL = "/services/feedback/answers";
const questionAndAnswerURL = "/services/feedback/questions-and-answers"

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

export const updateFeedbackRequest = async (feedbackRequest, cookie) => {
  return resolve({
    method: "put",
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
    url: `${questionAndAnswerURL}`,
    responseType: "json",
    params: {
      questionId: questionId,
      requestId: requestId,
    },
    headers: { "X-CSRF-Header": cookie }
  });

}

export const getAllAnswersFromRequestAndQuestionId = async (requestId, questions, cookie) => {
  let answerReqs = []
  questions.forEach((question) => {
    answerReqs.push(resolve({
      url: `${questionAndAnswerURL}`,
      responseType: "json",
      params: {
        questionId: question.id,
        requestId: requestId,
      },
      headers: { "X-CSRF-Header": cookie }
    }));
  });

  return Promise.all(answerReqs).then((res) => {
    let finalReturn = []
    res.forEach((questionAnswerPair) => {
      if (questionAnswerPair && questionAnswerPair.payload && questionAnswerPair.payload.data && !questionAnswerPair.error) {
        finalReturn.push(questionAnswerPair.payload.data)
      }
    })
    return finalReturn;
  });
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
    data: answer,
    headers: { "X-CSRF-Header": cookie }
  });
}

export const updateAllAnswers = (answers, cookie) => {
  const answerReqs = [];
  answers.forEach((answer) => {
    answerReqs.push(resolve({
      method: "put",
      url: answerURL,
      responseType: "json",
      data: answer,
      headers: { "X-CSRF-Header": cookie }
    }));
  });

 return Promise.all(answerReqs).then((res) => {
    return res;
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