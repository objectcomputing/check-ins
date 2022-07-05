import { resolve } from "./api.js";

const emailNotificationURL = "/services/email-notifications";
const emailNewsletterURL = "/services/email";
const testEmailURL = process.env.REACT_APP_API_URL
    ? process.env.REACT_APP_API_URL + "/feedback/submit?request="
    : "https://checkins.objectcomputing.com/feedback/submit?request=";

export const sendReminderNotification = async (feedbackRequestId, recipients, cookie) => {
  let subject = "Please fill out your OCI feedback request!"
  let content = "Please go to " + testEmailURL + feedbackRequestId + " to complete this feedback request. Thanks!"
  return resolve({
    method: "post",
    url: emailNotificationURL,
    responseType: "json",
    data: {subject: subject, content: content, recipients: recipients},
    headers: { "X-CSRF-Header": cookie },
  });
};

export const sendEmail = async (subject, content, html, recipients, cookie) => {
  return resolve({
    method: "post",
    url: emailNewsletterURL,
    responseType: "json",
    data: {
      subject: subject,
      content: content,
      html: html,
      recipients: recipients
    },
    headers: { "X-CSRF-Header": cookie }
  });
};