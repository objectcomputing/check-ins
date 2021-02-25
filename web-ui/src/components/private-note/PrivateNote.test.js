import React from "react";
import PrivateNote from "./PrivateNote";
import { AppContextProvider } from "../../context/AppContext";

const checkin = {
  id: "3a1906df-d45c-4ff5-a6f8-7dacba97ff1a",
  checkinid: "bf9975f8-a5b2-4551-b729-afd56b49e2cc",
  createdbyid: "5425d835-dcd1-4d91-9540-200c06f18f28",
  description: "updated string",
};

const initialState = {
  state: {
    userProfile: {
      name: "holmes",
      memberProfile: {
        pdlId: "",
        title: "Tester",
        workEmail: "test@tester.com",
      },
      role: ["MEMBER"],
      imageUrl:
        "https://upload.wikimedia.org/wikipedia/commons/7/74/SNL_MrBill_Doll.jpg",
    },
  },
};

it("renders correctly", () => {
  snapshot(
    <AppContextProvider value={initialState}>
      <PrivateNote
        checkin={checkin}
        memberName={initialState.state.userProfile.name}
      />
    </AppContextProvider>
  );
});
