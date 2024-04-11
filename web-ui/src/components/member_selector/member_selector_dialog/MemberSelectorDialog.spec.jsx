import React from "react";
import {render, screen, within} from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import {AppContextProvider} from "../../../context/AppContext.jsx";
import MemberSelectorDialog from "./MemberSelectorDialog.jsx";
import {setupServer} from "msw/node";
import {http, HttpResponse} from "msw";

const managerProfile = {
  name: "Jane Doe",
  id: "3p45j3k45",
  startDate: [2017, 2, 11],
  location: "STL",
  title: "Manager",
  workEmail: "janedoe@objectcomputing.com",
};

const currentUserProfile = {
  id: 9876,
  pdlId: 8765,
  name: "Current User",
  firstName: "Current",
  lastName: "User",
  supervisorid: managerProfile.id
};

const memberProfile = {
  name: "Bob Jones",
  id: "2o34i2j34",
  startDate: [2018, 1, 10],
  location: "STL",
  title: "Engineer",
  workEmail: "bobjones@objectcomputing.com",
  supervisorid: currentUserProfile.id
};

const testGuild = {
  id: 111,
  name: "Test Guild",
  description: "A guild used for testing.",
  guildLeads: [{id: 124, name: managerProfile.name}],
  guildMembers: []
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
    memberProfile,
    managerProfile
  ],
  skills: [
    { id: "918275", name: "skill1", description: "first" },
    { id: "9183455", name: "skill2", description: "second" },
  ],
  checkins: [],
  guilds: [testGuild],
  teams: [],
  roles: [],
  userRoles: [],
  memberSkills: [],
  index: 0,
};

const server = setupServer(
  http.get(`http://localhost:8080/services/guilds/members?guildId=${testGuild.id}`, () =>
    HttpResponse.json([{memberId: managerProfile.id, guildId: testGuild.id}])
  )
);

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

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

    within(memberList[0]).getByText(memberProfile.name, {});
  });

  it("Filters the list of members by guild", async () => {
    let memberList = await screen.findAllByRole("listitem", {});
    expect(memberList).toHaveLength(initialState.memberProfiles.length);

    const filterField = await screen.findByRole("combobox", { name: /filter members/i });
    let filterTypeField = await screen.findByRole("combobox", { name: /filter by/i });
    expect(filterField.innerHTML).toBe("");
    expect(filterTypeField.innerHTML).toBe("Team");

    // Set the filter type to guild
    await userEvent.click(filterTypeField);
    let optionToSelect = await screen.findByRole("option", { name: /guild/i });
    await userEvent.click(optionToSelect);
    expect(filterTypeField.innerHTML).toBe("Guild");

    // Select a guild to use as the filter
    await userEvent.click(filterField);
    optionToSelect = await screen.findByRole("option", { name: testGuild.name });
    await userEvent.click(optionToSelect);
    expect(filterField.value).toBe(testGuild.name);

    // Only members in the guild should be visible
    memberList = await screen.findAllByRole("listitem", {});
    expect(memberList).toHaveLength(1);
    within(memberList[0]).getByText(managerProfile.name, {});
  });

  it("Filters the list of members by manager (with indirect reports)", async () => {
    let memberList = await screen.findAllByRole("listitem", {});
    expect(memberList).toHaveLength(initialState.memberProfiles.length);

    const filterField = await screen.findByRole("combobox", { name: /filter members/i });
    let filterTypeField = await screen.findByRole("combobox", { name: /filter by/i });
    expect(filterField.innerHTML).toBe("");
    expect(filterTypeField.innerHTML).toBe("Team");

    // Set filter type to manager
    await userEvent.click(filterTypeField);
    let optionToSelect = await screen.findByRole("option", { name: /manager/i });
    await userEvent.click(optionToSelect);
    expect(filterTypeField.innerHTML).toBe("Manager");

    // Select a manager to use as the filter
    await userEvent.click(filterField);
    optionToSelect = await screen.findByRole("option", { name: managerProfile.name });
    await userEvent.click(optionToSelect);
    expect(filterField.value).toBe(managerProfile.name);

    // All subordinates of the selected manager should be visible
    memberList = await screen.findAllByRole("listitem", {});
    expect(memberList).toHaveLength(2);
    screen.getByText(memberProfile.name, {});
    screen.getByText(currentUserProfile.name, {});

    // Change the checkbox to only show direct reports
    const checkbox = await screen.findByRole("checkbox", { name: /direct reports only/i });
    expect(checkbox).not.toBeChecked();
    await userEvent.click(checkbox);
    expect(checkbox).toBeChecked();

    // Only direct reports should be visible
    memberList = await screen.findAllByRole("listitem", {});
    expect(memberList).toHaveLength(1);
    screen.getByText(currentUserProfile.name, {});
  });

});