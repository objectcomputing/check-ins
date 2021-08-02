import React from "react";
import SelectDate from "./SelectDate";
import {BrowserRouter} from "react-router-dom";
import {AppContextProvider} from "../../context/AppContext";
import {MuiPickersUtilsProvider} from "@material-ui/pickers";


it("renders the date selector component.", () => {
  shallowSnapshot(
    <BrowserRouter>
    <AppContextProvider>
      <MuiPickersUtilsProvider>
      <SelectDate/>
      </MuiPickersUtilsProvider>
    </AppContextProvider>
    </BrowserRouter>
  );
});