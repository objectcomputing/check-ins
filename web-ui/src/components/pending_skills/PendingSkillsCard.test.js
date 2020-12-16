import React from "react";
import PendingSkillsCard from "./PendingSkillsCard";
import PendingSkillsPage from "../../pages/PendingSkillsPage";
import { AppContextProvider } from "../../context/AppContext";
import { rest } from "msw";
import { setupServer } from "msw/node";

const initialState = {
  state: {
    userProfile: {
      memberProfile: {
        id: "912834091823",
      },
      role: ["MEMBER"],
    },
    memberProfiles: ["912834091823"],
    skills: [
      { id: "918275", name: "skill1", description: "first" },
      { id: "9183455", name: "skill2", description: "second" },
    ],
  },
};

const pendingSkill = {
  extraneous: false,
  id: "1134511f3e-7ab7-4edf-86f5-ab0b0a0d2ca9",
  name: "Le test skill",
  pending: false,
};

let open = true;
const handleClose = () => (open = false);

const server = setupServer(
  rest.get("http://localhost:8080/services/skill", (req, res, ctx) => {
    return res(ctx.json("Hello World"));
  }),
  rest.get("http://localhost:8080/fail", (req, res, ctx) => {
    return res(ctx.status(500), ctx.json({ message: "Internal Server Error" }));
  })
);

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

it("renders correctly", () => {
  snapshot(
    <AppContextProvider value={initialState}>
      <PendingSkillsCard pendingSkill={pendingSkill} />
    </AppContextProvider>
  );
});

it("renders correctly", () => {
  {
    /* PendingSkillsPage contains CombineSkillsModal */
  }
  snapshot(
    <AppContextProvider value={initialState}>
      <PendingSkillsPage />
    </AppContextProvider>
  );
});
