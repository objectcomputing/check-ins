import { resolve } from "./api.js";
import { groupBy } from "lodash/function";

const feedbackAnswerUrl = "/services/feedback/answers";
const templateQuestionsUrl = "/services/feedback/template_questions";

export const createFeedbackAnswer = async (feedbackAnswer, cookie) => {
  return resolve({
    method: "post",
    url: feedbackAnswerUrl,
    responseType: "json",
    data: feedbackAnswer,
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getFeedbackAnswerById = async (feedbackAnswerId, cookie) => {
  return resolve({
    url: `${feedbackAnswerUrl}/${feedbackAnswerId}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getAnswersFromRequest = async (feedbackRequestId, cookie) => {
  return resolve({
    url: feedbackAnswerUrl,
    params: {
      requestId: feedbackRequestId
    },
    responseType: "json",
    headers: { "X-CSRF-Header": cookie }
  });
}

export const getQuestionsAndAnswers = async (feedbackRequests, cookie) => {
  const answerReqs = [];
  for (let request of feedbackRequests) {
    answerReqs.push(getAnswersFromRequest(request.id, cookie));
  }

  Promise.all(answerReqs).then((answers) => {
    const questionsAndAnswers = groupBy(answers, "questionId");
    console.log(questionsAndAnswers);
  });
}
