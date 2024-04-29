import * as React from 'react';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import './DatePickerField.css';

export default function DatePickerField({ date, setDate, label}) {
  return (
    <div className="datePickerField">
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      <DatePicker
        label={label}
        value={date}
        onChange={newValue => setDate(newValue)}
      />
    </LocalizationProvider>
    </div>
  );
}
