import { resolve } from "./api.js";
import { rest } from "msw";
import { setupServer } from "msw/node";

const server = setupServer(
    rest.get("http://localhost:8080/fail", (req, res, ctx) => {
      return res(
          ctx.status(500),
          ctx.json({ message: 'Internal Server Error' }),
      );
    })
);

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

test("Error Resolve", async () => {
  window.snackDispatch = () => {};
  let res = await resolve({
    url: "/fail"
  });
  expect(res.error.response.data.message).toStrictEqual("Internal Server Error");
});
