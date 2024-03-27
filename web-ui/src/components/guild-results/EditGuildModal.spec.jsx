import React from "react";
import {AppContextProvider} from "../../context/AppContext";
import EditGuildModal from "./EditGuildModal";
import { rest } from 'msw'
import { setupServer } from 'msw/node'
import { render, fireEvent, waitFor, screen } from '@testing-library/react';
import user from "@testing-library/user-event";
import { act } from "react-dom/test-utils";

window.snackDispatch = jest.fn();

const server = setupServer(
  rest.get('http://localhost:8080/csrf/cookie', (req, res, ctx) => {
      return res(ctx.text("O_3eLX2-e05qpS_yOeg1ZVAs9nDhspEi"));
    }),
  rest.get('http://localhost:8080/services/member-profile/current', (req, res, ctx) => {
    return res(ctx.json({ id: "12345", name: "Test User" }));
  }),
  rest.get('http://localhost:8080/services/guilds/members', (req, res, ctx) => {
    return res(ctx.json([{ id: "12345", name: "Test User" }]));
  })
);

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

const testGuild = {
    name: "Test Guild",
    description: "A guild used for testing.",
    guildLeads: [{id:123, name:"Guild Leader"}, {id:124, name: "Other Leader"}],
    guildMembers: [{id:125, name:"Guild Member"}, {id:126, name: "Other Member"}]
};

const emptyGuild = {
    name: "Test Guild",
    description: "A guild used for testing.",
}

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
    memberProfiles: [{id:123, name:"Guild Leader", lastName:"Leader"}, {id:124, name: "Other Leader", lastName:"OLeader"}, {id:125, name:"Guild Member", lastName:"Member"}, {id:126, name: "Other Member", lastName:"OMember"}]
  }
}


it("Cannot save without lead", async () => {
  const mockOnSave = jest.fn();

  render(
    <AppContextProvider value={initialState}>
      <EditGuildModal guild={testGuild} open={true} onSave={mockOnSave} onClose={jest.fn()} headerText="Edit your guild"/>
    </AppContextProvider>
  );

  await waitFor(() => screen.getByText(/Edit your guild/i));

  const guildNameInput = screen.getByLabelText(/Guild Name/i);
  const guildDescriptionInput = screen.getByLabelText(/Description/i);

  expect(guildNameInput).toHaveValue(testGuild.name);
  expect(guildDescriptionInput).toHaveValue(testGuild.description);

  const saveBtn = screen.getByText(/Save Guild/i);
  expect(saveBtn).toBeDisabled();
  //expect(() => user.click(saveBtn)).toThrow();
  expect(mockOnSave).not.toHaveBeenCalledWith({...testGuild});
});

