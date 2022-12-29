import React from "react";
import MyBirthday from "./MyBirthday";
import { AppContextProvider } from "../../context/AppContext";
import { BrowserRouter } from "react-router-dom";

const me = {
  "name": "SumanMaroju",
  "birthDay": "12/27",
  "userId": "1b4f99da-ef70-4a76-9b37-8bb783b749ad"
}

it("renders correctly", () => {
  snapshot(
    <AppContextProvider>
      <BrowserRouter>
        <MyBirthday
          me={me}
        />
      </BrowserRouter>
    </AppContextProvider>
  );
});
