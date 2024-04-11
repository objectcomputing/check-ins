import { resolve } from "./api.js";

const githubURL = `/services/github-issue`;

export const newGitHubIssue = async (body, title, cookie) => {
  return resolve({
    method: "POST",
    url: githubURL,
    data: {
      body: body,
      title: title,
    },
    headers: { "X-CSRF-Header": cookie, "Accept": "application/json", "Content-Type": "application/json;charset=UTF-8" },
  });
};
