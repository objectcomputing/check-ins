import React from "react";
import MemberSelectorDialog from "./MemberSelectorDialog";
import {AppContextProvider} from "../../../context/AppContext";

const currentUserProfile = {
  id: 9876,
  pdlId: 8765,
  name: "Current User",
  firstName: "Current",
  lastName: "User",
};

const initialState = {
  state: {
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
  },
};

const createPortal = vi.fn((element) => {
  return element;
});

vi.mock('react-dom', async (importOriginal) => {
  return {
    ...(await importOriginal('react-dom'))(),
    createPortal
  };
});

describe("MemberSelectorDialog", () => {
  afterEach(() => {
    createPortal.mockClear();
  });

  it("renders correctly", async () => {
    rootSnapshot(
      <AppContextProvider value={initialState}>
        <MemberSelectorDialog
          open={true}
          selectedMembers={initialState.state.memberProfiles}
          onClose={vi.fn()}
          onSubmit={vi.fn()}
        />
      </AppContextProvider>
    );
  });
});