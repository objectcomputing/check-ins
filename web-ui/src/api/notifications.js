import { resolve } from './api.js';
import { getMember } from './member.js';

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
export const emailGuildLeaders = async (members, guild, cookie) => {
  members.forEach(member => {
    if (!member.workEmail || !guild?.name) {
      console.warn(
        'Unable to send guild leader email as member is missing required fields',
        member
      );
      return;
    }

    const subject = `You have been assigned as a guild leader of ${guild.name}`;
    const body = `Congratulations, you have been assigned as a guild leader of ${guild.name}`;
    sendEmail(subject, body, false, [member.workEmail], cookie);
  });
};
