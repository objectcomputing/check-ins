import { resolve } from "./api.js";

const feedbackTemplateUrl = "/services/feedback/templates";
const templateQuestionsUrl = "/services/feedback/template_questions";

export const createFeedbackTemplate = async (feedbackTemplate, cookie) => {
  return resolve({
    method: "post",
    url: feedbackTemplateUrl,
    responseType: "json",
    data: feedbackTemplate,
    headers: { "X-CSRF-Header": cookie },
  });
};

export const createFeedbackTemplateWithQuestion = async (template, question, cookie) => {

  const templateReq = resolve({
    method: "post",
    url: feedbackTemplateUrl,
    responseType: "json",
    data: template,
    headers: { "X-CSRF-Header": cookie }
  });

  const questionReq = templateReq.then((templateRes) => {
    if (!templateRes.error && templateRes.payload && templateRes.payload.data) {
      question.templateId = templateRes.payload.data.id;
      return resolve({
        method: "post",
        url: templateQuestionsUrl,
        responseType: "json",
        data: question,
        headers: {"X-CSRF-Header": cookie}
      });
    }
  });

  return Promise.all([templateReq, questionReq]).then(([templateRes, questionRes]) => {
    return {templateRes, questionRes};
  });
}

export const createTemplateQuestions = async (questions, cookie) => {
  const questionReqs = [];
  questions.forEach((question) => {
    questionReqs.push(resolve({
      method: "post",
      url: templateQuestionsUrl,
      responseType: "json",
      data: question,
      headers: { "X-CSRF-Header": cookie }
    }));
  });

  Promise.all(questionReqs).then((res) => {
    return res;
  });
}

export const getFeedbackTemplate = async (feedbackTemplateId, cookie) => {
  return resolve({
    url: `${feedbackTemplateUrl}/${feedbackTemplateId}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getQuestionsOnTemplate = async (templateId, cookie) => {
  return resolve({
    url: templateQuestionsUrl,
    params: {
      templateId: templateId
    },
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
}

export const getFeedbackTemplateWithQuestions = async (templateId, cookie) => {
  const templateReq = getFeedbackTemplate(templateId, cookie);
  const questionsReq = getQuestionsOnTemplate(templateId, cookie);

  return Promise.all([templateReq, questionsReq]).then(([templateRes, questionsRes]) => {
    const templateData =
      templateRes.payload &&
      templateRes.payload.data &&
      !templateRes.error
        ? templateRes.payload.data
        : null;

    const questionsData =
      questionsRes.payload &&
      questionsRes.payload.data &&
      !questionsRes.error
        ? questionsRes.payload.data
        : null;

    let templateWithQuestions = {};
    if (templateData) {
      if (questionsData) {
        templateData.questions = questionsData;
      } else {
        templateData.questions = [];
      }
      templateWithQuestions = templateData;
    }

    return templateWithQuestions;
  });
}

export const softDeleteAdHocTemplates = async (creatorId, cookie) => {
  return resolve({
    method: "delete",
    url: feedbackTemplateUrl,
    params: {
      creatorId: creatorId,
    },
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
}

export const getAllFeedbackTemplates = async (cookie) => {
  return resolve({
    url: feedbackTemplateUrl,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
}