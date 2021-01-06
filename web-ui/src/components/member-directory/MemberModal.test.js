import React from "react";
import MemberSummaryCard from "./MemberSummaryCard";
import { AppContextProvider } from "../../context/AppContext";

const initialState = {
  state: {
    userProfile: {
      memberProfile: {
        id: "912834091823",
      },
      role: ["MEMBER"],
    },
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
  supervisorid: "43j2i43o2",
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
