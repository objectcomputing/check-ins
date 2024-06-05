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

export const emailPDLAssignment = async (member, cookie) => {
  if (member.pdlId && member.lastName && member.firstName && member.workEmail) {
    let res = await getMember(member.pdlId);
    let pdl = res.payload?.data && !res.error ? res.payload.data : null;
    if (pdl?.workEmail) {
      await sendEmail(
        'You have been assigned as the PDL of ' +
          member.firstName +
          ' ' +
          member.lastName,
        member.firstName +
          ' ' +
          member.lastName +
          ' will now report to you as their PDL. Please engage with them: ' +
          member.workEmail,
        false,
        [pdl.workEmail],
        cookie
      );
    } else {
      console.warn(
        'Unable to send email regarding ' +
          member.firstName +
          ' ' +
          member.lastName +
          "'s PDL update as the PDL was unable to be pulled up correctly"
      );
    }
  } else {
    console.warn(
      'Unable to send email regarding as member was not valid and missing required fields',
      member
    );
  }
};
export const emailSupervisorAssignment = async (member, cookie) => {
  if (
    member.supervisorid &&
    member.lastName &&
    member.firstName &&
    member.workEmail
  ) {
    let res = await getMember(member.supervisorid);
    let supervisor = res.payload?.data && !res.error ? res.payload.data : null;
    if (supervisor?.workEmail) {
      await sendEmail(
        'You have been assigned as the supervisor of ' +
          member.firstName +
          ' ' +
          member.lastName,
        member.firstName +
          ' ' +
          member.lastName +
          ' will now report to you as their supervisor. Please engage with them: ' +
          member.workEmail,
        false,
        [supervisor.workEmail],
        cookie
      );
    } else {
      console.warn(
        'Unable to send email regarding ' +
          member.firstName +
          ' ' +
          member.lastName +
          "'s supervisor update as the supervisor was unable to be pulled up correctly"
      );
    }
  } else {
    console.warn(
      'Unable to send email regarding as member was not valid and missing required fields',
      member
    );
  }
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
