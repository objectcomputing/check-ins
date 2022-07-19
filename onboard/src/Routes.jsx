import React from "react";
import { Switch, Route } from "react-router-dom";
import AccessCodePage from "./pages/AccessCodePage";
import WebPortal from "./pages/WebPortal";

import SendRequestPage from "./pages/SendRequestPage";
import EmbedRequestPage from "./pages/EmbedRequestPage";

export default function Routes() {
  return (
    <Switch>
      <Route path="/accesscode">
        <AccessCodePage />
      </Route>

      <Route path="/testSendRequest">
        <SendRequestPage />
      </Route>

      <Route path="/testEmbedRequest">
        <EmbedRequestPage />
      </Route>

      <Route>
        <WebPortal />
      </Route>
    </Switch>
  );
}
