import { resolve, BASE_API_URL } from "./api.js";
import { rest } from "msw";
import { setupServer } from "msw/node";

const server = setupServer(
    rest.get(
        "http://localhost:8080/",
        (req, res, ctx) => {
          return res(ctx.json("Hello World"));
        }
    ),
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

test("Happy Path Resolve", done => {
  setTimeout(async () => {
    let res = await resolve({
      url: "/"
    });
    expect(res.payload.data).toStrictEqual("Hello World");
    done();
  }, 4000);
});

test("Error Resolve", async () => {
  window.snackDispatch = () => {};
  let res = await resolve({
    url: "/fail"
  });
  expect(res.error.response.data.message).toStrictEqual("Internal Server Error");
});
