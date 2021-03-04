import React from "react";
import MemberSummaryCard from "./MemberSummaryCard";
import { AppContextProvider } from "../../context/AppContext";

const initialState = {
  state: {
    userProfile: {
      memberProfile: {
        id: "912834091823",
        name: "Current",
        pdlId: "0987654321",
        supervisorId: "9876543210"
      },
      role: ["MEMBER"],
    },
    memberProfiles: [
        {
          id: "0987654321",
          name: "TestName"
        },
        {
          id: "9876543210",
          name: "TestName2"
        },
    ],
  },
};

const onSave = (member) => {
  console.log(member);
};

const open = () => {
  console.log("Open");
};

const close = () => {
  console.log("Closed");
};

const member = {
  name: "testerson",
  id: "2o34i2j34",
  startDate: [2018, 1, 10],
  location: "STL",
  imageURL: "url.com",
  title: "engineer",
  workEmail: "testerson@oci.com",
};

it("renders correctly", () => {
  snapshot(
    <AppContextProvider value={initialState}>
      <MemberSummaryCard
        member={member}
        onSave={onSave}
        open={open}
        onClose={close}
      />
    </AppContextProvider>
  );
});
