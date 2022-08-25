import { resolve } from "./api.js";

const kudosUrl = "/services/kudos";

export const createKudos = async (kudos, cookie) => {
  return resolve({
    method: "post",
    url: kudosUrl,
    data: kudos,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie }
  });
};

export const getReceivedKudos = async (memberId, cookie) => {
  return resolve({
    url: kudosUrl,
    params: {
      recipientId: memberId,
      includePending: false
    },
    responseType: "json",
    headers: { "X-CSRF-Header": cookie }
  });
};

export const getSentKudos = async (memberId, cookie) => {
  return resolve({
    url: kudosUrl,
    params: {
      senderId: memberId,
      includePending: true
    },
    responseType: "json",
    headers: { "X-CSRF-Header": cookie }
  });
};