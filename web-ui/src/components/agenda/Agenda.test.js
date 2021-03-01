import React from "react";
import Agenda from "./Agenda";
import { AppContextProvider } from "../../context/AppContext";
import { createMemoryHistory } from "history";
import { Router } from 'react-router-dom';

const mockMemberId = "912834091823";
const mockCheckinId = "837465917381";

const initialState = {
  state: {
    userProfile: {
      memberProfile: {
        id: mockMemberId,
      },
    },
    checkins: [
      {
        id: mockCheckinId,
        completed: false,
      }
    ]
  },
};

const history = createMemoryHistory(`/checkins/${mockMemberId}/${mockCheckinId}`);

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'), // use actual for all non-hook parts
  useParams: () => ({
    memberId: mockMemberId,
    checkinId: mockCheckinId,
  }),
  useRouteMatch: () => ({ url: `/checkins/${mockMemberId}/${mockCheckinId}` }),
}));

it("renders correctly", () => {
  snapshot(
    <Router history={history} >
    <AppContextProvider value={initialState}>
      <Agenda checkinId="394810298371" memberName="mr. test" />
    </AppContextProvider>
    </Router>
  );
});
