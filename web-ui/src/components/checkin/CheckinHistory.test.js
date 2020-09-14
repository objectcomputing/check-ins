import React from "react";
import CheckinsHistory from "./CheckinHistory";
import { AppContextProvider } from "../../context/AppContext";
import { act } from "react-dom/test-utils";
import { render } from "@testing-library/react";

const initialState = {
  state: {
    checkins: [
      {
        id: "3a1906df-d45c-4ff5-a6f8-7dacba97ff1a",
        checkinid: "bf9975f8-a5b2-4551-b729-afd56b49e2cc",
        createdbyid: "5425d835-dcd1-4d91-9540-200c06f18f28",
        description: "updated string",
        checkInDate: [2020, 9, 8],
      },
      {
        id: "3a1906df-d45c-4ff5-a6f8-7dacba97ff1b",
        checkinid: "bf9975f8-a5b2-4551-b729-afd56b49e2cd",
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
    index: 0,
  },
};

it("renders correctly", async () => {
  let asFragment;
  await act(async () => {
    ({ asFragment } = render(
      <AppContextProvider value={initialState}>
        <CheckinsHistory
          checkins={initialState.state.checkins}
          index={initialState.state.index}
        />
      </AppContextProvider>
    ));
  });
  expect(asFragment()).toMatchSnapshot();
});
