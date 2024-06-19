import { resolve } from './api.js';

const emailNotificationURL = '/services/email-notifications';
const emailURL = '/services/email';
const testEmailURL = import.meta.env.VITE_APP_API_URL
  ? import.meta.env.VITE_APP_URL + '/feedback/submit?request='
  : 'http://localhost:8080/feedback/submit?request=';

export const sendReminderNotification = async (
  feedbackRequestId,
  recipients,
  cookie
) => {
  let subject = 'Please fill out your OCI feedback request!';
  let content =
    'Please go to ' +
    testEmailURL +
    feedbackRequestId +
    ' to complete this feedback request. Thanks!';
  return resolve({
    method: 'POST',
    url: emailNotificationURL,
    data: { subject: subject, content: content, recipients: recipients },
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const sendEmail = async (subject, content, html, recipients, cookie) => {
  return resolve({
    method: 'POST',
    url: emailURL,
    data: {
      subject: subject,
      content: content,
      html: html,
      recipients: recipients
    },
    headers: {
      'X-CSRF-Header': cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};
