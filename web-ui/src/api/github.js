import { resolve } from "./api.js";

const githubURL = `/services/github-issue`;

export const newGitHubIssue = async (body, title, cookie) => {
  return resolve({
    method: "post",
    url: githubURL,
    responseType: "json",
    data: {
      body: body,
      title: title,
    },
    withCredentials: true,
    headers: { "X-CSRF-Header": cookie },
  });
};
