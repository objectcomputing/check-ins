import React from 'react';
import SelectDate from './SelectDate';
import {AppContextProvider} from '../../context/AppContext';
import DateFnsUtils from '@date-io/date-fns';
import { MuiPickersUtilsProvider } from "@material-ui/pickers";

export default {
    title: 'Check-Ins/SelectDate',
    component: SelectDate,
    decorators: [(Story) => {
        return (<AppContextProvider><MuiPickersUtilsProvider utils={DateFnsUtils}><Story/></MuiPickersUtilsProvider></AppContextProvider>);
    }]
};

const Template = (args) => <SelectDate {...args} />;
export const DefaultDatePicker = Template.bind({});