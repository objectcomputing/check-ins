import { resolve } from "./api.js";
import { chain } from "lodash";
import {getFeedbackQuestion} from "./feedbacktemplate";

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

// export const getAllAnswers = async (feedbackRequests, cookie) => {
//
//   console.log(feedbackRequests[0]);
//   getAnswersFromRequest(feedbackRequests[0], cookie).then((res) => {
//     console.log(res);
//   })
//
//   console.log(feedbackRequests.map(request => getFeedbackQuestion(request, cookie)));
//   return Promise.all(feedbackRequests.map((request) => getFeedbackQuestion(request, cookie)));
// }

export const getQuestionsAndAnswers = async (feedbackRequests, cookie) => {

  const answerReqs = feedbackRequests.map((request) => {
    return getAnswersFromRequest(request, cookie);
  });

  Promise.all(answerReqs).then((responses) => {
    return responses.map((res) => res.payload.data);
  }).then(([answers]) => {
    const questionsAndAnswers = chain(answers)
      .groupBy("questionId")
      .map((value, key) => ({ questionId: key, answers: value }))
      .value();

    const questionReqs = questionsAndAnswers.map((qna) => {
      return getFeedbackQuestion(qna.questionId, cookie);
    });

    Promise.all(questionReqs).then((questionResponses) => {
      console.log(questionResponses);
      const questions = questionResponses.map((questionRes) => questionRes.payload.data);
      console.log(questions);
      for (let question of questions) {
        console.log(question);
        const qnaData = questionsAndAnswers.map((qna) => {

        })
      }
    });

  });

  // return Promise.all(answerReqs).then((res) => {
  //   console.log(res);
  //   const questionsAndAnswers = groupBy(res.payload.data, "questionId");
  //   console.log(questionsAndAnswers);
  //   return questionsAndAnswers;
  // }).then((res) => {
  //   console.log(res);
  //   const questionReqs = [];
    // for (let obj of res) {
    //   const question = await getFeedbackQuestion(obj.templateId);
    // }
    // console.log(questionReqs);
    //
    // return Promise.all(questionReqs).then((questionsRes) => {
    //   for (let question of questionsRes) {
    //
    //   }
    // });
  // }).catch((error) => {
  //   console.error(error);
  // });

}
