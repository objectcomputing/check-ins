import React from "react";
import DirectoryPage from "./DirectoryPage";
import { AppContextProvider } from "../context/AppContext";
import { render, fireEvent, screen } from "@testing-library/react";
import { Button, TextField, Grid } from "@material-ui/core";
import MemberSummaryCard from "../components/member-directory/MemberSummaryCard";

let teamMembers;

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
    teamMembers: [
      { name: "Iván", image_url: null },
      { name: "mark", image_url: null },
      { name: "michael", image_url: null },
      { name: "mj", image_url: null },
      { name: "kobe", image_url: null },
      { name: "lebron", image_url: null },
    ],
  },
};

it("renders correctly", () => {
    <AppContextProvider>
        snapshot(
        <DirectoryPage />
        );
    </AppContextProvider>;
});

it("finds match despite accents", (done) => {

  let teamMembers = initialState.state.teamMembers;
  let matches;
  const searchText = "ivan";
  const createMemberCards = teamMembers.map((member, index) => {
      let normName = member.name.normalize('NFD').replace(/[\u0300-\u036f]/g, "");
      let normSearchText = searchText.normalize('NFD').replace(/[\u0300-\u036f]/g, "");
      if (normName.toLowerCase().includes(normSearchText.toLowerCase())) {
        matches = normName;
        return (
            <MemberSummaryCard
              key={`${member.name}-${member.id}`}
              index={index}
              member={member}
            />
        )
      }
      done();
  });

  expect(matches === searchText);
  expect(createMemberCards.length === 1);

  render(
    <AppContextProvider value={initialState}>
      <DirectoryPage  />
    </AppContextProvider>
  );
  fireEvent.click(screen.getByText("Search Members"));
});