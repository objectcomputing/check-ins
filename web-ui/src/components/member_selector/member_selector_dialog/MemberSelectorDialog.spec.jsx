import React from "react";
import {render, screen} from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import {AppContextProvider} from "../../../context/AppContext.jsx";
import MemberSelectorDialog from "./MemberSelectorDialog.jsx";

const currentUserProfile = {
  id: 9876,
  pdlId: 8765,
  name: "Current User",
  firstName: "Current",
  lastName: "User",
};

const initialState = {
  csrf: "O_3eLX2-e05qpS_yOeg1ZVAs9nDhspEi",
  userProfile: {
    name: currentUserProfile.name,
    firstName: currentUserProfile.firstName,
    lastName: currentUserProfile.lastName,
    role: ["MEMBER"],
    imageUrl: "https://upload.wikimedia.org/wikipedia/commons/7/74/SNL_MrBill_Doll.jpg",
    memberProfile: currentUserProfile
  },
  memberProfiles: [
    currentUserProfile,
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
  ],
  skills: [
    { id: "918275", name: "skill1", description: "first" },
    { id: "9183455", name: "skill2", description: "second" },
  ],
  checkins: [],
  guilds: [],
  teams: [],
  roles: [],
  userRoles: [],
  memberSkills: [],
  index: 0,
};

describe("MemberSelectorDialog", () => {

  beforeEach(async () => {
    render(
      <AppContextProvider value={{ state: initialState }}>
        <MemberSelectorDialog
          selectedMembers={[]}
          open={true}
          onClose={vi.fn()}
          onSubmit={vi.fn()}
        />
      </AppContextProvider>
    );

    // Wait for the dialog to be visible
    await screen.findByRole("heading", {name: "Select Members"});
  });

  it("Populates the list of members by default", async () => {
    const memberList = await screen.findAllByRole("listitem", {});
    expect(memberList).toHaveLength(initialState.memberProfiles.length);
  });

  it("Filters the list of members by name", async () => {
    let memberList = await screen.findAllByRole("listitem", {});
    expect(memberList).toHaveLength(initialState.memberProfiles.length);
    const nameField = await screen.findByRole("textbox", { name: /name/i });
    expect(nameField).toHaveValue("");

    await userEvent.type(nameField, "bob");
    expect(nameField).toHaveValue("bob");
    memberList = await screen.findAllByRole("listitem", {});
    expect(memberList).toHaveLength(1);

    await screen.findByRole("button", { name: /Bob Jones/ });
  });

  it("Filters the list of members by guild", async () => {
    let memberList = await screen.findAllByRole("listitem", {});
    expect(memberList).toHaveLength(initialState.memberProfiles.length);
    const filterField = await screen.findByRole("combobox", { name: /filter members/i });
    const filterTypeField = await screen.findByRole("combobox", { name: /filter by/i });

    await userEvent.selectOptions(filterTypeField, "Guild");
  });

  it("Filters the list of members by manager (with indirect reports)", () => {

  });

});