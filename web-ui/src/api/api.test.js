import { resolve } from "./api.js";
import { http, HttpResponse } from "msw";
import { setupServer } from "msw/node";

const successResult = { id: 123, name: "test result" };

const server = setupServer(...[
    http.get("http://localhost:8080/fail", () => {
      return HttpResponse.json({ message: 'Internal Server PROBLEM' }, { status: 500 });
    }),
    http.get("http://localhost:8080/success", () => {
        return HttpResponse.json(successResult);
    })
  ]
);

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

test("Error Resolve", async () => {
  window.snackDispatch = () => {};
  let res = await resolve({
    url: "/fail"
  });
  expect(res.error.message).toStrictEqual("Internal Server PROBLEM");
});

test("Resolve", async () => {
    window.snackDispatch = () => {};
    let res = await resolve({
        url: "/success"
    });
    expect(res.payload.data).toStrictEqual(successResult);
});
