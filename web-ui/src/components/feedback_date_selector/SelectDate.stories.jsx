import React from "react";
import SelectDate from "./SelectDate";
import {AppContextProvider} from "../../context/AppContext";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";

export default {
    title: "Check-Ins/SelectDate",
    component: SelectDate,
    args: {
      changeQuery: (data1, data2) => {}
    },
    decorators: [(Story) => {
        return (
          <AppContextProvider>
              <LocalizationProvider dateAdapter={AdapterDateFns}>
                  <Story/>
              </LocalizationProvider>
          </AppContextProvider>
        );
    }]
};

const Template = (args) => <SelectDate {...args} />;
export const DefaultDatePicker = Template.bind({});