import { resolve } from './api.js';

const feedbackTemplateUrl = '/services/feedback/templates';
const templateQuestionsUrl = '/services/feedback/template_questions';

export const createFeedbackTemplate = async (feedbackTemplate, cookie) => {
  return resolve({
    method: 'POST',
    url: feedbackTemplateUrl,
    data: feedbackTemplate,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const createFeedbackTemplateWithQuestion = async (
  template,
  question,
  cookie
) => {
  const templateReq = resolve({
    method: 'POST',
    url: feedbackTemplateUrl,
    data: template,
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });

  const questionReq = templateReq.then(templateRes => {
    if (!templateRes.error && templateRes.payload && templateRes.payload.data) {
      question.templateId = templateRes.payload.data.id;
      return resolve({
        method: 'POST',
        url: templateQuestionsUrl,
        data: question,
        headers: {
          'X-CSRF-Header': cookie,
          Accept: 'application/json',
          'Content-Type': 'application/json;charset=UTF-8'
        }
      });
    }
  });

  return Promise.all([templateReq, questionReq]).then(
    ([templateRes, questionRes]) => {
      return { templateRes, questionRes };
    }
  );
};

export const createTemplateQuestions = async (questions, cookie) => {
  const questionReqs = [];
  questions.forEach(question => {
    questionReqs.push(
      resolve({
        method: 'POST',
        url: templateQuestionsUrl,
        data: question,
        headers: {
          'X-CSRF-Header': cookie,
          Accept: 'application/json',
          'Content-Type': 'application/json;charset=UTF-8'
        }
      })
    );
  });

  Promise.all(questionReqs).then(res => {
    return res;
  });
};

export const getFeedbackTemplate = async (feedbackTemplateId, cookie) => {
  return resolve({
    url: `${feedbackTemplateUrl}/${feedbackTemplateId}`,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const getFeedbackQuestion = async (questionId, cookie) => {
  return resolve({
    url: `${templateQuestionsUrl}/${questionId}`,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const getQuestionsOnTemplate = async (templateId, cookie) => {
  return resolve({
    url: templateQuestionsUrl,
    params: {
      templateId: templateId
    },
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const getFeedbackTemplateWithQuestions = async (templateId, cookie) => {
  const templateReq = getFeedbackTemplate(templateId, cookie);
  const questionsReq = getQuestionsOnTemplate(templateId, cookie);

  const [templateRes, questionsRes] = await Promise.all([
    templateReq,
    questionsReq
  ]);

  const templateData =
    templateRes.payload && templateRes.payload.data && !templateRes.error
      ? templateRes.payload.data
      : null;

  let questionsData =
    questionsRes.payload && questionsRes.payload.data && !questionsRes.error
      ? questionsRes.payload.data
      : null;

  questionsData = questionsData
    ? questionsData.sort((a, b) => a.questionNumber - b.questionNumber)
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
};

export const softDeleteAdHocTemplates = async (creatorId, cookie) => {
  return resolve({
    method: 'DELETE',
    url: `${feedbackTemplateUrl}/creator/${creatorId}`,
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const getAllFeedbackTemplates = async cookie => {
  return resolve({
    url: feedbackTemplateUrl,
    params: {
      creatorId: null,
      title: null
    },
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};
