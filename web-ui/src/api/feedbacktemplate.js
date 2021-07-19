import { resolve } from "./api.js";

const feedbackTemplateUrl = "/services/feedback/templates";

export const createFeedbackTemplate = async (feedbackTemplate, cookie) => {
  return resolve({
    method: "post",
    url: feedbackTemplateUrl,
    responseType: "json",
    data: feedbackTemplate,
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getFeedbackTemplate = async (feedbackTemplateId, cookie) => {
  return resolve({
    url: `${feedbackTemplateUrl}/${feedbackTemplateId}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  });
};

export const getAllFeedbackTemplates = async (cookie) => {
  return resolve({
    url: `${feedbackTemplateUrl}/?onlyActive=${true}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie },
  })
}