import React from "react";
import AccessCodePage from "./AccessCodePage";
import { createMemoryHistory } from "history";
import { Router } from 'react-router-dom';

const mockMemberId = "912834091823";
const mockCheckinId = "837465917381";

const history = createMemoryHistory(`/checkins/${mockMemberId}/${mockCheckinId}`);

it("renders correctly", () => {
  snapshot(
    <Router history={history}>
    <AccessCodePage/>
    </Router>
  );
});
