import React from "react";
import Profile from "./Profile";
import { AppContextProvider } from "../../context/AppContext";

it("renders correctly", () => {
  snapshot(
    <AppContextProvider>
      <Profile />
    </AppContextProvider>
  );
});

it("renders image_url", () => {
  snapshot(
    <AppContextProvider>
      <Profile image_url="http://someurl.com/das.png" />
    </AppContextProvider>
  );
});
