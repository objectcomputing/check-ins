import React from "react";
import {AppContextProvider} from "../../context/AppContext";
import { createMount } from "@material-ui/core/test-utils";
import EditTeamModal from "./EditTeamModal";
import { rest } from 'msw'
import { setupServer } from 'msw/node'
import { render, fireEvent, waitFor, screen } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import user from "@testing-library/user-event";
import { act } from "react-dom/test-utils";

window.snackDispatch = jest.fn();

const server = setupServer(
  rest.get('http://localhost:8080/services/member-profile/current', (req, res, ctx) => {
    return res(ctx.json({ id: "12345", name: "Test User" }));
  }),
  rest.get('http://localhost:8080/services/team/member', (req, res, ctx) => {
    return res(ctx.json([{ id: "12345", name: "Test User" }]));
  })
);

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

const testTeam = {
    name: "Test Team",
    description: "A team used for testing.",
    teamLeads: [{id:123, name:"Team Leader"}, {id:124, name: "Other Leader"}],
    teamMembers: [{id:125, name:"Team Member"}, {id:126, name: "Other Member"}]
};

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
    memberProfileEntities: [{id:123, name:"Team Leader"}, {id:124, name: "Other Leader"}, {id:125, name:"Team Member"}, {id:126, name: "Other Member"}]
  }
}

it("renders open with team", async () => {
  const mockOnSave = jest.fn();

  render(
    <AppContextProvider value={initialState}>
      <EditTeamModal team={testTeam} open={true} onSave={mockOnSave} onClose={jest.fn()} />
    </AppContextProvider>
  );
  
  await waitFor(() => screen.getByText(/Edit your team/i));

  const teamNameInput = screen.getByLabelText(/Team Name/i);
  const teamDescriptionInput = screen.getByLabelText(/Description/i);

  expect(teamNameInput).toHaveValue(testTeam.name);
  expect(teamDescriptionInput).toHaveValue(testTeam.description);

  await act(() => user.click(screen.getByText(/Save Team/i)));

  expect(mockOnSave).toHaveBeenCalledWith({...testTeam});
});
