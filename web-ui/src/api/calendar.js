import { resolve } from "./api.js";

const calendarUrl = "/services/calendar";

export const uploadEvent = async (cookie) => {
    return resolve({
      headers: { "X-CSRF-Header": cookie },
      method: "post",
      url: calendarUrl,
    });
  };