import qs from 'qs';
import { resolve } from './api.js';
import { getFeedbackTemplateWithQuestions } from './feedbacktemplate.js';

const feedbackSuggestionURL = '/services/feedback/suggestions';
const feedbackRequestURL = '/services/feedback/requests';
const answerURL = '/services/feedback/answers';
const questionAndAnswerURL = '/services/feedback/questions-and-answers';

export const findReviewRequestsByPeriodAndTeamMembers = async (
  period,
  teamMemberIds,
  cookie
) => {
  return resolve({
    url: feedbackRequestURL,
    params: {
      reviewPeriodId: period?.id,
      templateId: period?.reviewTemplateId,
      requesteeIds: teamMemberIds
    },
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const findSelfReviewRequestsByPeriodAndTeamMembers = async (
  period,
  teamMemberIds,
  cookie
) => {
  return resolve({
    url: feedbackRequestURL,
    params: {
      reviewPeriodId: period?.id,
      templateId: period?.selfReviewTemplateId,
      requesteeIds: teamMemberIds
    },
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const findSelfReviewRequestsByPeriodAndTeamMember = async (
  period,
  teamMemberId,
  cookie
) => {
  return resolve({
    url: feedbackRequestURL,
    params: {
      reviewPeriodId: period?.id,
      templateId: period?.selfReviewTemplateId,
      requesteeId: teamMemberId,
      recipientId: teamMemberId
    },
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const getFeedbackSuggestion = async (id, cookie) => {
  return resolve({
    url: `${feedbackSuggestionURL}/${id}`,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const createFeedbackRequest = async (feedbackRequest, cookie) => {
  return resolve({
    method: 'POST',
    url: feedbackRequestURL,
    data: feedbackRequest,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const updateFeedbackRequest = async (feedbackRequest, cookie) => {
  return resolve({
    method: 'PUT',
    url: feedbackRequestURL,
    data: feedbackRequest,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const cancelFeedbackRequest = async (feedbackRequest, cookie) => {
  return resolve({
    method: 'PUT',
    url: feedbackRequestURL,
    data: {
      ...feedbackRequest,
      status: 'canceled',
      dueDate: null
    },
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const deleteFeedbackRequestById = async (id, cookie) => {
  return resolve({
    method: 'DELETE',
    url: `${feedbackRequestURL}/${id}`,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const getFeedbackRequestById = async (id, cookie) => {
  return resolve({
    url: `${feedbackRequestURL}/${id}`,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const getAnswerByRequestAndQuestionId = async (
  requestId,
  questionId,
  cookie
) => {
  return resolve({
    url: `${questionAndAnswerURL}`,
    params: {
      questionId: questionId,
      requestId: requestId
    },
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const getAllAnswersFromRequestAndQuestionId = async (
  requestId,
  questions,
  cookie
) => {
  let answerReqs = [];
  questions.forEach(question => {
    answerReqs.push(
      resolve({
        url: `${questionAndAnswerURL}`,
        params: {
          questionId: question.id,
          requestId: requestId
        },
        headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
      })
    );
  });

  return Promise.all(answerReqs).then(res => {
    let finalReturn = [];
    res.forEach(questionAnswerPair => {
      if (
        questionAnswerPair &&
        questionAnswerPair.payload &&
        questionAnswerPair.payload.data &&
        !questionAnswerPair.error
      ) {
        finalReturn.push(questionAnswerPair.payload.data);
      }
    });
    return finalReturn;
  });
};

export const getQuestionsByRequestId = async (requestId, cookie) => {
  const requestRes = await getFeedbackRequestById(requestId, cookie);

  if (requestRes.payload && requestRes.payload.data && !requestRes.error) {
    return getFeedbackTemplateWithQuestions(
      requestRes.payload.data.templateId,
      cookie
    );
  }
};

export const saveSingleAnswer = (answer, cookie) => {
  return resolve({
    url: answerURL,
    method: 'POST',
    data: answer,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const updateSingleAnswer = (answer, cookie) => {
  return resolve({
    url: answerURL,
    method: 'PUT',
    data: answer,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const updateAllAnswers = (answers, cookie) => {
  const answerReqs = [];
  answers.forEach(answer => {
    answerReqs.push(
      resolve({
        method: 'PUT',
        url: answerURL,
        data: answer,
        headers: {
          'X-CSRF-Header': cookie,
          Accept: 'application/json',
          'Content-Type': 'application/json;charset=UTF-8'
        }
      })
    );
  });

  return Promise.all(answerReqs).then(res => {
    return res;
  });
};

export const getFeedbackRequestsByCreator = async (creatorId, cookie) => {
  return resolve({
    url: feedbackRequestURL,
    params: {
      creatorId: creatorId
    },
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const getFeedbackRequestsByRecipient = async (recipientId, cookie) => {
  return resolve({
    url: feedbackRequestURL,
    params: {
      recipientId: recipientId
    },
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const getFeedbackRequestsByRequestee = async (
  requesteeId,
  oldestDate,
  cookie
) => {
  return resolve({
    url: feedbackRequestURL,
    params: {
      requesteeId,
      oldestDate
    },
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const getFeedbackRequestsByRequestees = async (
  requesteeIds,
  oldestDate,
  cookie
) => {
  return resolve({
    url: feedbackRequestURL,
    params: {
      requesteeIds,
      oldestDate
    },
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};
