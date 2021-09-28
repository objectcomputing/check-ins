import { resolve } from "./api.js";

let emailURL = "/services/email-notifications";
let testEmailURL = process.env.REACT_APP_API_URL
    ? process.env.REACT_APP_API_URL + "/feedback/submit?request="
    : "https://checkins.objectcomputing.com/feedback/submit?request=";

export const sendReminderNotification = async (feedbackRequestId, recipients, cookie) => {
  let subject = "Please fill out your OCI feedback request!"
  let content = "Please go to " + testEmailURL + feedbackRequestId + " to complete this feedback request. Thanks!"
  return resolve({
    method: "post",
    url: emailURL,
    responseType: "json",
    data: {subject: subject, content: content, recipients: recipients},
    headers: { "X-CSRF-Header": cookie },
  });
};