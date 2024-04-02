import React from "react";
import MemberSelectorDialog from "./MemberSelectorDialog";
import {AppContextProvider} from "../../../context/AppContext";

const members = [
  {
    name: "Bob Jones",
    id: "2o34i2j34",
    startDate: [2018, 1, 10],
    location: "STL",
    title: "Engineer",
    workEmail: "bobjones@objectcomputing.com",
  },
  {
    name: "Jane Doe",
    id: "3p45j3k45",
    startDate: [2017, 2, 11],
    location: "STL",
    title: "Manager",
    workEmail: "janedoe@objectcomputing.com",
  }
];


describe("MemberSelectorDialog", () => {
  it("renders correctly", () => {
    snapshot(
      <AppContextProvider>
        <MemberSelectorDialog
          open={true}
          selectedMembers={members}
          onClose={jest.fn()}
          onSubmit={jest.fn()}
        />
      </AppContextProvider>
    );
  });
});
