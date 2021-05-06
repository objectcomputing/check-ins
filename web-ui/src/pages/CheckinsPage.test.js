import React from "react";
import CheckinsPage from "./CheckinsPage";
import { AppContextProvider } from "../context/AppContext";
import { createMemoryHistory } from "history";
import { Router } from "react-router-dom";
import { MuiPickersUtilsProvider } from "@material-ui/pickers";
import DateFnsUtils from "@date-io/date-fns";

const mockMemberId = "bf9975f8-a5b2-4551-b729-afd56b49e2cc";
const mockCheckinId = "3a1906df-d45c-4ff5-a6f8-7dacba97ff1a";

const initialState = {
  state: {
    checkins: [
      {
        id: mockCheckinId,
        memberId: mockMemberId,
        createdbyid: "5425d835-dcd1-4d91-9540-200c06f18f28",
        description: "updated string",
        checkInDate: [2020, 9, 8],
      },
      {
        id: "3a1906df-d45c-4ff5-a6f8-7dacba97ff1b",
        memberId: "bf9975f8-a5b2-4551-b729-afd56b49e2cc",
        createdbyid: "5425d835-dcd1-4d91-9540-200c06f18f29",
        description: "second updated string",
        checkInDate: [2020, 10, 18],
      },
    ],
    userProfile: {
      name: "holmes",
      role: ["MEMBER"],
      imageUrl:
        "https://upload.wikimedia.org/wikipedia/commons/7/74/SNL_MrBill_Doll.jpg",
    },
    memberProfiles: [
      {
        id: mockMemberId,
        name: "holmes",
      },
    ],
    index: 0,
  },
};

it("renders correctly", () => {
  const history = createMemoryHistory(
    `/checkins/${mockMemberId}/${mockCheckinId}`
  );
  snapshot(
    <MuiPickersUtilsProvider utils={DateFnsUtils}>
      <Router history={history}>
        <AppContextProvider value={initialState}>
          <CheckinsPage />
        </AppContextProvider>
      </Router>
    </MuiPickersUtilsProvider>
  );
});
