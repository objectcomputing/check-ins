import React from 'react';
import { MemoryRouter } from 'react-router-dom';
import GuidesPanel from './GuidesPanel';
import { AppContextProvider } from '../../context/AppContext.jsx';
import {setupServer} from "msw/node";
import {http, HttpResponse} from "msw";

const initialState = {
  state: {
    csrf: 'O_3eLX2-e05qpS_yOeg1ZVAs9nDhspEi',
    userProfile: {
      name: 'holmes',
      role: ['MEMBER', 'PDL'],
      imageUrl:
        'https://upload.wikimedia.org/wikipedia/commons/7/74/SNL_MrBill_Doll.jpg'
    },
    roles: [
      { id: 1, role: 'MEMBER' },
      { id: 2, role: 'PDL' }
    ],
    teams: []
  }
};

const mockuments = [
  {
    id: 'mockument-1',
    name: 'Expectations Discussion Guide for Team Members',
    url: '/pdfs/Expectations_Discussion_Guide_for_Team_Members.pdf',
    description: 'My description'
  },
  {
    id: 'mockument-2',
    name: 'Expectations Worksheet',
    url: '/pdfs/Expectations_Worksheet.pdf',
    description: 'My worksheet'
  }
];

const pdlMockuments = [
  {
    id: 'mockument-3',
    name: 'Expectations Discussion Guide for PDLs',
    url: '/pdfs/Expectations_Discussion_Guide_for_PDLs.pdf',
    description: 'My PDL description'
  }
];

const server = setupServer(
    http.get('http://localhost:8080/services/document/1', () => {
      return HttpResponse.json(mockuments);
    }),
    http.get('http://localhost:8080/services/document/2', () => {
      return HttpResponse.json(pdlMockuments);
    })
);

beforeAll(() => server.listen({ onUnhandledRequest(request, print) {} }));
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

it('renders correctly', async () => {
  await waitForSnapshot('mockument-3',
    <MemoryRouter>
      <AppContextProvider value={initialState}>
        <GuidesPanel />
      </AppContextProvider>
    </MemoryRouter>
  );
});
