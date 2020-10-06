import { resolve, BASE_API_URL } from "./api.js";

test("Happy Path Resolve", async () => {
  let p = new Promise(function (resolve, reject) {
    resolve("Hello World");
  });

  let res = await resolve(p);
  expect(res).toStrictEqual({
    payload: "Hello World",
    error: null,
  });
});

test("Error Resolve", async () => {
  let p = new Promise(function (resolve, reject) {
    reject("End of World");
  });

  window.snackDispatch = () => {};
  let res = await resolve(p);
  expect(res).toStrictEqual({
    payload: null,
    error: "End of World",
  });
});
