import React from "react";
import { AppContextProvider } from "../../context/AppContext";
import MemberSelector from "./MemberSelector";

const initialState = {
  state: {
    userProfile: {
      memberProfile: {
        id: "912834091823",
        name: "Current",
        pdlId: "0987654321",
        supervisorId: "9876543210",
      },
      role: ["MEMBER"],
    },
    memberProfiles: [
      {
        id: "0987654321",
        name: "TestName",
        lastName: "Name",
      },
      {
        id: "9876543210",
        name: "TestName2",
        lastName: "Name2",
      },
    ],
  },
};

it("renders correctly", () => {
  snapshot(
    <AppContextProvider value={initialState}>
      <MemberSelector
        onChange={vi.fn()}
        listHeight={300}
        className="test-class"
        style={{ margin: "10px" }}
      />
    </AppContextProvider>
  );
});
