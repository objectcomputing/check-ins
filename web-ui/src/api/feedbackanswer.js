import { resolve } from "./api.js";
import { chain } from "lodash";

const feedbackAnswerUrl = "/services/feedback/answers";
const questionsAndAnswersUrl = "/services/feedback/questions-and-answers";

export const createFeedbackAnswer = async (feedbackAnswer, cookie) => {
  return resolve({
    method: "POST",
    url: feedbackAnswerUrl,
    data: feedbackAnswer,
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json", "Content-Type": "application/json;charset=UTF-8" },
  });
};

export const getFeedbackAnswerById = async (feedbackAnswerId, cookie) => {
  return resolve({
    url: `${feedbackAnswerUrl}/${feedbackAnswerId}`,
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json" },
  });
};

export const getAnswersFromRequest = async (feedbackRequestId, cookie) => {
  return resolve({
    url: feedbackAnswerUrl,
    params: {
      requestId: feedbackRequestId
    },
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json" },
  });
}

export const getQuestionAndAnswer = async (requestId, cookie) => {
  return resolve({
    url: `${questionsAndAnswersUrl}/${requestId}`,
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json" },
  });
}

export const getQuestionsAndAnswers = async (feedbackRequests, cookie) => {
  const qnaReqs = feedbackRequests.map((request) => {
    return getQuestionAndAnswer(request, cookie);
  });

  return Promise.all(qnaReqs).then((qnaRes) => {
    if (!qnaRes || qnaRes.error) {
      return null;
    }

    // Destructure question data to top of object
    const questionsAndAnswers = qnaRes.map((qna) => {
      if (!qna || !qna.payload || !qna.payload.data) {
        return null;
      }

      return qna.payload.data.map((obj) => {
        return {
          answer: {
            ...obj.answer,
            responder: obj.request.recipientId
          },
          ...obj.question
        }
      });
    });

    // Destructure arrays of questions to prepare for grouping
    const responses = [];
    questionsAndAnswers.forEach((quesAndAns) => {
      responses.push(...quesAndAns);
    });

    // Chain questions and answers so that questions are a top level, while answers are a property of the top level object
    return chain(responses)
      .groupBy("id")
      .map((val, key) => {
        // Obtain array of answers that are related to this question
        let answersForThisQuestion = val.map((val) => {
          return {
            id: val.answer.id,
            answer: val.answer.answer,
            requestId: val.answer.requestId,
            sentiment: val.answer.sentiment,
            responder: val.answer.responder
          }
        });

        // Map aforementioned array of answers to a top-level question object that they all share
        const [questionInfo] = val;
        return {
          id: key,
          question: questionInfo.question,
          questionNumber: questionInfo.questionNumber,
          inputType: questionInfo.inputType,
          templateId: questionInfo.templateId,
          answers: answersForThisQuestion,
        };
      })
      .value();
  });
}
