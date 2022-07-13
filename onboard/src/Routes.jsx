import React from "react";
import { Switch, Route } from "react-router-dom";
import AccessCodePage from "./pages/AccessCodePage";
import WebPortal from "./pages/WebPortal";

import SendRequest from "./pages/TestSendRequestPage";
import EmbedRequest from "./pages/TestEmbeddedSigningPage";

export default function Routes() {
  return (
    <Switch>
      <Route path="/accesscode">
        <AccessCodePage />
      </Route>

      <Route path="/testSendRequest">
        <SendRequest />
      </Route>

      <Route path="/testEmbedRequest">
        <EmbedRequest />
      </Route>

      <Route>
        <WebPortal />
      </Route>
    </Switch>
  );
}
