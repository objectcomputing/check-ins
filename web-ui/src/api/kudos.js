import { resolve } from "./api.js";

const kudosUrl = "/services/kudos";

export const createKudos = async (kudos, cookie) => {
  return resolve({
    method: "post",
    url: kudosUrl,
    data: kudos,
    responseType: "json",
    headers: {
      "X-CSRF-Header": cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'
    }
  });
};

export const getReceivedKudos = async (memberId, cookie) => {
  return resolve({
    url: kudosUrl,
    params: {
      recipientId: memberId
    },
    responseType: "json",
    headers: { "X-CSRF-Header": cookie, Accept: 'application/json' }
  });
};

export const getSentKudos = async (memberId, cookie) => {
  return resolve({
    url: kudosUrl,
    params: {
      senderId: memberId
    },
    responseType: "json",
    headers: { "X-CSRF-Header": cookie, Accept: 'application/json' }
  });
};

export const getAllKudos = async (cookie, isPending) => {
  return resolve({
    url: kudosUrl,
    params: {
      isPending: isPending
    },
    responseType: "json",
    headers: { 'X-CSRF-Header': cookie, Accept: 'application/json' }
  });
};

export const approveKudos = async (kudos, cookie) => {
  return resolve({
    method: "put",
    url: kudosUrl,
    data: kudos,
    responseType: "json",
    headers: {
      "X-CSRF-Header": cookie,
      Accept: 'application/json',
      'Content-Type': 'application/json;charset=UTF-8'}
  });
};

export const deleteKudos = async (kudosId, cookie) => {
  return resolve({
    method: "delete",
    url: `${kudosUrl}/${kudosId}`,
    responseType: "json",
    headers: { "X-CSRF-Header": cookie, Accept: 'application/json' }
  });
};