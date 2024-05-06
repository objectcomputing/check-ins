import React, { useEffect, useState } from 'react';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import './DatePickerField.css';

export default function DatePickerField({ date, setDate, label}) {

  const [cleared, setCleared] = useState(false);

  useEffect(() => {
    if (cleared) {
      const timeout = setTimeout(() => {
        setCleared(false);
      }, 1500);

      return () => clearTimeout(timeout);
    }
    return () => {};
  }, [cleared]);

  return (
    <div className="datePickerField">
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      <DatePicker
        label={label}
        value={date}
        format="YYYY-MM-DD"
        onChange={setDate}
        slotProps={{
          field: { clearable: true, onClear: () => setCleared(true) },
        }}
      />
    </LocalizationProvider>
    </div>
  );
}
