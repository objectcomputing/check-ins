import { resolve } from "./api.js";
import { getFeedbackTemplateWithQuestions } from './feedbacktemplate.js'

const feedbackSuggestionURL = "/services/feedback/suggestions";
const feedbackRequestURL = "/services/feedback/requests";
const answerURL = "/services/feedback/answers";
const questionAndAnswerURL = "/services/feedback/questions-and-answers"

export const findReviewRequestsByPeriodAndTeamMember =  async (period, teamMemberId, cookie) => {
  return resolve({
    url: feedbackRequestURL,
    params: {
      reviewPeriodId: period?.id,
      templateId: period?.reviewTemplateId,
      requesteeId: teamMemberId,
    },
    responseType: "json",
    headers: { "X-CSRF-Header": cookie }
  });
};

export const findSelfReviewRequestsByPeriodAndTeamMember =  async (period, teamMemberId, cookie) => {
  return resolve({
    url: feedbackRequestURL,
    params: {
      reviewPeriodId: period?.id,
      templateId: period?.selfReviewTemplateId,
      requesteeId: teamMemberId,
      recipientId: teamMemberId,
    },
    responseType: "json",
    headers: { "X-CSRF-Header": cookie }
  });
};

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

export const cancelFeedbackRequest = async (feedbackRequest, cookie) => {
  return resolve({
    method: "put",
    url: feedbackRequestURL,
    responseType: "json",
    data: {
      ...feedbackRequest,
      status: "canceled",
      dueDate: null
    },
    headers: { "X-CSRF-Header": cookie }
  });
}


export const deleteFeedbackRequestById = async (id, cookie) => {
  return resolve({
    method: "delete",
    url: `${feedbackRequestURL}/${id}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie }
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
  const requestRes = await getFeedbackRequestById(requestId, cookie);

  if (requestRes.payload && requestRes.payload.data && !requestRes.error) {
    return getFeedbackTemplateWithQuestions(requestRes.payload.data.templateId, cookie);
  }
}

export const saveSingleAnswer = (answer, cookie) => {
  return resolve({
    url: answerURL,
    method: "post",
    responseType: "json",
    data: answer,
    headers: { "X-CSRF-Header": cookie }
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

export const getFeedbackRequestsByRecipient = async(recipientId, cookie) => {
  return resolve({
    url: feedbackRequestURL,
    params: {
      recipientId: recipientId
    },
    responseType: "json",
    headers: { "X-CSRF-Header": cookie }
  });
}

export const getFeedbackRequestsByRequestee = async(requesteeId, oldestDate, cookie) => {
  return resolve({
    url: feedbackRequestURL,
    params: {
      requesteeId,
      oldestDate
    },
    responseType: "json",
    headers: { "X-CSRF-Header": cookie }
  });
}
